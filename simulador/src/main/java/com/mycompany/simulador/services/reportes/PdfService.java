package com.mycompany.simulador.services.reportes;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.simulador.config.AppConfig;
import com.mycompany.simulador.interfaces.IReporteService;
import com.mycompany.simulador.model.report.ReporteFinal;
import com.mycompany.simulador.config.Constantes;
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

    /**
     * Genera un reporte consolidado con todas las simulaciones ejecutadas, ordenado por numero de simulacion.
     * Incluye resumen por simulacion y el detalle de cada turno con su matriz final.
     */
    public File generarReporteSimulaciones(List<ReporteFinal> reportes) {
        if (reportes == null) reportes = Collections.emptyList();
        File pdf = AppConfig.getBaseData().resolve("reporte_simulaciones.pdf").toFile();
        Map<String, List<EstadoTurnoDetalle>> estados = cargarEstadosPorEscenario();

        List<String> lineas = new ArrayList<>();
        lineas.add("REPORTE DE SIMULACIONES");
        lineas.add("");
        int index = 1;
        for (ReporteFinal r : reportes) {
            if (r == null) continue;
            String escenario = r.getEscenario() == null ? "--" : r.getEscenario();
            lineas.add("Simulacion " + index + " - " + escenario);
            lineas.add("Turnos ejecutados: " + r.getTotalTurnos());
            lineas.add("Presas finales: " + r.getPresasFinales());
            lineas.add("Depredadores finales: " + r.getDepredadoresFinales());
            lineas.add("Tercera especie final: " + r.getTerceraEspecieFinal());
            lineas.add("Turno de extincion: " + (r.getTurnoExtincion() < 0 ? "N/A" : r.getTurnoExtincion()));
            lineas.add(String.format("Ocupacion final: %.1f%%", r.getPorcentajeOcupacionFinal()));
            lineas.add("");

            List<EstadoTurnoDetalle> lista = estados.getOrDefault(escenario, Collections.emptyList());
            lista.sort(Comparator.comparingInt(d -> d.turno));
            for (EstadoTurnoDetalle d : lista) {
                lineas.add("  Turno " + d.turno + " - Presas=" + d.presas + ", Depredadores=" + d.depredadores
                        + ", Tercera=" + d.tercera + ", Ocupadas=" + d.ocupadas);
                for (String fila : d.matriz) {
                    lineas.add("    " + fila);
                }
                lineas.add("");
            }
            lineas.add("------------------------------------------------------------");
            lineas.add("");
            index++;
        }

        generarPdfSimple(pdf.toPath(), lineas);
        return pdf;
    }

    private Map<String, List<EstadoTurnoDetalle>> cargarEstadosPorEscenario() {
        Path path = AppConfig.ARCHIVO_ESTADO_TURNOS;
        if (!Files.exists(path)) return Collections.emptyMap();
        Map<String, List<EstadoTurnoDetalle>> mapa = new HashMap<>();
        try {
            List<String> lineas = Files.readAllLines(path);
            int filas = Constantes.MATRIZ_FILAS;
            int cols = Constantes.MATRIZ_COLUMNAS;
            for (int i = 0; i < lineas.size(); i++) {
                String l = lineas.get(i);
                if (!l.startsWith("ESCENARIO=")) continue;
                EstadoTurnoDetalle det = parsearHeader(l);
                List<String> matriz = new ArrayList<>();
                for (int f = 0; f < filas && (i + 1 + f) < lineas.size(); f++) {
                    String row = lineas.get(i + 1 + f);
                    if (row.startsWith("ESCENARIO=") || row.startsWith("----")) break;
                    if (row.length() > cols) row = row.substring(0, cols);
                    matriz.add(row);
                }
                det.matriz.addAll(matriz);
                mapa.computeIfAbsent(det.escenario, k -> new ArrayList<>()).add(det);
            }
        } catch (IOException e) {
            LogUtils.error("Error leyendo estados para el reporte", e);
        }
        return mapa;
    }

    private EstadoTurnoDetalle parsearHeader(String linea) {
        // Formato: ESCENARIO=X;TURNO=1;PRESAS=..;DEPREDADORES=..;TERCERA=..;OCUPADAS=..
        String[] partes = linea.split(";");
        String esc = partes[0].split("=")[1];
        int turno = extraerEntero(partes, "TURNO");
        int presas = extraerEntero(partes, "PRESAS");
        int depredadores = extraerEntero(partes, "DEPREDADORES");
        int tercera = extraerEntero(partes, "TERCERA");
        int ocupadas = extraerEntero(partes, "OCUPADAS");
        return new EstadoTurnoDetalle(esc, turno, presas, depredadores, tercera, ocupadas);
    }

    private int extraerEntero(String[] partes, String clave) {
        for (String p : partes) {
            if (p.startsWith(clave + "=")) {
                try { return Integer.parseInt(p.split("=")[1]); }
                catch (NumberFormatException ignored) { return 0; }
            }
        }
        return 0;
    }

    private static class EstadoTurnoDetalle {
        final String escenario;
        final int turno;
        final int presas;
        final int depredadores;
        final int tercera;
        final int ocupadas;
        final List<String> matriz = new ArrayList<>();

        EstadoTurnoDetalle(String escenario, int turno, int presas, int depredadores, int tercera, int ocupadas) {
            this.escenario = escenario;
            this.turno = turno;
            this.presas = presas;
            this.depredadores = depredadores;
            this.tercera = tercera;
            this.ocupadas = ocupadas;
        }
    }

    /**
     * Genera un PDF sencillo (texto) con una sola pУgina usando comandos bбsicos.
     */
    private void generarPdfSimple(Path destino, List<String> lineas) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            List<Integer> offsets = new ArrayList<>();
            java.util.function.Consumer<String> write = s -> {
                try { out.write(s.getBytes(StandardCharsets.US_ASCII)); }
                catch (IOException ignored) { }
            };

            write.accept("%PDF-1.4\n");

            // Obj1: catalog
            offsets.add(out.size());
            write.accept("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

            // Obj2: pages
            offsets.add(out.size());
            write.accept("2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");

            // Obj3: page
            offsets.add(out.size());
            write.accept("3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 4 0 R /Resources << /Font << /F1 5 0 R >> >> >>\nendobj\n");

            // Obj4: content stream
            StringBuilder contenido = new StringBuilder();
            contenido.append("BT\n/F1 12 Tf\n12 TL\n50 750 Td\n");
            for (String l : lineas) {
                contenido.append("(").append(escapar(l)).append(") Tj\nT*\n");
            }
            contenido.append("ET\n");
            byte[] contenidoBytes = contenido.toString().getBytes(StandardCharsets.US_ASCII);

            offsets.add(out.size());
            write.accept("4 0 obj\n<< /Length " + contenidoBytes.length + " >>\nstream\n");
            try { out.write(contenidoBytes); } catch (IOException ignored) { }
            write.accept("endstream\nendobj\n");

            // Obj5: font
            offsets.add(out.size());
            write.accept("5 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n");

            // xref
            int xrefStart = out.size();
            write.accept("xref\n0 6\n0000000000 65535 f \n");
            for (int off : offsets) {
                write.accept(String.format("%010d 00000 n \n", off));
            }
            write.accept("trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n");
            write.accept(String.valueOf(xrefStart));
            write.accept("\n%%EOF");

            Files.createDirectories(destino.getParent());
            try (FileOutputStream fos = new FileOutputStream(destino.toFile())) {
                out.writeTo(fos);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage(), e);
        }
    }

    private String escapar(String texto) {
        if (texto == null) return "";
        return texto
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)");
    }
}
