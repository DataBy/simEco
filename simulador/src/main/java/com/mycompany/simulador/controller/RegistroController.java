package com.mycompany.simulador.controller;

import com.mycompany.simulador.dto.UsuarioDTO;
import com.mycompany.simulador.repository.UsuarioRepositoryTXT;
import com.mycompany.simulador.services.usuario.UsuarioService;
import com.mycompany.simulador.view.RegistroView;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class RegistroController {

    private final Stage stage;
    private final RegistroView view;
    private final UsuarioService usuarioService;

    public RegistroController(Stage stage) {
        this.stage = stage;
        this.view = new RegistroView();
        this.usuarioService = new UsuarioService(new UsuarioRepositoryTXT());
        Scene scene = new Scene(view.getRoot(), stage.getWidth(), stage.getHeight());
        stage.setScene(scene);
        init();
    }

    private void init() {
        view.setOnListo(() -> {
            UsuarioDTO dto = new UsuarioDTO();
            dto.setCedula(view.getCedula());
            dto.setNombre(view.getNombre());
            dto.setFechaNacimiento(view.getFechaNacimiento());
            dto.setGenero(view.getGenero());
            dto.setCorreo(view.getCorreo());
            dto.setContrasenaPlano(view.getContrasena());

            String error = usuarioService.registrarUsuario(dto);
            if (error != null) {
                view.mostrarError(error);
            } else {
                view.mostrarMensaje("Usuario registrado correctamente");
            }
        });

        view.setOnVolver(() -> new MenuInicioController(stage));
    }
}
