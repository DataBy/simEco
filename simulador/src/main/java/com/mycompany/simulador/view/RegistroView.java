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
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class RegistroView {

    private final StackPane root = new StackPane();
    private final TextField txtCedula = new TextField();
    private final TextField txtNombre = new TextField();
    private final DatePicker dpFecha  = new DatePicker(LocalDate.of(2000,1,1));
    private final RadioButton rbM = new RadioButton("Masculino");
    private final RadioButton rbF = new RadioButton("Femenino");
    private final TextField txtCorreo = new TextField();
    private final PasswordField txtContrasena = new PasswordField();
    private final Button btnListo = new Button();
    private final Button btnVolver = new Button();

    public RegistroView() {
        txtCedula.setPromptText("Cédula");
        txtNombre.setPromptText("Nombre completo");
        txtCorreo.setPromptText("Correo electrónico");
        txtContrasena.setPromptText("Contraseña");

        ToggleGroup grupoGenero = new ToggleGroup();
        rbM.setToggleGroup(grupoGenero);
        rbF.setToggleGroup(grupoGenero);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        int row = 0;
        grid.add(new Label("Cédula:"), 0, row);
        grid.add(txtCedula, 1, row++);
        grid.add(new Label("Nombre:"), 0, row);
        grid.add(txtNombre, 1, row++);
        grid.add(new Label("Fecha nacimiento:"), 0, row);
        grid.add(dpFecha, 1, row++);
        grid.add(new Label("Género:"), 0, row);
        grid.add(new VBox(5, rbM, rbF), 1, row++);
        grid.add(new Label("Correo:"), 0, row);
        grid.add(txtCorreo, 1, row++);
        grid.add(new Label("Contraseña:"), 0, row);
        grid.add(txtContrasena, 1, row++);

        IconosUtils.configurarBotonConHover(btnListo,
                RutasArchivos.REGISTRO_BTN_LISTO,
                RutasArchivos.REGISTRO_BTN_LISTO);
        IconosUtils.configurarBotonConHover(btnVolver,
                RutasArchivos.REGISTRO_BTN_RETROCEDER,
                RutasArchivos.REGISTRO_BTN_RETROCEDER);

        VBox botones = new VBox(10, btnListo, btnVolver);
        botones.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20, grid, botones);
        layout.setAlignment(Pos.CENTER);

        ImageView background = IconosUtils.crearImageViewFondo(
                RutasArchivos.REGISTRO_BACKGROUND, root);
        root.getChildren().addAll(background, layout);
    }

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
