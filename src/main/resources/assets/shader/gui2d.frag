#version 460

uniform bool isTexture;
uniform sampler2D saTexture;

in vec2 ouv;

out vec4 fragColor;

void main() {
    if (isTexture) {
        fragColor = texture(saTexture,ouv);
    } else {
        fragColor = vec4(0,0,0,1.0);
    }
}