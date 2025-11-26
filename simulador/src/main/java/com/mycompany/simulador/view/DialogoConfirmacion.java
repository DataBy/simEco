package com.mycompany.simulador.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class DialogoConfirmacion {

    public static void mostrar(String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK);
        a.setHeaderText("Confirmación");
        a.showAndWait();
    }
}
