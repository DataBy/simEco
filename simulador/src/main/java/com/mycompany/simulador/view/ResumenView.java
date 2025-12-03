package com.mycompany.simulador.view;

import com.mycompany.simulador.config.RutasArchivos;
import com.mycompany.simulador.model.report.ReporteFinal;
import com.mycompany.simulador.services.reportes.GraficosService;
import com.mycompany.simulador.utils.IconosUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ResumenView {

    private final StackPane root = new StackPane();
    private final BorderPane content = new BorderPane();

    private final Spinner<Integer> spTotalTurnos = new Spinner<>();
    private final TextField txtTurnoExtincion = new TextField();
    private final VBox panelGraficoPoblaciones = new VBox();
    private final VBox panelGraficoOcupacion = new VBox();
    private final VBox panelComparativo = new VBox();
    private final TextField txtComentario = new TextField();

    public ResumenView() {
        construirLayout();
    }

    private void construirLayout() {
        ImageView bg = IconosUtils.crearImageViewFondo(RutasArchivos.RESUMEN_BACKGROUND, root);

        spTotalTurnos.setEditable(false);
        spTotalTurnos.setDisable(true);
        txtTurnoExtincion.setEditable(false);
        txtComentario.setEditable(false);

        HBox top = new HBox(20);
        top.setPadding(new Insets(15));
        top.setAlignment(Pos.CENTER_LEFT);
        top.getChildren().addAll(
                new Label("Total de turnos ejecutados:"), spTotalTurnos,
                new Label("Turno de extinción:"), txtTurnoExtincion
        );

        panelComparativo.setSpacing(8);
        panelComparativo.setPadding(new Insets(10));
        panelComparativo.getChildren().addAll(new Label("Análisis comparativo global:"), txtComentario);

        HBox center = new HBox(20, panelGraficoPoblaciones, panelGraficoOcupacion, panelComparativo);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(20));
        HBox.setHgrow(panelGraficoPoblaciones, Priority.ALWAYS);
        HBox.setHgrow(panelGraficoOcupacion, Priority.ALWAYS);
        HBox.setHgrow(panelComparativo, Priority.ALWAYS);

        content.setTop(top);
        content.setCenter(center);
        root.getChildren().addAll(bg, content);
    }

    public Parent getRoot() {
        return root;
    }

    public void actualizarDatos(int totalTurnos, int presas, int depredadores,
                                int turnoExtincion, int ocupadas, int vacias) {
        spTotalTurnos.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Math.max(totalTurnos, 1), totalTurnos));
        txtTurnoExtincion.setText(turnoExtincion < 0 ? "N/A" : String.valueOf(turnoExtincion));

        GraficosService g = new GraficosService();
        panelGraficoPoblaciones.getChildren().setAll(
                g.crearGraficoPastel(presas, depredadores, "Presas", "Depredadores")
        );
        panelGraficoOcupacion.getChildren().setAll(
                g.crearGraficoOcupacion(ocupadas, vacias)
        );
    }

    public void actualizarComparativo(java.util.List<ReporteFinal> reportes) {
        if (reportes == null || reportes.isEmpty()) return;
        int presasDom = 0, depredadoresDom = 0;
        for (ReporteFinal r : reportes) {
            if (r.getPresasFinales() > r.getDepredadoresFinales()) presasDom++;
            else if (r.getDepredadoresFinales() > r.getPresasFinales()) depredadoresDom++;
        }
        String comentario;
        if (presasDom == depredadoresDom) comentario = "Equilibrio similar entre escenarios.";
        else if (presasDom > depredadoresDom) comentario = "Predominio de presas en más escenarios.";
        else comentario = "Predominio de depredadores en más escenarios.";
        txtComentario.setText(comentario);
    }
}
