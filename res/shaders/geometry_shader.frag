#version 130

// Используем отдельные компоненты цвета как в вашем примере
uniform float color_r;
uniform float color_g;
uniform float color_b;
uniform float color_a;

void main() {
    gl_FragColor = vec4(color_r, color_g, color_b, color_a);
}