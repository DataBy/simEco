package com.mycompany.simulador.dto;

import com.mycompany.simulador.model.report.ReporteFinal;

public class ReporteDTO {

    private ReporteFinal reporteFinal;

    public ReporteDTO(ReporteFinal reporteFinal) {
        this.reporteFinal = reporteFinal;
    }

    public ReporteFinal getReporteFinal() {
        return reporteFinal;
    }
}
