package com.mycompany.simulador.model.report;

public class EstadoTurno {

    private int turno;
    private String escenario;
    private int presas;
    private int depredadores;
    private int terceraEspecie;
    private int celdasOcupadas;

    public EstadoTurno(int turno, String escenario, int presas,
                       int depredadores, int terceraEspecie, int celdasOcupadas) {
        this.turno = turno;
        this.escenario = escenario;
        this.presas = presas;
        this.depredadores = depredadores;
        this.terceraEspecie = terceraEspecie;
        this.celdasOcupadas = celdasOcupadas;
    }

    public int getTurno() { return turno; }
    public String getEscenario() { return escenario; }
    public int getPresas() { return presas; }
    public int getDepredadores() { return depredadores; }
    public int getTerceraEspecie() { return terceraEspecie; }
    public int getCeldasOcupadas() { return celdasOcupadas; }
}
