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

    // ===========================================
    //       CONFIGURACIÓN SMTP PARA GMAIL ✔
    // ===========================================
    // Gmail siempre usa este host
    public static final String SMTP_HOST = "smtp.gmail.com";

    // Gmail usa el puerto 587 con STARTTLS
    public static final int SMTP_PORT = 587;

    // Tu correo remitente Gmail
    public static final String SMTP_USUARIO = "gocodeia@gmail.com";

    // App Password de Gmail (PONLA AQUÍ SIN ESPACIOS)
    // Ejemplo: "hnhhzqqwpkogysgd"
    public static final String SMTP_CONTRASENA = "hnhhzqqwpkogysgd";

    // Gmail requiere TLS
    public static final boolean SMTP_TLS = true;

    public static Path getBaseData() {
        return BASE_DATA;
    }

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
