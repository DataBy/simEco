package com.mycompany.simulador.services.simulacion;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.simulador.config.Constantes;
import com.mycompany.simulador.interfaces.IReproduccionStrategy;
import com.mycompany.simulador.model.ecosystem.Celda;
import com.mycompany.simulador.model.ecosystem.Ecosistema;
import com.mycompany.simulador.model.species.Depredador;
import com.mycompany.simulador.model.species.Especie;
import com.mycompany.simulador.model.species.Presa;
import com.mycompany.simulador.utils.AleatorioUtils;
import com.mycompany.simulador.utils.MatrizUtils;
import com.mycompany.simulador.utils.SimLogger;

public class ReproduccionService implements IReproduccionStrategy {

    private java.util.function.Consumer<String> logCallback;
    private java.util.function.Consumer<com.mycompany.simulador.services.simulacion.TurnoEvento> eventoCallback;

    public void setLogCallback(java.util.function.Consumer<String> cb) {
        this.logCallback = cb;
    }

    public void setEventoCallback(java.util.function.Consumer<com.mycompany.simulador.services.simulacion.TurnoEvento> cb) {
        this.eventoCallback = cb;
    }

    @Override
    public void reproducir(Ecosistema e) {
        boolean creadoEnTurno = false;
        List<Celda> snapshot = new ArrayList<>();
        for (Celda[] fila : e.getMatriz()) {
            for (Celda c : fila) {
                if (!c.estaVacia()) snapshot.add(c);
            }
        }
        for (Celda c : snapshot) {
            if (creadoEnTurno) break;
            Especie esp = c.getEspecie();
            if (esp == null || !esp.isViva()) continue;
            if (esp instanceof Presa &&
                esp.getTurnosSobrevividos() >= Constantes.TURNOS_SOBREVIVIR_REPRO_PRESA) {
                boolean creado = reproducirEnVecinoVacio(e, c, new Presa("Presa"), "Presa");
                if (creado) {
                    esp.reiniciarTurnosSobrevividos();
                    creadoEnTurno = true;
                }
            } else if (esp instanceof Depredador &&
                    esp.haComidoRecientemente(Constantes.VENTANA_TURNOS_REPRO_DEPREDADOR)) {
                boolean creadoDep = reproducirEnVecinoVacio(e, c, new Depredador("Depredador"), "Depredador");
                if (creadoDep) {
                    creadoEnTurno = true;
                }
            }
        }
    }

    private boolean reproducirEnVecinoVacio(Ecosistema e, Celda origen, Especie cria, String tipo) {
        List<Celda> libres = new ArrayList<>();
        for (var coord : MatrizUtils.vecinosOrtogonales(origen.getCoordenada())) {
            Celda c = e.getCelda(coord.getFila(), coord.getColumna());
            if (c.estaVacia()) libres.add(c);
        }
        if (libres.isEmpty()) {
            log(tipo + " en " + coord(origen) + " intenta reproducirse, pero no hay espacio libre");
            return false;
        }
        Celda destino = AleatorioUtils.elegirAleatorio(libres);
        destino.setEspecie(cria);
        cria.setPosicion(destino.getCoordenada());
        log(tipo + " en " + coord(origen) + " se reproduce en " + coord(destino));
        notificarEvento(destino.getCoordenada(), com.mycompany.simulador.services.simulacion.TurnoEvento.Tipo.NACIMIENTO);
        return true;
    }

    private String coord(Celda c) {
        return "(" + c.getCoordenada().getFila() + "," + c.getCoordenada().getColumna() + ")";
    }

    private void log(String msg) {
        SimLogger.log(msg);
        if (logCallback != null) {
            logCallback.accept(msg);
        }
    }

    private void notificarEvento(com.mycompany.simulador.model.ecosystem.Coordenada coord,
                                 com.mycompany.simulador.services.simulacion.TurnoEvento.Tipo tipo) {
        if (eventoCallback != null && coord != null) {
            eventoCallback.accept(new com.mycompany.simulador.services.simulacion.TurnoEvento(coord, tipo));
        }
    }
}
