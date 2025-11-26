package com.mycompany.simulador.interfaces;

import java.io.File;

import com.mycompany.simulador.model.report.ReporteFinal;

public interface IReporteService {
    File generarReportePDF(ReporteFinal reporte);
}
