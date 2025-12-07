package com.mycompany.simulador.services.reportes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.mycompany.simulador.config.AppConfig;
import com.mycompany.simulador.config.Constantes;
import com.mycompany.simulador.interfaces.IReporteService;
import com.mycompany.simulador.model.report.ReporteFinal;

public class PdfService implements IReporteService {

    private static final float MARGIN = 40f;
    private static final float LEADING = 14f;

    @Override
    public File generarReportePDF(ReporteFinal r) {
        return generarReporteSimulaciones(List.of(r), "", Collections.emptyMap(), Collections.emptyList());
    }

    public File generarReporteSimulaciones(List<ReporteFinal> reportes) {
        return generarReporteSimulaciones(reportes, "", Collections.emptyMap(), Collections.emptyList());
    }

    /**
     * Genera un PDF profesional con gr├íbles pastel y resumenes.
     */
    public File generarReporteSimulaciones(List<ReporteFinal> reportes,
                                           String analisisFinal,
                                           Map<String, List<String>> historialPorEscenario,
                                           List<String> historialGlobal) {
        if (reportes == null) reportes = Collections.emptyList();
        if (historialPorEscenario == null) historialPorEscenario = Collections.emptyMap();
        if (historialGlobal == null) historialGlobal = Collections.emptyList();

        File pdf = archivoUnico("reporte_simulaciones");

        try (PDDocument doc = new PDDocument()) {
            List<ReporteFinal> lista = new ArrayList<>(reportes);
            lista.sort((a, b) -> safe(a.getEscenario()).compareToIgnoreCase(safe(b.getEscenario())));

            addPaginaResumen(doc, lista);
            for (ReporteFinal r : lista) {
                addPaginaEscenario(doc, r, historialPorEscenario.getOrDefault(safe(r.getEscenario()), Collections.emptyList()));
            }
            if (analisisFinal != null && !analisisFinal.isBlank()) {
                addPaginaAnalisis(doc, "Analisis comparativo final", analisisFinal);
            }
            addPaginaHistorial(doc, "Historial global de la simulacion", historialGlobal);

            Files.createDirectories(pdf.toPath().getParent());
            doc.save(pdf);
        } catch (IOException e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage(), e);
        }
        return pdf;
    }

    // ------------------------------------------------------
    // P├üginas
    // ------------------------------------------------------
    private void addPaginaResumen(PDDocument doc, List<ReporteFinal> reportes) throws IOException {
        PDPage page = new PDPage(PDRectangle.LETTER);
        doc.addPage(page);
        PDRectangle box = page.getMediaBox();

        int totalTurnos = reportes.stream().mapToInt(ReporteFinal::getTotalTurnos).sum();
        int presasFinales = reportes.stream().mapToInt(ReporteFinal::getPresasFinales).sum();
        int depredadoresFinales = reportes.stream().mapToInt(ReporteFinal::getDepredadoresFinales).sum();
        int tercerasFinales = reportes.stream().mapToInt(ReporteFinal::getTerceraEspecieFinal).sum();
        int turnoExtincion = reportes.stream().mapToInt(ReporteFinal::getTurnoExtincion).filter(v -> v >= 0).min().orElse(-1);
        double ocupacionProm = reportes.stream().mapToDouble(ReporteFinal::getPorcentajeOcupacionFinal).average().orElse(0);
        int totalCeldas = Constantes.MATRIZ_FILAS * Constantes.MATRIZ_COLUMNAS;
        int ocupadas = (int) Math.round(ocupacionProm * totalCeldas / 100.0);
        int vacias = Math.max(0, totalCeldas - ocupadas);

        BufferedImage piePoblaciones = crearPieChart("Poblaciones finales",
                Map.of("Presas", presasFinales,
                        "Depredadores", depredadoresFinales,
                        "Tercera especie", tercerasFinales),
                new Color[]{new Color(236, 112, 99), new Color(241, 196, 15), new Color(46, 204, 113)});

        BufferedImage pieOcupacion = crearPieChart("Ocupacion del ecosistema",
                Map.of("Ocupadas", ocupadas, "Vacias", vacias),
                new Color[]{new Color(231, 76, 60), new Color(241, 196, 15)});

        BufferedImage barras = crearBarChartPoblaciones(reportes);

        try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
            float y = box.getHeight() - MARGIN;

            y = drawTitle(cs, "Reporte de Simulaciones", MARGIN, y, 18);
            y = drawLine(cs, "Total de turnos ejecutados: " + totalTurnos, MARGIN, y, 12);
            y = drawLine(cs, "Turno de extincion: " + (turnoExtincion < 0 ? "N/A" : turnoExtincion), MARGIN, y, 12);
            y = drawLine(cs, String.format("Ocupacion promedio: %.1f%%", ocupacionProm), MARGIN, y, 12);
            y -= 10;

            float chartWidth = (box.getWidth() - MARGIN * 2 - 20) / 2;
            float chartHeight = 200;
            float yCharts = y - chartHeight;

            drawImage(doc, cs, piePoblaciones, MARGIN, yCharts, chartWidth, chartHeight);
            drawImage(doc, cs, pieOcupacion, MARGIN + chartWidth + 20, yCharts, chartWidth, chartHeight);

            y = yCharts - 20;
            if (barras != null) {
                float barHeight = 220;
                drawImage(doc, cs, barras, MARGIN, y - barHeight, box.getWidth() - MARGIN * 2, barHeight);
            }
        }
    }

    private void addPaginaEscenario(PDDocument doc, ReporteFinal r, List<String> historial) throws IOException {
        PDPage page = new PDPage(PDRectangle.LETTER);
        doc.addPage(page);
        PDRectangle box = page.getMediaBox();

        BufferedImage pieEsc = crearPieChart("Poblaciones finales",
                Map.of("Presas", r.getPresasFinales(),
                        "Depredadores", r.getDepredadoresFinales(),
                        "Tercera especie", r.getTerceraEspecieFinal()),
                new Color[]{new Color(236, 112, 99), new Color(241, 196, 15), new Color(46, 204, 113)});

        try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
            float y = box.getHeight() - MARGIN;
            y = drawTitle(cs, "Simulacion - " + safe(r.getEscenario()), MARGIN, y, 16);
            y = drawLine(cs, "Turnos ejecutados: " + r.getTotalTurnos(), MARGIN, y, 12);
            y = drawLine(cs, "Presas finales: " + r.getPresasFinales(), MARGIN, y, 12);
            y = drawLine(cs, "Depredadores finales: " + r.getDepredadoresFinales(), MARGIN, y, 12);
            y = drawLine(cs, "Tercera especie final: " + r.getTerceraEspecieFinal(), MARGIN, y, 12);
            y = drawLine(cs, "Turno de extincion: " + (r.getTurnoExtincion() < 0 ? "N/A" : r.getTurnoExtincion()), MARGIN, y, 12);
            y = drawLine(cs, String.format("Ocupacion final: %.1f%%", r.getPorcentajeOcupacionFinal()), MARGIN, y, 12);
            y -= 6;

            float chartW = 230;
            float chartH = 180;
            drawImage(doc, cs, pieEsc, MARGIN, y - chartH, chartW, chartH);

            float textStartX = MARGIN + chartW + 20;
            float textWidth = box.getWidth() - textStartX - MARGIN;
            y = y - 4;
            y = drawWrapped(cs, "Historial (primeros eventos):", textStartX, y, textWidth, 12);
            y -= 4;
            if (historial.isEmpty()) {
                drawLine(cs, "(sin eventos registrados)", textStartX, y, 11);
            } else {
                int count = 1;
                for (String ev : historial) {
                    y = drawWrapped(cs, count + ". " + ev, textStartX, y, textWidth, 11);
                    count++;
                    if (count > 12 || y < MARGIN + LEADING) {
                        break;
                    }
                }
            }
        }

        if (!historial.isEmpty()) {
            addPaginaHistorial(doc, "Historial " + safe(r.getEscenario()), historial);
        }
    }

    private void addPaginaAnalisis(PDDocument doc, String titulo, String texto) throws IOException {
        PDPage page = new PDPage(PDRectangle.LETTER);
        doc.addPage(page);
        PDRectangle box = page.getMediaBox();
        try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
            float y = box.getHeight() - MARGIN;
            y = drawTitle(cs, titulo, MARGIN, y, 16);
            drawParagraph(cs, texto, MARGIN, y, box.getWidth() - MARGIN * 2, 12);
        }
    }

    private void addPaginaHistorial(PDDocument doc, String titulo, List<String> eventos) throws IOException {
        PDPage page = new PDPage(PDRectangle.LETTER);
        doc.addPage(page);
        PDRectangle box = page.getMediaBox();
        PDPageContentStream cs = new PDPageContentStream(doc, page);
        try {
            float y = box.getHeight() - MARGIN;
            y = drawTitle(cs, titulo, MARGIN, y, 16);
            float maxWidth = box.getWidth() - MARGIN * 2;
            if (eventos.isEmpty()) {
                drawLine(cs, "(sin eventos registrados)", MARGIN, y, 12);
            } else {
                int idx = 1;
                for (String ev : eventos) {
                    y = drawWrapped(cs, idx + ". " + ev, MARGIN, y, maxWidth, 11);
                    idx++;
                    if (y < MARGIN + LEADING) {
                        cs.close();
                        page = new PDPage(PDRectangle.LETTER);
                        doc.addPage(page);
                        box = page.getMediaBox();
                        cs = new PDPageContentStream(doc, page);
                        y = box.getHeight() - MARGIN;
                        y = drawTitle(cs, titulo + " (continuacion)", MARGIN, y, 14);
                        y -= 6;
                    }
                }
            }
        } finally {
            cs.close();
        }
    }

    // ------------------------------------------------------
    // Helpers PDF
    // ------------------------------------------------------
    private float drawTitle(PDPageContentStream cs, String text, float x, float y, float size) throws IOException {
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
        return y - (size + 6);
    }

    private float drawLine(PDPageContentStream cs, String text, float x, float y, float size) throws IOException {
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
        return y - LEADING;
    }

    private float drawWrapped(PDPageContentStream cs, String text, float x, float y, float width, float size) throws IOException {
        List<String> lines = wrapText(text, PDType1Font.HELVETICA, size, width);
        float yy = y;
        for (String l : lines) {
            yy = drawLine(cs, l, x, yy, size);
        }
        return yy;
    }

    private void drawParagraph(PDPageContentStream cs, String text, float x, float y, float width, float size) throws IOException {
        List<String> lines = wrapText(text, PDType1Font.HELVETICA, size, width);
        float yy = y;
        for (String l : lines) {
            yy = drawLine(cs, l, x, yy, size);
        }
    }

    private void drawImage(PDDocument doc, PDPageContentStream cs, BufferedImage img, float x, float y, float width, float height) throws IOException {
        if (img == null) return;
        PDImageXObject pdImage = LosslessFactory.createFromImage(doc, img);
        cs.drawImage(pdImage, x, y, width, height);
    }

    private List<String> wrapText(String text, PDType1Font font, float fontSize, float width) throws IOException {
        if (text == null) return List.of("");

        final String tabSpaces = "    "; // expand tabs for consistent rendering
        List<String> result = new ArrayList<>();

        for (String rawLine : text.split("\\r?\\n", -1)) {
            String lineToWrap = rawLine.replace("\t", tabSpaces);
            if (lineToWrap.isEmpty()) {
                result.add("");
                continue;
            }
            String[] words = lineToWrap.split("\\s+");
            StringBuilder line = new StringBuilder();
            for (String w : words) {
                String candidate = line.length() == 0 ? w : line + " " + w;
                float size = fontSize * font.getStringWidth(candidate) / 1000;
                if (size > width && line.length() > 0) {
                    result.add(line.toString());
                    line = new StringBuilder(w);
                } else {
                    line = new StringBuilder(candidate);
                }
            }
            if (line.length() > 0) {
                result.add(line.toString());
            }
        }
        return result;
    }

    // ------------------------------------------------------
    // Charts
    // ------------------------------------------------------
    private BufferedImage crearPieChart(String titulo, Map<String, Integer> datos, Color[] colores) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        datos.forEach(dataset::setValue);
        JFreeChart chart = ChartFactory.createPieChart(titulo, dataset, true, false, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 10));
        int idx = 0;
        for (Comparable key : dataset.getKeys()) {
            Color c = colores[idx % colores.length];
            plot.setSectionPaint(key, c);
            idx++;
        }
        return chart.createBufferedImage(420, 320);
    }

    private BufferedImage crearBarChartPoblaciones(List<ReporteFinal> reportes) {
        if (reportes == null || reportes.isEmpty()) return null;
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (ReporteFinal r : reportes) {
            String esc = safe(r.getEscenario());
            dataset.addValue(r.getPresasFinales(), "Presas", esc);
            dataset.addValue(r.getDepredadoresFinales(), "Depredadores", esc);
            dataset.addValue(r.getTerceraEspecieFinal(), "Tercera especie", esc);
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Poblaciones finales por escenario",
                "Escenario",
                "Cantidad",
                dataset);
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRangeGridlinePaint(new Color(200, 200, 200));
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(236, 112, 99));
        renderer.setSeriesPaint(1, new Color(241, 196, 15));
        renderer.setSeriesPaint(2, new Color(46, 204, 113));
        renderer.setMaximumBarWidth(0.12);
        renderer.setDrawBarOutline(false);
        renderer.setDefaultOutlineStroke(new BasicStroke(0.5f));
        return chart.createBufferedImage(700, 320);
    }

    // ------------------------------------------------------
    // Utils
    // ------------------------------------------------------
    private File archivoUnico(String prefijo) {
        String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombre = prefijo + "_" + stamp + ".pdf";
        return AppConfig.getBaseData().resolve(nombre).toFile();
    }

    private String safe(String v) {
        return v == null ? "--" : v;
    }
}
