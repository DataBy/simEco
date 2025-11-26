package com.mycompany.simulador.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class DialogoErrorView {

    public static void mostrar(String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR, mensaje, ButtonType.OK);
        a.setHeaderText("Error");
        a.showAndWait();
    }
}
