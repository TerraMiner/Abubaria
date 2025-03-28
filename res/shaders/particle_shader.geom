#version 330 core
layout (points) in;
layout (triangle_strip, max_vertices = 4) out;

in vec2 TexCoords[];
in float StartTime[];

out vec2 FragTexCoords;

uniform float time;
uniform vec2 gridSize;
uniform float lifeSpan;

void main()
{
    float t = time - StartTime[0];
    if (t < 0.0 || t > lifeSpan) return;

    float progress = t / lifeSpan;
    float size = 1.0 - progress;

    vec2 displacement = vec2(sin(progress * 3.14) * 0.5, -progress);
    vec2 positions[4] = vec2[](vec2(-0.5, -0.5), vec2(0.5, -0.5), vec2(-0.5, 0.5), vec2(0.5, 0.5));
    vec2 texCoords[4] = vec2[](vec2(0.0, 0.0), vec2(1.0, 0.0), vec2(0.0, 1.0), vec2(1.0, 1.0));

    for (int i = 0; i < 4; i++) {
        vec2 finalPos = gl_in[0].gl_Position.xy + (positions[i] + displacement) * size;
        gl_Position = vec4(finalPos, gl_in[0].gl_Position.z, gl_in[0].gl_Position.w);
        FragTexCoords = texCoords[i];
        EmitVertex();
    }
    EndPrimitive();
}
