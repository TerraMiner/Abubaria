#version 330 core

layout(location = 0) in vec4 rectData;    // x, y, width, height (для линии: x1, y1, x2, y2)
layout(location = 1) in vec4 uvData;      // uvX, uvY, uvMX, uvMY
layout(location = 2) in float zIndex;     // z-index
layout(location = 3) in vec4 colorData;   // r, g, b, a
layout(location = 4) in vec2 transformData; // thickness, rotation
layout(location = 5) in float ignoreCamera; // флаг игнорирования масштаба камеры (0 или 1)
layout(location = 6) in float renderType;   // тип рендеринга

const vec4 vertexData[6] = vec4[](
vec4(0.0, 0.0, 0.0, 0.0),
vec4(0.0, 1.0, 0.0, 1.0),
vec4(1.0, 1.0, 1.0, 1.0),

vec4(0.0, 0.0, 0.0, 0.0),
vec4(1.0, 1.0, 1.0, 1.0),
vec4(1.0, 0.0, 1.0, 0.0)
);

out vec2 texCoords;
out vec4 vertexColor;
flat out float fragRenderType;
out vec2 rectSize; // Передаем размер прямоугольника для расчета границы

uniform mat4 projection;
uniform vec4 color;
uniform vec2 cameraScale;
uniform vec2 cameraCenter;

void main() {
    vec4 vertex = vertexData[gl_VertexID];
    vec2 pos;
    
    if (renderType == 3.0) { // LINE
        // Для линии rectData содержит x1, y1, x2, y2 (абсолютные координаты)
        vec2 p1 = rectData.xy;
        vec2 p2 = rectData.zw;
        
        // Применяем масштаб камеры к обеим точкам, если ignoreCamera == 0
        if (ignoreCamera == 0.0) {
            p1 = cameraCenter + (p1 - cameraCenter) * cameraScale;
            p2 = cameraCenter + (p2 - cameraCenter) * cameraScale;
        }
        
        // Вычисляем вектор направления линии
        vec2 dir = p2 - p1;
        float len = length(dir);
        
        if (len > 0.0) {
            dir = normalize(dir);
            
            // Вычисляем перпендикулярный вектор
            vec2 perp = vec2(-dir.y, dir.x) * transformData.x * 0.5; // transformData.x = thickness
            
            // Создаем четырехугольник вдоль линии
            if (gl_VertexID == 0) {
                pos = p1 - perp;
            } else if (gl_VertexID == 1) {
                pos = p1 + perp;
            } else if (gl_VertexID == 2) {
                pos = p2 + perp;
            } else if (gl_VertexID == 3) {
                pos = p1 - perp;
            } else if (gl_VertexID == 4) {
                pos = p2 + perp;
            } else { // gl_VertexID == 5
                pos = p2 - perp;
            }
        } else {
            // Если длина линии равна 0, создаем точку
            pos = p1;
        }
        
        // Для линии не применяем масштаб камеры повторно, так как уже применили выше
    } else {
        // Для текстуры, заполненного и полого прямоугольника
        vec2 size = rectData.zw;
        rectSize = size; // Передаем размер прямоугольника для расчета границы
        
        vec2 relPos = vertex.xy * size;
        
        // Применяем поворот, если он не равен 0
        if (transformData.y != 0.0) {
            float cosA = cos(transformData.y);
            float sinA = sin(transformData.y);
            vec2 center = size * 0.5;
            vec2 rotatedPos = vec2(
                cosA * (relPos.x - center.x) - sinA * (relPos.y - center.y),
                sinA * (relPos.x - center.x) + cosA * (relPos.y - center.y)
            );
            relPos = rotatedPos + center;
        }
        
        pos = rectData.xy + relPos;
        
        // Применяем масштаб камеры, если ignoreCamera == 0
        if (ignoreCamera == 0.0) {
            pos = cameraCenter + (pos - cameraCenter) * cameraScale;
        }
    }

    // Устанавливаем z-координату в выходной позиции
    gl_Position = projection * vec4(pos, zIndex, 1.0);

    texCoords = uvData.xy + vertex.zw * (uvData.zw - uvData.xy);
    vertexColor = colorData;
    fragRenderType = renderType;
}