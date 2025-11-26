package com.mycompany.simulador.model.user;

public class Credenciales {

    private int cedula;
    private String contrasenaHash;

    public Credenciales(int cedula, String contrasenaHash) {
        this.cedula = cedula;
        this.contrasenaHash = contrasenaHash;
    }

    public int getCedula() { return cedula; }
    public String getContrasenaHash() { return contrasenaHash; }
}
