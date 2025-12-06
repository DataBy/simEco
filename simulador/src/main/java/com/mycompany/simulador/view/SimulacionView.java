package com.mycompany.simulador.view;

import com.mycompany.simulador.config.Constantes;
import com.mycompany.simulador.config.RutasArchivos;
import com.mycompany.simulador.utils.IconosUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen; // <-- NUEVO IMPORT
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import com.mycompany.simulador.utils.AleatorioUtils;
import com.mycompany.simulador.model.ecosystem.Coordenada;
import com.mycompany.simulador.services.simulacion.TurnoEvento;

public class SimulacionView {

    private final BorderPane root = new BorderPane();

    // Selección de dificultad
    private final ToggleGroup grupoDificultad = new ToggleGroup();
    private final RadioButton rbFacil   = new RadioButton("Fácil (Presas 70%)");
    private final RadioButton rbMedio   = new RadioButton("Medio (50/50)");
    private final RadioButton rbDificil = new RadioButton("Difícil (Depredadores 70%)");

    // Spinner turnos (valor DEFAULT = 5)
    private final Spinner<Integer> spMaxTurnos =
            new Spinner<>(1, 500, 5, 1);

    // Botones principales
    private final Button btnIniciar   = new Button("INICIAR SIMULACIÓN");
    private final Button btnInicio    = new Button("Inicio");
    private final Button btnSalir     = new Button("Salir");
    private final Button btnTercera   = new Button("Añadir Tercera Especie");
    private final Button btnMutacion  = new Button("Añadir Mutación");
    private final Button btnAleatorio = new Button("Escenario Aleatorio");
    private final Button btnPersonal  = new Button("Escenario Personalizado");

    // Historial
    private final TextArea txtHistorial = new TextArea();
    private final Label lblTiempoTurno = new Label("Turno 0");
    private final Label lblTiempoDia = new Label("Dia 0");
    private final Label lblTiempoEstacion = new Label("Estacion: --");

    // Celdas de la matriz
    private final ImageView[][] matrizCeldas =
            new ImageView[Constantes.MATRIZ_FILAS][Constantes.MATRIZ_COLUMNAS];
    private final StackPane[][] cellWrappers =
            new StackPane[Constantes.MATRIZ_FILAS][Constantes.MATRIZ_COLUMNAS];

    // Tamaño dinámico de cada celda del grid
    private final DoubleProperty cellSize =
            new SimpleDoubleProperty(Constantes.MATRIZ_TAM_CELDA);
    private final String[][] iconCache =
            new String[Constantes.MATRIZ_FILAS][Constantes.MATRIZ_COLUMNAS];
    private final char[][] simboloCache =
            new char[Constantes.MATRIZ_FILAS][Constantes.MATRIZ_COLUMNAS];

    private final List<String> iconosPresas = List.of(
            RutasArchivos.ICON_PRESA,
            RutasArchivos.ICON_PRESA_GACELA,
            RutasArchivos.ICON_PRESA_EXTRA
    );
    private final List<String> iconosDepredadores = List.of(
            RutasArchivos.ICON_DEPREDADOR,
            RutasArchivos.ICON_DEPREDADOR_HIENA,
            RutasArchivos.ICON_DEPREDADOR_TIGRE
    );
    private final List<String> iconosTercera = List.of(
            RutasArchivos.ICON_TERCERA_ESPECIE,
            RutasArchivos.ICON_TERCERA_ESPECIE_BUFALO
    );
    private final List<String> iconosMutacion = List.of(RutasArchivos.ICON_MUTACION);
    private List<String> iconosElementos = new ArrayList<>(List.of(
            RutasArchivos.ICON_ELEMENTO_ARBUSTO,
            RutasArchivos.ICON_ELEMENTO_CARRO,
            RutasArchivos.ICON_ELEMENTO_LAGO,
            RutasArchivos.ICON_ELEMENTO_PASTO_AMARILLO,
            RutasArchivos.ICON_ELEMENTO_PASTO_VERDE,
            RutasArchivos.ICON_ELEMENTO_ROCA,
            RutasArchivos.ICON_ELEMENTO_TRONCO
    ));

    private char[][] matrizBase;
    private final List<int[]> mutaciones = new ArrayList<>();
    private final Set<String> tercerasBufalo = new HashSet<>();
    private boolean siguienteBufalo = true;

    public SimulacionView() {
        construirUI();
        inicializarEstadoInteractivo();
        prepararTiempoSabanero("--");
    }

