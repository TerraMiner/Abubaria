#version 330 core

in vec2 texCoords;
in vec4 vertexColor;
flat in float fragRenderType;
in vec2 rectSize;

uniform sampler2D sampler;

void main() {
    vec4 color;

    if (fragRenderType == 0.0) {
        color = texture2D(sampler, texCoords) * vertexColor;
        if (color.a < 0.00001)
            discard;
    }
    else if (fragRenderType == 1.0) {
        color = vertexColor;
    }
    else if (fragRenderType == 2.0) {
        float borderThickness = 1.0;
        float borderWidthX = borderThickness / rectSize.x;
        float borderWidthY = borderThickness / rectSize.y;

        if (texCoords.x < borderWidthX || texCoords.x > 1.0 - borderWidthX ||
            texCoords.y < borderWidthY || texCoords.y > 1.0 - borderWidthY) {
            color = vertexColor;
        } else {
            discard;
        }
    }
    else if (fragRenderType == 3.0) {
        color = vertexColor;
    }
    else {
        color = vertexColor;
    }

    gl_FragColor = color;
}