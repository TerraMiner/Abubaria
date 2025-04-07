#version 330 core

in vec2 fragTexCoord;

uniform vec2 screenSize;
uniform float ambientDarkness;
uniform float time;
uniform sampler2D noiseTexture;
uniform sampler2D obstacleTexture; // Текстура для препятствий
uniform float lightPenetration; // Параметр для силы проходимости света

#define MAX_LIGHTS 20

uniform int lightCount;
uniform vec3 lightPositions[MAX_LIGHTS]; // x, y, radius
uniform float lightFalloffs[MAX_LIGHTS];
uniform vec3 lightColors[MAX_LIGHTS]; // r, g, b
uniform float lightIntensities[MAX_LIGHTS]; // Интенсивность каждого источника света
uniform float lightFlickerAmount[MAX_LIGHTS]; // Величина мерцания (0.0 - без мерцания)
uniform float lightColorShift[MAX_LIGHTS]; // Скорость изменения цвета
uniform float lightPenetrationMultipliers[MAX_LIGHTS]; // Множители проходимости для каждого источника света

out vec4 fragColor;

float hash(float n) {
    return fract(sin(n) * 43758.5453);
}

float noise(float x) {
    float i = floor(x);
    float f = fract(x);
    float u = f * f * (3.0 - 2.0 * f);
    return mix(hash(i), hash(i + 1.0), u);
}

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

// Улучшенная функция для расчета распространения света как тепла
float calculateHeatLikeLightPropagation(vec2 pixelPos, vec2 lightPos, float penetrationMultiplier, float radius) {
    vec2 dir = lightPos - pixelPos;
    float dist = length(dir);

    if (dist < 1.0) return 1.0;

    dir = normalize(dir);
    
    // Эффективная проходимость света
    float effectivePenetration = penetrationMultiplier;//clamp(lightPenetration * penetrationMultiplier, 0.01, 1);
    
    // Параметры распространения
    float maxDistance = radius; // Максимальное расстояние для расчета
    float distanceFactor = clamp(1.0 - dist/maxDistance, 0.0, 1.0);
    
    // Базовое затухание по расстоянию (обратный квадрат)
    float basePropagation = 1.0 / (1.0 + 0.01 * dist * dist);
    
    // Количество шагов для семплирования препятствий на пути света
    const int STEPS = 64;
    float stepSize = min(dist, maxDistance) / float(STEPS);
    
    // Накопленное затухание из-за препятствий
    float accumulatedObstruction = 0.0;
    
    // Проходим по пути от текущего пикселя к источнику света
    for (int i = 0; i < STEPS; i++) {
        // Позиция текущего шага
        float t = float(i) / float(STEPS - 1);
        vec2 samplePos = mix(pixelPos, lightPos, t);
        
        // Получаем значение препятствия в точке семплирования
        vec2 sampleUV = samplePos / screenSize;
        float obstacleAlpha = texture(obstacleTexture, sampleUV).a;
        
        // Вес шага (ближе к источнику света - важнее)
        float stepWeight = mix(0.5, 1.0, t);
        
        // Накапливаем затухание, учитывая проницаемость
        float stepObstruction = obstacleAlpha * (1.0 - effectivePenetration) * stepWeight;
        accumulatedObstruction += stepObstruction;
    }

    accumulatedObstruction = clamp(accumulatedObstruction / float(STEPS), 0.0, 1.0);

    vec2 currentUV = pixelPos / screenSize;
    float currentObstacleAlpha = texture(obstacleTexture, currentUV).a;

    float localObstruction = currentObstacleAlpha * (1.0 - effectivePenetration);

    float totalObstruction = mix(accumulatedObstruction, localObstruction, 0.01);

    float propagation = basePropagation * (1.0 - totalObstruction) * distanceFactor;

    propagation = pow(propagation, 1.0 + totalObstruction * 2.0);

    return smoothstep(0.0, 0.01, propagation);
}

float calculateLightAttenuation(vec2 pixelPos, vec2 lightPos, float radius, float falloff) {
    float dist = distance(pixelPos, lightPos);
    if (dist > radius) return 0.0;
    float normalizedDist = dist / radius;
    float attenuation = 1.0 - normalizedDist;
    attenuation *= falloff;
    return attenuation;
}

