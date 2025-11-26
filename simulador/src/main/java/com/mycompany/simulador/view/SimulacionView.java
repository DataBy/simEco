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
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SimulacionView {

    private final BorderPane root = new BorderPane();

    private final ToggleGroup grupoEscenarios = new ToggleGroup();
    private final RadioButton rbEquilibrado   = new RadioButton("Equilibrado");
    private final RadioButton rbDepDom        = new RadioButton("Depredadores dominantes");
    private final RadioButton rbPresDom       = new RadioButton("Presas dominantes");
    private final Spinner<Integer> spMaxTurnos =
            new Spinner<>(1, 500, 50, 1);

    private final Button btnIniciar = new Button();

    private final ImageView[][] matrizCeldas =
            new ImageView[Constantes.MATRIZ_FILAS][Constantes.MATRIZ_COLUMNAS];

    public SimulacionView() {
        construirLayout();
    }

    private void construirLayout() {
        // Panel izquierda (menú)
        VBox panelIzq = new VBox(15);
        panelIzq.setPadding(new Insets(15));
        panelIzq.setAlignment(Pos.TOP_CENTER);

        rbEquilibrado.setToggleGroup(grupoEscenarios);
        rbDepDom.setToggleGroup(grupoEscenarios);
        rbPresDom.setToggleGroup(grupoEscenarios);
        rbEquilibrado.setSelected(true);

        VBox radios = new VBox(5, rbEquilibrado, rbDepDom, rbPresDom);
        radios.setFillWidth(true);

        Label lblMax = new Label("Máx. turnos:");
        spMaxTurnos.setMaxWidth(120);

        IconosUtils.configurarBotonConHover(btnIniciar,
                RutasArchivos.SIM_BTN_INICIAR,
                RutasArchivos.SIM_BTN_INICIAR_HOVER);

        panelIzq.getChildren().addAll(
                new Label("Escenario:"), radios,
                lblMax, spMaxTurnos,
                btnIniciar
        );

        // Panel centro (escenario)
        StackPane panelCentro = new StackPane();
        ImageView fondo = IconosUtils.crearImageViewFondo(
                RutasArchivos.SIM_ESC_BACKGROUND, panelCentro);

        GridPane grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);
        grid.setPadding(new Insets(10));
        grid.setAlignment(Pos.CENTER);

        for (int i = 0; i < Constantes.MATRIZ_FILAS; i++) {
            for (int j = 0; j < Constantes.MATRIZ_COLUMNAS; j++) {
                ImageView iv = new ImageView(IconosUtils.cargarImagen(RutasArchivos.ICON_CELDA_VACIA));
                iv.setPreserveRatio(true);
                iv.setFitWidth(40); // se adapta con la GridPane
                matrizCeldas[i][j] = iv;
                grid.add(iv, j, i);
            }
        }

        panelCentro.getChildren().addAll(fondo, grid);

        root.setLeft(panelIzq);
        root.setCenter(panelCentro);
    }

    public Parent getRoot() { return root; }

    public String getEscenarioSeleccionado() {
        if (rbDepDom.isSelected()) return "DEPREDADORES_DOMINANTES";
        if (rbPresDom.isSelected()) return "PRESAS_DOMINANTES";
        return "EQUILIBRADO";
    }

    public int getMaxTurnos() {
        return spMaxTurnos.getValue();
    }

    public void setOnIniciar(Runnable r) { btnIniciar.setOnAction(e -> r.run()); }

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
