package com.mycompany.simulador.model.genetics;

import com.mycompany.simulador.model.species.Especie;

public class Mutacion {

    private Especie especie;
    private String descripcion;

    public Mutacion(Especie especie, String descripcion) {
        this.especie = especie;
        this.descripcion = descripcion;
    }

    public Especie getEspecie() {
        return especie;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
