package com.mycompany.simulador.dto;

import java.time.LocalDate;

public class UsuarioDTO {

    private int cedula;
    private String nombre;
    private LocalDate fechaNacimiento;
    private String genero;
    private String correo;
    private String contrasenaPlano;

    public int getCedula() { return cedula; }
    public void setCedula(int cedula) { this.cedula = cedula; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasenaPlano() { return contrasenaPlano; }
    public void setContrasenaPlano(String contrasenaPlano) { this.contrasenaPlano = contrasenaPlano; }
}
