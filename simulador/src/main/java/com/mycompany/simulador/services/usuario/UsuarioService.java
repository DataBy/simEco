package com.mycompany.simulador.services.usuario;

import java.time.LocalDate;

import com.mycompany.simulador.dto.UsuarioDTO;
import com.mycompany.simulador.interfaces.IUsuariosRepository;
import com.mycompany.simulador.model.user.Usuario;
import com.mycompany.simulador.utils.CifradoUtils;
import com.mycompany.simulador.utils.ValidacionesUtils;

public class UsuarioService {

    private final IUsuariosRepository repo;

    public UsuarioService(IUsuariosRepository repo) {
        this.repo = repo;
    }

    public String registrarUsuario(UsuarioDTO dto) {
        LocalDate nacimiento = dto.getFechaNacimiento();
        if (!ValidacionesUtils.esMayorDeEdad(nacimiento, 18)) {
            return "Debes ser mayor de 18 años";
        }
        if (!ValidacionesUtils.esCorreoValido(dto.getCorreo())) {
            return "Correo inválido";
        }
        Usuario existente = repo.buscarPorCedula(dto.getCedula());
        if (existente != null) {
            return "Ya existe un usuario con esa cédula";
        }
        Usuario u = new Usuario();
        u.setCedula(dto.getCedula());
        u.setNombre(dto.getNombre());
        u.setFechaNacimiento(dto.getFechaNacimiento());
        u.setGenero(dto.getGenero());
        u.setCorreo(dto.getCorreo());
        u.setContrasenaHash(CifradoUtils.hashSHA256(dto.getContrasenaPlano()));
        repo.guardar(u);
        return null;
    }
}
