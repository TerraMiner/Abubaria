#version 330 core
in vec2 TexCoords;
in float StartTime;

out vec4 FragColor;

uniform sampler2D texture1;
uniform float time;

void main()
{
    if (time < StartTime) discard;
    FragColor = texture(texture1, TexCoords);
}