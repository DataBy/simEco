package com.mycompany.simulador.controller;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.simulador.config.ConfigSimulacion;
import com.mycompany.simulador.config.Constantes;
import com.mycompany.simulador.config.RutasArchivos;
import com.mycompany.simulador.dto.SimulacionConfigDTO;
import com.mycompany.simulador.interfaces.ISimulador;
import com.mycompany.simulador.model.report.ReporteFinal;
import com.mycompany.simulador.repository.EcossitemaRepositoryTXT;
import com.mycompany.simulador.repository.EstadoTurnosRepositoryTXT;
import com.mycompany.simulador.services.simulacion.SimuladorService;
import com.mycompany.simulador.view.ResumenView;
import com.mycompany.simulador.view.SimulacionView;
import com.mycompany.simulador.view.EscenarioSnapshot;
import com.mycompany.simulador.view.EscenariosGaleriaView;

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
        ((SimuladorService) this.simulador).setLogCallback(msg ->
                Platform.runLater(() -> view.log(msg)));
        init();
    }

    private void init() {
        // Solo esto: cuando den clic en "INICIAR SIMULACIÓN" se corre todo
        view.setOnIniciar(this::iniciarSimulacion);
        view.setOnEscenarioAleatorio(() -> Platform.runLater(() -> {
            view.setElementosUnicos(RutasArchivos.ICON_ELEMENTO_PASTO_AMARILLO);
            view.mostrarEscenarioAleatorio();
        }));
        view.setOnMutacion(() -> Platform.runLater(view::agregarMutacion));
        view.setOnTerceraEspecie(() -> Platform.runLater(view::agregarTerceraEspecieMixta));
        view.setOnEscenarioPersonalizado(() -> Platform.runLater(view::mostrarEditorPersonalizado));
        view.setOnInicio(() -> Platform.runLater(() -> new MenuInicioController(stage)));
        view.setOnSalir(Platform::exit);
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

    private List<String> definirOrdenPersonalizada(String[][] elementos) {
        if (elementos == null) {
            return List.of("VERANO", "PRIMAVERA", "INVIERNO");
        }
        int ver = 0, pri = 0, inv = 0;
        for (int i = 0; i < elementos.length; i++) {
            for (int j = 0; j < elementos[i].length; j++) {
                String val = elementos[i][j];
                if (val == null) continue;
                switch (val.toUpperCase()) {
                    case "VERANO" -> ver++;
                    case "PRIMAVERA" -> pri++;
                    case "INVIERNO" -> inv++;
                }
            }
        }
        int max = Math.max(ver, Math.max(pri, inv));
        if (max == 0) {
            return List.of("VERANO", "PRIMAVERA", "INVIERNO");
        }
        if (ver >= pri && ver >= inv) {
            return List.of("VERANO", "PRIMAVERA", "INVIERNO");
        }
        if (inv >= ver && inv >= pri) {
            return List.of("INVIERNO", "VERANO", "PRIMAVERA");
        }
        return List.of("PRIMAVERA", "INVIERNO", "VERANO");
    }

    private String iconoElementoPorEstacion(String estacion) {
        if (estacion == null) return RutasArchivos.ICON_ELEMENTO_PASTO_AMARILLO;
        return switch (estacion.toUpperCase()) {
            case "PRIMAVERA" -> RutasArchivos.ICON_ELEMENTO_PASTO_VERDE;
            case "INVIERNO" -> RutasArchivos.ICON_ELEMENTO_LAGO;
            default -> RutasArchivos.ICON_ELEMENTO_PASTO_AMARILLO;
        };
    }

    private void iniciarSimulacion() {
        SimulacionConfigDTO base = crearConfig();

        boolean usarPersonalizado = view.esPersonalizadoActivo() && view.getMatrizPersonalizada() != null;
        char[][] matrizPersonalizada = usarPersonalizado ? view.getMatrizPersonalizada() : null;
        String[][] elementosPersonalizados = usarPersonalizado ? view.getElementosPersonalizados() : null;

        if (usarPersonalizado) {
            Platform.runLater(view::mostrarSimulacionPrincipal);
        }

        List<String> orden = usarPersonalizado
                ? definirOrdenPersonalizada(elementosPersonalizados)
                : List.of("VERANO", "PRIMAVERA", "INVIERNO");

        Thread hilo = new Thread(() -> {
            List<ReporteFinal> resultados = new ArrayList<>();
            List<EscenarioSnapshot> snapshots = new ArrayList<>();

            for (String estacion : orden) {
                String iconoElemento = iconoElementoPorEstacion(estacion);
                ejecutarEscenario(estacion, base.getMaxTurnos(),
                        iconoElemento, resultados, snapshots, matrizPersonalizada);
            }

            Platform.runLater(() -> mostrarGaleria(snapshots, resultados));
        });

        hilo.setDaemon(true);
        hilo.start();
    }

    private void mostrarReportes(List<ReporteFinal> reportes) {
        ResumenView rView = new ResumenView();
        Scene scene = new Scene(rView.getRoot(), stage.getWidth(), stage.getHeight());
        stage.setScene(scene);

        rView.setOnInicio(() -> Platform.runLater(() -> new MenuInicioController(stage)));

        int totalTurnos = reportes.stream().mapToInt(ReporteFinal::getTotalTurnos).sum();
        int presasFinales = reportes.stream().mapToInt(ReporteFinal::getPresasFinales).sum();
        int depredadoresFinales = reportes.stream().mapToInt(ReporteFinal::getDepredadoresFinales).sum();
        int tercerasFinales = reportes.stream().mapToInt(ReporteFinal::getTerceraEspecieFinal).sum();
        int turnoExtincion = reportes.stream()
                .mapToInt(ReporteFinal::getTurnoExtincion)
                .filter(v -> v >= 0)
                .min()
                .orElse(-1);
        double ocupacionProm = reportes.stream()
                .mapToDouble(ReporteFinal::getPorcentajeOcupacionFinal)
                .average()
                .orElse(0);
        int totalCeldas = Constantes.MATRIZ_FILAS * Constantes.MATRIZ_COLUMNAS;
        int ocupadas = (int) Math.round(ocupacionProm * totalCeldas / 100.0);
        int vacias = Math.max(0, totalCeldas - ocupadas);

        rView.actualizarDatos(
                totalTurnos,
                presasFinales,
                depredadoresFinales,
                tercerasFinales,
                turnoExtincion,
                ocupadas, vacias
        );
        rView.actualizarComparativo(reportes);
    }

    private void ejecutarEscenario(String nombre, int turnos, String elemento,
                                   List<ReporteFinal> resultados,
                                   List<EscenarioSnapshot> snapshots,
                                   char[][] matrizPersonalizada) {
        SimulacionConfigDTO cfg = crearConfig();
        cfg.setEscenario(nombre);
        cfg.setMaxTurnos(turnos);
        if (matrizPersonalizada != null) {
            cfg.setMatrizPersonalizada(clonar(matrizPersonalizada));
        }
        // Fijar fondo sin mezclar ni reiniciar especies
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        Platform.runLater(() -> {
            view.setElementosUnicosSinReset(elemento);
            view.prepararTiempoSabanero(nombre);
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException ignored) { }

        ReporteFinal r = simulador.ejecutarSimulacion(
                cfg,
                new ISimulador.SimulacionListener() {
                    @Override
                    public void onTurnoActualizado(int turnoActual, char[][] matrizSimbolos) {
                        Platform.runLater(() -> {
                            view.actualizarTiempoSabanero(turnoActual, nombre);
                            view.actualizarMatriz(matrizSimbolos, false);
                            if (turnoActual == 1) {
                                guardarSnapshot(view.crearSnapshot(nombre), snapshots);
                            }
                        });
                    }

                    @Override
                    public void onMovimiento(int turnoActual,
                                             com.mycompany.simulador.model.ecosystem.Coordenada origen,
                                             com.mycompany.simulador.model.ecosystem.Coordenada destino,
                                             boolean comio,
                                             boolean esDepredador,
                                             char[][] matrizPaso) {
                        Platform.runLater(() -> view.mostrarMovimiento(origen, destino, esDepredador, comio, matrizPaso));
                    }

                    @Override
                    public void onEventos(int turnoActual, java.util.List<com.mycompany.simulador.services.simulacion.TurnoEvento> eventos) {
                        Platform.runLater(() -> view.mostrarEventos(eventos));
                    }
                }
        );
        resultados.add(r);
    }

    private void mostrarGaleria(List<EscenarioSnapshot> snaps, List<ReporteFinal> reportes) {
        EscenariosGaleriaView gView = new EscenariosGaleriaView();
        gView.setEscenarios(snaps);
        Scene scene = new Scene(gView.getRoot(), stage.getWidth(), stage.getHeight());
        stage.setScene(scene);
        gView.setOnSiguiente(() -> Platform.runLater(() -> mostrarReportes(reportes)));
    }

    private void guardarSnapshot(EscenarioSnapshot snapshot, List<EscenarioSnapshot> snaps) {
        if (snapshot == null || snaps == null) return;
        boolean existe = snaps.stream().anyMatch(s -> s.getNombre().equalsIgnoreCase(snapshot.getNombre()));
        if (existe) return;
        snaps.add(snapshot);
    }

    private char[][] clonar(char[][] src) {
        if (src == null) return null;
        char[][] copia = new char[src.length][];
        for (int i = 0; i < src.length; i++) {
            copia[i] = new char[src[i].length];
            System.arraycopy(src[i], 0, copia[i], 0, src[i].length);
        }
        return copia;
    }
}
