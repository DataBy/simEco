package com.mycompany.simulador.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public final class FechaUtils {

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private FechaUtils() {}

    public static LocalDate parse(String texto) {
        return LocalDate.parse(texto, FORMAT);
    }

    public static String format(LocalDate fecha) {
        return fecha.format(FORMAT);
    }

    public static int calcularEdad(LocalDate nacimiento) {
        return Period.between(nacimiento, LocalDate.now()).getYears();
    }
}
