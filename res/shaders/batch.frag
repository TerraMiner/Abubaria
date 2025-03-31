#version 120

varying vec2 texCoords;
varying vec4 vertexColor;
varying float renderType;

uniform sampler2D sampler;

void main() {
    vec4 color;

    if (renderType == 0.0) { // TEXTURE
        color = texture2D(sampler, texCoords) * vertexColor;
    } else if (renderType == 1.0) { // GEOMETRY
        color = vertexColor;
    } else { // PARTICLE
        color = texture2D(sampler, texCoords) * vertexColor;
    }

    gl_FragColor = color;
} 