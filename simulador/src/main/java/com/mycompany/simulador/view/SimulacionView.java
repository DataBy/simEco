package com.mycompany.simulador.view;

import com.mycompany.simulador.config.Constantes;
import com.mycompany.simulador.config.RutasArchivos;
import com.mycompany.simulador.utils.IconosUtils;

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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class SimulacionView {

    private final BorderPane root = new BorderPane();

    // Selección de dificultad
    private final ToggleGroup grupoDificultad = new ToggleGroup();
    private final RadioButton rbFacil  = new RadioButton("Fácil (Presas 70%)");
    private final RadioButton rbMedio  = new RadioButton("Medio (50/50)");
    private final RadioButton rbDificil = new RadioButton("Difícil (Depredadores 70%)");

    // Spinner turnos (valor DEFAULT = 5)
    private final Spinner<Integer> spMaxTurnos =
            new Spinner<>(1, 500, 5, 1);

    // Botones principales
    private final Button btnIniciar   = new Button();
    private final Button btnInicio    = new Button();
    private final Button btnSalir     = new Button();
    private final Button btnTercera   = new Button();
    private final Button btnMutacion  = new Button();
    private final Button btnAleatorio = new Button();
    private final Button btnPersonal  = new Button();

    // Historial
    private final TextArea txtHistorial = new TextArea();

    private final ImageView[][] matrizCeldas =
            new ImageView[Constantes.MATRIZ_FILAS][Constantes.MATRIZ_COLUMNAS];


    public SimulacionView() {
        construirUI();
    }


    private void construirUI() {

        // Fondo base
        root.setStyle("-fx-background-color: #A1D164;");

        // ======================================================
        // PANEL IZQUIERDO
        // ======================================================
        StackPane panelIzq = new StackPane();
        panelIzq.setPrefWidth(420);

        ImageView fondoIzq = IconosUtils.crearImageViewFondo(
                RutasArchivos.SIM_MENU_IZQ_BACKGROUND,
                panelIzq
        );

        VBox menuIzq = new VBox(20);
        menuIzq.setPadding(new Insets(240, 25, 25, 25));
        menuIzq.setAlignment(Pos.TOP_CENTER);

        // Botones
        IconosUtils.configurarBotonConHover(btnAleatorio,
                RutasArchivos.SIM_BTN_ESCENARIO_ALEATORIO,
                RutasArchivos.SIM_BTN_ESCENARIO_ALEATORIO_HOVER);

        IconosUtils.configurarBotonConHover(btnPersonal,
                RutasArchivos.SIM_BTN_AÑADIR,
                RutasArchivos.SIM_BTN_AÑADIR_HOVER);

        IconosUtils.configurarBotonConHover(btnInicio,
                RutasArchivos.SIM_BTN_INICIO,
                RutasArchivos.SIM_BTN_INICIO_HOVER);

        IconosUtils.configurarBotonConHover(btnSalir,
                RutasArchivos.SIM_BTN_SALIR,
                RutasArchivos.SIM_BTN_SALIR_HOVER);

        IconosUtils.configurarBotonConHover(btnTercera,
                RutasArchivos.SIM_BTN_TERCERA,
                RutasArchivos.SIM_BTN_TERCERA_HOVER);

        IconosUtils.configurarBotonConHover(btnMutacion,
                RutasArchivos.SIM_BTN_AÑADIR,
                RutasArchivos.SIM_BTN_AÑADIR_HOVER);

        IconosUtils.configurarBotonConHover(btnIniciar,
                RutasArchivos.SIM_BTN_INICIAR,
                RutasArchivos.SIM_BTN_INICIAR_HOVER);

        menuIzq.getChildren().addAll(
                btnAleatorio,
                btnPersonal,
                btnInicio,
                btnSalir,
                btnTercera,
                btnMutacion
        );

        // ======================================================
        // PANEL GLASSY DIFICULTAD
        // ======================================================
        VBox panelDificultad = crearPanelGlassy(250, 210);

        Label lblTituloDif = new Label("Elige un Escenario:");
        lblTituloDif.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        rbFacil.setToggleGroup(grupoDificultad);
        rbMedio.setToggleGroup(grupoDificultad);
        rbDificil.setToggleGroup(grupoDificultad);
        rbMedio.setSelected(true);

        VBox turnosCentro = new VBox(6);
        turnosCentro.setAlignment(Pos.CENTER);
        Label lblTurnos = new Label("Turnos:");
        spMaxTurnos.setMaxWidth(120);
        turnosCentro.getChildren().addAll(lblTurnos, spMaxTurnos);

        panelDificultad.getChildren().addAll(
                lblTituloDif,
                rbFacil, rbMedio, rbDificil,
                turnosCentro
        );

        VBox iniciarBox = new VBox();
        iniciarBox.setAlignment(Pos.CENTER);
        iniciarBox.setPadding(new Insets(10, 0, 0, 0));
        iniciarBox.getChildren().add(btnIniciar);

        menuIzq.getChildren().add(panelDificultad);
        menuIzq.getChildren().add(iniciarBox);

        panelIzq.getChildren().addAll(fondoIzq, menuIzq);

        // ======================================================
        // PANEL DERECHO: HISTORIAL
        // ======================================================
        StackPane panelDer = new StackPane();
        panelDer.setPrefWidth(420);

        ImageView fondoDer = IconosUtils.crearImageViewFondo(
                RutasArchivos.SIM_MENU_DER_BACKGROUND,
                panelDer
        );

        VBox contDer = new VBox(15);

        // BAJAMOS TODO EL BLOQUE (ANTES 140)
        contDer.setPadding(new Insets(190, 25, 25, 25));
        contDer.setAlignment(Pos.TOP_CENTER);

        Label lblHist = new Label("Historial de la Simulación");
        lblHist.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // HACEMOS EL GLASSY MÁS GRANDE SIN INVADIR NADA
        VBox panelHistorial = crearPanelGlassy(350, 700); // <-- CAMBIA EL 1400 A LO QUE QUIERAS

        txtHistorial.setWrapText(true);
        txtHistorial.setEditable(false);
        txtHistorial.setStyle("-fx-background-color: transparent;");

        // Hacemos el textarea más grande
        txtHistorial.setPrefWidth(330);   
        txtHistorial.setPrefHeight(600); // <-- ALTURA REAL DEL TEXTAREA

        // Para permitir que CREZCA dentro del glassy
        VBox.setVgrow(txtHistorial, javafx.scene.layout.Priority.ALWAYS);

        panelHistorial.getChildren().add(txtHistorial);
        panelHistorial.setFillWidth(true);

        contDer.getChildren().addAll(lblHist, panelHistorial);
        panelDer.getChildren().addAll(fondoDer, contDer);

        // ======================================================
        // PANEL CENTRAL
        // ======================================================
        StackPane panelCentro = new StackPane();

        ImageView fondoEsc = IconosUtils.crearImageViewFondo(
                RutasArchivos.SIM_ESC_BACKGROUND,
                panelCentro
        );

        GridPane grid = new GridPane();
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setAlignment(Pos.CENTER);

        for (int i = 0; i < Constantes.MATRIZ_FILAS; i++) {
            for (int j = 0; j < Constantes.MATRIZ_COLUMNAS; j++) {
                ImageView iv = new ImageView(
                        IconosUtils.cargarImagen(RutasArchivos.ICON_CELDA_VACIA)
                );
                iv.setFitWidth(Constantes.MATRIZ_TAM_CELDA);
                iv.setFitHeight(Constantes.MATRIZ_TAM_CELDA);
                iv.setPreserveRatio(false);
                matrizCeldas[i][j] = iv;
                grid.add(iv, j, i);
            }
        }

        panelCentro.getChildren().addAll(fondoEsc, grid);

        // ======================================================
        // ARMADO FINAL
        // ======================================================
        root.setLeft(panelIzq);
        root.setCenter(panelCentro);
        root.setRight(panelDer);
    }


    // Panel glassy reutilizable
    private VBox crearPanelGlassy(double w, double h) {
        VBox v = new VBox(12);
        v.setPrefSize(w, h);
        v.setAlignment(Pos.TOP_CENTER);

        v.setStyle("""
            -fx-background-color: rgba(255,255,255,0.25);
            -fx-background-radius: 18;
            -fx-border-radius: 18;
            -fx-border-color: rgba(255,255,255,0.4);
            -fx-border-width: 2;
        """);

        v.setPadding(new Insets(15));

        DropShadow sombra = new DropShadow();
        sombra.setColor(Color.rgb(0,0,0,0.15));
        v.setEffect(sombra);

        return v;
    }

    public Parent getRoot() { return root; }

    // Métodos para controlador
    public String getDificultad() {
        if (rbFacil.isSelected()) return "FACIL";
        if (rbDificil.isSelected()) return "DIFICIL";
        return "MEDIO";
    }

    public int getMaxTurnos() { return spMaxTurnos.getValue(); }

    public void setOnIniciar(Runnable r) { btnIniciar.setOnAction(e -> r.run()); }
    public void setOnSalir(Runnable r)   { btnSalir.setOnAction(e -> r.run()); }
    public void setOnInicio(Runnable r)  { btnInicio.setOnAction(e -> r.run()); }

    public void log(String mensaje) {
        txtHistorial.appendText(mensaje + "\n");
    }

    public void actualizarMatriz(char[][] simbolos) {
        for (int i = 0; i < Constantes.MATRIZ_FILAS; i++) {
            for (int j = 0; j < Constantes.MATRIZ_COLUMNAS; j++) {

                char c = simbolos[i][j];
                String ruta;

                if (c == 'P') ruta = RutasArchivos.ICON_PRESA;
                else if (c == 'D') ruta = RutasArchivos.ICON_DEPREDADOR;
                else if (c == 'T') ruta = RutasArchivos.ICON_TERCERA_ESPECIE;
                else ruta = RutasArchivos.ICON_CELDA_VACIA;

                matrizCeldas[i][j].setImage(IconosUtils.cargarImagen(ruta));
            }
        }
    }
}
