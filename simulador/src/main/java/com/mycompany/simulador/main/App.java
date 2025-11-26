package com.mycompany.simulador.main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.mycompany.simulador.view.MenuInicioView;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        // Aquí cargas tu primera vista
        MenuInicioView vistaInicial = new MenuInicioView();

        Scene scene = new Scene(vistaInicial.getRoot());

        stage.setScene(scene);
        stage.setTitle("Simulador");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
