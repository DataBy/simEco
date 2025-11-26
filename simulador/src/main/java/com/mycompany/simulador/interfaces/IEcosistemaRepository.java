package com.mycompany.simulador.interfaces;

import com.mycompany.simulador.dto.SimulacionConfigDTO;

public interface IEcosistemaRepository {

    void guardarConfiguracion(SimulacionConfigDTO config);

    SimulacionConfigDTO cargarUltimaConfiguracion();
}
