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

    public ReportesView() {
        construirLayout();
    }

    private void construirLayout() {
        HBox top = new HBox(20);
        top.setPadding(new Insets(15));
        top.setAlignment(Pos.CENTER_LEFT);
        top.getChildren().addAll(
                new Label("Total de turnos:"), lblTotalTurnos,
                new Label("Turno de extinción:"), lblTurnoExtincion
        );

        HBox center = new HBox(10, panelGraficoPoblaciones, panelGraficoOcupacion);
        center.setPadding(new Insets(10));
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

    public void actualizarDatos(int totalTurnos, int presas, int depredadores,
                                int turnoExtincion, int ocupadas, int vacias) {
        lblTotalTurnos.setText(String.valueOf(totalTurnos));
        lblTurnoExtincion.setText(turnoExtincion < 0 ? "N/A" : String.valueOf(turnoExtincion));

        GraficosService g = new GraficosService();
        panelGraficoPoblaciones.getChildren().setAll(
                g.crearGraficoPastel(presas, depredadores, "Presas", "Depredadores")
        );
        panelGraficoOcupacion.getChildren().setAll(
                g.crearGraficoOcupacion(ocupadas, vacias)
        );
    }

    public void setOnGenerarPdfYEnviar(Runnable r) {
        btnGenerarPdfYEnviar.setOnAction(e -> r.run());
    }
}
