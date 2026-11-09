package com.emm233.games.core;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
    private final int programID;
    private int vertexShaderID;
    private int fragmentShaderID;
    private final Map<String,Integer> uniforms;

    public ShaderProgram() throws Exception {
        this.programID = glCreateProgram();
        if (programID == 0) {
            throw new Exception("Could not create shader program");
        }
        this.uniforms = new HashMap<>();
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderID = createShader(shaderCode,GL_VERTEX_SHADER);
        glDeleteShader(vertexShaderID);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderID = createShader(shaderCode,GL_FRAGMENT_SHADER);
        glDeleteShader(fragmentShaderID);
    }

    protected int createShader(String shaderCode,int shaderType) throws Exception {
        int shaderID = glCreateShader(shaderType);
        if (shaderID==0) throw new Exception("Error creating shader.Type" + shaderType);

        glShaderSource(shaderID,shaderCode);
        glCompileShader(shaderID);

        if (glGetShaderi(shaderID,GL_COMPILE_STATUS) == 0) {
            glDeleteShader(shaderID);
            throw new Exception("Error compiling shader code : " + glGetShaderInfoLog(shaderID,1024));
        }

        glAttachShader(programID,shaderID);
        return shaderID;
    }

    public void link() throws Exception {
        glLinkProgram(programID);
        if (glGetProgrami(programID,GL_LINK_STATUS) == 0) throw new Exception("Error linking shader code: " + glGetProgramInfoLog(programID,1024));

        if (vertexShaderID != 0) {
            glDetachShader(programID,vertexShaderID);
        }
        if (fragmentShaderID != 0) {
            glDetachShader(programID,fragmentShaderID);
        }

        glValidateProgram(programID);
        if (glGetProgrami(programID,GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating shader code : " + glGetProgramInfoLog(programID,1024));
        }
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(programID,uniformName);
        if (uniformLocation < 0) {
            //娘的，glsl会优化未使用的unifrom，fuck you，tnnd，之前直接抛出异常，窗口一开就蹦我还以为啥东西写错了
            System.err.println("Error creating uniform : " + uniformName);
        }
        uniforms.put(uniformName,uniformLocation);
    }

    public void setUniform(String uniform, Vector3f vector3f) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            vector3f.get(buffer);
            glUniform3fv(uniforms.get(uniform),buffer);
        }
    }

    public void setUniform(String uniform, Matrix4f matrix4f) {
        if (glGetInteger(GL_CURRENT_PROGRAM) != this.programID) {
            /**
             * ooooooooooooooooooooooooiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii
             */
            throw new RuntimeException("Current shader program is not the target.Please bind the target program before use");
        }
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            matrix4f.get(buffer);
            glUniformMatrix4fv(uniforms.get(uniform),false,buffer);
        }
    }

    public void setUniform(String uniform, int value) {
        glUniform1i(uniforms.get(uniform),value);
    }

    public void bind() {
        glUseProgram(programID);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public int getVertexShaderID() {
        return this.vertexShaderID;
    }

    public int getFragmentShaderID() {
        return this.fragmentShaderID;
    }

    public void cleanup() {
        unbind();
        if (programID != 0) {
            glDeleteProgram(programID);
        }
    }
}
