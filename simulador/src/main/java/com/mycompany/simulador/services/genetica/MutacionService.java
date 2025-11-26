package com.mycompany.simulador.services.genetica;

import com.mycompany.simulador.config.Constantes;
import com.mycompany.simulador.interfaces.IGeneticaService;
import com.mycompany.simulador.model.ecosystem.Celda;
import com.mycompany.simulador.model.ecosystem.Ecosistema;
import com.mycompany.simulador.model.species.Especie;
import com.mycompany.simulador.utils.AleatorioUtils;

public class MutacionService implements IGeneticaService {

    @Override
    public void aplicarMutaciones(Ecosistema ecosistema) {
        for (Celda[] fila : ecosistema.getMatriz()) {
            for (Celda c : fila) {
                Especie e = c.getEspecie();
                if (e != null && e.isViva() && AleatorioUtils.probabilidad(Constantes.PROB_MUTACION)) {
                    e.getGenotipo().setMutacionVelocidad(true);
                }
            }
        }
    }
}
