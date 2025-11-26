package com.mycompany.simulador.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

import com.mycompany.simulador.config.AppConfig;

public final class LogUtils {

    private LogUtils() {}

    public static void info(String msg) {
        escribir("INFO", msg, null);
    }

    public static void error(String msg, Throwable t) {
        escribir("ERROR", msg, t);
    }

    private static void escribir(String nivel, String msg, Throwable t) {
        String linea = String.format("[%s] [%s] %s",
                LocalDateTime.now(), nivel, msg);
        System.out.println(linea);
        if (t != null) t.printStackTrace();

        try {
            Files.createDirectories(AppConfig.ARCHIVO_LOG.getParent());
            Files.write(AppConfig.ARCHIVO_LOG,
                    (linea + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) { }
    }
}
