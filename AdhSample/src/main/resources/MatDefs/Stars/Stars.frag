varying float time;
varying vec3 norm;
varying vec2 texCoord;

// Vector3(amount, brightness, size)
uniform vec3 m_Stars_0;
uniform vec4 m_Color_0;
uniform vec2 m_Speed_0;

uniform vec3 m_Stars_1;
uniform vec4 m_Color_1;
uniform vec2 m_Speed_1;

uniform vec3 m_Stars_2;
uniform vec4 m_Color_2;
uniform vec2 m_Speed_2;

uniform vec3 m_Stars_3;
uniform vec4 m_Color_3;
uniform vec2 m_Speed_3;

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


void main()
{
    vec3 cloudPos = norm * 1900. / norm.y;
    vec2 tcVar = vec2(cloudPos.x,cloudPos.z);
    vec2 coord = tcVar;

    vec3 result = vec3(0.0);

    result += stars(vec2(coord.x + ((time + 0.0) * m_Speed_0.x), coord.y + ((time + 5674.0) * m_Speed_0.y)) , m_Stars_0.x, m_Stars_0.z, m_Stars_0.y)  * m_Color_0.rgb;
    result += stars(vec2(coord.x + ((time + 2345.0) * m_Speed_1.x), coord.y + ((time + 7344.0) * m_Speed_1.y)) , m_Stars_1.x, m_Stars_1.z, m_Stars_1.y)  * m_Color_1.rgb;
    result += stars(vec2(coord.x + ((time + 3465.0) * m_Speed_2.x), coord.y + ((time + 6542.0) * m_Speed_2.y)) , m_Stars_2.x, m_Stars_2.z, m_Stars_2.y)  * m_Color_2.rgb;
    result += stars(vec2(coord.x + ((time + 7345.0) * m_Speed_3.x), coord.y + ((time + 3345.0) * m_Speed_3.y)) , m_Stars_3.x, m_Stars_3.z, m_Stars_3.y)  * m_Color_3.rgb;


    // result += stars(vec2(coord.x + (time * 0.02), coord.y) , 10., 0.010, 2.)  * vec3(.74, .74, .74);
    // result += stars(vec2(coord.x + time * 1.10,coord.y) , 1., 0.10, 2.)  * vec3(.74, .74, .74);
    // result += stars(vec2(coord.x + time * 0.8,coord.y) , 1., 0.10, 2.)  * vec3(.74, .74, .74);
    // result += stars(vec2(coord.x + time * 0.5,coord.y) , 2., 0.09, 2.) * vec3(.74, .74, .74);
    // result += stars(vec2(coord.x + time * 0.2,coord.y) , 4., 0.08, 2.) * vec3(.74, .74, .74);
    // result += stars(vec2(coord.x + time * 0.000, coord.y + time * 0.00), 1., 0.35, 0.8);// * vec3(.95, .74, .74);
    // result += stars(vec2(coord.x + time * 0.000, coord.y + time * 0.00), 1., 0.35, 0.8);// * vec3(.95, .95, .95);
    // result += stars(coord, 20., 0.025, .5) * vec3(.9, .9, .95);
    // result += stars(vec2(coord.x + time * 0.025,coord.y), 10., 0.05,0.8) * vec3(.95, .95, .95);

    // float transp = (1.0 - texCoord.y);
    // float transp = 1.0;
    // float transp = max(0, texCoord.y - .1);

	gl_FragColor = vec4(result, texCoord.y);

}