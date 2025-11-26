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
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);

        IconosUtils.configurarBotonConHover(btnSignIn,
                RutasArchivos.INICIO_BTN_SIGN_IN,
                RutasArchivos.INICIO_BTN_SIGN_IN);
        IconosUtils.configurarBotonConHover(btnSignUp,
                RutasArchivos.INICIO_BTN_SIGN_UP,
                RutasArchivos.INICIO_BTN_SIGN_UP);
        IconosUtils.configurarBotonConHover(btnExit,
                RutasArchivos.INICIO_BTN_EXIT,
                RutasArchivos.INICIO_BTN_EXIT);

        vbox.getChildren().addAll(btnSignIn, btnSignUp, btnExit);

        ImageView background = IconosUtils.crearImageViewFondo(
                RutasArchivos.INICIO_BACKGROUND, root);

        root.getChildren().addAll(background, vbox);
    }

    public Parent getRoot() {
        return root;
    }

    public void setOnSignIn(Runnable r) { btnSignIn.setOnAction(e -> r.run()); }
    public void setOnSignUp(Runnable r) { btnSignUp.setOnAction(e -> r.run()); }
    public void setOnExit(Runnable r)   { btnExit.setOnAction(e -> r.run()); }
}
