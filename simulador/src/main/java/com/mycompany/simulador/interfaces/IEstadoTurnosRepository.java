package com.mycompany.simulador.interfaces;

import java.util.List;

import com.mycompany.simulador.dto.EstadoTurnoDTO;

public interface IEstadoTurnosRepository {

    void guardarEstado(EstadoTurnoDTO estado, char[][] matrizSimbolos);

    List<EstadoTurnoDTO> cargarEstados();
}
