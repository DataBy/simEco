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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ResumenView {

    private final StackPane root = new StackPane();
    private final VBox content = new VBox(24);

    private final Spinner<Integer> spTotalTurnos = new Spinner<>();
    private final TextField txtTurnoExtincion = new TextField();
    private final VBox panelGraficoPoblaciones = new VBox();
    private final VBox panelGraficoOcupacion = new VBox();
    private final VBox panelComparativo = new VBox();
    private final TextField txtComentario = new TextField();
    private static final double PANEL_GRAF_WIDTH = 400;

    public ResumenView() {
        construirLayout();
    }

    private void construirLayout() {
        ImageView bg = IconosUtils.crearImageViewFondo(RutasArchivos.RESUMEN_BACKGROUND, root);

        spTotalTurnos.setEditable(false);
        spTotalTurnos.setPrefWidth(140);
        spTotalTurnos.setStyle("-fx-opacity: 1; -fx-background-color: rgba(255,255,255,0.85);");
        spTotalTurnos.setFocusTraversable(false);
        txtTurnoExtincion.setEditable(false);
        txtTurnoExtincion.setPrefWidth(180);
        txtTurnoExtincion.setStyle("-fx-opacity: 1; -fx-background-color: rgba(255,255,255,0.85);");
        txtComentario.setEditable(false);
        txtComentario.setPrefWidth(260);

        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(26));
        content.setMaxWidth(1100);
        content.setMinWidth(780);

        HBox top = new HBox(18);
        top.setAlignment(Pos.CENTER);
        top.getChildren().addAll(
                new Label("Total de turnos ejecutados:"), spTotalTurnos,
                new Label("Turno de extincion:"), txtTurnoExtincion
        );

        configurarPanelGrafico(panelGraficoPoblaciones);
        configurarPanelGrafico(panelGraficoOcupacion);

        VBox grafPoblacionesBox = wrapGrafico("Poblaciones finales", panelGraficoPoblaciones);
        VBox grafOcupacionBox = wrapGrafico("Ocupacion del tablero", panelGraficoOcupacion);

        HBox center = new HBox(40, grafPoblacionesBox, grafOcupacionBox);
        center.setAlignment(Pos.CENTER);
        HBox.setHgrow(grafPoblacionesBox, Priority.ALWAYS);
        HBox.setHgrow(grafOcupacionBox, Priority.ALWAYS);

        panelComparativo.setSpacing(8);
        panelComparativo.setPadding(new Insets(12, 12, 12, 12));
        panelComparativo.setAlignment(Pos.CENTER_LEFT);
        panelComparativo.setStyle("""
            -fx-background-color: rgba(255, 255, 255, 0.3);
            -fx-background-radius: 12;
        """);
        panelComparativo.setMaxWidth(900);
        panelComparativo.getChildren().addAll(new Label("Analisis comparativo global:"), txtComentario);

        content.getChildren().addAll(top, center, panelComparativo);
        StackPane.setAlignment(content, Pos.CENTER);

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
        int presasDom = 0, depredadoresDom = 0;
        for (ReporteFinal r : reportes) {
            if (r.getPresasFinales() > r.getDepredadoresFinales()) presasDom++;
            else if (r.getDepredadoresFinales() > r.getPresasFinales()) depredadoresDom++;
        }
        String comentario;
        if (presasDom == depredadoresDom) comentario = "Equilibrio similar entre escenarios.";
        else if (presasDom > depredadoresDom) comentario = "Predominio de presas en mas escenarios.";
        else comentario = "Predominio de depredadores en mas escenarios.";
        txtComentario.setText(comentario);
    }

    private void configurarPanelGrafico(VBox panel) {
        panel.setSpacing(10);
        panel.setAlignment(Pos.CENTER);
        panel.setFillWidth(false);
        panel.setPadding(new Insets(10));
        panel.setMinWidth(PANEL_GRAF_WIDTH);
        panel.setPrefWidth(PANEL_GRAF_WIDTH);
    }

    private VBox wrapGrafico(String titulo, VBox panel) {
        Label lbl = new Label(titulo);
        lbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #3b2a18;");
        VBox cont = new VBox(12, lbl, panel);
        cont.setAlignment(Pos.CENTER);
        cont.setFillWidth(false);
        return cont;
    }
}
