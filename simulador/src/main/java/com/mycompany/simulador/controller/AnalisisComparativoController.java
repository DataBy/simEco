package com.mycompany.simulador.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.mycompany.simulador.dto.EstadoTurnoDTO;
import com.mycompany.simulador.repository.EstadoTurnosRepositoryTXT;
import com.mycompany.simulador.view.AnalisisComparativoView;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class AnalisisComparativoController {

    private final Stage stage;
    private final AnalisisComparativoView view;
    private final EstadoTurnosRepositoryTXT repo = new EstadoTurnosRepositoryTXT();

    public AnalisisComparativoController(Stage stage) {
        this.stage = stage;
        this.view = new AnalisisComparativoView();
        Scene scene = new Scene(view.getRoot(), stage.getWidth(), stage.getHeight());
        stage.setScene(scene);
        generarAnalisis();
    }

    private void generarAnalisis() {
        List<EstadoTurnoDTO> estados = repo.cargarEstados();
        StringBuilder sb = new StringBuilder();

        for (String esc : new String[]{
                "EQUILIBRADO",
                "DEPREDADORES_DOMINANTES",
                "PRESAS_DOMINANTES"}) {

            List<EstadoTurnoDTO> lista = estados.stream()
                    .filter(e -> e.getEscenario().equals(esc))
                    .collect(Collectors.toList());
            if (lista.isEmpty()) continue;
            EstadoTurnoDTO ultimo = lista.get(lista.size() - 1);
            sb.append("Escenario ").append(esc).append(":\n");
            sb.append("  Turnos ejecutados: ").append(ultimo.getTurno()).append("\n");
            sb.append("  Presas finales: ").append(ultimo.getPresas()).append("\n");
            sb.append("  Depredadores finales: ").append(ultimo.getDepredadores()).append("\n");
            sb.append("  Tercera especie final: ").append(ultimo.getTerceraEspecie()).append("\n\n");
        }

        view.setAnalisis(sb.toString());
    }
}
