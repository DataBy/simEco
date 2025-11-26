package com.mycompany.simulador.services.usuario;

import com.mycompany.simulador.dto.LoginDTO;
import com.mycompany.simulador.interfaces.IUsuariosRepository;
import com.mycompany.simulador.model.user.Usuario;
import com.mycompany.simulador.utils.CifradoUtils;

public class AutenticacionService {

    private final IUsuariosRepository repo;

    public AutenticacionService(IUsuariosRepository repo) {
        this.repo = repo;
    }

    public Usuario autenticar(LoginDTO dto) {
        Usuario u = repo.buscarPorCedula(dto.getCedula());
        if (u == null) return null;
        if (CifradoUtils.verificar(dto.getContrasenaPlano(), u.getContrasenaHash())) {
            return u;
        }
        return null;
    }
}
