package com.emm233.launcher.window.menu;

import com.emm233.games.core.ShaderProgram;

import javax.annotation.Nullable;

public interface IMenu extends AutoCloseable {
    void init();
    void updata();
    void render(ShaderProgram shaderProgram);
}
