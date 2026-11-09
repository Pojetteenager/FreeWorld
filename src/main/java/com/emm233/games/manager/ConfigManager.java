package com.emm233.games.manager;

import com.emm233.launcher.util.Config;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private final Map<String, Config> configs = new HashMap<>();
    public void load(String key,Config config) {
        this.configs.remove(key);
        this.configs.put(key, config);
    }

    public Config get(String key) {
        return configs.get(key);
    }
}
