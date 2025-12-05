package com.mycompany.simulador.services.simulacion;

import com.mycompany.simulador.model.ecosystem.Coordenada;

public record TurnoEvento(Coordenada coordenada, Tipo tipo) {
    public enum Tipo { NACIMIENTO_PRE, NACIMIENTO, MUERTE }
}
