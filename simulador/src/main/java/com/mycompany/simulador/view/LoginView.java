package com.mycompany.simulador.view;

import com.mycompany.simulador.config.RutasArchivos;
import com.mycompany.simulador.utils.IconosUtils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginView {

    private final StackPane root = new StackPane();
    private final TextField txtCedula = new TextField();
    private final PasswordField txtContrasena = new PasswordField();
    private final Button btnIngresar = new Button();
    private final Button btnVolver   = new Button();

    public LoginView() {
        txtCedula.setPromptText("Cédula");
        txtContrasena.setPromptText("Contraseña");

        VBox form = new VBox(10,
                new Label("Cédula:"), txtCedula,
                new Label("Contraseña:"), txtContrasena);
        form.setMaxWidth(300);
        form.setAlignment(Pos.CENTER_LEFT);
        form.setPadding(new Insets(10));

        IconosUtils.configurarBotonConHover(btnIngresar,
                RutasArchivos.LOGIN_BTN_LISTO,
                RutasArchivos.LOGIN_BTN_LISTO);
        IconosUtils.configurarBotonConHover(btnVolver,
                RutasArchivos.LOGIN_BTN_RETROCEDER,
                RutasArchivos.LOGIN_BTN_RETROCEDER);

        VBox botones = new VBox(10, btnIngresar, btnVolver);
        botones.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20, form, botones);
        layout.setAlignment(Pos.CENTER);

        ImageView background = IconosUtils.crearImageViewFondo(
                RutasArchivos.LOGIN_BACKGROUND, root);

        root.getChildren().addAll(background, layout);
    }

    public Parent getRoot() {
        return root;
    }

    public int getCedula() {
        try {
            return Integer.parseInt(txtCedula.getText().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String getContrasena() {
        return txtContrasena.getText();
    }

    public void setOnIngresar(Runnable r) { btnIngresar.setOnAction(e -> r.run()); }
    public void setOnVolver(Runnable r)   { btnVolver.setOnAction(e -> r.run()); }

    public void mostrarError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText("Error");
        a.showAndWait();
    }

    public void mostrarMensaje(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.showAndWait();
    }
}