void main() {
    vec2 pixelPos = fragTexCoord * screenSize;
    vec2 uv = fragTexCoord * 2.0 - 1.0;

    float darkness = ambientDarkness;
    vec3 lightColor = vec3(0.0);
    float globalGlow = 0.0;

    // Ограничиваем количество обрабатываемых источников света
    int actualLightCount = min(lightCount, MAX_LIGHTS);

    // Массивы для хранения промежуточных результатов
    float lightPropagations[MAX_LIGHTS];
    vec3 colors[MAX_LIGHTS];

    // Первый проход: вычисляем распространение света и цвета для каждого источника
    for (int i = 0; i < actualLightCount; i++) {
        vec2 lightPos = lightPositions[i].xy;
        float lightRadius = lightPositions[i].z;
        float dist = distance(pixelPos, lightPos);

        // Пропускаем источники света, которые находятся слишком далеко
        if (dist > lightRadius * 1.5) { // Увеличиваем радиус проверки для учета проникновения
            lightPropagations[i] = 0.0;
            continue;
        }

        // Вычисляем мерцание
        float flicker = 1.0;
        if (lightFlickerAmount[i] > 0.0) {
            // Используем несколько октав шума для более естественного мерцания
            float noise1 = noise(time * 10.0 + float(i) * 123.4567);
            float noise2 = noise(time * 5.0 + float(i) * 76.5432) * 0.5;
            flicker = 1.0 - lightFlickerAmount[i] * (0.5 + 0.5 * (noise1 + noise2));
        }

        // Вычисляем цвет источника света
        vec3 baseColor = lightColors[i];
        if (lightColorShift[i] > 0.0) {
            float hueShift = fract(time * lightColorShift[i] * 0.1);
            // Сохраняем яркость оригинального цвета
            float origBrightness = dot(baseColor, vec3(0.299, 0.587, 0.114));
            baseColor = hsv2rgb(vec3(hueShift, 0.8, origBrightness));
        }
        colors[i] = baseColor * lightIntensities[i] * flicker;

        // Вычисляем распространение света как тепла
        lightPropagations[i] = calculateHeatLikeLightPropagation(pixelPos, lightPos, lightPenetrationMultipliers[i], lightRadius);

        // Добавляем вклад в глобальное свечение
        float normalizedDist = dist / lightRadius;
        if (normalizedDist < 1.5) { // Увеличиваем радиус для учета проникновения
            float glowFactor = (1.0 - normalizedDist/1.5) * lightFalloffs[i];
            globalGlow += glowFactor * lightIntensities[i] * flicker * lightPropagations[i];
        }
    }

    // Второй проход: вычисляем освещение для каждого источника света
    for (int i = 0; i < actualLightCount; i++) {
        // Пропускаем источники с нулевым распространением
        float propagation = lightPropagations[i];
        if (propagation <= 0.0) continue;

        vec2 lightPos = lightPositions[i].xy;
        float lightRadius = lightPositions[i].z;
        float falloff = lightFalloffs[i];

        // Вычисляем затухание света от точечного источника
        float attenuation = calculateLightAttenuation(pixelPos, lightPos, lightRadius, falloff);

        // Комбинируем затухание с распространением света
        float finalEffect = attenuation * propagation;

        // Если эффект слишком маленький, пропускаем
        if (finalEffect <= 0.01) continue;

        // Добавляем вклад этого источника света в итоговый цвет
        lightColor += colors[i] * finalEffect;

        // Уменьшаем темноту в освещенных областях
        darkness *= (1.0 - finalEffect);
    }

    // Добавляем глобальное свечение с естественным цветовым оттенком
//    vec3 glowColor = vec3(1.0, 0.95, 0.9) * globalGlow * 0.2;
//    lightColor += glowColor;

    float vignette = 1.0 - length(uv) * 0.5;
    vignette = smoothstep(0.0, 1.0, vignette);
    darkness = mix(darkness, darkness + 0.2, 1.0 - vignette);

    darkness = clamp(darkness, 0.0, 1.0);
    lightColor = clamp(lightColor, 0.0, 1.0);

    // Применяем гамма-коррекцию
    lightColor = pow(lightColor, vec3(1.1));

    vec3 finalColor = mix(lightColor, vec3(0.0), darkness);

    // Прозрачность соответствует темноте
    fragColor = vec4(finalColor, darkness);
}