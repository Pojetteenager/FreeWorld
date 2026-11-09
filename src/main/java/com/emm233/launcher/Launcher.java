package com.emm233.launcher;

import com.emm233.games.manager.ConfigManager;
import com.emm233.games.manager.ShaderProgramManager;
import com.emm233.games.manager.TextureManager;
import com.emm233.launcher.util.Config;
import com.emm233.launcher.window.Window;

import static org.lwjgl.glfw.GLFW.*;

import java.util.List;

public class Launcher {
    public static Launcher launcher;
    private Window window;
    private boolean swap;
    private int maxFps;
    private boolean isInGame;
    private final ShaderProgramManager shaderProgramManager = new ShaderProgramManager();
    private final ConfigManager configManager = new ConfigManager();
    private final TextureManager textureManager = new TextureManager();

    static void main() throws Exception {
        new Launcher();
    }

    public Launcher() throws Exception {
        Launcher.launcher = this;
        this.init();
        this.loop();
        this.close();
    }

    public void init() throws Exception {
        List<Config.Field> windowConfigField = List.of(
                new Config.Field("windowWidth", "900"),
                new Config.Field("windowHeight", "550"),
                new Config.Field("title", "Free World"),
                new Config.Field("swap", "true"),
                new Config.Field("maxFps", "60")
        );
        this.configManager.load("window", new Config("window", windowConfigField,false,false));

        this.window = new Window(this.getWindowConfig());

        this.textureManager.load(
                "mainbackground", "assets/textures/mainmenu/mainbackground.png"
        );

        this.shaderProgramManager.load("window_gui_2d","assets/shader/gui2d.vert","assets/shader/gui2d.frag",(shaderProgram) -> {
            shaderProgram.createUniform("isTexture");
            shaderProgram.createUniform("saTexture");
        });

        this.swap = this.getWindowConfig().getBooleanValue("swap");
        this.maxFps = this.getWindowConfig().getIntValue("maxFps");
        this.isInGame = false;
    }

    public void loop() {
        double loopTime = 1/30.0;
        double interval = 0.0;

        double preTime = glfwGetTime();
        if (this.swap) {
            glfwSwapInterval(1);
        } else {
            glfwSwapInterval(0);
        }

        while (!glfwWindowShouldClose(this.window.getId())) {
            double startTime = glfwGetTime();
            interval +=startTime-preTime;

            while (interval >= loopTime) {
                this.input();
                this.update();

                interval -= loopTime;
            }

            this.window.render(this.shaderProgramManager.get("window_gui_2d"));

            if (!this.swap) {
                double shouldTime = startTime + 1.0/this.maxFps;
                while (glfwGetTime() < shouldTime) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            preTime = startTime;
        }
    }

    public void input() {

    }

    public void update() {

    }

    private void close() {
        this.shaderProgramManager.close();
        this.textureManager.close();

        this.window.onClose();
    }

    public Config getWindowConfig() {
        return this.configManager.get("window");
    }

    public Window getWindow() {
        return window;
    }

    public int getMaxFps() {
        return maxFps;
    }

    public boolean isInGame() {
        return isInGame;
    }

    public ShaderProgramManager getShaderProgramManager() {
        return shaderProgramManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public TextureManager getTextureManager() {
        return textureManager;
    }
}