    private void construirUI() {

        // Fondo base global
        root.setStyle("-fx-background-color: #A1D164;");

        // ======================================================
        // PANEL IZQUIERDO (fondo + menú glassy)
        // ======================================================
        StackPane panelIzq = new StackPane();
        panelIzq.setMinWidth(260);
        panelIzq.setPrefWidth(320);
        panelIzq.setMaxWidth(380);

        // Mantiene tu PNG de fondo original
        ImageView fondoIzq = IconosUtils.crearImageViewFondo(
                RutasArchivos.SIM_MENU_IZQ_BACKGROUND,
                panelIzq
        );

        VBox menuIzq = new VBox(18);
        menuIzq.setPadding(new Insets(100, 24, 40, 24));
        menuIzq.setAlignment(Pos.TOP_CENTER);

        // --------- Botones glassy (sin PNG) ----------
        configurarBotonMenu(btnAleatorio);
        configurarBotonMenu(btnPersonal);
        configurarBotonMenu(btnInicio);
        configurarBotonMenu(btnSalir);
        configurarBotonMenu(btnTercera);
        configurarBotonMenu(btnMutacion);
        configurarBotonMenuPrincipal(btnIniciar);

        // Para centrar verticalmente usando espaciadores
        Region topSpacer    = new Region();
        Region bottomSpacer = new Region();
        VBox.setVgrow(topSpacer, Priority.ALWAYS);
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);

        // ======================================================
        // PANEL GLASSY DIFICULTAD + TURNOS
        // ======================================================
        VBox panelDificultad = crearPanelGlassy(260, 0);

        Label lblTituloDif = new Label("Elige un Escenario:");
        lblTituloDif.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #235217;");

        rbFacil.setToggleGroup(grupoDificultad);
        rbMedio.setToggleGroup(grupoDificultad);
        rbDificil.setToggleGroup(grupoDificultad);
        rbMedio.setSelected(true);

        rbFacil.setStyle("-fx-text-fill: #235217;");
        rbMedio.setStyle("-fx-text-fill: #235217;");
        rbDificil.setStyle("-fx-text-fill: #235217;");

        VBox turnosCentro = new VBox(6);
        turnosCentro.setAlignment(Pos.CENTER);
        Label lblTurnos = new Label("Turnos:");
        lblTurnos.setStyle("-fx-text-fill: #235217; -fx-font-weight: bold;");
        spMaxTurnos.setMaxWidth(120);

        turnosCentro.getChildren().addAll(lblTurnos, spMaxTurnos);

        panelDificultad.getChildren().addAll(
                lblTituloDif,
                rbFacil, rbMedio, rbDificil,
                turnosCentro
        );

        // Caja para el botón iniciar, debajo del glassy
        VBox iniciarBox = new VBox(btnIniciar);
        iniciarBox.setAlignment(Pos.CENTER);
        iniciarBox.setPadding(new Insets(10, 0, 0, 0));

        // Orden final en el menú izquierdo
        menuIzq.getChildren().addAll(
                topSpacer,
                btnAleatorio,
                btnMutacion,
                btnInicio,
                btnSalir,
                btnTercera,
                btnPersonal,
                panelDificultad,
                iniciarBox,
                bottomSpacer
        );

        panelIzq.getChildren().addAll(fondoIzq, menuIzq);

        // ======================================================
        // PANEL DERECHO: HISTORIAL RESPONSIVE
        // ======================================================
        StackPane panelDer = new StackPane();
        panelDer.setMinWidth(260);
        panelDer.setPrefWidth(320);
        panelDer.setMaxWidth(380);

        // Mantiene tu PNG de fondo original
        ImageView fondoDer = IconosUtils.crearImageViewFondo(
                RutasArchivos.SIM_MENU_DER_BACKGROUND,
                panelDer
        );

        VBox contDer = new VBox(15);
        contDer.setPadding(new Insets(100, 24, 40, 24));
        contDer.setAlignment(Pos.TOP_CENTER);
        contDer.setFillWidth(true);

        Label lblHist = new Label("Historial de la Simulación");
        lblHist.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #235217;");

        // Panel glassy que crecerá con el alto disponible
        VBox panelHistorial = crearPanelGlassy(280, 0);
        VBox.setVgrow(panelHistorial, Priority.ALWAYS);

        txtHistorial.setWrapText(true);
        txtHistorial.setEditable(false);
        txtHistorial.setStyle("-fx-background-color: transparent;");
        txtHistorial.setPrefRowCount(15);
        txtHistorial.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        txtHistorial.setPrefHeight(480); // ajusta el contenido

        
        VBox.setVgrow(panelHistorial, Priority.NEVER);

        panelHistorial.setPrefHeight(500);
        panelHistorial.getChildren().add(txtHistorial);
        panelHistorial.setFillWidth(true);

        HBox tiempoSabanero = crearPanelTiempoSabanero();
        tiempoSabanero.setAlignment(Pos.CENTER_RIGHT);
        tiempoSabanero.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(tiempoSabanero, new Insets(8, 0, 0, 0));

        contDer.getChildren().addAll(lblHist, panelHistorial, tiempoSabanero);
        panelDer.getChildren().addAll(fondoDer, contDer);

