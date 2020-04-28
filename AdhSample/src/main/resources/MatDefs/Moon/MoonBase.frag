
uniform float m_MoonPhase;
varying vec2 texCoord;

uniform sampler2D m_MoonTex;
uniform sampler2D m_Noise;

// uniform float m_Phase;
uniform float m_Phase;


vec2 hash2(vec2 p ) {
    return fract(sin(vec2(dot(p, vec2(123.4, 748.6)), dot(p, vec2(547.3, 659.3))))*5232.85324);
}

float fbm(vec2 p) {
    float h = 0.0;
    float a = 0.5;
    for (int i = 0;i<4;i++) {
        //h+=noise(p)*a;
        h+= texture2D(m_Noise, p).x*a;
        p*=2.0;
        a*=0.5;
    }
    return h;
}

float voronoi(vec2 p) {
    vec2 n = floor(p);
    vec2 f = fract(p);
    float md = 5.0;
    vec2 m = vec2(0.0);
    for (int i = -1;i<=1;i++) {
        for (int j = -1;j<=1;j++) {
            vec2 g = vec2(i, j);
            vec2 o = hash2(n+g);
            o = 0.5+0.5*sin(m_Phase+5.038*o);
            vec2 r = g + o - f;
            float d = dot(r, r);
            if (d<md) {
                md = d;
                m = n+g+o;
            }
        }
    }
    return 1.0-md;
}

vec3 project(vec2 p) {
    return vec3(p.x, p.y, sqrt(-(p.x*p.x+p.y*p.y-0.24)));
}

void main() {

    vec2 uv = texCoord - 0.5;
    vec3 n = project(uv);
    vec3 sun = vec3(sin(m_Phase*0.5) * 2048.0, 0.0, cos(m_Phase*0.5) * 2048.0);
    vec3 I = sun-n;
    I = normalize(I);
    float s = dot(n, I);
    s = clamp(s*1.9, 0.0, 1.0);

    float dist = length(uv);
    vec4 moonTex = texture2D(m_MoonTex, texCoord);
    vec3 moon = moonTex.rgb;
    moon = moon*(1.0-0.2*smoothstep(0.4, 0.44, dist));

    float tex = fbm((uv+vec2(5.0))*0.03);
    vec3 vtex = vec3(0.9+0.1*voronoi(uv*10.0));

    vec4 result = vec4(mix(moon*(0.6+0.4*tex), vtex, fbm(uv*0.02)) * s, moonTex.a);
    result.a = result.r;

    gl_FragColor = result;

}

