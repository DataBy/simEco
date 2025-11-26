package com.mycompany.simulador.interfaces;

import com.mycompany.simulador.dto.SimulacionConfigDTO;
import com.mycompany.simulador.model.report.ReporteFinal;

public interface ISimulador {

    ReporteFinal ejecutarSimulacion(SimulacionConfigDTO config,
                                    SimulacionListener listener);

    interface SimulacionListener {
        void onTurnoActualizado(int turnoActual, char[][] matrizSimbolos);
    }
}
