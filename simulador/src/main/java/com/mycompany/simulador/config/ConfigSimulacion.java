package com.mycompany.simulador.config;

public final class ConfigSimulacion {

    private ConfigSimulacion() {}

    public static final int MAX_TURNOS_POR_DEFECTO = 50;

    // Escenario equilibrado
    public static final int EQUILIBRADO_PRESAS       = 30;
    public static final int EQUILIBRADO_DEPREDADORES = 25;
    public static final int EQUILIBRADO_TERCERA      = 5;

    // Depredadores dominantes
    public static final int DEPREDADORES_DOM_PRESAS       = 20;
    public static final int DEPREDADORES_DOM_DEPREDADORES = 60;
    public static final int DEPREDADORES_DOM_TERCERA      = 5;

    // Presas dominantes
    public static final int PRESAS_DOM_PRESAS       = 70;
    public static final int PRESAS_DOM_DEPREDADORES = 15;
    public static final int PRESAS_DOM_TERCERA      = 5;
}
