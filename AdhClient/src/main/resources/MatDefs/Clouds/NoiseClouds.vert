#import "Common/ShaderLib/Instancing.glsllib"

uniform vec3 g_CameraPosition;
uniform float g_Time;

attribute vec3 inPosition;
attribute vec2 inTexCoord;

varying float time;
varying vec3 norm;
varying vec2 texCoord;

void main() {

    time = g_Time;
    texCoord = inTexCoord;

    vec4 modelSpacePos = vec4(inPosition, 1.0);
    gl_Position = TransformWorldViewProjection(modelSpacePos);

    vec3 v3Pos = vec3(inPosition);
    float len = length(v3Pos);
    v3Pos = v3Pos / len;
    norm = v3Pos;

}