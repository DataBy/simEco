package com.mycompany.simulador.model.species;

import com.mycompany.simulador.model.ecosystem.Coordenada;
import com.mycompany.simulador.model.genetics.Genotipo;

public abstract class Especie {

    public enum Tipo { PRESA, DEPREDADOR, TERCERA_ESPECIE }

    private Tipo tipo;
    private String nombre;
    private Coordenada posicion;
    private boolean viva = true;
    private int turnosSinComer;
    private int turnosSobrevividos;
    private boolean comioEnVentana;
    private Genotipo genotipo = new Genotipo();

    protected Especie(Tipo tipo, String nombre) {
        this.tipo = tipo;
        this.nombre = nombre;
    }

    public Tipo getTipo() { return tipo; }
    public String getNombre() { return nombre; }

    public Coordenada getPosicion() { return posicion; }
    public void setPosicion(Coordenada posicion) { this.posicion = posicion; }

    public boolean isViva() { return viva; }
    public void setViva(boolean viva) { this.viva = viva; }

    public int getTurnosSinComer() { return turnosSinComer; }
    public void incrementarTurnosSinComer() { this.turnosSinComer++; }
    public void reiniciarTurnosSinComer() { this.turnosSinComer = 0; }

    public int getTurnosSobrevividos() { return turnosSobrevividos; }
    public void incrementarTurnosSobrevividos() { this.turnosSobrevividos++; }

    public boolean isComioEnVentana() { return comioEnVentana; }
    public void setComioEnVentana(boolean comioEnVentana) { this.comioEnVentana = comioEnVentana; }

    public Genotipo getGenotipo() { return genotipo; }
}
