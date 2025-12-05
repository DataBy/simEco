package com.mycompany.simulador.services.simulacion;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;

import com.mycompany.simulador.config.Constantes;
import com.mycompany.simulador.interfaces.IMovimientosStrategy;
import com.mycompany.simulador.model.ecosystem.Celda;
import com.mycompany.simulador.model.ecosystem.Coordenada;
import com.mycompany.simulador.model.ecosystem.Ecosistema;
import com.mycompany.simulador.model.species.Depredador;
import com.mycompany.simulador.model.species.Especie;
import com.mycompany.simulador.model.species.TerceraEspecie;
import com.mycompany.simulador.services.simulacion.TurnoEvento;
import com.mycompany.simulador.utils.AleatorioUtils;
import com.mycompany.simulador.utils.MatrizUtils;
import com.mycompany.simulador.utils.SimLogger;

public class MovimientosService implements IMovimientosStrategy {

    private Consumer<MovimientoPaso> stepCallback;
    private Consumer<String> logCallback;
    private Consumer<TurnoEvento> eventoCallback;
    private Movimiento ultimoMovimiento;

    public record Movimiento(com.mycompany.simulador.model.ecosystem.Coordenada origen,
                             com.mycompany.simulador.model.ecosystem.Coordenada destino,
                             boolean comio,
                             boolean esDepredador,
                             boolean murioPorHambre) { }

    public record MovimientoPaso(Coordenada origen,
                                 Coordenada destino,
                                 boolean comio,
                                 boolean esDepredador,
                                 boolean preparacion) { }

    private record CaminoSeleccion(Celda origen, List<Coordenada> camino) { }

    public void setStepCallback(Consumer<MovimientoPaso> callback) {
        this.stepCallback = callback;
    }

    public void setLogCallback(Consumer<String> callback) {
        this.logCallback = callback;
    }

    public void setEventoCallback(Consumer<TurnoEvento> cb) {
        this.eventoCallback = cb;
    }

    @Override
    public void moverEspecies(Ecosistema e) {
        moverEspecies(e, stepCallback);
    }

    public void moverEspecies(Ecosistema e, Consumer<MovimientoPaso> stepCb) {
        ultimoMovimiento = null;
        incrementarSobrevivencia(e);
        moverUnaPresa(e, stepCb);
        List<Celda> depredadores = obtenerDepredadores(e);

        CaminoSeleccion camino = elegirCaminoAPresa(e, depredadores);
        if (camino == null) {
            SimLogger.log("No hay depredadores con un camino disponible hacia presas.");
            return;
        }

        Celda origen = camino.origen();
        Especie esp = origen.getEspecie();
        if (esp == null || !esp.isViva()) {
            return;
        }

        boolean esDep = esDepredador(esp);

        notificarPaso(stepCb, new MovimientoPaso(origen.getCoordenada(), null, false, esDep, true));
        dormirPasoLento();

        for (int i = 1; i < camino.camino().size(); i++) {
            Coordenada destinoCoord = camino.camino().get(i);
            Celda destino = e.getCelda(destinoCoord.getFila(), destinoCoord.getColumna());

            boolean comio = !destino.estaVacia() && destino.getEspecie().getTipo() == Especie.Tipo.PRESA;
            if (comio) {
                notificarEvento(destino.getCoordenada(), TurnoEvento.Tipo.MUERTE);
                if (destino.getEspecie() != null) {
                    destino.getEspecie().setViva(false);
                }
            }

            Celda origenAnterior = origen;
            origenAnterior.setEspecie(null);
            destino.setEspecie(esp);
            esp.setPosicion(destino.getCoordenada());
            esp.setViva(true);
            if (comio) {
                esp.registrarComida();
            }

            ultimoMovimiento = new Movimiento(
                    origenAnterior.getCoordenada(),
                    destino.getCoordenada(),
                    comio,
                    esDep,
                    false
            );
            logMovimiento(esp, origenAnterior.getCoordenada(), destino.getCoordenada(), comio);
            notificarPaso(stepCb, new MovimientoPaso(origenAnterior.getCoordenada(), destino.getCoordenada(), comio, esDep, false));
            origen = destino;
            dormirPasoLento();
        }
    }

