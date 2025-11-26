package com.mycompany.simulador.controller;

import java.io.File;

import com.mycompany.simulador.model.report.ReporteFinal;
import com.mycompany.simulador.services.correo.CorreoService;
import com.mycompany.simulador.services.reportes.PdfService;
import com.mycompany.simulador.view.DialogoConfirmacion;
import com.mycompany.simulador.view.ReportesView;

import javafx.stage.Stage;

public class ReportesController {

    private final Stage stage;
    private final ReportesView view;
    private final ReporteFinal reporte;
    private final String correoUsuario;
    private final PdfService pdfService = new PdfService();
    private final CorreoService correoService = new CorreoService();

    public ReportesController(Stage stage, ReportesView view,
                              ReporteFinal reporte, String correoUsuario,
                              int totalCeldas) {
        this.stage = stage;
        this.view = view;
        this.reporte = reporte;
        this.correoUsuario = correoUsuario;
        init();
    }

    private void init() {
        view.setOnGenerarPdfYEnviar(() -> {
            File pdf = pdfService.generarReportePDF(reporte);
            correoService.enviarCorreoConAdjunto(
                    correoUsuario,
                    "Reporte simulación ecosistema",
                    "Adjunto el reporte de la simulación.",
                    pdf);
            DialogoConfirmacion.mostrar("Reporte generado y correo simulado.");
        });
    }
}
