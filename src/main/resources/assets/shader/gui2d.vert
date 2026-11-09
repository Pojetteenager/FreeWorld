#version 460

layout(location=0) in vec3 vertexPosition;
layout(location=1) in vec2 iuv;

out vec2 ouv;

void main() {
    gl_Position = vec4(vertexPosition,1.0);
    ouv = iuv;
}