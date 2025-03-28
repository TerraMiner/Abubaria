#version 130

attribute vec2 texCoord;
varying vec2 texCoords;
uniform mat4 projection;
uniform float translateX;
uniform float translateY;
uniform float scaleX;
uniform float scaleY;
uniform float angle;

const vec2 vertexPositions[4] = vec2[](
vec2(0.0, 1.0),
vec2(1.0, 1.0),
vec2(1.0, 0.0),
vec2(0.0, 0.0)
);

void main() {
    vec2 position = vertexPositions[gl_VertexID];
    texCoords = texCoord;

    vec2 scale = vec2(scaleX, scaleY);
    vec2 scaledPos = position * scale;
    vec2 translatedPos;

    if (angle == 0.0) {
        translatedPos = scaledPos + vec2(translateX, translateY);
    } else {
        vec2 center = scale * 0.5;
        vec2 centeredPos = scaledPos - center;
        float cosA = cos(angle);
        float sinA = sin(angle);
        vec2 rotatedPos = vec2(
        cosA * centeredPos.x - sinA * centeredPos.y,
        sinA * centeredPos.x + cosA * centeredPos.y
        );
        translatedPos = rotatedPos + center + vec2(translateX, translateY);
    }

    gl_Position = projection * vec4(translatedPos, 0.0, 1.0);
}
