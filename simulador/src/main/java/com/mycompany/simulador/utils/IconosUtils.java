package com.mycompany.simulador.utils;

import java.io.InputStream;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.Region;

public final class IconosUtils {

    private IconosUtils() {}

    public static Image cargarImagen(String ruta) {
        InputStream is = IconosUtils.class.getResourceAsStream(ruta);
        if (is == null) {
            LogUtils.error("No se encontró el recurso: " + ruta, null);
            return null;
        }
        return new Image(is);
    }

    public static ImageView crearImageViewFondo(String ruta, Region bindTo) {
        Image img = cargarImagen(ruta);
        ImageView iv = new ImageView(img);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        // Responsive: el fondo se adapta al tamaño del contenedor
        iv.fitWidthProperty().bind(bindTo.widthProperty());
        iv.fitHeightProperty().bind(bindTo.heightProperty());
        return iv;
    }

    public static void configurarBotonConHover(Button boton,
                                               String rutaNormal,
                                               String rutaHover) {
        Image normal = cargarImagen(rutaNormal);
        Image hover  = cargarImagen(rutaHover);

        ImageView iv = new ImageView(normal);
        iv.setPreserveRatio(true);
        boton.setGraphic(iv);

        boton.setBackground(Background.EMPTY);
        boton.setBorder(Border.EMPTY);
        boton.setText(null);
        boton.setPickOnBounds(true); // permite clickear por la imagen

        boton.setOnMouseEntered(e -> {
            if (hover != null) iv.setImage(hover);
        });
        boton.setOnMouseExited(e -> {
            if (normal != null) iv.setImage(normal);
        });
    }
}
