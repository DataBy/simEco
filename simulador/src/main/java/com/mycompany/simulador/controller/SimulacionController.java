package com.mycompany.simulador.controller;

import com.mycompany.simulador.config.ConfigSimulacion;
import com.mycompany.simulador.config.Constantes;
import com.mycompany.simulador.dto.SimulacionConfigDTO;
import com.mycompany.simulador.interfaces.ISimulador;
import com.mycompany.simulador.model.report.ReporteFinal;
import com.mycompany.simulador.repository.EcossitemaRepositoryTXT;
import com.mycompany.simulador.repository.EstadoTurnosRepositoryTXT;
import com.mycompany.simulador.services.simulacion.SimuladorService;
import com.mycompany.simulador.view.ReportesView;
import com.mycompany.simulador.view.SimulacionView;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SimulacionController {

    private final Stage stage;
    private final SimulacionView view;
    private final ISimulador simulador;
    private final String correoUsuario;

    public SimulacionController(Stage stage, String correoUsuario) {
        this.stage = stage;
        this.correoUsuario = correoUsuario;
        this.view = new SimulacionView();
        this.simulador = new SimuladorService(
                new EcossitemaRepositoryTXT(),
                new EstadoTurnosRepositoryTXT()
        );

        Scene scene = new Scene(view.getRoot(), stage.getWidth(), stage.getHeight());
        stage.setScene(scene);
        init();
    }

    private void init() {
        // Solo esto: cuando den clic en "INICIAR SIMULACIÓN" se corre todo
        view.setOnIniciar(this::iniciarSimulacion);
    }

    /**
     * Crea el DTO de configuración usando la NUEVA dificultad del view.
     * FACIL  -> ESCENARIO_PRESAS_DOM
     * MEDIO  -> ESCENARIO_EQUILIBRADO
     * DIFICIL-> ESCENARIO_DEPREDADORES_DOM
     */
    private SimulacionConfigDTO crearConfig() {
        SimulacionConfigDTO dto = new SimulacionConfigDTO();

        // 1) Leemos dificultad del cuadro glassy
        String dificultad = view.getDificultad();

        // 2) La mapeamos a tus escenarios originales
        String esc;
        switch (dificultad) {
            case "FACIL" -> esc = Constantes.ESCENARIO_PRESAS_DOM;
            case "DIFICIL" -> esc = Constantes.ESCENARIO_DEPREDADORES_DOM;
            default -> esc = Constantes.ESCENARIO_EQUILIBRADO; // MEDIO
        }

        dto.setEscenario(esc);
        dto.setMaxTurnos(view.getMaxTurnos());

        // 3) Misma lógica de siempre, según el escenario
        switch (esc) {
            case Constantes.ESCENARIO_EQUILIBRADO -> {
                dto.setPresasIniciales(ConfigSimulacion.EQUILIBRADO_PRESAS);
                dto.setDepredadoresIniciales(ConfigSimulacion.EQUILIBRADO_DEPREDADORES);
                dto.setTerceraEspecieInicial(ConfigSimulacion.EQUILIBRADO_TERCERA);
            }
            case Constantes.ESCENARIO_DEPREDADORES_DOM -> {
                dto.setPresasIniciales(ConfigSimulacion.DEPREDADORES_DOM_PRESAS);
                dto.setDepredadoresIniciales(ConfigSimulacion.DEPREDADORES_DOM_DEPREDADORES);
                dto.setTerceraEspecieInicial(ConfigSimulacion.DEPREDADORES_DOM_TERCERA);
            }
            case Constantes.ESCENARIO_PRESAS_DOM -> {
                dto.setPresasIniciales(ConfigSimulacion.PRESAS_DOM_PRESAS);
                dto.setDepredadoresIniciales(ConfigSimulacion.PRESAS_DOM_DEPREDADORES);
                dto.setTerceraEspecieInicial(ConfigSimulacion.PRESAS_DOM_TERCERA);
            }
        }
        return dto;
    }

    private void iniciarSimulacion() {
        SimulacionConfigDTO config = crearConfig();

        Thread hilo = new Thread(() -> {
            ReporteFinal reporte = simulador.ejecutarSimulacion(
                    config,
                    (turno, matriz) -> Platform.runLater(() -> view.actualizarMatriz(matriz))
            );

            Platform.runLater(() -> mostrarReportes(reporte));
        });

        hilo.setDaemon(true);
        hilo.start();
    }

    private void mostrarReportes(ReporteFinal reporte) {
        ReportesView rView = new ReportesView();
        Scene scene = new Scene(rView.getRoot(), stage.getWidth(), stage.getHeight());
        stage.setScene(scene);

        int totalCeldas = Constantes.MATRIZ_FILAS * Constantes.MATRIZ_COLUMNAS;
        int ocupadas = (int) Math.round(
                reporte.getPorcentajeOcupacionFinal() * totalCeldas / 100.0);
        int vacias = totalCeldas - ocupadas;

        rView.actualizarDatos(
                reporte.getTotalTurnos(),
                reporte.getPresasFinales(),
                reporte.getDepredadoresFinales(),
                reporte.getTurnoExtincion(),
                ocupadas, vacias
        );

        new ReportesController(stage, rView, reporte, correoUsuario, totalCeldas);
    }
}
