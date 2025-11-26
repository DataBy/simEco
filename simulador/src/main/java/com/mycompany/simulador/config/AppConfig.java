package com.mycompany.simulador.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class AppConfig {

    private AppConfig() {}

    private static final Path BASE_DATA = Paths.get("data");

    public static final Path ARCHIVO_USUARIOS      = BASE_DATA.resolve("usuarios.txt");
    public static final Path ARCHIVO_ECOSISTEMA    = BASE_DATA.resolve("ecosistema.txt");
    public static final Path ARCHIVO_ESTADO_TURNOS = BASE_DATA.resolve("estado_turnos.txt");
    public static final Path ARCHIVO_LOG           = BASE_DATA.resolve("simulador.log");

    // ✔ Respeta tu config de correo, nada se toca
    public static final String SMTP_HOST       = "smtp.tu-proveedor.com";
    public static final int    SMTP_PORT       = 587;
    public static final String SMTP_USUARIO    = "correo_emisor@ejemplo.com";
    public static final String SMTP_CONTRASENA = "CAMBIA_ESTA_CONTRASENA";
    public static final boolean SMTP_TLS       = true;

    public static Path getBaseData() {
        return BASE_DATA;
    }

    // ✔ Añadido, sin modificar nada tuyo
    public static void ensureDataFolder() {
        try {
            if (!Files.exists(BASE_DATA)) {
                Files.createDirectories(BASE_DATA);
            }
        } catch (Exception e) {
            throw new RuntimeException("No se pudo crear la carpeta 'data': " + e.getMessage(), e);
        }
    }
}
