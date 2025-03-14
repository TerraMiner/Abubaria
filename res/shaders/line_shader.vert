#version 120

attribute vec2 vertices;  // Вершины линии (начало и конец)
attribute float lineWidth; // Толщина линии

uniform mat4 projection;  // Проекционная матрица

varying float width;      // Передаем толщину линии во фрагментный шейдер

void main() {
    width = lineWidth;

    // Проецируем координаты линии
    gl_Position = projection * vec4(vertices, 0.0, 1.0);
}
