package com.mycompany.simulador.utils;

import java.util.List;
import java.util.Random;

public final class AleatorioUtils {

    private static final Random RANDOM = new Random();

    private AleatorioUtils() {}

    public static int enteroEnRango(int minInclusive, int maxInclusive) {
        return minInclusive + RANDOM.nextInt(maxInclusive - minInclusive + 1);
    }

    public static boolean probabilidad(double prob0a1) {
        return RANDOM.nextDouble() < prob0a1;
    }

    public static <T> T elegirAleatorio(List<T> lista) {
        if (lista == null || lista.isEmpty()) return null;
        int idx = RANDOM.nextInt(lista.size());
        return lista.get(idx);
    }
}
