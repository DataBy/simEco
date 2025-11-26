package com.mycompany.simulador.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class AppConfig {

    private AppConfig() {}

    private static final Path BASE_DATA = Paths.get("data");

    public static final Path ARCHIVO_USUARIOS      = BASE_DATA.resolve("usuarios.txt");
    public static final Path ARCHIVO_ECOSISTEMA    = BASE_DATA.resolve("ecosistema.txt");
    public static final Path ARCHIVO_ESTADO_TURNOS = BASE_DATA.resolve("estado_turnos.txt");
    public static final Path ARCHIVO_LOG           = BASE_DATA.resolve("simulador.log");

    // Config de correo (EJEMPLO: cámbialos por valores reales)
    public static final String SMTP_HOST       = "smtp.tu-proveedor.com";
    public static final int    SMTP_PORT       = 587;
    public static final String SMTP_USUARIO    = "correo_emisor@ejemplo.com";
    public static final String SMTP_CONTRASENA = "CAMBIA_ESTA_CONTRASENA";
    public static final boolean SMTP_TLS       = true;

    public static Path getBaseData() {
        return BASE_DATA;
    }
}
