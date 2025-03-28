#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoords;
layout (location = 2) in float aStartTime;

out vec2 TexCoords;
out float StartTime;

uniform mat4 projection;
uniform mat4 view;
uniform float time;

void main()
{
    TexCoords = aTexCoords;
    StartTime = aStartTime;
    gl_Position = projection * view * vec4(aPos, 1.0);
}