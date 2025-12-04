package com.mycompany.simulador.services.simulacion;

import com.mycompany.simulador.config.Constantes;
import com.mycompany.simulador.interfaces.IAlimentacionStrategy;
import com.mycompany.simulador.model.ecosystem.Celda;
import com.mycompany.simulador.model.ecosystem.Ecosistema;
import com.mycompany.simulador.model.species.Depredador;
import com.mycompany.simulador.model.species.Especie;
import com.mycompany.simulador.model.species.TerceraEspecie;
import com.mycompany.simulador.utils.SimLogger;

public class AlimentacionService implements IAlimentacionStrategy {

    @Override
    public void procesarAlimentacion(Ecosistema e) {
        for (Celda[] fila : e.getMatriz()) {
            for (Celda c : fila) {
                Especie esp = c.getEspecie();
                if (esp == null || !esp.isViva()) continue;

                if (esp instanceof Depredador || esp instanceof TerceraEspecie) {
                    boolean comioEsteTurno = esp.isComioEnVentana();
                    if (comioEsteTurno) {
                        esp.reiniciarTurnosSinComer();
                    } else {
                        esp.registrarAyuno();
                        if (esp.getTurnosSinComer() >= Constantes.MAX_TURNOS_SIN_COMER_DEPREDADOR) {
                            esp.setViva(false);
                            c.setEspecie(null); // celda queda vacía
                            SimLogger.log("Depredador muere por hambre en " + formatear(c));
                            continue;
                        }
                    }
                    esp.setComioEnVentana(false);
                }
            }
        }
    }

    private String formatear(Celda c) {
        return "(" + c.getCoordenada().getFila() + "," + c.getCoordenada().getColumna() + ")";
    }

    private String describir(Especie esp) {
        return (esp instanceof TerceraEspecie) ? "Tercera especie" : "Depredador";
    }
}