    private List<Celda> obtenerDepredadores(Ecosistema e) {
        List<Celda> depredadores = new ArrayList<>();
        for (Celda[] fila : e.getMatriz()) {
            for (Celda c : fila) {
                Especie esp = c.getEspecie();
                if (esp != null && esp.isViva() && esp.getTipo() == Especie.Tipo.DEPREDADOR) {
                    depredadores.add(c);
                }
            }
        }
        return depredadores;
    }

    private CaminoSeleccion elegirCaminoAPresa(Ecosistema e, List<Celda> depredadores) {
        List<CaminoSeleccion> candidatos = new ArrayList<>();
        for (Celda dep : depredadores) {
            List<Coordenada> camino = calcularCaminoAPresa(e, dep);
            if (camino.size() > 1) {
                candidatos.add(new CaminoSeleccion(dep, camino));
            }
        }
        if (candidatos.isEmpty()) {
            return null;
        }
        int minimo = candidatos.stream()
                .map(c -> c.camino().size())
                .min(Comparator.naturalOrder())
                .orElse(Integer.MAX_VALUE);
        List<CaminoSeleccion> masCortos = candidatos.stream()
                .filter(c -> c.camino().size() == minimo)
                .toList();
        return AleatorioUtils.elegirAleatorio(masCortos);
    }

    private List<Coordenada> calcularCaminoAPresa(Ecosistema e, Celda origen) {
        boolean[][] visitado = new boolean[Constantes.MATRIZ_FILAS][Constantes.MATRIZ_COLUMNAS];
        Map<Coordenada, Coordenada> padre = new HashMap<>();
        Queue<Coordenada> queue = new ArrayDeque<>();

        Coordenada inicio = origen.getCoordenada();
        queue.add(inicio);
        visitado[inicio.getFila()][inicio.getColumna()] = true;

        Coordenada encontrada = null;

        while (!queue.isEmpty()) {
            Coordenada actual = queue.poll();
            Celda celdaActual = e.getCelda(actual.getFila(), actual.getColumna());
            if (!actual.equals(inicio)
                    && !celdaActual.estaVacia()
                    && celdaActual.getEspecie().getTipo() == Especie.Tipo.PRESA) {
                encontrada = actual;
                break;
            }
            for (Coordenada vecino : MatrizUtils.vecinosOrtogonales(actual)) {
                if (visitado[vecino.getFila()][vecino.getColumna()]) continue;
                Celda celdaVecina = e.getCelda(vecino.getFila(), vecino.getColumna());
                if (esTransitableParaDepredador(celdaVecina)) {
                    padre.put(vecino, actual);
                    visitado[vecino.getFila()][vecino.getColumna()] = true;
                    queue.add(vecino);
                }
            }
        }

        if (encontrada == null) {
            return List.of();
        }

        List<Coordenada> camino = new ArrayList<>();
        Coordenada cursor = encontrada;
        while (cursor != null) {
            camino.add(cursor);
            cursor = padre.get(cursor);
        }
        java.util.Collections.reverse(camino);
        return camino;
    }

    private boolean esTransitableParaDepredador(Celda celda) {
        if (celda == null) return false;
        if (celda.estaVacia()) return true;
        Especie esp = celda.getEspecie();
        return esp != null && esp.getTipo() == Especie.Tipo.PRESA;
    }

