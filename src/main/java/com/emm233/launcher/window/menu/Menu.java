package com.emm233.launcher.window.menu;

import com.emm233.games.core.ShaderProgram;
import com.emm233.launcher.window.Window;

public class Menu implements IMenu {
    private final IMenu parent;
    private final Window window;
    public Menu(Window window) {
        this(null,window);
    }

    public Menu(IMenu menu, Window window) {
        this.parent = menu;
        this.window = window;
    }

    @Override
    public void init() {

    }

    @Override
    public void updata() {

    }

    @Override
    public void render(ShaderProgram shaderProgram) {

    }

    @Override
    public void close() {
        if (this.parent != null) {

        }
    }

    public IMenu getParent() {
        return parent;
    }

    public Window getWindow() {
        return window;
    }
}
