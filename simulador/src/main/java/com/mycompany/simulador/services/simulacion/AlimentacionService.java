package com.mycompany.simulador.services.simulacion;

import com.mycompany.simulador.config.Constantes;
import com.mycompany.simulador.interfaces.IAlimentacionStrategy;
import com.mycompany.simulador.model.ecosystem.Celda;
import com.mycompany.simulador.model.ecosystem.Ecosistema;
import com.mycompany.simulador.model.species.Depredador;
import com.mycompany.simulador.model.species.Especie;
import com.mycompany.simulador.model.species.TerceraEspecie;

public class AlimentacionService implements IAlimentacionStrategy {

    @Override
    public void procesarAlimentacion(Ecosistema e) {
        for (Celda[] fila : e.getMatriz()) {
            for (Celda c : fila) {
                Especie esp = c.getEspecie();
                if (esp == null || !esp.isViva()) continue;

                if (esp instanceof Depredador || esp instanceof TerceraEspecie) {
                    int extra = esp instanceof TerceraEspecie ? 1 : 0;
                    if (esp.getTurnosSinComer() >= Constantes.MAX_TURNOS_SIN_COMER_DEPREDADOR + extra) {
                        c.vaciar();
                    } else if (esp.isComioEnVentana()) {
                        esp.reiniciarTurnosSinComer();
                        esp.setComioEnVentana(false);
                    } else {
                        esp.incrementarTurnosSinComer();
                    }
                }
            }
        }
    }
}
