package com.mycompany.simulador.services.simulacion;

import com.mycompany.simulador.config.Constantes;
import com.mycompany.simulador.config.AppConfig;
import com.mycompany.simulador.dto.EstadoTurnoDTO;
import com.mycompany.simulador.dto.SimulacionConfigDTO;
import com.mycompany.simulador.interfaces.IAlimentacionStrategy;
import com.mycompany.simulador.interfaces.IEcosistemaRepository;
import com.mycompany.simulador.interfaces.IEstadoTurnosRepository;
import com.mycompany.simulador.interfaces.IGeneticaService;
import com.mycompany.simulador.interfaces.IReproduccionStrategy;
import com.mycompany.simulador.interfaces.ISimulador;
import com.mycompany.simulador.model.ecosystem.Ecosistema;
import com.mycompany.simulador.model.ecosystem.Turno;
import com.mycompany.simulador.model.report.ReporteFinal;
import com.mycompany.simulador.services.genetica.MutacionService;
import com.mycompany.simulador.utils.SimLogger;

public class SimuladorService implements ISimulador {

    private final EcosistemaService ecosistemaService = new EcosistemaService();
    private final MovimientosService movimientosStrategy = new MovimientosService();
    private final IAlimentacionStrategy alimentacionStrategy = new AlimentacionService();
    private final IReproduccionStrategy reproduccionStrategy = new ReproduccionService();
    private final IGeneticaService geneticaService = new MutacionService();
    private final IEcosistemaRepository ecosistemaRepository;
    private final IEstadoTurnosRepository estadoTurnosRepository;
    private java.util.function.Consumer<String> logCallback;

    public SimuladorService(IEcosistemaRepository ecoRepo,
                            IEstadoTurnosRepository estadoRepo) {
        this.ecosistemaRepository = ecoRepo;
        this.estadoTurnosRepository = estadoRepo;
    }

    @Override
    public ReporteFinal ejecutarSimulacion(SimulacionConfigDTO config,
                                           SimulacionListener listener) {

        AppConfig.ensureDataFolder();
        ecosistemaRepository.guardarConfiguracion(config);

        Ecosistema e = ecosistemaService.crearEcosistema(config);
        ReporteFinal reporteFinal = new ReporteFinal();
        reporteFinal.setEscenario(config.getEscenario());
        int turnoExtincion = -1;
        java.util.List<TurnoEvento> eventosTurno = new java.util.ArrayList<>();
        int turnosEjecutados = 0;

        if (alimentacionStrategy instanceof AlimentacionService ali) {
            ali.setLogCallback(this::logEvent);
        }

        for (int turno = 1; turno <= config.getMaxTurnos(); turno++) {
            turnosEjecutados = turno;
            int turnoActual = turno;
            java.util.List<com.mycompany.simulador.model.species.Especie> vivosInicio = snapshotEspeciesVivas(e);
            movimientosStrategy.setLogCallback(m -> {
                logEvent(m);
            });
            movimientosStrategy.setStepCallback(paso -> {
                if (listener == null) return;
                char[][] matrizPaso = ecosistemaService.construirMatrizSimbolos(e);
                listener.onMovimiento(turnoActual, paso.origen(), paso.destino(), paso.comio(), paso.esDepredador(), matrizPaso);
            });
            if (reproduccionStrategy instanceof ReproduccionService repro) {
                repro.setLogCallback(this::logEvent);
                repro.setEventoCallback(eventosTurno::add);
            }
            movimientosStrategy.setEventoCallback(eventosTurno::add);
            movimientosStrategy.moverEspecies(e);

            alimentacionStrategy.procesarAlimentacion(e);
            reproduccionStrategy.reproducir(e);
            geneticaService.aplicarMutaciones(e);
            incrementarSobrevivencia(vivosInicio);

            Turno t = ecosistemaService.calcularTurno(turno, e);
            char[][] matrizSimbolos = ecosistemaService.construirMatrizSimbolos(e);

            EstadoTurnoDTO dto = new EstadoTurnoDTO(turno, config.getEscenario(),
                    t.getPresas(), t.getDepredadores(), t.getTerceraEspecie(),
                    t.getCeldasOcupadas());
            estadoTurnosRepository.guardarEstado(dto, matrizSimbolos);

            // Un solo refresh por tick: estado final del turno (solo un movimiento).
            if (listener != null) {
                listener.onTurnoActualizado(turno, matrizSimbolos);
                if (!eventosTurno.isEmpty()) {
                    listener.onEventos(turno, new java.util.ArrayList<>(eventosTurno));
                }
            }
            eventosTurno.clear();

            if ((t.getPresas() == 0 || t.getDepredadores() == 0) && turnoExtincion == -1) {
                turnoExtincion = turno;
            }

            if (ecosistemaService.estaExtinto(e)) {
                break;
            }

            try {
                Thread.sleep(Constantes.DELAY_TURNO_MS);
            } catch (InterruptedException ignored) { }
        }

        Turno ultimo = ecosistemaService.calcularTurno(0, e);
        reporteFinal.setTotalTurnos(turnosEjecutados);
        reporteFinal.setPresasFinales(ultimo.getPresas());
        reporteFinal.setDepredadoresFinales(ultimo.getDepredadores());
        reporteFinal.setTerceraEspecieFinal(ultimo.getTerceraEspecie());
        reporteFinal.setTurnoExtincion(turnoExtincion);
        int totalCeldas = Constantes.MATRIZ_FILAS * Constantes.MATRIZ_COLUMNAS;
        reporteFinal.setPorcentajeOcupacionFinal(
                totalCeldas == 0 ? 0 : (ultimo.getCeldasOcupadas() * 100.0 / totalCeldas));
        return reporteFinal;
    }

    public void setLogCallback(java.util.function.Consumer<String> cb) {
        this.logCallback = cb;
    }

    private void logEvent(String msg) {
        SimLogger.log(msg);
        if (logCallback != null) {
            logCallback.accept(msg);
        }
    }

    private java.util.List<com.mycompany.simulador.model.species.Especie> snapshotEspeciesVivas(Ecosistema e) {
        java.util.List<com.mycompany.simulador.model.species.Especie> vivos = new java.util.ArrayList<>();
        for (com.mycompany.simulador.model.ecosystem.Celda[] fila : e.getMatriz()) {
            for (com.mycompany.simulador.model.ecosystem.Celda c : fila) {
                if (c.getEspecie() != null && c.getEspecie().isViva()) {
                    vivos.add(c.getEspecie());
                }
            }
        }
        return vivos;
    }

    private void incrementarSobrevivencia(java.util.List<com.mycompany.simulador.model.species.Especie> snapshot) {
        if (snapshot == null) return;
        for (com.mycompany.simulador.model.species.Especie esp : snapshot) {
            if (esp != null && esp.isViva()) {
                esp.incrementarTurnosSobrevividos();
            }
        }
    }
}
