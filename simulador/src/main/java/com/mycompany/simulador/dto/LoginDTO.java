package com.mycompany.simulador.dto;

public class LoginDTO {

    private int cedula;
    private String contrasenaPlano;

    public LoginDTO(int cedula, String contrasenaPlano) {
        this.cedula = cedula;
        this.contrasenaPlano = contrasenaPlano;
    }

    public int getCedula() { return cedula; }
    public String getContrasenaPlano() { return contrasenaPlano; }
}
