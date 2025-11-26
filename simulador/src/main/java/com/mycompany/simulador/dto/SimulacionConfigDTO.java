package com.mycompany.simulador.dto;

public class SimulacionConfigDTO {

    private String escenario;
    private int presasIniciales;
    private int depredadoresIniciales;
    private int terceraEspecieInicial;
    private int maxTurnos;

    public String getEscenario() { return escenario; }
    public void setEscenario(String escenario) { this.escenario = escenario; }

    public int getPresasIniciales() { return presasIniciales; }
    public void setPresasIniciales(int presasIniciales) { this.presasIniciales = presasIniciales; }

    public int getDepredadoresIniciales() { return depredadoresIniciales; }
    public void setDepredadoresIniciales(int depredadoresIniciales) { this.depredadoresIniciales = depredadoresIniciales; }

    public int getTerceraEspecieInicial() { return terceraEspecieInicial; }
    public void setTerceraEspecieInicial(int terceraEspecieInicial) { this.terceraEspecieInicial = terceraEspecieInicial; }

    public int getMaxTurnos() { return maxTurnos; }
    public void setMaxTurnos(int maxTurnos) { this.maxTurnos = maxTurnos; }
}
