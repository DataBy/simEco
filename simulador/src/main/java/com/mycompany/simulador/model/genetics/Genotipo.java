package com.mycompany.simulador.model.genetics;

public class Genotipo {

    private boolean mutacionVelocidad;
    private boolean mutacionEscapar;
    private boolean mutacionFertilidad;

    public boolean isMutacionVelocidad() {
        return mutacionVelocidad;
    }

    public void setMutacionVelocidad(boolean mutacionVelocidad) {
        this.mutacionVelocidad = mutacionVelocidad;
    }

    public boolean isMutacionEscapar() {
        return mutacionEscapar;
    }

    public void setMutacionEscapar(boolean mutacionEscapar) {
        this.mutacionEscapar = mutacionEscapar;
    }

    public boolean isMutacionFertilidad() {
        return mutacionFertilidad;
    }

    public void setMutacionFertilidad(boolean mutacionFertilidad) {
        this.mutacionFertilidad = mutacionFertilidad;
    }
}
