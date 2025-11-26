package com.mycompany.simulador.utils;

import com.mycompany.simulador.config.Constantes;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

public final class VentanaUtils {

    private VentanaUtils() {}

    public static void configurarVentanaEstandar(Stage stage) {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        double baseW = Constantes.VENTANA_ANCHO;
        double baseH = Constantes.VENTANA_ALTO;

        double escala = Math.min(bounds.getWidth() / baseW,
                                 bounds.getHeight() / baseH);

        double ancho = baseW * escala * 0.95;
        double alto  = baseH * escala * 0.95;

        stage.setWidth(ancho);
        stage.setHeight(alto);
        stage.setX(bounds.getMinX() + (bounds.getWidth() - ancho) / 2);
        stage.setY(bounds.getMinY() + (bounds.getHeight() - alto) / 2);
    }
}
