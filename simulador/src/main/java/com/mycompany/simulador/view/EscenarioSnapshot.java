package com.mycompany.simulador.view;

public class EscenarioSnapshot {

    private final String nombre;
    private final char[][] matriz;
    private final String[][] iconos;

    public EscenarioSnapshot(String nombre, char[][] matriz, String[][] iconos) {
        this.nombre = nombre;
        this.matriz = matriz;
        this.iconos = iconos;
    }

    public String getNombre() {
        return nombre;
    }

    public char[][] getMatriz() {
        return matriz;
    }

    public String[][] getIconos() {
        return iconos;
    }
}
