package com.emm233.games.core;

import org.lwjgl.opengl.GL30;

public class Texture implements AutoCloseable {
    private final int textureID;
    private final int width;
    private final int height;
    private boolean hasClose = false;

    public Texture(int textureID, int width, int height) {
        this.textureID = textureID;
        this.width = width;
        this.height = height;
    }

    @Override
    public void close() {
        if (hasClose) return;
        this.hasClose = true;
        GL30.glDeleteTextures(this.textureID);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTextureID() {
        return textureID;
    }
}
