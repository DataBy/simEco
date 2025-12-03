package com.mycompany.simulador.services.simulacion;

import com.mycompany.simulador.config.Constantes;
import com.mycompany.simulador.config.AppConfig;
import com.mycompany.simulador.dto.EstadoTurnoDTO;
import com.mycompany.simulador.dto.SimulacionConfigDTO;
import com.mycompany.simulador.interfaces.IAlimentacionStrategy;
import com.mycompany.simulador.interfaces.IEcosistemaRepository;
import com.mycompany.simulador.interfaces.IEstadoTurnosRepository;
import com.mycompany.simulador.interfaces.IGeneticaService;
import com.mycompany.simulador.interfaces.IMovimientosStrategy;
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
        SimLogger.log("Iniciando simulacion. Escenario: " + config.getEscenario()
                + ", turnos maximos: " + config.getMaxTurnos());

        ecosistemaRepository.guardarConfiguracion(config);

        Ecosistema e = ecosistemaService.crearEcosistema(config);
        ReporteFinal reporteFinal = new ReporteFinal();
        int turnoExtincion = -1;

        for (int turno = 1; turno <= config.getMaxTurnos(); turno++) {
            int turnoActual = turno;
            movimientosStrategy.setLogCallback(m -> {
                SimLogger.log(m);
                if (logCallback != null) logCallback.accept(m);
            });
            SimLogger.log("Turno " + turno + " - movimiento");
            movimientosStrategy.moverEspecies(e);

            // Mostrar solo un refresh por tick tras el movimiento
            if (listener != null) {
                char[][] mPaso = ecosistemaService.construirMatrizSimbolos(e);
                listener.onTurnoActualizado(turnoActual, mPaso);
            }
            try {
                Thread.sleep(Constantes.DELAY_PASO_MS);
            } catch (InterruptedException ignored) { }

            SimLogger.log("Turno " + turno + " - alimentacion");
            alimentacionStrategy.procesarAlimentacion(e);
            SimLogger.log("Turno " + turno + " - reproduccion");
            reproduccionStrategy.reproducir(e);
            geneticaService.aplicarMutaciones(e);

            Turno t = ecosistemaService.calcularTurno(turno, e);
            char[][] matrizSimbolos = ecosistemaService.construirMatrizSimbolos(e);

            EstadoTurnoDTO dto = new EstadoTurnoDTO(turno, config.getEscenario(),
                    t.getPresas(), t.getDepredadores(), t.getTerceraEspecie(),
                    t.getCeldasOcupadas());
            estadoTurnosRepository.guardarEstado(dto, matrizSimbolos);

            if (listener != null) {
                listener.onTurnoActualizado(turno, matrizSimbolos);
            }

            SimLogger.log("Resumen turno " + turno + ": Presas=" + t.getPresas()
                    + ", Depredadores=" + t.getDepredadores()
                    + ", Tercera especie=" + t.getTerceraEspecie()
                    + ", Ocupadas=" + t.getCeldasOcupadas());
            SimLogger.logMatriz(matrizSimbolos);
            SimLogger.log("Estado del turno guardado en " + AppConfig.ARCHIVO_ESTADO_TURNOS);

            if ((t.getPresas() == 0 || t.getDepredadores() == 0) && turnoExtincion == -1) {
                turnoExtincion = turno;
            }

            if (ecosistemaService.estaExtinto(e)) {
                SimLogger.log("Extincion alcanzada en el turno " + turno);
                break;
            }

            try {
                Thread.sleep(Constantes.DELAY_TURNO_MS);
            } catch (InterruptedException ignored) { }
        }

        Turno ultimo = ecosistemaService.calcularTurno(0, e);
        reporteFinal.setTotalTurnos(config.getMaxTurnos());
        reporteFinal.setPresasFinales(ultimo.getPresas());
        reporteFinal.setDepredadoresFinales(ultimo.getDepredadores());
        reporteFinal.setTerceraEspecieFinal(ultimo.getTerceraEspecie());
        reporteFinal.setTurnoExtincion(turnoExtincion);
        int totalCeldas = Constantes.MATRIZ_FILAS * Constantes.MATRIZ_COLUMNAS;
        reporteFinal.setPorcentajeOcupacionFinal(
                totalCeldas == 0 ? 0 : (ultimo.getCeldasOcupadas() * 100.0 / totalCeldas));
        SimLogger.log("Simulacion finalizada. Presas finales=" + ultimo.getPresas()
                + ", Depredadores finales=" + ultimo.getDepredadores()
                + ", Tercera especie final=" + ultimo.getTerceraEspecie());
        return reporteFinal;
    }

    public void setLogCallback(java.util.function.Consumer<String> cb) {
        this.logCallback = cb;
    }
}
