package com.mycompany.simulador.view;

public class EscenarioSnapshot {

    private final String nombre;
    private final char[][] matriz;

    public EscenarioSnapshot(String nombre, char[][] matriz) {
        this.nombre = nombre;
        this.matriz = matriz;
    }

    public String getNombre() {
        return nombre;
    }

    public char[][] getMatriz() {
        return matriz;
    }
}
