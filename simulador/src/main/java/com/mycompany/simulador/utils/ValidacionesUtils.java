package com.mycompany.simulador.utils;

import java.time.LocalDate;
import java.util.regex.Pattern;

public final class ValidacionesUtils {

    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private ValidacionesUtils() {}

    public static boolean esMayorDeEdad(LocalDate nacimiento, int edadMinima) {
        return FechaUtils.calcularEdad(nacimiento) >= edadMinima;
    }

    public static boolean esCorreoValido(String correo) {
        return correo != null && PATRON_CORREO.matcher(correo).matches();
    }
}
