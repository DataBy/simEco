package com.mycompany.simulador.view;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public class AnalisisComparativoView {

    private final BorderPane root = new BorderPane();
    private final TextArea txtAnalisis = new TextArea();

    public AnalisisComparativoView() {
        txtAnalisis.setEditable(false);
        txtAnalisis.setWrapText(true);
        root.setPadding(new Insets(10));
        root.setCenter(txtAnalisis);
    }

    public Parent getRoot() { return root; }

    public void setAnalisis(String texto) {
        txtAnalisis.setText(texto);
    }
}
