package com.mycompany.simulador.services.reportes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;

public class GraficosService {

    private static final double PREF_CHART_SIZE = 380;

    public Node crearGraficoPastel(int valorA, int valorB,
                                   String etiquetaA, String etiquetaB) {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList(
                new PieChart.Data(etiquetaA, valorA),
                new PieChart.Data(etiquetaB, valorB)
        );
        return crearPieComun(data);
    }

    public Node crearGraficoPoblaciones(int presas, int depredadores, int tercera) {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList(
                new PieChart.Data("Presas", presas),
                new PieChart.Data("Depredadores", depredadores),
                new PieChart.Data("Tercera especie", tercera)
        );
        return crearPieComun(data);
    }

    public Node crearGraficoOcupacion(int ocupadas, int vacias) {
        return crearGraficoPastel(ocupadas, vacias, "Ocupadas", "Vac\u00edas");
    }

    private PieChart crearPieComun(ObservableList<PieChart.Data> data) {
        PieChart chart = new PieChart(data);
        chart.setLegendVisible(true);
        chart.setLegendSide(Side.BOTTOM);
        chart.setLabelsVisible(true);
        chart.setClockwise(true);
        chart.setMinSize(PREF_CHART_SIZE, PREF_CHART_SIZE);
        chart.setPrefSize(PREF_CHART_SIZE, PREF_CHART_SIZE);
        chart.setMaxSize(PREF_CHART_SIZE, PREF_CHART_SIZE);
        chart.setAnimated(false);
        return chart;
    }
}
