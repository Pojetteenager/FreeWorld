package com.emm233.launcher.window;

import com.emm233.games.core.ShaderProgram;
import com.emm233.launcher.Launcher;
import com.emm233.launcher.util.Config;
import com.emm233.launcher.util.RenderUtil;
import com.emm233.launcher.util.VAB;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.glViewport;

public class Window {
    private final long id;
    private int width;
    private int height;
    private double acpectRadio = 1;

    private final VAB windowVAB;

    public Window(Config config) {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) throw new RuntimeException("Falied to init GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        this.id = glfwCreateWindow(config.getIntValue("windowWidth"), config.getIntValue("windowHeight"), config.getValue("title"), 0L, 0L);
        if (this.id == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create GLFW window (check OpenGL 3.3 support)");
        }
        this.width = config.getIntValue("windowWidth");
        this.height = config.getIntValue("windowHeight");
        this.acpectRadio = (double) this.width / this.height;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer wb = stack.mallocInt(1);
            IntBuffer hb = stack.mallocInt(1);

            glfwGetWindowSize(this.id, wb, hb);

            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            if (vidMode != null) {
                glfwSetWindowPos(this.id, (vidMode.width() - wb.get()) / 2, (vidMode.height() - hb.get()) / 2);
            }
        }
        glfwSetFramebufferSizeCallback(this.id, (id, fbw, fbh) -> {
            glViewport(0, 0, fbw, fbh);
        });
        glfwSetWindowSizeCallback(this.id, (id, w, h) -> {
            this.resiza(w, h);
        });

        glfwMakeContextCurrent(this.id);

        glfwShowWindow(this.id);
        createCapabilities();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer wb = stack.mallocInt(1);
            IntBuffer hb = stack.mallocInt(1);
            glfwGetFramebufferSize(this.id, wb, hb);
            glViewport(0, 0, wb.get(), hb.get());
        }

        float[] vertexs = new float[]{
                -1.0f, 1.0f, 0,
                1.0f, 1.0f, 0,
                -1.0f, -1.0f, 0,
                1.0f, -1.0f, 0
        };
        int[] uv = new int[]{
                0,0,
                1,0,
                0,1,
                1,1
        };

        this.windowVAB = RenderUtil.getVAB(new RenderUtil.IntegerArrayEBO(GL_STATIC_DRAW,new int[]{1,0,2,2,3,1}),
                new RenderUtil.FloatArrayVBO("vertexs",GL_STATIC_DRAW,vertexs,0,3,false),new RenderUtil.IntArrayVBO("uv",GL_STATIC_DRAW,uv,1,2,false));
    }

    public void render(ShaderProgram shaderProgram) {
        glClearColor(1.0f,1.0f,1.0f,1.0f);
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
        glfwPollEvents();

        if (!Launcher.launcher.isInGame()) {
            this.drawBackGround(shaderProgram);
        }

        glfwSwapBuffers(this.id);
    }

    public void drawBackGround(ShaderProgram shaderProgram) {
        shaderProgram.bind();

        this.windowVAB.bind();

        shaderProgram.setUniform("isTexture",1);
        shaderProgram.setUniform("saTexture",0);
        RenderUtil.bindTexture2D("mainbackground");
        glDrawElements(GL_TRIANGLES,6,GL_UNSIGNED_INT,0);

        this.windowVAB.unbind();


        shaderProgram.unbind();
    }

    public void onClose() {
        this.windowVAB.close();
        Callbacks.glfwFreeCallbacks(this.id);
        glfwDestroyWindow(this.id);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void resiza(int w,int h) {
        this.width = w;
        this.height = h;
        this.acpectRadio = (double) w /h;
    }

    public long getId() {
        return id;
    }

    public double getAcpectRadio() {
        return acpectRadio;
    }

    public boolean isKeyDown(int key) {
        return glfwGetKey(this.id,key) == GLFW_PRESS;
    }

    public boolean isKeyRelease(int key) {
        return glfwGetKey(this.id,key) == GLFW_RELEASE;
    }
}
