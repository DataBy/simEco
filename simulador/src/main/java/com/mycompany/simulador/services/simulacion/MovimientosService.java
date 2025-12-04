package com.mycompany.simulador.services.simulacion;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.simulador.config.Constantes;
import com.mycompany.simulador.interfaces.IMovimientosStrategy;
import com.mycompany.simulador.model.ecosystem.Celda;
import com.mycompany.simulador.model.ecosystem.Ecosistema;
import com.mycompany.simulador.model.species.Depredador;
import com.mycompany.simulador.model.species.Especie;
import com.mycompany.simulador.model.species.Presa;
import com.mycompany.simulador.model.species.TerceraEspecie;
import com.mycompany.simulador.utils.AleatorioUtils;
import com.mycompany.simulador.utils.MatrizUtils;
import com.mycompany.simulador.utils.SimLogger;
import java.util.function.Consumer;

public class MovimientosService implements IMovimientosStrategy {

    private Consumer<Ecosistema> stepCallback;
    private Consumer<String> logCallback;

    public void setStepCallback(Consumer<Ecosistema> callback) {
        this.stepCallback = callback;
    }

    public void setLogCallback(Consumer<String> callback) {
        this.logCallback = callback;
    }

    @Override
    public void moverEspecies(Ecosistema e) {
        moverEspecies(e, stepCallback);
    }

    @Override
    public void moverEspecies(Ecosistema e, Consumer<Ecosistema> stepCb) {
        List<Celda> candidatos = new ArrayList<>();
        List<Celda> depredadores = new ArrayList<>();
        for (Celda[] fila : e.getMatriz()) {
            for (Celda c : fila) {
                Especie esp = c.getEspecie();
                if (esp != null && esp.isViva() &&
                        (esp instanceof Presa || esp instanceof Depredador || esp instanceof TerceraEspecie)) {
                    if (esp instanceof Depredador || esp instanceof TerceraEspecie) {
                        depredadores.add(c);
                    } else {
                        candidatos.add(c);
                    }
                }
            }
        }
        // Incrementa supervivencia para todos los vivos
        for (Celda c : candidatos) {
            c.getEspecie().incrementarTurnosSobrevividos();
        }
        for (Celda c : depredadores) {
            c.getEspecie().incrementarTurnosSobrevividos();
        }

        if (candidatos.isEmpty() && depredadores.isEmpty()) {
            notificarPaso(e, stepCb);
            return;
        }

        Celda origen;
        if (!depredadores.isEmpty()) {
            origen = AleatorioUtils.elegirAleatorio(depredadores);
        } else {
            origen = AleatorioUtils.elegirAleatorio(candidatos);
        }
        Especie esp = origen.getEspecie();
        if (esp == null || !esp.isViva()) {
            notificarPaso(e, stepCb);
            return;
        }

        Celda destino = null;
        boolean comio = false;
        if (esp instanceof Presa) {
            destino = moverPresa(e, origen);
        } else if (esp instanceof Depredador || esp instanceof TerceraEspecie) {
            var res = moverDepredadorLike(e, origen);
            destino = res[0];
            comio = res[1] != null;
            if (!comio) {
                esp.registrarAyuno();
                if (esp.getTurnosSinComer() >= Constantes.MAX_TURNOS_SIN_COMER_DEPREDADOR) {
                    SimLogger.log(describir(esp) + " muere por hambre en " + coord(destino != null ? destino : origen));
                    if (destino != null) {
                        destino.vaciar();
                    } else {
                        origen.vaciar();
                    }
                    destino = null;
                }
            } else {
                esp.registrarComida();
            }
        }

        if (destino != null) {
            logMovimiento(esp, origen.getCoordenada(), destino.getCoordenada(), comio);
        }
        notificarPaso(e, stepCb);
    }

    private Celda moverPresa(Ecosistema e, Celda origen) {
        List<Celda> libres = new ArrayList<>();
        for (var coord : MatrizUtils.vecinosOrtogonales(origen.getCoordenada())) {
            Celda c = e.getCelda(coord.getFila(), coord.getColumna());
            if (c.estaVacia()) libres.add(c);
        }
        if (libres.isEmpty()) {
            SimLogger.log("Presa permanece en " + coord(origen) + " (sin celdas libres)");
            return null;
        }
        Celda destino = AleatorioUtils.elegirAleatorio(libres);
        destino.setEspecie(origen.getEspecie());
        destino.getEspecie().setPosicion(destino.getCoordenada());
        SimLogger.log("Presa se mueve de " + coord(origen) + " a " + coord(destino));
        origen.vaciar();
        return destino;
    }

    private Celda[] moverDepredadorLike(Ecosistema e, Celda origen) {
        Especie dep = origen.getEspecie();
        List<Celda> vecinos = new ArrayList<>();
        for (var coord : MatrizUtils.vecinosOrtogonales(origen.getCoordenada())) {
            vecinos.add(e.getCelda(coord.getFila(), coord.getColumna()));
        }
        Celda objetivoPresa = null;
        for (Celda c : vecinos) {
            if (!c.estaVacia() && c.getEspecie().getTipo() == Especie.Tipo.PRESA) {
                objetivoPresa = c;
                break;
            }
        }
        if (objetivoPresa != null) {
            objetivoPresa.setEspecie(dep);
            dep.setPosicion(objetivoPresa.getCoordenada());
            dep.reiniciarTurnosSinComer();
            origen.vaciar();
            dep.setComioEnVentana(true);
            SimLogger.log(describir(dep) + " se mueve de " + coord(origen) + " a " + coord(objetivoPresa)
                    + " y come una presa");
            return new Celda[]{objetivoPresa, objetivoPresa};
        }
        List<Celda> libres = new ArrayList<>();
        for (Celda c : vecinos) {
            if (c.estaVacia()) libres.add(c);
        }
        if (libres.isEmpty()) {
            SimLogger.log(describir(dep) + " queda en " + coord(origen) + " (sin movimiento posible)");
            return new Celda[]{null, null};
        }
        Celda destino = AleatorioUtils.elegirAleatorio(libres);
        destino.setEspecie(dep);
        dep.setPosicion(destino.getCoordenada());
        SimLogger.log(describir(dep) + " se mueve de " + coord(origen) + " a " + coord(destino));
        origen.vaciar();
        return new Celda[]{destino, null};
    }

    private String coord(Celda c) {
        return "(" + c.getCoordenada().getFila() + "," + c.getCoordenada().getColumna() + ")";
    }

    private String describir(Especie esp) {
        if (esp instanceof TerceraEspecie) return "Tercera especie";
        if (esp instanceof Depredador) return "Depredador";
        return "Especie";
    }

    private void notificarPaso(Ecosistema e, Consumer<Ecosistema> stepCb) {
        if (stepCb != null) {
            stepCb.accept(e);
        }
    }

    private void logMovimiento(Especie esp, com.mycompany.simulador.model.ecosystem.Coordenada origen,
                               com.mycompany.simulador.model.ecosystem.Coordenada destino, boolean comio) {
        if (logCallback == null) return;
        String tipo = describir(esp);
        String msg = "Movimiento: " + tipo + " de (" + origen.getFila() + "," + origen.getColumna() + ") a ("
                + destino.getFila() + "," + destino.getColumna() + ")" + (comio ? " (comió)" : "");
        logCallback.accept(msg);
    }
}
