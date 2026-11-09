package com.emm233.games.manager;

import com.emm233.games.core.ShaderProgram;
import com.emm233.launcher.util.Resource;

import java.util.HashMap;
import java.util.Map;

public class ShaderProgramManager implements AutoCloseable {
    private final Map<String, ShaderProgram> programs = new HashMap<>();

    public void load(String key,String vertex,String frag,UnifromSetter setter) throws Exception {
        if (this.programs.containsKey(key)) {
            this.programs.get(key).cleanup();
            this.programs.remove(key);
        }
        ShaderProgram shaderProgram = new ShaderProgram();
        try {
            shaderProgram.createVertexShader(Resource.getStringByBytes(vertex));
            shaderProgram.createFragmentShader(Resource.getStringByBytes(frag));
            shaderProgram.link();

            setter.set(shaderProgram);
            this.programs.put(key,shaderProgram);
        } catch (Exception e) {
            shaderProgram.cleanup();
        }
    }

    public ShaderProgram get(String key) {
        if (!this.programs.containsKey(key)) {
            throw new RuntimeException("Error getting shader program");
        }
        return this.programs.get(key);
    }

    @Override
    public void close() {
        this.programs.values().forEach(ShaderProgram::cleanup);
        this.programs.clear();
    }

    public interface UnifromSetter {
        void set(ShaderProgram program) throws Exception;
    }
}
