package com.emm233.launcher.util;

import java.util.Map;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

public record VAB(int vao, Map<String, Integer> vbos, int ebo) implements AutoCloseable {
    public void bind()   { glBindVertexArray(vao); }
    public void unbind() { glBindVertexArray(0); }

    @Override
    public void close() {
        glDeleteVertexArrays(vao);
        for (int vbo : vbos.values()) glDeleteBuffers(vbo);
        if (ebo != 0) glDeleteBuffers(ebo);
    }
}