package com.mycompany.simulador.view;

import com.mycompany.simulador.config.Constantes;
import com.mycompany.simulador.config.RutasArchivos;
import com.mycompany.simulador.utils.IconosUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class EscenariosGaleriaView {

    private final BorderPane root = new BorderPane();
    private final Label lblTitulo = new Label("Escenarios");
    private final Label lblEscenario = new Label("--");
    private final Button btnPrev = new Button("<");
    private final Button btnNext = new Button(">");
    private final Button btnSiguiente = new Button("Siguiente");
    private final GridPane grid = new GridPane();
    private final ImageView[][] celdas = new ImageView[Constantes.MATRIZ_FILAS][Constantes.MATRIZ_COLUMNAS];

    private List<EscenarioSnapshot> escenarios = new ArrayList<>();
    private int index = 0;
    private Runnable onSiguiente;

    public EscenariosGaleriaView() {
        construirUI();
    }

    private void construirUI() {
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #A1D164;");

        lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #235217;");
        lblEscenario.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #235217;");

        btnPrev.setOnAction(e -> mostrarAnterior());
        btnNext.setOnAction(e -> mostrarSiguiente());
        btnSiguiente.setOnAction(e -> {
            if (onSiguiente != null) onSiguiente.run();
        });

        HBox header = new HBox(12, btnPrev, lblEscenario, btnNext);
        header.setAlignment(Pos.CENTER);

        VBox topBox = new VBox(10, lblTitulo, header);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(10));
        root.setTop(topBox);

        grid.setHgap(2);
        grid.setVgap(2);
        grid.setAlignment(Pos.CENTER);
        for (int i = 0; i < Constantes.MATRIZ_FILAS; i++) {
            for (int j = 0; j < Constantes.MATRIZ_COLUMNAS; j++) {
                ImageView iv = new ImageView(IconosUtils.cargarImagen(RutasArchivos.ICON_CELDA_VACIA));
                iv.setFitWidth(Constantes.MATRIZ_TAM_CELDA * 0.5);
                iv.setFitHeight(Constantes.MATRIZ_TAM_CELDA * 0.5);
                iv.setPreserveRatio(false);
                celdas[i][j] = iv;
                grid.add(iv, j, i);
            }
        }
        root.setCenter(grid);

        HBox footer = new HBox(btnSiguiente);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(10));
        root.setBottom(footer);
    }

    public void setEscenarios(List<EscenarioSnapshot> lista) {
        this.escenarios = (lista == null) ? new ArrayList<>() : new ArrayList<>(lista);
        this.index = 0;
        render();
    }

    public void setOnSiguiente(Runnable r) {
        this.onSiguiente = r;
    }

    public Parent getRoot() {
        return root;
    }

    private void mostrarAnterior() {
        if (escenarios.isEmpty()) return;
        index = (index - 1 + escenarios.size()) % escenarios.size();
        render();
    }

    private void mostrarSiguiente() {
        if (escenarios.isEmpty()) return;
        index = (index + 1) % escenarios.size();
        render();
    }

    private void render() {
        if (escenarios.isEmpty()) {
            lblEscenario.setText("--");
            pintarMatriz(new char[Constantes.MATRIZ_FILAS][Constantes.MATRIZ_COLUMNAS]);
            return;
        }
        EscenarioSnapshot snap = escenarios.get(index);
        lblEscenario.setText(snap.getNombre());
        pintarMatriz(snap.getMatriz());
    }

    private void pintarMatriz(char[][] matriz) {
        for (int i = 0; i < Constantes.MATRIZ_FILAS; i++) {
            for (int j = 0; j < Constantes.MATRIZ_COLUMNAS; j++) {
                char val = (matriz != null && i < matriz.length && j < matriz[i].length)
                        ? matriz[i][j] : 'E';
                String ruta = rutaPara(val);
                var img = IconosUtils.cargarImagen(ruta);
                if (img == null) {
                    img = IconosUtils.cargarImagen(RutasArchivos.ICON_CELDA_VACIA);
                }
                celdas[i][j].setImage(img);
            }
        }
    }

    private String rutaPara(char c) {
        return switch (c) {
            case 'P' -> RutasArchivos.ICON_PRESA;
            case 'D' -> RutasArchivos.ICON_DEPREDADOR;
            case 'T' -> RutasArchivos.ICON_TERCERA_ESPECIE;
            case 'M' -> RutasArchivos.ICON_MUTACION;
            default -> RutasArchivos.ICON_CELDA_VACIA;
        };
    }
}
