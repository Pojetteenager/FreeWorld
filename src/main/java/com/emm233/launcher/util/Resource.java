package com.emm233.launcher.util;

import com.emm233.launcher.Launcher;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Resource {
    public static Path getJarPath() {
        try {
            return Paths.get(Launcher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getStringFromFile(@Nonnull Path path) {
        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            return null;
        }
    }

    public static String getStringByBytes(String path) {
        return new String(getBytes(path), StandardCharsets.UTF_8);
    }

    public static byte[] getBytes(String path) {
        try(InputStream inputStream = Resource.class.getResourceAsStream("/"+path )) {
            assert inputStream != null;
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
