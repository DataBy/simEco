package com.mycompany.simulador.view;

import java.time.LocalDate;

import com.mycompany.simulador.config.RutasArchivos;
import com.mycompany.simulador.utils.IconosUtils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RegistroView {

    private final StackPane root = new StackPane();

    private final TextField txtCedula = new TextField();
    private final TextField txtNombre = new TextField();
    private final DatePicker dpFecha  = new DatePicker(LocalDate.of(2000,1,1));
    private final RadioButton rbM = new RadioButton("Masculino");
    private final RadioButton rbF = new RadioButton("Femenino");
    private final TextField txtCorreo = new TextField();
    private final PasswordField txtContrasena = new PasswordField();

    private final Button btnListo = new Button("Registrar");
    private final Button btnVolver = new Button("Volver");

    private static final double ANCHO_COLUMNAS = 280;

    public RegistroView() {

        // ============================================================
        // 1. Fondo responsive
        // ============================================================
        ImageView background = IconosUtils.crearImageViewFondo(
                RutasArchivos.REGISTRO_BACKGROUND, root
        );

        // ============================================================
        // 2. Caja glassy premium
        // ============================================================
        VBox glassBox = new VBox(28);
        glassBox.setPadding(new Insets(35));
        glassBox.setAlignment(Pos.CENTER);
        glassBox.setMaxWidth(480);

        Rectangle glassBg = new Rectangle();
        glassBg.widthProperty().bind(glassBox.widthProperty());
        glassBg.heightProperty().bind(glassBox.heightProperty());
        glassBg.setArcWidth(35);
        glassBg.setArcHeight(35);
        glassBg.setFill(Color.web("#ffffff22"));
        glassBg.setStroke(Color.web("#ffffff55"));
        glassBg.setStrokeWidth(1.2);
        glassBg.setEffect(new GaussianBlur(23));

        DropShadow neoShadow = new DropShadow(18, Color.rgb(0,0,0,0.35));
        neoShadow.setOffsetY(10);
        glassBox.setEffect(neoShadow);

        // ============================================================
        // 3. Estilos de campos y labels
        // ============================================================
        estiloCampo(txtCedula);
        estiloCampo(txtNombre);
        estiloCampo(txtCorreo);
        estiloCampo(txtContrasena);

        estiloDate(dpFecha);

        Label lblCed = crearLabel("Cédula");
        Label lblNom = crearLabel("Nombre completo");
        Label lblFecha = crearLabel("Fecha de nacimiento");
        Label lblGenero = crearLabel("Género");
        Label lblCorreo = crearLabel("Correo electrónico");
        Label lblContra = crearLabel("Contraseña");

        // Radio buttons alineados
        ToggleGroup grupoGenero = new ToggleGroup();
        rbM.setToggleGroup(grupoGenero);
        rbF.setToggleGroup(grupoGenero);
        rbM.setTextFill(Color.WHITE);
        rbF.setTextFill(Color.WHITE);
        rbM.setStyle("-fx-font-size: 15px;");
        rbF.setStyle("-fx-font-size: 15px;");

        VBox generoBox = new VBox(6, rbM, rbF);
        generoBox.setAlignment(Pos.CENTER);

        // Todos los widths uniformes
        lblCed.setMaxWidth(ANCHO_COLUMNAS);
        txtCedula.setMaxWidth(ANCHO_COLUMNAS);

        lblNom.setMaxWidth(ANCHO_COLUMNAS);
        txtNombre.setMaxWidth(ANCHO_COLUMNAS);

        lblFecha.setMaxWidth(ANCHO_COLUMNAS);
        dpFecha.setMaxWidth(ANCHO_COLUMNAS);

        lblGenero.setMaxWidth(ANCHO_COLUMNAS);
        generoBox.setMaxWidth(ANCHO_COLUMNAS);

        lblCorreo.setMaxWidth(ANCHO_COLUMNAS);
        txtCorreo.setMaxWidth(ANCHO_COLUMNAS);

        lblContra.setMaxWidth(ANCHO_COLUMNAS);
        txtContrasena.setMaxWidth(ANCHO_COLUMNAS);


        // ============================================================
        // 4. Formulario en una sola columna centrada (sin GridPane)
        // ============================================================
        VBox form = new VBox(20,
                lblCed, txtCedula,
                lblNom, txtNombre,
                lblFecha, dpFecha,
                lblGenero, generoBox,
                lblCorreo, txtCorreo,
                lblContra, txtContrasena
        );
        form.setAlignment(Pos.CENTER);


        // ============================================================
        // 5. Botones glassy en horizontal
        // ============================================================
        estiloBoton(btnListo);
        estiloBoton(btnVolver);

        HBox botones = new HBox(40, btnListo, btnVolver);
        botones.setAlignment(Pos.CENTER);
        VBox.setMargin(botones, new Insets(10, 0, 0, 0));


        // ============================================================
        // 6. Ensamblado final
        // ============================================================
        glassBox.getChildren().addAll(form, botones);

        StackPane wrapper = new StackPane(glassBg, glassBox);
        wrapper.setAlignment(Pos.CENTER);

        root.getChildren().addAll(background, wrapper);
    }


    // ============================================================
    // Estilos de campos
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

    private void estiloDate(DatePicker dp) {
        dp.setStyle("""
            -fx-background-color: rgba(255,255,255,0.25);
            -fx-background-radius: 12;
            -fx-border-radius: 12;
            -fx-border-color: rgba(255,255,255,0.6);
            -fx-border-width: 1.2;
            -fx-padding: 8 12;
            -fx-font-size: 15px;
            -fx-text-fill: white;
        """);
        dp.setEffect(new DropShadow(10, Color.rgb(0,0,0,0.35)));
    }

    private void estiloBoton(Button btn) {
        btn.setStyle("""
            -fx-background-color: rgba(255,255,255,0.22);
            -fx-background-radius: 18;
            -fx-border-radius: 18;
            -fx-border-color: rgba(255,255,255,0.55);
            -fx-border-width: 1.2;
            -fx-padding: 8 26;
            -fx-font-size: 16px;
            -fx-text-fill: white;
            -fx-font-weight: bold;
        """);

        btn.setEffect(new DropShadow(12, Color.rgb(0,0,0,0.38)));

        btn.setOnMouseEntered(e -> btn.setStyle("""
            -fx-background-color: rgba(255,255,255,0.38);
            -fx-background-radius: 18;
            -fx-border-radius: 18;
            -fx-border-color: rgba(255,255,255,0.8);
            -fx-border-width: 1.2;
            -fx-padding: 8 26;
            -fx-font-size: 16px;
            -fx-text-fill: black;
            -fx-font-weight: bold;
        """));

        btn.setOnMouseExited(e -> estiloBoton(btn));
    }

    private Label crearLabel(String texto) {
        Label lbl = new Label(texto);
        lbl.setTextFill(Color.WHITE);
        lbl.setAlignment(Pos.CENTER);
        lbl.setStyle("""
            -fx-font-size: 17px;
            -fx-font-weight: bold;
        """);
        return lbl;
    }

    // ============================================================
    // Métodos funcionales usados por el Controller
    // ============================================================

    public Parent getRoot() { return root; }

    public int getCedula() {
        try { return Integer.parseInt(txtCedula.getText().trim()); }
        catch (NumberFormatException e) { return -1; }
    }

    public String getNombre() { return txtNombre.getText().trim(); }

    public LocalDate getFechaNacimiento() { return dpFecha.getValue(); }

    public String getGenero() {
        if (rbM.isSelected()) return "M";
        if (rbF.isSelected()) return "F";
        return "";
    }

    public String getCorreo() { return txtCorreo.getText().trim(); }

    public String getContrasena() { return txtContrasena.getText(); }

    public void setOnListo(Runnable r) { btnListo.setOnAction(e -> r.run()); }
    public void setOnVolver(Runnable r) { btnVolver.setOnAction(e -> r.run()); }

    public void mostrarMensaje(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.showAndWait();
    }

    public void mostrarError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }
}
