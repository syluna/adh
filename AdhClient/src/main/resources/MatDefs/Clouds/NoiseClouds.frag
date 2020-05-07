
uniform vec4 m_Color;
uniform float m_Scale;
uniform sampler2D m_Noise;
uniform vec2 m_Speed;
uniform float m_Brightness;
uniform float m_MorphSpeed;
uniform float m_MorphDirection;
uniform float m_Cover;

varying float time;
varying vec3 norm;
varying vec2 texCoord;

void main() {

    vec3 cloudPos = norm * 0.2 / norm.y;
    vec2 tcVar = vec2(cloudPos.x,cloudPos.z);

    vec4 colorOutput = vec4( 0.0 );
    vec2 elapsed = time * m_Speed;

    vec2 uv = (tcVar + elapsed) * m_Scale;

    for( int i = 1; i <= 8; i++ ) {
        float f = float( i );

        float divis = pow( 2.0, f );
        float uvPow = pow( 2.0, f - 1.0 );

        vec4 computed = texture2D(
            m_Noise, uvPow * ( uv + vec2( 0.1, 0.0 ) + ( time * 0.001 * m_MorphSpeed ) )
        ) / divis;
        computed += texture2D(
            m_Noise, uvPow * ( uv + vec2( 0.1 ) )
        ) / divis;
        computed += texture2D(
            m_Noise, uvPow * ( uv + vec2( 0.0, 0.1 ) + ( m_MorphDirection * time * 0.001 * m_MorphSpeed ) )
        ) / divis;

        computed *= 0.25;

        colorOutput += computed;
    }

    colorOutput = max( colorOutput - ( 1.0 - m_Cover ), 0.0 );
    colorOutput = vec4( 1.0 - pow( ( 1.0 - m_Brightness ), colorOutput.r * 255.0 ) );

    gl_FragColor = vec4( m_Color.rgb * colorOutput.rgb, texCoord.y );

}