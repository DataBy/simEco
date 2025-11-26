package com.mycompany.simulador.utils;

import com.mycompany.simulador.config.Constantes;
import com.mycompany.simulador.model.ecosystem.Coordenada;

import java.util.ArrayList;
import java.util.List;

public final class MatrizUtils {

    private MatrizUtils() {}

    public static boolean dentroDeRango(int fila, int col) {
        return fila >= 0 && fila < Constantes.MATRIZ_FILAS &&
               col  >= 0 && col  < Constantes.MATRIZ_COLUMNAS;
    }

    public static List<Coordenada> vecinosOrtogonales(Coordenada c) {
        List<Coordenada> vecinos = new ArrayList<>();
        int f = c.getFila();
        int k = c.getColumna();
        agregarSiValida(vecinos, f - 1, k);
        agregarSiValida(vecinos, f + 1, k);
        agregarSiValida(vecinos, f, k - 1);
        agregarSiValida(vecinos, f, k + 1);
        return vecinos;
    }

    private static void agregarSiValida(List<Coordenada> lista, int fila, int col) {
        if (dentroDeRango(fila, col)) {
            lista.add(new Coordenada(fila, col));
        }
    }
}
