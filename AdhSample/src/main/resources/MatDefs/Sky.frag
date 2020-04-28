
// clouds
uniform sampler2D m_Cloud_Noise;
uniform vec4 m_Cloud_Color;
uniform vec2 m_Cloud_Speed;
uniform float m_Cloud_MorphDirection;
uniform int m_Cloud_Octaves;

uniform vec4 m_Cloud_Settings;

// varying float time;
varying vec3 norm;
varying vec2 texCoord;

// stars
uniform vec3 m_Stars;
uniform sampler2D m_StarColors;

// sky
varying vec3 vRayleighColor;
varying vec3 vMieColor;
varying vec3 vDirection;

// Phase function
uniform float m_G;
uniform float m_G2;

// Global time variable
uniform float g_Time;

// Direction of the light.
uniform vec3 m_LightDir;

// Exposure.
uniform float m_Exposure;

// Cloud stuff
// uniform sampler2D m_CloudTexture;
// uniform sampler2D m_CloudNormal;
uniform vec4 m_SunColor;
uniform vec4 m_AmbientColor;

// end fields

// clouds
vec4 getCloudColor() {

    vec3 cloudPos = norm * 0.2 / norm.y;
    vec2 tcVar = vec2(cloudPos.x,cloudPos.z);

    vec4 colorOutput = vec4( 0.0 );
    vec2 elapsed = g_Time * m_Cloud_Speed;

    vec2 uv = (tcVar + elapsed) * m_Cloud_Settings.x;

    // generate a cloud color
    for( int i = 1; i <= m_Cloud_Octaves; i++ ) {

        float f = float( i );

        float divis = pow( 2.0, f );
        float uvPow = pow( 2.0, f - 1.0 );

        vec4 computed = texture2D(
                m_Cloud_Noise, uvPow * ( uv + vec2( 0.1, 0.0 ) + ( g_Time * 0.001 * m_Cloud_Settings.w ) )
        ) / divis;

        computed += texture2D(
                m_Cloud_Noise, uvPow * ( uv + vec2( 0.1 ) )
        ) / divis;

        computed += texture2D(
                m_Cloud_Noise, uvPow * ( uv + vec2( 0.0, 0.1 ) + ( m_Cloud_MorphDirection * g_Time * 0.001 * m_Cloud_Settings.w ) )
        ) / divis;

        computed *= 0.2;

        colorOutput += computed;
    }

    colorOutput = max( colorOutput - ( 1.0 - m_Cloud_Settings.y ), 0.0 );
    colorOutput = vec4( 1.0 - pow( ( 1.0 - m_Cloud_Settings.z ), colorOutput.r * 255.0 ) );

    //float cloudFactor = (  (colorOutput.r) * (colorOutput.r) )
            //* clamp( exp(20.0 * (norm.y - 0.2) ), 0.0, 1.0);

    //float cloudFactor = colorOutput.r * clamp( exp(120.0 * (norm.y - 0.2)), 0.0, 1.0 );

    //return vec4( m_Cloud_Color.rgb * colorOutput.rgb, texCoord.y );
    //vec4 color = vec4( m_Cloud_Color * colorOutput );
    //vec4 color = vec4( (m_Cloud_Color.rgb * (1.5 + norm.y) ) * colorOutput.rgb, 1.0 );

    // darken the clouds as the sun goes down.
    colorOutput.rgb -= clamp(1.0 - m_LightDir.y, 0.0, 0.2);

    //colorOutput.rgb *= 2.0;
    //colorOutput.rgb *= texCoord.y;
    //colorOutput.a = (colorOutput.r + colorOutput.g + colorOutput.b) / 3.0;
    //return clamp(color, 0.0, 0.5);
    return colorOutput * m_Cloud_Color;
}

// stars
vec2 rand2(vec2 p) {
    p = vec2(dot(p, vec2(12.9898,78.233)), dot(p, vec2(26.65125, 83.054543)));
    return fract(sin(p) * 43758.5453);
}

float rand(vec2 p) {
    return fract(sin(dot(p.xy ,vec2(54.90898,18.233))) * 4337.5453);
}

float stars(in vec2 x, float numCells, float size, float br) {

    vec2 n = x * numCells;
    vec2 f = floor(n);

    float d = 1.0e10;
    for (int i = -1; i <= 1; ++i)
    {
        for (int j = -1; j <= 1; ++j)
        {
            vec2 g = f + vec2(float(i), float(j));
            g = n - g - rand(mod(g, numCells)) + rand2(g);
            // Control size
            g *= 1. / (numCells * size);
            d = min(d, dot(g, g));
        }
    }

    return br * (smoothstep(.95, 1., (1. - sqrt(d))));
}

