package com.mycompany.simulador.view;

import com.mycompany.simulador.services.reportes.GraficosService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ReportesView {

    private final BorderPane root = new BorderPane();

    private final Label lblTotalTurnos = new Label();
    private final Label lblTurnoExtincion = new Label();
    private final VBox panelGraficoPoblaciones = new VBox();
    private final VBox panelGraficoOcupacion = new VBox();
    private final Button btnGenerarPdfYEnviar = new Button("Generar PDF y enviar por correo");
    private static final double PANEL_GRAF_WIDTH = 340;

    public ReportesView() {
        construirLayout();
    }

    private void construirLayout() {
        HBox top = new HBox(20);
        top.setPadding(new Insets(15));
        top.setAlignment(Pos.CENTER_LEFT);
        top.getChildren().addAll(
                new Label("Total de turnos:"), lblTotalTurnos,
                new Label("Turno de extincion:"), lblTurnoExtincion
        );

        configurarPanelGrafico(panelGraficoPoblaciones);
        configurarPanelGrafico(panelGraficoOcupacion);

        HBox center = new HBox(30, panelGraficoPoblaciones, panelGraficoOcupacion);
        center.setPadding(new Insets(20));
        center.setAlignment(Pos.CENTER);
        HBox.setHgrow(panelGraficoPoblaciones, Priority.ALWAYS);
        HBox.setHgrow(panelGraficoOcupacion, Priority.ALWAYS);

        HBox bottom = new HBox(btnGenerarPdfYEnviar);
        bottom.setPadding(new Insets(10));
        bottom.setAlignment(Pos.CENTER);

        root.setTop(top);
        root.setCenter(center);
        root.setBottom(bottom);
    }

    public Parent getRoot() { return root; }

    public void actualizarDatos(int totalTurnos, int presas, int depredadores, int terceras,
                                int turnoExtincion, int ocupadas, int vacias) {
        lblTotalTurnos.setText(String.valueOf(totalTurnos));
        lblTurnoExtincion.setText(turnoExtincion < 0 ? "N/A" : String.valueOf(turnoExtincion));

        GraficosService g = new GraficosService();
        panelGraficoPoblaciones.getChildren().setAll(
                g.crearGraficoPoblaciones(presas, depredadores, terceras)
        );
        panelGraficoOcupacion.getChildren().setAll(
                g.crearGraficoOcupacion(ocupadas, vacias)
        );
    }

    public void setOnGenerarPdfYEnviar(Runnable r) {
        btnGenerarPdfYEnviar.setOnAction(e -> r.run());
    }

    private void configurarPanelGrafico(VBox panel) {
        panel.setSpacing(10);
        panel.setAlignment(Pos.CENTER);
        panel.setFillWidth(false);
        panel.setPadding(new Insets(10));
        panel.setMinWidth(PANEL_GRAF_WIDTH);
        panel.setPrefWidth(PANEL_GRAF_WIDTH);
    }
}
