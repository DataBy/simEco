package com.mycompany.simulador.interfaces;

import java.util.List;

import com.mycompany.simulador.model.user.Usuario;

public interface IUsuariosRepository {

    void guardar(Usuario usuario);

    Usuario buscarPorCedula(int cedula);

    List<Usuario> listarTodos();
}