vec4 getStarsColor() {

    vec3 starPos = norm * 1900.0 / norm.y;
    vec2 tcVar = vec2(starPos.x,starPos.z);

    vec2 coord = tcVar;

    // we dont want the actual color, just a tint.
    vec4 starColor = vec4(1.0) - texture2D(m_StarColors, texCoord) * 0.4;
    vec3 result = stars(vec2(coord.x, coord.y) , m_Stars.x, m_Stars.z, m_Stars.y) * starColor.rgb;


    result *= (.5 - m_LightDir.y); // reduce the brightness of the stars as the sun rises.
    result *= texCoord.y; // remove stars from near the bottom of the sphere.

    return vec4(result, texCoord.y);
}

// sky
vec4 getSkyColor() {
    // Calculate the base atmosphere color.
    vec3 skyColor = vec3(0.0);

    if(m_LightDir.y >= -0.3){
        float cosA = dot(m_LightDir, normalize(vDirection));
        float cosA2 = cosA*cosA;

        float rayleighPhase = 0.75 * (1.0 + 0.5*cosA2);

        float miePhase = 1.5 * ((1.0 - m_G2) / (2.0 + m_G2)) *
        (1.0 + cosA2) / pow(1.0 + m_G2 - 2.0 * m_G * cosA, 1.5);

        skyColor = rayleighPhase * vRayleighColor + miePhase * vMieColor;

        #if !defined(HDR_ENABLED)
            skyColor = vec3(1.0 - exp(-m_Exposure*skyColor));
        #endif
    }
    // ******************************** clouds *********************

    // The base texture coordinate offset.
    //  vec2 offset = g_Time*0.005*vec2(1,1);
    // Get the fragment position on the projected plane.
    // vec3 n = norm;
    // vec3 cloudPos = n * 0.2 / n.y;

    // vec2 tcVar = vec2(cloudPos.x,cloudPos.z);
    // float initDens = texture2D(m_CloudTexture,tcVar + offset).r;

    // Calculate cloud density between sun and fragment (approx).
    // float density = 0.25*initDens; // Initial value.
    // vec3 dirToLight = normalize(m_LightDir - cloudPos);
    // vec2 dirToLightTc = normalize(m_LightDir.xz - cloudPos.xz);
    //vec2 dirToLightTc = normalize(vec2(dirToLight.x,dirToLight.z));
    // for(int i = 1; i < 4; i++){
    // Ray-trace along dirToLight.
    //tcVar += 0.01*dirToLightTc; // 0.01
    //float d = texture2D(m_CloudTexture,tcVar + offset).r;
    //density += 0.25*d;
    //}
    // A value used for cloud transparency.
    // float cloudFactor = (1.0 - (1.0 - initDens)*(1.0 - initDens))*clamp(exp(20.0*(n.y - 0.2)),0.0,1.0);
    //float cloudFactor = initDens*clamp(exp(20.0*(n.y - 0.2)),0.0,1.0);

    //vec3 cloudNorm = -(2.0*texture2D(m_CloudNormal, vec2(cloudPos.x,cloudPos.z) + offset).rbg-1.0);
    //cloudNorm.x = -cloudNorm.x;
    //float colMod = 0.15*(1.0 - clamp(dot(-dirToLight,cloudNorm),0.0,1.0)) + 0.85;
    // vec3 cloudColor = (m_AmbientColor.rgb*0.4 + m_SunColor.rgb)*(0.4*(1.0 - density) + 0.6);//*colMod*0.9;

    float blend = 1.0;
    if(m_LightDir.y <= -0.1){
        // From 1.0 at -0.1 to 0 at -0.2.
        blend = clamp( 1.0 + (m_LightDir.y + 0.1) * 10.0, 0.0, 1.0);
    }

    // skyColor = mix(skyColor, cloudColor, cloudFactor);
    // gl_FragColor = vec4(skyColor,max(blend,cloudFactor));
    return vec4(skyColor, 1.0);
}

void main() {

    vec4 cloudColor = getCloudColor();
    vec4 starsColor = getStarsColor();

    // reduce the star brightness based on the "density" of the cloud.
    // the "thicker" the clouds, the less likely the star will be visible.
	//starsColor.rgb *= clamp(1.0 - (cloudColor.rgb * 2.0), 0.0, 1.0);

	vec4 starCloudMix = max(cloudColor, starsColor);
    vec4 skyColor = getSkyColor();

	//vec4 result = clamp(starCloudMix + skyColor, 0.0, 1.0);
    //vec4 result = mix(skyColor, cloudColor, cloudColor.a);
	vec4 result = mix(skyColor, starCloudMix + cloudColor, cloudColor.a);

    gl_FragColor = result;

}