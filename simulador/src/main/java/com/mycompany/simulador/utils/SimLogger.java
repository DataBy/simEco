package com.mycompany.simulador.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.mycompany.simulador.config.AppConfig;

/**
 * Logger simple que imprime en consola y persiste en un archivo dentro de la carpeta data.
 */
public final class SimLogger {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Path LOG_PATH = AppConfig.ARCHIVO_LOG;

    static {
        try {
            AppConfig.ensureDataFolder();
        } catch (Exception e) {
            System.err.println("No se pudo preparar la carpeta de datos: " + e.getMessage());
        }
    }

    private SimLogger() { }

    public static synchronized void log(String mensaje) {
        String linea = "[" + LocalDateTime.now().format(FORMATTER) + "] " + mensaje;
        System.out.println(linea);
        try {
            Files.writeString(LOG_PATH, linea + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("No se pudo escribir en el log: " + e.getMessage());
        }
    }

    public static void logMatriz(char[][] matriz) {
        if (matriz == null) return;
        StringBuilder sb = new StringBuilder();
        for (char[] fila : matriz) {
            sb.append(new String(fila)).append('\n');
        }
        log("Estado de la matriz:\n" + sb);
    }
}
