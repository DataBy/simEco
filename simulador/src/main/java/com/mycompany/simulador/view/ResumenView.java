package com.mycompany.simulador.view;

import com.mycompany.simulador.config.RutasArchivos;
import com.mycompany.simulador.model.report.ReporteFinal;
import com.mycompany.simulador.services.reportes.GraficosService;
import com.mycompany.simulador.utils.IconosUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ResumenView {

    private final StackPane root = new StackPane();
    private final VBox content = new VBox(26);

    private final Spinner<Integer> spTotalTurnos = new Spinner<>();
    private final TextField txtTurnoExtincion = new TextField();
    private final VBox panelGraficoPoblaciones = new VBox();
    private final VBox panelGraficoOcupacion = new VBox();
    private final VBox panelComparativo = new VBox();
    private final TitledPane panelDetalleComparativo = new TitledPane();
    private final VBox panelDetalleContenido = new VBox(12);
    private final Button btnInicio = new Button();
    private final Button btnEnvioReporte = new Button();
    private final Button btnEnviarCorreo = new Button();
    private static final double PANEL_GRAF_WIDTH = 260;

    public ResumenView() {
        construirLayout();
    }

    private void construirLayout() {
        ImageView bg = IconosUtils.crearImageViewFondo(RutasArchivos.RESUMEN_BACKGROUND, root);

        spTotalTurnos.setEditable(false);
        spTotalTurnos.setPrefWidth(140);
        spTotalTurnos.setStyle("-fx-opacity: 1; -fx-background-color: rgba(255,255,255,0.85);");
        spTotalTurnos.setFocusTraversable(false);
        spTotalTurnos.setMouseTransparent(true);
        txtTurnoExtincion.setEditable(false);
        txtTurnoExtincion.setPrefWidth(180);
        txtTurnoExtincion.setStyle("-fx-opacity: 1; -fx-background-color: rgba(255,255,255,0.85);");

        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(90, 22, 48, 22));
        content.setMaxWidth(1100);
        content.setMinWidth(820);
        content.setBackground(Background.EMPTY);

        configurarBotonPlano(btnInicio, RutasArchivos.RESUMEN_BTN_INICIO);
        configurarBotonPlano(btnEnvioReporte, RutasArchivos.RESUMEN_BTN_ENVIO_REPORTE);
        configurarBotonPlano(btnEnviarCorreo, RutasArchivos.RESUMEN_BTN_ENVIAR_CORREO);
        HBox acciones = new HBox(18, btnInicio, btnEnvioReporte, btnEnviarCorreo);
        acciones.setAlignment(Pos.CENTER);

        HBox top = new HBox(18);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(0, 0, 0, 260)); // desplaza a la derecha para no chocar con el título de fondo
        top.getChildren().addAll(
                new Label("Total de turnos ejecutados:"), spTotalTurnos,
                new Label("Turno de extincion:"), txtTurnoExtincion
        );

        configurarPanelGrafico(panelGraficoPoblaciones);
        configurarPanelGrafico(panelGraficoOcupacion);

        VBox grafPoblacionesBox = wrapGrafico("Poblaciones finales", panelGraficoPoblaciones, crearLeyendaPoblaciones());
        VBox grafOcupacionBox = wrapGrafico("Ocupacion del tablero", panelGraficoOcupacion, crearLeyendaOcupacion());

        HBox center = new HBox(40, grafPoblacionesBox, grafOcupacionBox);
        center.setAlignment(Pos.CENTER);
        HBox.setHgrow(grafPoblacionesBox, Priority.ALWAYS);
        HBox.setHgrow(grafOcupacionBox, Priority.ALWAYS);

        panelDetalleContenido.setSpacing(10);
        panelDetalleContenido.setPadding(new Insets(12, 10, 12, 10));
        panelDetalleContenido.setBackground(Background.EMPTY);

        ScrollPane scrollDetalle = new ScrollPane(panelDetalleContenido);
        scrollDetalle.setFitToWidth(true);
        scrollDetalle.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollDetalle.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollDetalle.setPrefViewportHeight(180);
        scrollDetalle.setStyle("""
            -fx-background-color: transparent;
            -fx-background-insets: 0;
            -fx-border-color: #d9d9d9;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
        """);

        VBox contenidoComparativo = new VBox(10, scrollDetalle);
        contenidoComparativo.setFillWidth(true);
        contenidoComparativo.setPadding(new Insets(8, 6, 8, 6));
        contenidoComparativo.setStyle("-fx-background-color: rgba(255,255,255,0.94); -fx-background-radius: 10;");

        panelDetalleComparativo.setText("Analisis comparativo general");
        panelDetalleComparativo.setCollapsible(false);
        panelDetalleComparativo.setAnimated(false);
        panelDetalleComparativo.setContent(contenidoComparativo);
        panelDetalleComparativo.setMaxWidth(980);
        panelDetalleComparativo.setPrefWidth(980);
        panelDetalleComparativo.setMinWidth(980);
        panelDetalleComparativo.setMaxHeight(Double.MAX_VALUE);

        panelComparativo.setSpacing(8);
        panelComparativo.setPadding(new Insets(12, 12, 12, 12));
        panelComparativo.setAlignment(Pos.CENTER_LEFT);
        panelComparativo.setStyle("""
            -fx-background-color: rgba(255, 255, 255, 0.9);
            -fx-background-radius: 12;
        """);
        panelComparativo.setFillWidth(true);
        panelComparativo.setPrefWidth(980);
        panelComparativo.setMinWidth(980);
        panelComparativo.setMaxWidth(980);
        panelComparativo.getChildren().setAll(panelDetalleComparativo);

        content.getChildren().setAll(top, center, panelComparativo, acciones);
        VBox.setMargin(top, new Insets(24, 0, 14, 0));
        VBox.setMargin(center, new Insets(22, 0, 0, 0));
        VBox.setMargin(panelComparativo, new Insets(16, 0, 0, 0));
        VBox.setMargin(acciones, new Insets(16, 0, 0, 0));

        StackPane.setAlignment(content, Pos.TOP_CENTER);
        root.getChildren().addAll(bg, content);
    }

    public Parent getRoot() {
        return root;
    }

    public void actualizarDatos(int totalTurnos, int presas, int depredadores, int terceras,
                                int turnoExtincion, int ocupadas, int vacias) {
        spTotalTurnos.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Math.max(totalTurnos, 1), totalTurnos));
        txtTurnoExtincion.setText(turnoExtincion < 0 ? "N/A" : String.valueOf(turnoExtincion));

        GraficosService g = new GraficosService();
        panelGraficoPoblaciones.getChildren().setAll(
                g.crearGraficoPoblaciones(presas, depredadores, terceras)
        );
        panelGraficoOcupacion.getChildren().setAll(
                g.crearGraficoOcupacion(ocupadas, vacias)
        );
    }

    public void actualizarComparativo(java.util.List<ReporteFinal> reportes) {
        if (reportes == null || reportes.isEmpty()) return;
        panelDetalleContenido.getChildren().clear();
        for (int i = 0; i < reportes.size(); i++) {
            ReporteFinal r = reportes.get(i);
            panelDetalleContenido.getChildren().add(crearDetalleEstacion(formatearEscenario(r.getEscenario(), i + 1), r));
        }
    }

    private void configurarPanelGrafico(VBox panel) {
        panel.setSpacing(8);
        panel.setAlignment(Pos.CENTER);
        panel.setFillWidth(false);
        panel.setPadding(new Insets(6, 10, 10, 10));
        panel.setMinWidth(PANEL_GRAF_WIDTH);
        panel.setPrefWidth(PANEL_GRAF_WIDTH);
    }

    private VBox wrapGrafico(String titulo, VBox panel, HBox leyenda) {
        Label lbl = new Label(titulo);
        lbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #3b2a18;");
        VBox cont = new VBox(4, lbl, panel, leyenda);
        cont.setAlignment(Pos.TOP_CENTER);
        cont.setFillWidth(false);
        cont.setMaxWidth(PANEL_GRAF_WIDTH + 40);
        return cont;
    }

    private VBox crearDetalleEstacion(String nombre, ReporteFinal r) {
        Label titulo = new Label(nombre);
        titulo.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2c241c;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(6);
        agregarFilaDetalle(grid, 0, "Turnos ejecutados", String.valueOf(r.getTotalTurnos()));
        agregarFilaDetalle(grid, 1, "Presas finales", String.valueOf(r.getPresasFinales()));
        agregarFilaDetalle(grid, 2, "Depredadores finales", String.valueOf(r.getDepredadoresFinales()));
        agregarFilaDetalle(grid, 3, "Tercera especie final", String.valueOf(r.getTerceraEspecieFinal()));
        agregarFilaDetalle(grid, 4, "Turno de extincion", r.getTurnoExtincion() < 0 ? "N/A" : String.valueOf(r.getTurnoExtincion()));
        agregarFilaDetalle(grid, 5, "Ocupacion final", String.format("%.1f%%", r.getPorcentajeOcupacionFinal()));

        VBox tarjeta = new VBox(6, titulo, grid);
        tarjeta.setStyle("-fx-background-color: rgba(255,255,255,0.55); -fx-background-radius: 8;");
        tarjeta.setPadding(new Insets(10, 12, 10, 12));
        tarjeta.setFillWidth(true);
        return tarjeta;
    }

    private void agregarFilaDetalle(GridPane grid, int row, String label, String valor) {
        Label lbl = new Label(label + ":");
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c241c;");
        grid.add(lbl, 0, row);
        Label val = new Label(valor);
        val.setStyle("-fx-text-fill: #2c241c;");
        grid.add(val, 1, row);
    }

    private String formatearEscenario(String escenario, int index) {
        String estacion = (escenario == null || escenario.isBlank()) ? "" : escenario.trim();
        String nombreBase = estacion.isEmpty() ? ("Simulacion " + index) : ("Simulacion " + index + " - " + estacion);
        return nombreBase;
    }

    private HBox crearLeyendaItem(String texto, String colorHex) {
        javafx.scene.shape.Circle c = new javafx.scene.shape.Circle(6, javafx.scene.paint.Paint.valueOf(colorHex));
        HBox item = new HBox(6, c, new Label(texto));
        item.setAlignment(Pos.CENTER_LEFT);
        return item;
    }

    private HBox crearLeyendaPoblaciones() {
        HBox box = new HBox(12,
                crearLeyendaItem("Presas", "#e48632"),
                crearLeyendaItem("Depredadores", "#d9534f"),
                crearLeyendaItem("Tercera especie", "#3da66d")
        );
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private HBox crearLeyendaOcupacion() {
        HBox box = new HBox(12,
                crearLeyendaItem("Ocupadas", "#d9534f"),
                crearLeyendaItem("Vacías", "#e6a63d")
        );
        box.setAlignment(Pos.CENTER);
        return box;
    }
    private void configurarBotonPlano(Button btn, String ruta) {
        ImageView iv = new ImageView(IconosUtils.cargarImagen(ruta));
        iv.setPreserveRatio(true);
        iv.setFitWidth(170);
        iv.setFitHeight(60);
        btn.setGraphic(iv);
        btn.setBackground(javafx.scene.layout.Background.EMPTY);
        btn.setBorder(javafx.scene.layout.Border.EMPTY);
        btn.setPickOnBounds(true);
        btn.setPadding(Insets.EMPTY);
    }

    public void setOnInicio(Runnable r) {
        btnInicio.setOnAction(e -> r.run());
    }

    public void setOnEnvioReporte(Runnable r) {
        btnEnvioReporte.setOnAction(e -> r.run());
    }

    public void setOnEnviarCorreo(Runnable r) {
        btnEnviarCorreo.setOnAction(e -> r.run());
    }
}

