package com.mycompany.simulador.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;

public final class ArchivoUtils {

    private ArchivoUtils() {}

    public static void escribirLineas(Path path, List<String> lineas, boolean append) {
        try {
            if (append) {
                Files.write(path, lineas, StandardCharsets.UTF_8,
                        StandardOpenOption.APPEND);
            } else {
                Files.write(path, lineas, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error al escribir en " + path + ": " + e.getMessage(), e);
        }
    }

    public static List<String> leerLineas(Path path) {
        try {
            if (!Files.exists(path)) return Collections.emptyList();
            return Files.readAllLines(path, StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException("Error al leer " + path + ": " + e.getMessage(), e);
        }
    }
}
