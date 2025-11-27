package com.mycompany.simulador.controller;

import com.mycompany.simulador.dto.LoginDTO;
import com.mycompany.simulador.repository.UsuarioRepositoryTXT;
import com.mycompany.simulador.services.usuario.AutenticacionService;
import com.mycompany.simulador.view.LoginView;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginController {

    private final Stage stage;
    private final LoginView view;
    private final AutenticacionService authService;

    public LoginController(Stage stage) {
        this.stage = stage;
        this.view = new LoginView();
        this.authService = new AutenticacionService(new UsuarioRepositoryTXT());
        Scene scene = new Scene(view.getRoot(), stage.getWidth(), stage.getHeight());
        stage.setScene(scene);
        init();
    }

    private void init() {
        view.setOnIngresar(() -> {
            int cedula = view.getCedula();
            String pass = view.getContrasena();
            if (cedula <= 0 || pass.isBlank()) {
                view.mostrarError("Debe ingresar cédula y contraseña");
                return;
            }
            var usuario = authService.autenticar(new LoginDTO(cedula, pass));
            if (usuario == null) {
                view.mostrarError("Credenciales inválidas");
            } else {
                new SimulacionController(stage, usuario.getCorreo());
            }
        });

        view.setOnVolver(() -> new MenuInicioController(stage));
    }
}
