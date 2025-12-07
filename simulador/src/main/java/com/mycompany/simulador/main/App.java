//mvn clean javafx:run 
// gocodeia@gmail.com
// admin@POO
// hnhh zqqw pkog ysgd


package com.mycompany.simulador.main;

import com.mycompany.simulador.controller.MenuInicioController;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Simulador de Ecosistema");
        new MenuInicioController(primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
