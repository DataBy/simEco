package com.mycompany.simulador.services.reportes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.mycompany.simulador.config.AppConfig;
import com.mycompany.simulador.interfaces.IReporteService;
import com.mycompany.simulador.model.report.ReporteFinal;
import com.mycompany.simulador.utils.LogUtils;

public class PdfService implements IReporteService {

    @Override
    public File generarReportePDF(ReporteFinal r) {
        File pdf = AppConfig.getBaseData().resolve("reporte_simulacion.pdf").toFile();
        try (FileWriter fw = new FileWriter(pdf)) {
            fw.write("REPORTE DE SIMULACION\n\n");
            fw.write("Total de turnos: " + r.getTotalTurnos() + "\n");
            fw.write("Presas finales: " + r.getPresasFinales() + "\n");
            fw.write("Depredadores finales: " + r.getDepredadoresFinales() + "\n");
            fw.write("Tercera especie final: " + r.getTerceraEspecieFinal() + "\n");
            fw.write("Turno de extincion: " + r.getTurnoExtincion() + "\n");
            fw.write("Porcentaje ocupacion final: " + r.getPorcentajeOcupacionFinal() + "\n");
        } catch (IOException e) {
            LogUtils.error("Error creando reporte PDF", e);
        }
        return pdf;
    }
}
