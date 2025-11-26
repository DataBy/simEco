package com.mycompany.simulador.model.ecosystem;

import com.mycompany.simulador.config.Constantes;
import com.mycompany.simulador.model.species.Especie;

public class Ecosistema {

    private final Celda[][] matriz;

    public Ecosistema() {
        matriz = new Celda[Constantes.MATRIZ_FILAS][Constantes.MATRIZ_COLUMNAS];
        for (int i = 0; i < Constantes.MATRIZ_FILAS; i++) {
            for (int j = 0; j < Constantes.MATRIZ_COLUMNAS; j++) {
                matriz[i][j] = new Celda(new Coordenada(i, j));
            }
        }
    }

    public Celda[][] getMatriz() {
        return matriz;
    }

    public Celda getCelda(int fila, int col) {
        return matriz[fila][col];
    }

    public void colocarEspecie(Especie especie, int fila, int col) {
        Celda c = getCelda(fila, col);
        c.setEspecie(especie);
        especie.setPosicion(c.getCoordenada());
    }
}
