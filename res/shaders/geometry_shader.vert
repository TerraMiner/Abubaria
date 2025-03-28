#version 130

const int LINE = 0;
const int RECT_OUTLINE = 1;
const int RECT_FILLED = 2;

uniform mat4 projection;
uniform int shapeType;

uniform float translateX;
uniform float translateY;
uniform float scaleX;
uniform float scaleY;

const vec2 vertexPositions[4] = vec2[](
vec2(0.0, 1.0),
vec2(1.0, 1.0),
vec2(1.0, 0.0),
vec2(0.0, 0.0)
);

void main() {
    vec2 position;

    if (shapeType == LINE) {
        float t = float(gl_VertexID);
        position = mix(vec2(translateX, translateY), vec2(scaleX, scaleY), t);
    } else {
        position = vertexPositions[gl_VertexID];
        position *= vec2(scaleX, scaleY);
        position += vec2(translateX, translateY);
    }

    gl_Position = projection * vec4(position, 0.0, 1.0);
}