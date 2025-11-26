package com.mycompany.simulador.view;

import com.mycompany.simulador.config.RutasArchivos;
import com.mycompany.simulador.utils.IconosUtils;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MenuInicioView {

    private final StackPane root = new StackPane();
    private final Button btnSignIn = new Button();
    private final Button btnSignUp = new Button();
    private final Button btnExit   = new Button();

    public MenuInicioView() {

        // 🔥 Evita cualquier borde o color adicional en la vista
        root.setStyle("-fx-padding: 0; -fx-background-color: transparent;");

        // ======================================================
        // 1. CONFIGURACIÓN DE BOTONES CON HOVER REAL
        // ======================================================
        IconosUtils.configurarBotonConHover(
                btnSignIn,
                RutasArchivos.INICIO_BTN_SIGN_IN,
                RutasArchivos.INICIO_BTN_SIGN_IN_HOVER
        );

        IconosUtils.configurarBotonConHover(
                btnSignUp,
                RutasArchivos.INICIO_BTN_SIGN_UP,
                RutasArchivos.INICIO_BTN_SIGN_UP_HOVER
        );

        IconosUtils.configurarBotonConHover(
                btnExit,
                RutasArchivos.INICIO_BTN_EXIT,
                RutasArchivos.INICIO_BTN_EXIT_HOVER
        );

        // Contenedor vertical para los botones
        VBox vbox = new VBox(25); // separación entre botones
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(btnSignIn, btnSignUp, btnExit);

        // ======================================================
        // 2. FONDO RESPONSIVE (escala automáticamente)
        // ======================================================
        ImageView background = IconosUtils.crearImageViewFondo(
                RutasArchivos.INICIO_BACKGROUND,
                root
        );

        // ======================================================
        // 3. ORDEN CORRECTO DE CAPAS (z-index)
        // ======================================================
        root.getChildren().addAll(background, vbox);
    }

    public Parent getRoot() {
        return root;
    }

    // ======================================================
    // 4. Eventos de botones (controlador los usará)
    // ======================================================
    public void setOnSignIn(Runnable r) { btnSignIn.setOnAction(e -> r.run()); }
    public void setOnSignUp(Runnable r) { btnSignUp.setOnAction(e -> r.run()); }
    public void setOnExit(Runnable r)   { btnExit.setOnAction(e -> r.run()); }
}
