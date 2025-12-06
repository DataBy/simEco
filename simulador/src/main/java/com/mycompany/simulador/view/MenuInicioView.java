package com.mycompany.simulador.view;

import com.mycompany.simulador.config.RutasArchivos;
import com.mycompany.simulador.utils.IconosUtils;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class MenuInicioView {

    private final StackPane root = new StackPane();
    private final Button btnSignIn = new Button();
    private final Button btnSignUp = new Button();
    private final Button btnExit   = new Button();

    public MenuInicioView() {

        // Fondo sin bordes
        root.setStyle("-fx-padding: 0; -fx-background-color: transparent;");

        // ======================================================
        // 1. Configurar iconos con hover
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

        // ======================================================
        // 2. Fondo responsive
        // ======================================================
        ImageView background = new ImageView(
                IconosUtils.cargarImagen(RutasArchivos.INICIO_BACKGROUND)
        );

        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(root.widthProperty().add(20));
        background.fitHeightProperty().bind(root.heightProperty().add(20));
        background.setTranslateX(-10);
        background.setTranslateY(-10);


        // ======================================================
        // 3. BOTONES POSICIONADOS COMO PEDISTE
        // ======================================================

        // Botones SignIn y SignUp juntos, abajo, horizontalmente
        HBox bottomButtons = new HBox(20);
        bottomButtons.setAlignment(Pos.CENTER);
        bottomButtons.getChildren().addAll(btnSignIn, btnSignUp);

        // Los bajo 80px para que estén justo sobre la parte inferior del cuadro central
        bottomButtons.setTranslateY(270);  


        // Botón Exit arriba a la izquierda
        StackPane.setAlignment(btnExit, Pos.TOP_LEFT);
        btnExit.setTranslateX(462); // peque�o ajuste a la derecha
        btnExit.setTranslateY(30);

        // ======================================================
        // 4. Capa para centrar los botones inferiores
        // ======================================================
        StackPane.setAlignment(bottomButtons, Pos.BOTTOM_CENTER);


        // ======================================================
        // 5. Agregar todo en orden correcto
        // ======================================================
        root.getChildren().addAll(
                background,   // fondo
                bottomButtons, // botones inferior horizontal
                btnExit       // botón exit arriba izquierda
        );
    }

    public Parent getRoot() {
        return root;
    }

    // Eventos para el controlador
    public void setOnSignIn(Runnable r) { btnSignIn.setOnAction(e -> r.run()); }
    public void setOnSignUp(Runnable r) { btnSignUp.setOnAction(e -> r.run()); }
    public void setOnExit(Runnable r)   { btnExit.setOnAction(e -> r.run()); }
}


