package com.mycompany.simulador.model.ecosystem;

public class Turno {

    private int numero;
    private int presas;
    private int depredadores;
    private int terceraEspecie;
    private int celdasOcupadas;

    public Turno(int numero, int presas, int depredadores,
                 int terceraEspecie, int celdasOcupadas) {
        this.numero = numero;
        this.presas = presas;
        this.depredadores = depredadores;
        this.terceraEspecie = terceraEspecie;
        this.celdasOcupadas = celdasOcupadas;
    }

    public int getNumero() { return numero; }
    public int getPresas() { return presas; }
    public int getDepredadores() { return depredadores; }
    public int getTerceraEspecie() { return terceraEspecie; }
    public int getCeldasOcupadas() { return celdasOcupadas; }
}
