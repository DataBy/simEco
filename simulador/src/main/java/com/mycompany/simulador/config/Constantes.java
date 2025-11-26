package com.mycompany.simulador.config;

public final class Constantes {

    private Constantes() {}

    // Tamaño base de la ventana (proporción 16:9)
    public static final double VENTANA_ANCHO = 1880;
    public static final double VENTANA_ALTO  = 1080;

    // Matriz del ecosistema
    public static final int MATRIZ_FILAS     = 10;
    public static final int MATRIZ_COLUMNAS  = 10;
    public static final int MATRIZ_TAM_CELDA = 102;

    // Delay entre turnos (ms)
    public static final int DELAY_TURNO_MS = 500;

    // Reglas del ecosistema
    public static final int MAX_TURNOS_SIN_COMER_DEPREDADOR = 3;
    public static final int TURNOS_SOBREVIVIR_REPRO_PRESA   = 2;
    public static final int VENTANA_TURNOS_REPRO_DEPREDADOR = 3;

    // Tipos de escenario
    public static final String ESCENARIO_EQUILIBRADO        = "EQUILIBRADO";
    public static final String ESCENARIO_DEPREDADORES_DOM   = "DEPREDADORES_DOMINANTES";
    public static final String ESCENARIO_PRESAS_DOM         = "PRESAS_DOMINANTES";

    // Simulación / mutaciones
    public static final double PROB_MUTACION = 0.1;
}
