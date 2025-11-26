package com.mycompany.simulador.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;

public final class ArchivoUtils {

    private ArchivoUtils() {}

    public static List<String> leerLineas(Path path) {
        try {
            if (!Files.exists(path)) return Collections.emptyList();
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LogUtils.error("Error leyendo archivo: " + path, e);
            return Collections.emptyList();
        }
    }

    public static void escribirLineas(Path path, List<String> lineas, boolean append) {
        try {
            Files.createDirectories(path.getParent());
            OpenOption option = append ? StandardOpenOption.APPEND : StandardOpenOption.CREATE;
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                    option, StandardOpenOption.WRITE)) {
                for (String l : lineas) {
                    writer.write(l);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            LogUtils.error("Error escribiendo archivo: " + path, e);
        }
    }
}
