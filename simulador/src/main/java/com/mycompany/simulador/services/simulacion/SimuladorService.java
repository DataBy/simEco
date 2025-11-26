package com.mycompany.simulador.services.simulacion;

import com.mycompany.simulador.config.Constantes;
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

public class SimuladorService implements ISimulador {

    private final EcosistemaService ecosistemaService = new EcosistemaService();
    private final IMovimientosStrategy movimientosStrategy = new MovimientosService();
    private final IAlimentacionStrategy alimentacionStrategy = new AlimentacionService();
    private final IReproduccionStrategy reproduccionStrategy = new ReproduccionService();
    private final IGeneticaService geneticaService = new MutacionService();
    private final IEcosistemaRepository ecosistemaRepository;
    private final IEstadoTurnosRepository estadoTurnosRepository;

    public SimuladorService(IEcosistemaRepository ecoRepo,
                            IEstadoTurnosRepository estadoRepo) {
        this.ecosistemaRepository = ecoRepo;
        this.estadoTurnosRepository = estadoRepo;
    }

    @Override
    public ReporteFinal ejecutarSimulacion(SimulacionConfigDTO config,
                                           SimulacionListener listener) {

        ecosistemaRepository.guardarConfiguracion(config);

        Ecosistema e = ecosistemaService.crearEcosistema(config);
        ReporteFinal reporteFinal = new ReporteFinal();
        int turnoExtincion = -1;

        for (int turno = 1; turno <= config.getMaxTurnos(); turno++) {
            movimientosStrategy.moverEspecies(e);
            alimentacionStrategy.procesarAlimentacion(e);
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
        reporteFinal.setTotalTurnos(config.getMaxTurnos());
        reporteFinal.setPresasFinales(ultimo.getPresas());
        reporteFinal.setDepredadoresFinales(ultimo.getDepredadores());
        reporteFinal.setTerceraEspecieFinal(ultimo.getTerceraEspecie());
        reporteFinal.setTurnoExtincion(turnoExtincion);
        int totalCeldas = Constantes.MATRIZ_FILAS * Constantes.MATRIZ_COLUMNAS;
        reporteFinal.setPorcentajeOcupacionFinal(
                totalCeldas == 0 ? 0 : (ultimo.getCeldasOcupadas() * 100.0 / totalCeldas));
        return reporteFinal;
    }
}