        // ======================================================
        // PANEL CENTRAL: MATRIZ RESPONSIVE (SIN FONDO CAFÉ)
        // ======================================================
        StackPane panelCentro = new StackPane();
        panelCentro.setPadding(new Insets(10));
        panelCentro.setStyle("-fx-background-color: transparent;");

        GridPane grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);
        grid.setAlignment(Pos.CENTER);

        // Celdas de la matriz
        for (int i = 0; i < Constantes.MATRIZ_FILAS; i++) {
            for (int j = 0; j < Constantes.MATRIZ_COLUMNAS; j++) {
                ImageView iv = new ImageView(
                        IconosUtils.cargarImagen(RutasArchivos.ICON_CELDA_VACIA)
                );
                iv.setPreserveRatio(false);
                iv.setSmooth(true); // <-- SUAVIZADO PARA ESCALADO
                iv.fitWidthProperty().bind(cellSize);
                iv.fitHeightProperty().bind(cellSize);

                StackPane wrapper = new StackPane(iv);
                wrapper.setStyle("-fx-border-color: transparent; -fx-border-width: 0; -fx-border-radius: 6; -fx-background-radius: 6;");

                cellWrappers[i][j] = wrapper;
                matrizCeldas[i][j] = iv;
                grid.add(wrapper, j, i);
            }
        }

        panelCentro.getChildren().add(grid);

        // Recalcular tamaño de celda cuando cambie el tamaño disponible
        panelCentro.widthProperty().addListener((obs, oldV, newV) ->
                ajustarTamanoCeldas(panelCentro, grid));
        panelCentro.heightProperty().addListener((obs, oldV, newV) ->
                ajustarTamanoCeldas(panelCentro, grid));

        // Llamada inicial
        panelCentro.layoutBoundsProperty().addListener((obs, oldB, newB) ->
                ajustarTamanoCeldas(panelCentro, grid));

        // ======================================================
        // ARMADO FINAL BORDERPANE
        // ======================================================
        root.setLeft(panelIzq);
        root.setCenter(panelCentro);
        root.setRight(panelDer);
    }

    private void inicializarEstadoInteractivo() {
        matrizBase = matrizVacia();
        pintarMatriz(matrizBase);

        rbFacil.setOnAction(e -> aplicarDistribucionSeleccion());
        rbMedio.setOnAction(e -> aplicarDistribucionSeleccion());
        rbDificil.setOnAction(e -> aplicarDistribucionSeleccion());
    }

    // ----------------------------------------------------------
    // Estilos para botones glassy
    // ----------------------------------------------------------
    private void configurarBotonMenu(Button b) {
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER);
        b.setStyle("""
            -fx-background-color: rgba(255,255,255,0.35);
            -fx-background-radius: 24;
            -fx-border-radius: 24;
            -fx-border-color: rgba(255,255,255,0.75);
            -fx-border-width: 1.5;
            -fx-text-fill: #235217;
            -fx-font-size: 16px;
            -fx-font-weight: bold;
            -fx-padding: 8 16 8 16;
        """);

        // Hover simple
        b.setOnMouseEntered(e -> b.setStyle("""
            -fx-background-color: rgba(255,255,255,0.55);
            -fx-background-radius: 24;
            -fx-border-radius: 24;
            -fx-border-color: rgba(255,255,255,0.9);
            -fx-border-width: 1.5;
            -fx-text-fill: #235217;
            -fx-font-size: 16px;
            -fx-font-weight: bold;
            -fx-padding: 8 16 8 16;
        """));
        b.setOnMouseExited(e -> configurarBotonMenu(b)); // vuelve al estilo base
    }

    private void configurarBotonMenuPrincipal(Button b) {
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER);
        b.setStyle("""
            -fx-background-color: rgba(255,255,255,0.55);
            -fx-background-radius: 28;
            -fx-border-radius: 28;
            -fx-border-color: rgba(255,255,255,0.95);
            -fx-border-width: 2;
            -fx-text-fill: #235217;
            -fx-font-size: 18px;
            -fx-font-weight: bold;
            -fx-padding: 10 24 10 24;
        """);
    }

    // ----------------------------------------------------------
    // Panel glassy reutilizable (sin fijar altura)
    // ----------------------------------------------------------
    private VBox crearPanelGlassy(double w, double hNoUsado) {
        VBox v = new VBox(10);
        v.setMinWidth(w);
        v.setPrefWidth(w);
        v.setMaxWidth(Double.MAX_VALUE);
        v.setAlignment(Pos.TOP_CENTER);

        v.setStyle("""
            -fx-background-color: rgba(255,255,255,0.25);
            -fx-background-radius: 18;
            -fx-border-radius: 18;
            -fx-border-color: rgba(255,255,255,0.4);
            -fx-border-width: 2;
        """);

        v.setPadding(new Insets(12));

        DropShadow sombra = new DropShadow();
        sombra.setColor(Color.rgb(0, 0, 0, 0.15));
        sombra.setRadius(12);
        v.setEffect(sombra);

        return v;
    }

    // ----------------------------------------------------------
    // Panel "Tiempo Sabanero"
    // ----------------------------------------------------------
    private HBox crearPanelTiempoSabanero() {
        HBox cont = new HBox(8);
        cont.setAlignment(Pos.CENTER_LEFT);
        cont.setMouseTransparent(true);
        cont.setPickOnBounds(false);
        cont.setFillHeight(false);

        VBox tarjeta = new VBox(6);
        tarjeta.setAlignment(Pos.TOP_LEFT);
        tarjeta.setPadding(new Insets(10, 12, 12, 12));
        tarjeta.setMinWidth(200);
        tarjeta.setMaxWidth(240);
        tarjeta.setStyle("""
            -fx-background-color: rgba(255,255,255,0.24);
            -fx-background-radius: 16;
            -fx-border-radius: 16;
            -fx-border-color: rgba(255,255,255,0.55);
            -fx-border-width: 1.2;
        """);

        DropShadow sombra = new DropShadow();
        sombra.setColor(Color.rgb(0, 0, 0, 0.14));
        sombra.setRadius(10);
        sombra.setOffsetY(2);
        tarjeta.setEffect(sombra);

        Label titulo = new Label("Tiempo Sabanero");
        titulo.setStyle("-fx-text-fill: #1e4c28; -fx-font-size: 17px; -fx-font-weight: bold;");

        Label lblTurnoTitulo = new Label("Turno:");
        lblTurnoTitulo.setStyle("-fx-text-fill: #1e4c28; -fx-font-size: 12px; -fx-font-weight: bold;");
        lblTiempoTurno.setStyle("-fx-text-fill: #235217; -fx-font-size: 15px; -fx-font-weight: bold;");
        HBox filaTurno = new HBox(6, lblTurnoTitulo, lblTiempoTurno);
        filaTurno.setAlignment(Pos.CENTER_LEFT);

        Label lblDiaTitulo = new Label("Dia:");
        lblDiaTitulo.setStyle("-fx-text-fill: #1e4c28; -fx-font-size: 12px; -fx-font-weight: bold;");
        lblTiempoDia.setStyle("-fx-text-fill: #235217; -fx-font-size: 15px; -fx-font-weight: bold;");
        HBox filaDia = new HBox(6, lblDiaTitulo, lblTiempoDia);
        filaDia.setAlignment(Pos.CENTER_LEFT);

        lblTiempoEstacion.setStyle("""
            -fx-text-fill: #1e4c28;
            -fx-font-size: 13px;
            -fx-font-weight: bold;
            -fx-background-color: rgba(255,255,255,0.32);
            -fx-background-radius: 10;
            -fx-padding: 5 10 5 10;
        """);

        tarjeta.getChildren().addAll(titulo, filaTurno, filaDia, lblTiempoEstacion);

        ImageView arbol = new ImageView(IconosUtils.cargarImagen(RutasArchivos.SIM_ARBOL_SABANA));
        if (arbol.getImage() != null) {
            arbol.setPreserveRatio(true);
            arbol.setFitHeight(90);
            arbol.setOpacity(0.88);
        } else {
            arbol.setVisible(false);
        }

        cont.getChildren().addAll(tarjeta, arbol);
        return cont;
    }

    public void prepararTiempoSabanero(String estacion) {
        String est = formatearEstacion(estacion);
        lblTiempoTurno.setText("Turno 0");
        lblTiempoDia.setText("Dia 0");
        lblTiempoEstacion.setText("Estacion: " + est);
    }

    public void actualizarTiempoSabanero(int turno, String estacion) {
        String est = formatearEstacion(estacion);
        lblTiempoTurno.setText("Turno " + turno);
        lblTiempoDia.setText("Dia " + turno);
        lblTiempoEstacion.setText("Estacion: " + est);
    }

    private String formatearEstacion(String estacion) {
        if (estacion == null) return "--";
        String clean = estacion.trim();
        if (clean.isEmpty()) return "--";
        String upper = clean.toUpperCase();
        return switch (upper) {
            case "VERANO" -> "Verano";
            case "PRIMAVERA" -> "Primavera";
            case "INVIERNO" -> "Invierno";
            default -> (clean.length() == 1)
                    ? clean.toUpperCase()
                    : clean.substring(0, 1).toUpperCase() + clean.substring(1).toLowerCase();
        };
    }

    // ----------------------------------------------------------
    // Lógica de tamaño RESPONSIVE de las celdas
    // ----------------------------------------------------------
    private void ajustarTamanoCeldas(StackPane panelCentro, GridPane grid) {
        double anchoDisponible = panelCentro.getWidth() - grid.getInsets().getLeft() - grid.getInsets().getRight();
        double altoDisponible  = panelCentro.getHeight() - grid.getInsets().getTop() - grid.getInsets().getBottom();

        if (anchoDisponible <= 0 || altoDisponible <= 0) {
            return;
        }

        double sizeByWidth  = anchoDisponible / Constantes.MATRIZ_COLUMNAS;
        double sizeByHeight = altoDisponible / Constantes.MATRIZ_FILAS;

        double size = Math.min(sizeByWidth, sizeByHeight);

        // --- LÍMITE MÁXIMO ADAPTADO A PANTALLA ---
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        double maxDesktop = Constantes.MATRIZ_TAM_CELDA; // 102 px
        double maxLaptop  = 80;                          // más pequeño en pantallas bajas

        // Si la pantalla es "baja" (típico laptop), usamos un máximo menor
        double max = (screenHeight <= 900) ? maxLaptop : maxDesktop;

        if (size > max) size = max;
        if (size < 10)  size = 10;   // tamaño mínimo de seguridad

        cellSize.set(size);
    }

    // ----------------------------------------------------------
    // Métodos para controlador
    // ----------------------------------------------------------
    public Parent getRoot() { return root; }

    // Regresa: FACIL | MEDIO | DIFICIL
    public String getDificultad() {
        if (rbFacil.isSelected())   return "FACIL";
        if (rbDificil.isSelected()) return "DIFICIL";
        return "MEDIO";
    }

    public int getMaxTurnos() {
        return spMaxTurnos.getValue();
    }

    public void setOnIniciar(Runnable r) {
        btnIniciar.setOnAction(e -> r.run());
    }

    public void setOnSalir(Runnable r) {
        btnSalir.setOnAction(e -> r.run());
    }

    public void setOnInicio(Runnable r) {
        btnInicio.setOnAction(e -> r.run());
    }

    public void setOnEscenarioAleatorio(Runnable r) {
        btnAleatorio.setOnAction(e -> r.run());
    }

    public void setOnMutacion(Runnable r) {
        btnMutacion.setOnAction(e -> r.run());
    }

    public void setOnTerceraEspecie(Runnable r) {
        btnTercera.setOnAction(e -> r.run());
    }

    public void setElementosUnicos(String rutaElemento) {
        setElementosInterno(rutaElemento, true);
    }

    public void setElementosUnicosSinReset(String rutaElemento) {
        setElementosInterno(rutaElemento, false);
    }

    private void setElementosInterno(String rutaElemento, boolean reset) {
        this.iconosElementos = new ArrayList<>(List.of(rutaElemento));
        if (reset) {
            this.mutaciones.clear();
            this.tercerasBufalo.clear();
            this.matrizBase = matrizVacia();
        }
        resetCacheIconos();
        pintarMatriz(matrizBase != null ? matrizBase : matrizVacia());
    }

    public void log(String mensaje) {
        txtHistorial.appendText(mensaje + "\n");
    }

    public void actualizarMatriz(char[][] simbolos) {
        actualizarMatriz(simbolos, false);
    }

    public void actualizarMatriz(char[][] simbolos, boolean limpiarResaltado) {
        if (limpiarResaltado) {
            limpiarResaltado();
        }
        matrizBase = normalizar(simbolos);
        pintarMatriz(matrizBase);
    }

    public void mostrarMovimiento(Coordenada origen, Coordenada destino, boolean esDepredador, boolean comio, char[][] matriz) {
        if (origen != null && destino != null && matriz != null) {
            int of = origen.getFila();
            int oc = origen.getColumna();
            int df = destino.getFila();
            int dc = destino.getColumna();
            if (of >= 0 && oc >= 0 && of < Constantes.MATRIZ_FILAS && oc < Constantes.MATRIZ_COLUMNAS
                    && df >= 0 && dc >= 0 && df < Constantes.MATRIZ_FILAS && dc < Constantes.MATRIZ_COLUMNAS) {
                String iconAnterior = iconCache[of][oc];
                char simboloDestino = matriz[df][dc];
                if (iconAnterior != null) {
                    // Conserva el mismo asset de la especie al desplazarse.
                    iconCache[df][dc] = iconAnterior;
                    simboloCache[df][dc] = simboloDestino;
                    iconCache[of][oc] = null;
                    simboloCache[of][oc] = '\0';
                }
            }
        }
        if (matriz != null) {
            actualizarMatriz(matriz, false);
        }
        if (origen == null) {
            return;
        }
        limpiarResaltado();

        double duracionSegundos = Math.max(2.5, (Constantes.DELAY_PASO_MS / 1000.0) + 0.4);

        // Preparación: resalte previo para guiar al ojo antes de mover
        if (destino == null) {
            String colorPrep = esDepredador ? "#ff0000" : "#2196f3";
            resaltarTemporal(origen.getFila(), origen.getColumna(), colorPrep, duracionSegundos);
            return;
        }

        String color = esDepredador
                ? (comio ? "#ff0000" : "#ff6f61")
                : "#2196f3";
        resaltarTemporal(origen.getFila(), origen.getColumna(), color, duracionSegundos);
        resaltarTemporal(destino.getFila(), destino.getColumna(), color, duracionSegundos);
    }

    public void mostrarEventos(java.util.List<TurnoEvento> eventos) {
        if (eventos == null) return;
        for (TurnoEvento ev : eventos) {
            if (ev == null || ev.coordenada() == null) continue;
            int f = ev.coordenada().getFila();
            int c = ev.coordenada().getColumna();
            if (ev.tipo() == TurnoEvento.Tipo.NACIMIENTO_PRE) {
                copiarIconoPadre(ev);
                resaltarTemporal(f, c, "#2ecc71", 2.5);
            } else if (ev.tipo() == TurnoEvento.Tipo.NACIMIENTO) {
                resaltarTemporal(f, c, "#2ecc71", 2.5);
            } else if (ev.tipo() == TurnoEvento.Tipo.MUERTE) {
                resaltarTemporal(f, c, "#000000", 2.5);
            }
        }
    }

    private void copiarIconoPadre(TurnoEvento ev) {
        if (ev.origen() == null || ev.coordenada() == null) return;
        int of = ev.origen().getFila();
        int oc = ev.origen().getColumna();
        int df = ev.coordenada().getFila();
        int dc = ev.coordenada().getColumna();
        if (!dentroRango(of, oc) || !dentroRango(df, dc)) return;
        String iconPadre = iconCache[of][oc];
        if (iconPadre != null) {
            iconCache[df][dc] = iconPadre;
            simboloCache[df][dc] = simboloCache[of][oc];
        }
    }

    private boolean dentroRango(int f, int c) {
        return f >= 0 && c >= 0 && f < Constantes.MATRIZ_FILAS && c < Constantes.MATRIZ_COLUMNAS;
    }

    public void mostrarEscenarioAleatorio() {
        asegurarMatrizBase();
        char[][] m = matrizVacia();
        // conservar terceras existentes
        for (int i = 0; i < Constantes.MATRIZ_FILAS; i++) {
            for (int j = 0; j < Constantes.MATRIZ_COLUMNAS; j++) {
                if (matrizBase[i][j] == 'T') {
                    m[i][j] = 'T';
                }
            }
        }
        int total = Constantes.MATRIZ_FILAS * Constantes.MATRIZ_COLUMNAS;
        int terceras = contar('T');
        int vacias = (int) Math.round(total * 0.4);
        int slotsEspecies = Math.max(0, total - vacias - terceras);

        colocarSimbolos(m, 'P', slotsEspecies / 2);
        colocarSimbolos(m, 'D', slotsEspecies - slotsEspecies / 2);
        rellenarElementos(m);
        matrizBase = normalizar(m);
        pintarMatriz(matrizBase);
    }

    public void aplicarDistribucionSeleccion() {
        aplicarDistribucionDificultad(getDificultad());
    }

    public void agregarMutacion() {
        asegurarMatrizBase();
        int[] pos = elegirCeldaLibre();
        if (pos == null) {
            pos = new int[]{
                    ThreadLocalRandom.current().nextInt(Constantes.MATRIZ_FILAS),
                    ThreadLocalRandom.current().nextInt(Constantes.MATRIZ_COLUMNAS)
            };
        }
        mutaciones.add(pos);
        pintarMatriz(matrizBase);
    }

    public void agregarTerceraEspecieMixta() {
        asegurarMatrizBase();
        int[] pos = elegirCeldaLibre();
        if (pos == null) return;
        matrizBase[pos[0]][pos[1]] = 'T';
        String key = pos[0] + "-" + pos[1];
        if (siguienteBufalo) {
            tercerasBufalo.add(key);
        } else {
            tercerasBufalo.remove(key);
        }
        siguienteBufalo = !siguienteBufalo;
        pintarMatriz(matrizBase);
    }

    // ----------------------------------------------------------
    // Helpers de matriz
    // ----------------------------------------------------------
    private void aplicarDistribucionDificultad(String dificultad) {
        int total = Constantes.MATRIZ_FILAS * Constantes.MATRIZ_COLUMNAS;
        int terceras = contar('T');
        int vacias = (int) Math.round(total * 0.4);
        int slotsEspecies = Math.max(0, total - vacias - terceras);

        int presas;
        int depredadores;
        if ("FACIL".equalsIgnoreCase(dificultad)) {
            presas = (int) Math.round(slotsEspecies * 0.7);
        } else if ("DIFICIL".equalsIgnoreCase(dificultad)) {
            presas = (int) Math.round(slotsEspecies * 0.3);
        } else {
            presas = (int) Math.round(slotsEspecies * 0.5);
        }
        depredadores = Math.max(0, slotsEspecies - presas);

        char[][] m = matrizVacia();
        // conservar terceras
        for (int i = 0; i < Constantes.MATRIZ_FILAS; i++) {
            for (int j = 0; j < Constantes.MATRIZ_COLUMNAS; j++) {
                if (matrizBase != null && matrizBase[i][j] == 'T') {
                    m[i][j] = 'T';
                }
            }
        }
        colocarSimbolos(m, 'P', presas);
        colocarSimbolos(m, 'D', depredadores);
        rellenarElementos(m);
        matrizBase = normalizar(m);
        pintarMatriz(matrizBase);
    }

    private void colocarSimbolos(char[][] m, char simbolo, int cantidad) {
        int intentos = 0;
        int colocados = 0;
        int maxIntentos = cantidad * 10 + 50;
        while (colocados < cantidad && intentos < maxIntentos) {
            intentos++;
            int f = AleatorioUtils.enteroEnRango(0, Constantes.MATRIZ_FILAS - 1);
            int c = AleatorioUtils.enteroEnRango(0, Constantes.MATRIZ_COLUMNAS - 1);
            if ((m[f][c] == 'E' || m[f][c] == '.') && !esMutacion(f, c)) {
                m[f][c] = simbolo;
                colocados++;
            }
        }
    }

    private void moverMutacionesLibres() {
        // Mutaciones estáticas: no se mueven solas entre ticks
    }

    private void pintarMatriz(char[][] base) {
        if (base == null) return;
        char[][] render = normalizar(base);
        matrizBase = render;
        for (int i = 0; i < Constantes.MATRIZ_FILAS; i++) {
            for (int j = 0; j < Constantes.MATRIZ_COLUMNAS; j++) {
                char simbolo = simboloParaRender(render, i, j);
                if (simboloCache[i][j] != simbolo || iconCache[i][j] == null) {
                    simboloCache[i][j] = simbolo;
                    iconCache[i][j] = rutaParaSimbolo(simbolo, i, j);
                }
                String ruta = iconCache[i][j];
                var img = IconosUtils.cargarImagen(ruta != null ? ruta : RutasArchivos.ICON_ELEMENTO_PASTO_VERDE);
                if (img == null) {
                    img = IconosUtils.cargarImagen(RutasArchivos.ICON_ELEMENTO_PASTO_VERDE);
                }
                matrizCeldas[i][j].setImage(img);
            }
        }
    }

    private char simboloParaRender(char[][] base, int f, int c) {
        if (esMutacion(f, c)) return 'M';
        return base[f][c];
    }

    private String rutaParaSimbolo(char c, int f, int cIndex) {
        String fallbackElemento = iconosElementos.isEmpty()
                ? RutasArchivos.ICON_ELEMENTO_PASTO_VERDE
                : iconosElementos.get(0);
        String ruta = switch (c) {
            case 'P' -> elegirIcono(iconosPresas, RutasArchivos.ICON_PRESA);
            case 'D' -> elegirIcono(iconosDepredadores, RutasArchivos.ICON_DEPREDADOR);
            case 'T' -> {
                String key = f + "-" + cIndex;
                boolean esBufalo = tercerasBufalo.contains(key);
                yield esBufalo
                        ? elegirIcono(List.of(RutasArchivos.ICON_TERCERA_ESPECIE_BUFALO), RutasArchivos.ICON_TERCERA_ESPECIE_BUFALO)
                        : elegirIcono(List.of(RutasArchivos.ICON_TERCERA_ESPECIE), RutasArchivos.ICON_TERCERA_ESPECIE);
            }
            case 'M' -> elegirIcono(iconosMutacion, RutasArchivos.ICON_MUTACION);
            case 'E', '.' -> fallbackElemento;
            default -> fallbackElemento;
        };
        return (ruta == null || ruta.isBlank()) ? fallbackElemento : ruta;
    }

    private String elegirIcono(List<String> lista, String respaldo) {
        if (lista == null || lista.isEmpty()) return respaldo;
        String icono = AleatorioUtils.elegirAleatorio(lista);
        return (icono == null || icono.isBlank()) ? respaldo : icono;
    }

    private boolean esMutacion(int f, int c) {
        for (int[] m : mutaciones) {
            if (m[0] == f && m[1] == c) return true;
        }
        return false;
    }

    private int contar(char simbolo) {
        if (matrizBase == null) return 0;
        int count = 0;
        for (int i = 0; i < Constantes.MATRIZ_FILAS; i++) {
            for (int j = 0; j < Constantes.MATRIZ_COLUMNAS; j++) {
                if (matrizBase[i][j] == simbolo) count++;
            }
        }
        return count;
    }

    private void rellenarElementos(char[][] m) {
        for (int i = 0; i < Constantes.MATRIZ_FILAS; i++) {
            for (int j = 0; j < Constantes.MATRIZ_COLUMNAS; j++) {
                char val = m[i][j];
                if (val != 'P' && val != 'D' && val != 'T' && val != 'M' && !esMutacion(i, j)) {
                    m[i][j] = 'E';
                }
            }
        }
    }

    private int[] elegirCeldaLibre() {
        List<int[]> libres = new ArrayList<>();
        for (int i = 0; i < Constantes.MATRIZ_FILAS; i++) {
            for (int j = 0; j < Constantes.MATRIZ_COLUMNAS; j++) {
                if (!esMutacion(i, j) && (matrizBase == null || matrizBase[i][j] == 'E')) {
                    libres.add(new int[]{i, j});
                }
            }
        }
        return libres.isEmpty() ? null : libres.get(ThreadLocalRandom.current().nextInt(libres.size()));
    }

    private void asegurarMatrizBase() {
        if (matrizBase == null) {
            matrizBase = matrizVacia();
        }
    }

    public EscenarioSnapshot crearSnapshot(String nombre) {
        return new EscenarioSnapshot(nombre, clonar(matrizBase), clonarIconos(iconCache));
    }

    private char[][] matrizVacia() {
        char[][] m = new char[Constantes.MATRIZ_FILAS][Constantes.MATRIZ_COLUMNAS];
        for (int i = 0; i < Constantes.MATRIZ_FILAS; i++) {
            for (int j = 0; j < Constantes.MATRIZ_COLUMNAS; j++) {
                m[i][j] = 'E';
            }
        }
        return m;
    }

    private char[][] clonar(char[][] src) {
        if (src == null) return matrizVacia();
        char[][] copia = new char[Constantes.MATRIZ_FILAS][Constantes.MATRIZ_COLUMNAS];
        for (int i = 0; i < Constantes.MATRIZ_FILAS; i++) {
            System.arraycopy(src[i], 0, copia[i], 0, Constantes.MATRIZ_COLUMNAS);
        }
        return copia;
    }

    private String[][] clonarIconos(String[][] src) {
        if (src == null) return null;
        String[][] copia = new String[Constantes.MATRIZ_FILAS][Constantes.MATRIZ_COLUMNAS];
        for (int i = 0; i < Constantes.MATRIZ_FILAS; i++) {
            for (int j = 0; j < Constantes.MATRIZ_COLUMNAS; j++) {
                copia[i][j] = src[i][j];
            }
        }
        return copia;
    }

    private char[][] normalizar(char[][] src) {
        char[][] copia = clonar(src);
        rellenarElementos(copia);
        for (int i = 0; i < Constantes.MATRIZ_FILAS; i++) {
            for (int j = 0; j < Constantes.MATRIZ_COLUMNAS; j++) {
                char val = copia[i][j];
                if (val != 'P' && val != 'D' && val != 'T' && val != 'M' && val != 'E') {
                    copia[i][j] = 'E';
                }
            }
        }
        return copia;
    }

    private void resetCacheIconos() {
        for (int i = 0; i < Constantes.MATRIZ_FILAS; i++) {
            for (int j = 0; j < Constantes.MATRIZ_COLUMNAS; j++) {
                simboloCache[i][j] = '\0';
                iconCache[i][j] = null;
            }
        }
    }

    private void resaltarTemporal(int fila, int col, String colorHex, double segundos) {
        if (fila < 0 || col < 0 || fila >= Constantes.MATRIZ_FILAS || col >= Constantes.MATRIZ_COLUMNAS) return;
        StackPane wrapper = cellWrappers[fila][col];
        if (wrapper == null) return;
        wrapper.setStyle("-fx-border-color: " + colorHex + "; -fx-border-width: 3; -fx-border-radius: 6; -fx-background-radius: 6;");
        Timeline tl = new Timeline(new KeyFrame(Duration.seconds(segundos), e -> wrapper.setStyle("-fx-border-color: transparent; -fx-border-width: 0; -fx-border-radius: 6; -fx-background-radius: 6;")));
        tl.setCycleCount(1);
        tl.play();
    }

    private void limpiarResaltado() {
        for (int i = 0; i < Constantes.MATRIZ_FILAS; i++) {
            for (int j = 0; j < Constantes.MATRIZ_COLUMNAS; j++) {
                StackPane w = cellWrappers[i][j];
                if (w != null) {
                    w.setStyle("-fx-border-color: transparent; -fx-border-width: 0; -fx-border-radius: 6; -fx-background-radius: 6;");
                }
            }
        }
    }
}
