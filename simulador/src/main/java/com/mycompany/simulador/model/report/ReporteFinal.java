package com.mycompany.simulador.model.report;

public class ReporteFinal {

    private int totalTurnos;
    private int presasFinales;
    private int depredadoresFinales;
    private int terceraEspecieFinal;
    private int turnoExtincion; // -1 si no hubo
    private double porcentajeOcupacionFinal;
    private String escenario;

    public int getTotalTurnos() { return totalTurnos; }
    public void setTotalTurnos(int totalTurnos) { this.totalTurnos = totalTurnos; }

    public int getPresasFinales() { return presasFinales; }
    public void setPresasFinales(int presasFinales) { this.presasFinales = presasFinales; }

    public int getDepredadoresFinales() { return depredadoresFinales; }
    public void setDepredadoresFinales(int depredadoresFinales) { this.depredadoresFinales = depredadoresFinales; }

    public int getTerceraEspecieFinal() { return terceraEspecieFinal; }
    public void setTerceraEspecieFinal(int terceraEspecieFinal) { this.terceraEspecieFinal = terceraEspecieFinal; }

    public int getTurnoExtincion() { return turnoExtincion; }
    public void setTurnoExtincion(int turnoExtincion) { this.turnoExtincion = turnoExtincion; }

    public double getPorcentajeOcupacionFinal() { return porcentajeOcupacionFinal; }
    public void setPorcentajeOcupacionFinal(double porcentajeOcupacionFinal) {
        this.porcentajeOcupacionFinal = porcentajeOcupacionFinal;
    }

    public String getEscenario() { return escenario; }
    public void setEscenario(String escenario) { this.escenario = escenario; }
}
