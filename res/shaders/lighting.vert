#version 330 core

layout (location = 0) in vec2 position;
layout (location = 1) in vec2 texCoord;
uniform float lightDepth;
uniform mat4 projection;
uniform vec2 cameraScale;
uniform vec2 cameraCenter;

out vec2 fragTexCoord;

void main() {
    gl_Position = projection * vec4(cameraCenter + (position - cameraCenter) * cameraScale, lightDepth, 1.0);
    fragTexCoord = texCoord;
} 