package com.mycompany.simulador.view;

import com.mycompany.simulador.config.Constantes;
import com.mycompany.simulador.config.RutasArchivos;
import com.mycompany.simulador.utils.IconosUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

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
        root.setPadding(new Insets(16, 16, 18, 16));
        root.setStyle("-fx-background-color: #A1D164;");

        btnPrev.setOnAction(e -> mostrarAnterior());
        btnNext.setOnAction(e -> mostrarSiguiente());
        btnSiguiente.setOnAction(e -> {
            if (onSiguiente != null) onSiguiente.run();
        });

        lblTitulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #235217;");
        lblEscenario.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #235217;");

        estilizarBotonNavegacion(btnPrev);
        estilizarBotonNavegacion(btnNext);
        estilizarBotonPrincipal(btnSiguiente);

        HBox header = new HBox(10, btnPrev, lblEscenario, btnNext);
        header.setAlignment(Pos.CENTER);

        VBox card = new VBox(8, lblTitulo, header, btnSiguiente);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(340);
        card.setPadding(new Insets(10, 12, 12, 12));
        card.setStyle("""
            -fx-background-color: rgba(255,255,255,0.25);
            -fx-background-radius: 14;
            -fx-border-radius: 14;
            -fx-border-color: rgba(255,255,255,0.55);
            -fx-border-width: 1.2;
        """);
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.16));
        shadow.setRadius(8);
        shadow.setOffsetY(1.5);
        card.setEffect(shadow);

        grid.setHgap(1.5);
        grid.setVgap(1.5);
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setPadding(new Insets(8, 0, 0, 0));
        for (int i = 0; i < Constantes.MATRIZ_FILAS; i++) {
            for (int j = 0; j < Constantes.MATRIZ_COLUMNAS; j++) {
                ImageView iv = new ImageView(IconosUtils.cargarImagen(RutasArchivos.ICON_CELDA_VACIA));
                iv.setFitWidth(Constantes.MATRIZ_TAM_CELDA * 0.6);
                iv.setFitHeight(Constantes.MATRIZ_TAM_CELDA * 0.6);
                iv.setPreserveRatio(false);
                celdas[i][j] = iv;
                grid.add(iv, j, i);
            }
        }

        VBox content = new VBox(8, card, grid);
        content.setAlignment(Pos.TOP_CENTER);
        BorderPane.setAlignment(content, Pos.TOP_CENTER);
        root.setCenter(content);
    }

    private void estilizarBotonNavegacion(Button b) {
        b.setPrefSize(32, 32);
        b.setStyle("""
            -fx-background-color: rgba(255,255,255,0.35);
            -fx-background-radius: 10;
            -fx-border-radius: 10;
            -fx-border-color: rgba(255,255,255,0.8);
            -fx-text-fill: #235217;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
        """);
    }

    private void estilizarBotonPrincipal(Button b) {
        b.setMaxWidth(Double.MAX_VALUE);
        b.setPrefWidth(180);
        b.setStyle("""
            -fx-background-color: rgba(255,255,255,0.55);
            -fx-background-radius: 12;
            -fx-border-radius: 12;
            -fx-border-color: rgba(255,255,255,0.9);
            -fx-border-width: 1.4;
            -fx-text-fill: #235217;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-padding: 6 12 6 12;
        """);
    }

    public void setEscenarios(List<EscenarioSnapshot> lista) {
        this.escenarios = ordenarPorEstacion(lista);
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
        lblEscenario.setText(formatearNombre(snap.getNombre()));
        pintarMatriz(snap.getMatriz(), snap.getIconos());
    }

    private void pintarMatriz(char[][] matriz) {
        pintarMatriz(matriz, null);
    }

    private void pintarMatriz(char[][] matriz, String[][] iconos) {
        for (int i = 0; i < Constantes.MATRIZ_FILAS; i++) {
            for (int j = 0; j < Constantes.MATRIZ_COLUMNAS; j++) {
                char val = (matriz != null && i < matriz.length && j < matriz[i].length)
                        ? matriz[i][j] : 'E';
                String ruta = rutaDesdeSnapshot(iconos, i, j);
                if (ruta == null) {
                    ruta = rutaPara(val);
                }
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

    private String rutaDesdeSnapshot(String[][] iconos, int fila, int col) {
        if (iconos == null) return null;
        if (fila < 0 || col < 0) return null;
        if (fila >= iconos.length || col >= iconos[fila].length) return null;
        String ruta = iconos[fila][col];
        return (ruta == null || ruta.isBlank()) ? null : ruta;
    }

    private String formatearNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "--";
        }
        String limpio = nombre.trim().toUpperCase();
        return limpio + " - Turno 1";
    }

    private List<EscenarioSnapshot> ordenarPorEstacion(List<EscenarioSnapshot> lista) {
        List<EscenarioSnapshot> base = (lista == null) ? new ArrayList<>() : new ArrayList<>(lista);
        List<EscenarioSnapshot> ordenada = new ArrayList<>();
        String[] estaciones = {"VERANO", "PRIMAVERA", "INVIERNO"};
        for (String estacion : estaciones) {
            EscenarioSnapshot snap = buscarPrimero(base, estacion);
            if (snap != null) {
                ordenada.add(snap);
            }
        }
        for (EscenarioSnapshot snap : base) {
            if (snap != null && !yaIncluido(ordenada, snap)) {
                ordenada.add(snap);
            }
        }
        if (ordenada.isEmpty()) {
            ordenada.addAll(base);
        }
        return ordenada;
    }

    private boolean yaIncluido(List<EscenarioSnapshot> lista, EscenarioSnapshot candidato) {
        for (EscenarioSnapshot snap : lista) {
            if (snap != null && candidato != null
                    && snap.getNombre() != null
                    && snap.getNombre().equalsIgnoreCase(candidato.getNombre())) {
                return true;
            }
        }
        return false;
    }

    private EscenarioSnapshot buscarPrimero(List<EscenarioSnapshot> lista, String nombre) {
        for (EscenarioSnapshot snap : lista) {
            if (snap != null && nombre.equalsIgnoreCase(snap.getNombre())) {
                return snap;
            }
        }
        return null;
    }
}