    private void moverUnaPresa(Ecosistema e, Consumer<MovimientoPaso> stepCb) {
        List<Celda> presas = new ArrayList<>();
        for (Celda[] fila : e.getMatriz()) {
            for (Celda c : fila) {
                Especie esp = c.getEspecie();
                if (esp != null && esp.isViva() && esp.getTipo() == Especie.Tipo.PRESA) {
                    List<Coordenada> vecinos = MatrizUtils.vecinosOrtogonales(c.getCoordenada());
                    boolean tieneVacio = vecinos.stream()
                            .anyMatch(coord -> e.getCelda(coord.getFila(), coord.getColumna()).estaVacia());
                    if (tieneVacio) {
                        presas.add(c);
                    }
                }
            }
        }
        if (presas.isEmpty()) return;

        Celda origen = AleatorioUtils.elegirAleatorio(presas);
        List<Coordenada> vacios = new ArrayList<>();
        for (Coordenada v : MatrizUtils.vecinosOrtogonales(origen.getCoordenada())) {
            if (e.getCelda(v.getFila(), v.getColumna()).estaVacia()) {
                vacios.add(v);
            }
        }
        if (vacios.isEmpty()) return;

        Coordenada destinoCoord = AleatorioUtils.elegirAleatorio(vacios);
        Celda destino = e.getCelda(destinoCoord.getFila(), destinoCoord.getColumna());
        Especie presa = origen.getEspecie();

        // Aviso previo para resaltar que presa se movera
        notificarPaso(stepCb, new MovimientoPaso(origen.getCoordenada(), null, false, false, true));
        // Pausa breve para que el borde azul previo sea visible antes de mover
        dormirPasoLento();

        origen.setEspecie(null);
        destino.setEspecie(presa);
        if (presa != null) {
            presa.setPosicion(destinoCoord);
        }
        // Aviso post-movimiento
        notificarPaso(stepCb, new MovimientoPaso(origen.getCoordenada(), destinoCoord, false, false, false));
        ultimoMovimiento = new Movimiento(
                origen.getCoordenada(),
                destinoCoord,
                false,
                false,
                false
        );
        logMovimiento(presa, origen.getCoordenada(), destinoCoord, false);
    }

    private boolean esDepredador(Especie esp) {
        return esp instanceof Depredador;
    }

    private void incrementarSobrevivencia(Ecosistema e) {
        for (Celda[] fila : e.getMatriz()) {
            for (Celda c : fila) {
                Especie esp = c.getEspecie();
                if (esp != null && esp.isViva()) {
                    esp.incrementarTurnosSobrevividos();
                }
            }
        }
    }

    private void notificarPaso(Consumer<MovimientoPaso> cb, MovimientoPaso paso) {
        if (cb != null && paso != null) {
            cb.accept(paso);
        }
    }

    private void dormirPasoLento() {
        try {
            Thread.sleep(Constantes.DELAY_PASO_MS);
        } catch (InterruptedException ignored) { }
    }

    private String describir(Especie esp) {
        if (esp instanceof TerceraEspecie) return "Tercera especie";
        if (esp instanceof Depredador) return "Depredador";
        if (esp != null && esp.getTipo() == Especie.Tipo.PRESA) return "Presa";
        return "Especie";
    }

    private void logMovimiento(Especie esp, com.mycompany.simulador.model.ecosystem.Coordenada origen,
                               com.mycompany.simulador.model.ecosystem.Coordenada destino, boolean comio) {
        if (logCallback == null) return;
        String tipo = describir(esp);
        String msg = "Movimiento: " + tipo + " de (" + origen.getFila() + "," + origen.getColumna() + ") a ("
                + destino.getFila() + "," + destino.getColumna() + ")" + (comio ? " (comio)" : "");
        logCallback.accept(msg);
    }

    public Movimiento getUltimoMovimiento() {
        return ultimoMovimiento;
    }

    private void notificarEvento(com.mycompany.simulador.model.ecosystem.Coordenada coord, TurnoEvento.Tipo tipo) {
        if (eventoCallback != null && coord != null) {
            eventoCallback.accept(new TurnoEvento(coord, tipo));
        }
    }
}
