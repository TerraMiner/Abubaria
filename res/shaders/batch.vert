#version 120

attribute vec4 vertexData;
attribute vec4 colorData;
attribute float instanceData;

varying vec2 texCoords;
varying vec4 vertexColor;
varying float renderType;

uniform mat4 projection;
uniform vec2 view;
uniform float time;

void main() {
    texCoords = vertexData.zw;
    vertexColor = colorData;
    renderType = instanceData;

    vec2 pos = vertexData.xy;
    pos += view;
    gl_Position = projection * vec4(pos, 0.0, 1.0);
}