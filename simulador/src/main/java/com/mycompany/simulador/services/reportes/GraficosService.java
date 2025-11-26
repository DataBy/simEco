package com.mycompany.simulador.services.reportes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;

public class GraficosService {

    public Node crearGraficoPastel(int valorA, int valorB,
                                   String etiquetaA, String etiquetaB) {
        int total = valorA + valorB;
        if (total <= 0) total = 1;
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList(
                new PieChart.Data(etiquetaA, valorA),
                new PieChart.Data(etiquetaB, valorB)
        );
        PieChart chart = new PieChart(data);
        chart.setLegendVisible(true);
        chart.setLabelsVisible(true);
        chart.setClockwise(true);
        return chart;
    }

    public Node crearGraficoOcupacion(int ocupadas, int vacias) {
        return crearGraficoPastel(ocupadas, vacias, "Ocupadas", "Vacías");
    }
}
