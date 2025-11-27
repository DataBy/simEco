package com.mycompany.simulador.view;

import com.mycompany.simulador.config.Constantes;
import com.mycompany.simulador.config.RutasArchivos;
import com.mycompany.simulador.utils.IconosUtils;

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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

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

    // Celdas de la matriz
    private final ImageView[][] matrizCeldas =
            new ImageView[Constantes.MATRIZ_FILAS][Constantes.MATRIZ_COLUMNAS];

    // Tamaño dinámico de cada celda del grid
    private final DoubleProperty cellSize =
            new SimpleDoubleProperty(Constantes.MATRIZ_TAM_CELDA);

    public SimulacionView() {
        construirUI();
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
        menuIzq.setPadding(new Insets(40, 24, 40, 24));
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
        contDer.setPadding(new Insets(40, 24, 40, 24));
        contDer.setAlignment(Pos.TOP_CENTER);

        Label lblHist = new Label("Historial de la Simulación");
        lblHist.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #235217;");

        // Panel glassy que crecerá con el alto disponible
        VBox panelHistorial = crearPanelGlassy(320, 0);
        VBox.setVgrow(panelHistorial, Priority.ALWAYS);

        txtHistorial.setWrapText(true);
        txtHistorial.setEditable(false);
        txtHistorial.setStyle("-fx-background-color: transparent;");
        txtHistorial.setPrefRowCount(15);
        txtHistorial.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(txtHistorial, Priority.ALWAYS);

        panelHistorial.getChildren().add(txtHistorial);
        panelHistorial.setFillWidth(true);

        contDer.getChildren().addAll(lblHist, panelHistorial);
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
                iv.fitWidthProperty().bind(cellSize);
                iv.fitHeightProperty().bind(cellSize);

                matrizCeldas[i][j] = iv;
                grid.add(iv, j, i);
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

        // Limita al tamaño máximo configurado en tus constantes (ej: 102px)
        double max = Constantes.MATRIZ_TAM_CELDA;
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

    public void log(String mensaje) {
        txtHistorial.appendText(mensaje + "\n");
    }

    public void actualizarMatriz(char[][] simbolos) {
        for (int i = 0; i < Constantes.MATRIZ_FILAS; i++) {
            for (int j = 0; j < Constantes.MATRIZ_COLUMNAS; j++) {

                char c = simbolos[i][j];
                String ruta;

                if (c == 'P')      ruta = RutasArchivos.ICON_PRESA;
                else if (c == 'D') ruta = RutasArchivos.ICON_DEPREDADOR;
                else if (c == 'T') ruta = RutasArchivos.ICON_TERCERA_ESPECIE;
                else               ruta = RutasArchivos.ICON_CELDA_VACIA;

                matrizCeldas[i][j].setImage(IconosUtils.cargarImagen(ruta));
            }
        }
    }
}
