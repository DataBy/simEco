package com.mycompany.simulador.model.ecosystem;

import com.mycompany.simulador.model.species.Especie;

public class Celda {

    private Coordenada coordenada;
    private Especie especie; // null => vacía

    public Celda(Coordenada coordenada) {
        this.coordenada = coordenada;
    }

    public Coordenada getCoordenada() {
        return coordenada;
    }

    public Especie getEspecie() {
        return especie;
    }

    public void setEspecie(Especie especie) {
        this.especie = especie;
    }

    public boolean estaVacia() {
        return especie == null || !especie.isViva();
    }

    public void vaciar() {
        if (especie != null) {
            especie.setViva(false);
        }
        especie = null;
    }
}
