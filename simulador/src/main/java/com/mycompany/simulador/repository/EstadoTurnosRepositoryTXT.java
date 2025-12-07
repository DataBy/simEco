package com.mycompany.simulador.repository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mycompany.simulador.config.AppConfig;
import com.mycompany.simulador.dto.EstadoTurnoDTO;
import com.mycompany.simulador.interfaces.IEstadoTurnosRepository;
import com.mycompany.simulador.utils.ArchivoUtils;

public class EstadoTurnosRepositoryTXT implements IEstadoTurnosRepository {

    private final Path path = AppConfig.ARCHIVO_ESTADO_TURNOS;

    /**
     * Limpia el archivo de estados para que una nueva simulacion no herede registros anteriores.
     */
    public void limpiar() {
        ArchivoUtils.escribirLineas(path, Collections.emptyList(), false);
    }

    @Override
    public void guardarEstado(EstadoTurnoDTO estado, char[][] matrizSimbolos) {
        List<String> lineas = new ArrayList<>();
        lineas.add("ESCENARIO=" + estado.getEscenario() + ";TURNO=" + estado.getTurno()
                + ";PRESAS=" + estado.getPresas()
                + ";DEPREDADORES=" + estado.getDepredadores()
                + ";TERCERA=" + estado.getTerceraEspecie()
                + ";OCUPADAS=" + estado.getCeldasOcupadas());
        lineas.add("----");
        ArchivoUtils.escribirLineas(path, lineas, true);
    }

    @Override
    public List<EstadoTurnoDTO> cargarEstados() {
        List<String> lineas = ArchivoUtils.leerLineas(path);
        List<EstadoTurnoDTO> estados = new ArrayList<>();
        for (String l : lineas) {
            if (l.startsWith("ESCENARIO=")) {
                String[] partes = l.split(";");
                String escenario = partes[0].split("=")[1];
                int turno = Integer.parseInt(partes[1].split("=")[1]);
                int presas = Integer.parseInt(partes[2].split("=")[1]);
                int depredadores = Integer.parseInt(partes[3].split("=")[1]);
                int tercera = Integer.parseInt(partes[4].split("=")[1]);
                int ocupadas = Integer.parseInt(partes[5].split("=")[1]);
                estados.add(new EstadoTurnoDTO(turno, escenario, presas,
                        depredadores, tercera, ocupadas));
            }
        }
        return estados;
    }
}
