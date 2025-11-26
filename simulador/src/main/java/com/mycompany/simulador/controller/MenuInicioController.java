package com.mycompany.simulador.controller;

import com.mycompany.simulador.utils.VentanaUtils;
import com.mycompany.simulador.view.MenuInicioView;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MenuInicioController {

    private final Stage stage;
    private final MenuInicioView view;

    public MenuInicioController(Stage stage) {
        this.stage = stage;
        this.view = new MenuInicioView();
        Scene scene = new Scene(view.getRoot());
        stage.setScene(scene);
        VentanaUtils.configurarVentanaEstandar(stage);
        init();
    }

    private void init() {
        view.setOnSignIn(() -> new LoginController(stage));
        view.setOnSignUp(() -> new RegistroController(stage));
        view.setOnExit(Platform::exit);
    }
}
