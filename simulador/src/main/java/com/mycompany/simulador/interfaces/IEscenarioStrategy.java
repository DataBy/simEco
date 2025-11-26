package com.mycompany.simulador.interfaces;

import com.mycompany.simulador.dto.SimulacionConfigDTO;
import com.mycompany.simulador.model.ecosystem.Ecosistema;

public interface IEscenarioStrategy {
    void configurarEscenario(Ecosistema ecosistema, SimulacionConfigDTO config);
}
