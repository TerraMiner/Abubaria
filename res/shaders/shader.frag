#version 130

uniform sampler2D sampler;
varying vec2 texCoords;
uniform float color_r;
uniform float color_g;
uniform float color_b;
uniform float color_a;

void main() {
    vec4 color = vec4(color_r, color_g, color_b, color_a);
    vec4 texColor = texture2D(sampler, texCoords);
    gl_FragColor = texColor * color;
}