package com.emm233.launcher.util;

import com.emm233.games.core.ShaderProgram;
import com.emm233.launcher.Launcher;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class RenderUtil {
    public static void bindTexture2D(String key) {
        bindTexture2D(key,0);
    }
    public static void bindTexture2D(String key,int active) {
        if (Launcher.launcher.getTextureManager().get(key) != null) {
            glActiveTexture(active);
            glBindTexture(GL_TEXTURE_2D,Launcher.launcher.getTextureManager().get(key).getTextureID());
        } else {
            throw new RuntimeException("Failed to get the texture from the texture manager by " + key);
        }
    }

    public static VAB getVAB(VBOGetter... getters) {
        return getVAB(null,getters);
    }

    public static VAB getVAB(EBOGetter eboGetter,VBOGetter... getters) {
        int vao = glGenVertexArrays();
        if (vao == 0) throw new RuntimeException("glGenVertexArrays failed");

        Map<String, Integer> vbos = new HashMap<>();
        Set<Integer> usedIndexes = new HashSet<>();
        int ebo = 0;

        glBindVertexArray(vao);
        try {
            for (VBOGetter getter : getters) {
                if (!usedIndexes.add(getter.index())) {
                    throw new IllegalStateException(
                            "Duplicate attribute index: " + getter.index());
                }
                int vbo = getter.getVBO();
                if (vbos.putIfAbsent(getter.vboName(), vbo) != null) {
                    throw new IllegalStateException(
                            "Duplicate vboName: " + getter.vboName());
                }
            }
            if (eboGetter != null) {
                ebo = eboGetter.getEBO();
            }
        } finally {
            glBindVertexArray(0);
        }

        return new VAB(vao, vbos,ebo);
    }

    public interface VBOGetter {
        String vboName();
        int index();

        default int getVBO() {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                int vbo = glGenBuffers();
                if (vbo == 0) throw new RuntimeException("glGenBuffers failed");
                putVBO(stack, vbo);
                return vbo;
            }
        }

        void putVBO(MemoryStack stack, int vbo);
    }

    public interface EBOGetter {

        default int getEBO() {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                int vbo = glGenBuffers();
                if (vbo == 0) throw new RuntimeException("glGenBuffers failed");
                putEBO(stack, vbo);
                return vbo;
            }
        }

        void putEBO(MemoryStack stack, int vbo);
    }

    public record IntegerArrayEBO(int usage,int[] date) implements EBOGetter {
        @Override
        public void putEBO(MemoryStack stack, int ebo) {
            IntBuffer buffer = stack.mallocInt(date.length);
            buffer.put(date).flip();

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,ebo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER,buffer,usage);
        }
    }

    public record FloatArrayVBO(String vboName, int usage, float[] data,
                                int index, int dataSize, boolean normalized)
            implements VBOGetter {

        @Override
        public void putVBO(MemoryStack stack, int vbo) {
            FloatBuffer buf = stack.mallocFloat(data.length).put(data).flip();

            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, buf, usage);

            glEnableVertexAttribArray(index);
            glVertexAttribPointer(index, dataSize, GL_FLOAT, normalized, 0, 0);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
    }

    public record IntArrayVBO(String vboName, int usage, int[] data,
                                int index, int dataSize, boolean normalized)
            implements VBOGetter {

        @Override
        public void putVBO(MemoryStack stack, int vbo) {
            IntBuffer buf = stack.mallocInt(data.length).put(data).flip();

            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, buf, usage);

            glEnableVertexAttribArray(index);
            glVertexAttribPointer(index, dataSize, GL_INT, normalized, 0, 0);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
    }
}