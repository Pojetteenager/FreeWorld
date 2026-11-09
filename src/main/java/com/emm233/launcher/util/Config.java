package com.emm233.launcher.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Config {

    private final Path filePath;
    private final List<String> keys = new ArrayList<>();
    private final Map<String, String> values = new LinkedHashMap<>();

    public Config(String configName, List<Field> fields, boolean replace, boolean needCheck) {
        Path jarPath   = Path.of(Resource.getJarPath().toString());
        Path configDir = jarPath.getParent().resolve("config");
        this.filePath  = configDir.resolve(configName + ".cfg");

        try {
            if (replace || Files.notExists(filePath)) {
                StringBuilder sb = new StringBuilder();
                for (Field f : fields) {
                    keys.add(f.Head);
                    values.put(f.Head, f.value);
                    sb.append(f.Head).append('=').append(f.value)
                            .append(System.lineSeparator());
                }
                Files.createDirectories(configDir);
                Files.writeString(filePath, sb.toString(), StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE);
            } else {
                for (Field f : fields) {
                    keys.add(f.Head);
                }
                for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                    if (line.isBlank()) continue;
                    int idx   = line.indexOf('=');
                    String k  = idx < 0 ? line : line.substring(0, idx);
                    String v  = idx < 0 ? ""    : line.substring(idx + 1);
                    values.put(k, v);
                }

                if (needCheck) {
                    for (String key : keys) {
                        if (!values.containsKey(key)) {
                            throw new RuntimeException(
                                    "Configuration '" + configName + "' is missing field: " + key);
                        }
                    }
                    for (String key : values.keySet()) {
                        if (!keys.contains(key)) {
                            IO.println("Having a non-existent field '" + key + "'");
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to init config: " + filePath, e);
        }
    }

    public String getValue(String key) {
        return values.getOrDefault(key, "");
    }

    public int getIntValue(String key) {
        return Integer.parseInt(getValue(key));
    }

    public float getFloatValue(String key) {
        return Float.parseFloat(getValue(key));
    }

    public double getDoubleValue(String key) {
        return Double.parseDouble(getValue(key));
    }

    public boolean getBooleanValue(String key) {
        return Boolean.parseBoolean(getValue(key));
    }

    public List<String> getKeys() {
        return keys;
    }

    public Path getFilePath() {
        return this.filePath;
    }

    public record Field(String Head, String value) {}
}