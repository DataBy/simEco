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
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class LoginView {

    private final StackPane root = new StackPane();
    private final TextField txtCedula = new TextField();
    private final PasswordField txtContrasena = new PasswordField();
    private final Button btnIngresar = new Button("Listo");
    private final Button btnVolver   = new Button("Retroceder");

    private static final double ANCHO_COLUMNAS = 260;

    public LoginView() {

        // ============================================================
        // 1. Fondo responsive
        // ============================================================
        ImageView background = IconosUtils.crearImageViewFondo(
                RutasArchivos.LOGIN_BACKGROUND, root
        );

        // ============================================================
        // 2. Caja Glassy Neomorphism centrada
        // ============================================================
        VBox glassBox = new VBox(25);
        glassBox.setPadding(new Insets(35));
        glassBox.setAlignment(Pos.CENTER);
        glassBox.setMaxWidth(420);

        Rectangle glassBg = new Rectangle();
        glassBg.widthProperty().bind(glassBox.widthProperty());
        glassBg.heightProperty().bind(glassBox.heightProperty());
        glassBg.setArcWidth(35);
        glassBg.setArcHeight(35);
        glassBg.setFill(Color.web("#ffffff22"));
        glassBg.setStroke(Color.web("#ffffff55"));
        glassBg.setStrokeWidth(1.2);
        glassBg.setEffect(new GaussianBlur(22));

        DropShadow shadow = new DropShadow(18, Color.rgb(0,0,0,0.35));
        shadow.setOffsetY(10);
        glassBox.setEffect(shadow);


        // ============================================================
        // 3. Campos y labels ALINEADOS a la perfección
        // ============================================================
        estiloCampo(txtCedula);
        estiloCampo(txtContrasena);

        Label lblCed = crearLabel("Cédula");
        Label lblCon = crearLabel("Contraseña");

        lblCed.setMaxWidth(ANCHO_COLUMNAS);
        lblCon.setMaxWidth(ANCHO_COLUMNAS);
        lblCed.setAlignment(Pos.CENTER);
        lblCon.setAlignment(Pos.CENTER);

        txtCedula.setMaxWidth(ANCHO_COLUMNAS);
        txtContrasena.setMaxWidth(ANCHO_COLUMNAS);

        VBox form = new VBox(18);
        form.setAlignment(Pos.CENTER);
        form.getChildren().addAll(
            lblCed,
            txtCedula,
            lblCon,
            txtContrasena
        );


        // ============================================================
        // 4. Botones alineados horizontalmente y MISMO ancho vertical
        // ============================================================
        estiloBoton(btnIngresar);
        estiloBoton(btnVolver);

        btnIngresar.setMinWidth(120);
        btnVolver.setMinWidth(120);

        HBox botones = new HBox(20, btnIngresar, btnVolver);
        botones.setAlignment(Pos.CENTER);


        // ============================================================
        // 5. Ensamblado final
        // ============================================================
        glassBox.getChildren().addAll(form, botones);

        StackPane wrapper = new StackPane(glassBg, glassBox);
        wrapper.setAlignment(Pos.CENTER);

        root.getChildren().addAll(background, wrapper);
    }


    // ============================================================
    // Estilizado perfecto de los TEXTFIELDS
    // ============================================================
    private void estiloCampo(TextField campo) {
        campo.setStyle("""
            -fx-background-color: rgba(255,255,255,0.28);
            -fx-background-radius: 12;
            -fx-border-radius: 12;
            -fx-border-color: rgba(255,255,255,0.6);
            -fx-border-width: 1.2;
            -fx-padding: 10 14;
            -fx-font-size: 16px;
            -fx-text-fill: white;
            -fx-prompt-text-fill: rgba(255,255,255,0.75);
        """);

        campo.setEffect(new DropShadow(10, Color.rgb(0,0,0,0.35)));
    }


    // ============================================================
    // Botones glassy con hover elegante sin cuadro feo
    // ============================================================
    private void estiloBoton(Button btn) {
        btn.setStyle("""
            -fx-background-color: rgba(255,255,255,0.22);
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-border-color: rgba(255,255,255,0.55);
            -fx-border-width: 1.2;
            -fx-padding: 8 20;
            -fx-text-fill: white;
            -fx-font-size: 16px;
            -fx-font-weight: bold;
        """);

        btn.setEffect(new DropShadow(12, Color.rgb(0,0,0,0.35)));

        btn.setOnMouseEntered(e -> btn.setStyle("""
            -fx-background-color: rgba(255,255,255,0.38);
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-border-color: rgba(255,255,255,0.75);
            -fx-border-width: 1.2;
            -fx-padding: 8 20;
            -fx-text-fill: black;
            -fx-font-size: 16px;
            -fx-font-weight: bold;
        """));

        btn.setOnMouseExited(e -> estiloBoton(btn));
    }


    // ============================================================
    // Labels grandes y centrados
    // ============================================================
    private Label crearLabel(String texto) {
        Label lbl = new Label(texto);
        lbl.setTextFill(Color.WHITE);
        lbl.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: bold;
        """);
        return lbl;
    }


    // ============================================================
    // Controladores
    // ============================================================
    public Parent getRoot() { return root; }

    public int getCedula() {
        try { return Integer.parseInt(txtCedula.getText().trim()); }
        catch (Exception e) { return -1; }
    }

    public String getContrasena() {
        return txtContrasena.getText();
    }

    public void setOnIngresar(Runnable r) { btnIngresar.setOnAction(e -> r.run()); }
    public void setOnVolver(Runnable r)   { btnVolver.setOnAction(e -> r.run()); }

    public void mostrarError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }

    public void mostrarMensaje(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.showAndWait();
    }
}
