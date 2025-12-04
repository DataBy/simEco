package com.mycompany.simulador.interfaces;

import com.mycompany.simulador.dto.SimulacionConfigDTO;
import com.mycompany.simulador.model.report.ReporteFinal;

public interface ISimulador {

    ReporteFinal ejecutarSimulacion(SimulacionConfigDTO config,
                                    SimulacionListener listener);

    interface SimulacionListener {
        void onTurnoActualizado(int turnoActual, char[][] matrizSimbolos);

        default void onMovimiento(int turnoActual,
                                  com.mycompany.simulador.model.ecosystem.Coordenada origen,
                                  com.mycompany.simulador.model.ecosystem.Coordenada destino,
                                  boolean comio,
                                  boolean esDepredador,
                                  char[][] matrizPaso) {
            // Implementación opcional
        }

        default void onEventos(int turnoActual, java.util.List<com.mycompany.simulador.services.simulacion.TurnoEvento> eventos) {
            // Implementación opcional
        }
    }
}
