package com.mycompany.simulador.controller;

import com.mycompany.simulador.config.Constantes;
import com.mycompany.simulador.view.MenuInicioView;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class MenuInicioController {

    private final Stage stage;
    private final MenuInicioView view;

    public MenuInicioController(Stage stage) {
        this.stage = stage;
        this.view = new MenuInicioView();

        Scene scene = new Scene(
                view.getRoot(),
                Constantes.VENTANA_ANCHO,
                Constantes.VENTANA_ALTO
        );

        stage.setScene(scene);

        // Ajusta el Stage exactamente al tamaño de la Scene
        stage.sizeToScene();

        // Centra la ventana en la pantalla
        stage.centerOnScreen();

        // Opcional: que no se pueda redimensionar
        stage.setResizable(false);
    }

    public MenuInicioView getView() {
        return view;
    }
}
