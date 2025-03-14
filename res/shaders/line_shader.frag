#version 120

uniform vec4 color;      // Цвет линии
uniform float lineWidth; // Толщина линии

varying float width;     // Получаем толщину линии из вершинного шейдера

void main() {
    // Если фрагмент находится внутри линии, рисуем его
    if (gl_FragCoord.x > 0.0 && gl_FragCoord.x < lineWidth) {
        gl_FragColor = color;  // Устанавливаем цвет линии
    } else {
        discard;  // Если фрагмент за пределами линии, не рисуем
    }
}
