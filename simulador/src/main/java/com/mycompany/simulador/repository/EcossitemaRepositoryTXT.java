package com.mycompany.simulador.repository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.simulador.config.AppConfig;
import com.mycompany.simulador.dto.SimulacionConfigDTO;
import com.mycompany.simulador.interfaces.IEcosistemaRepository;
import com.mycompany.simulador.utils.ArchivoUtils;

public class EcossitemaRepositoryTXT implements IEcosistemaRepository {

    private final Path path = AppConfig.ARCHIVO_ECOSISTEMA;

    @Override
    public void guardarConfiguracion(SimulacionConfigDTO config) {
        String linea = String.join(";",
                config.getEscenario(),
                String.valueOf(config.getPresasIniciales()),
                String.valueOf(config.getDepredadoresIniciales()),
                String.valueOf(config.getTerceraEspecieInicial()),
                String.valueOf(config.getMaxTurnos()));
        List<String> lineas = new ArrayList<>();
        lineas.add(linea);
        ArchivoUtils.escribirLineas(path, lineas, false);
    }

    @Override
    public SimulacionConfigDTO cargarUltimaConfiguracion() {
        List<String> lineas = ArchivoUtils.leerLineas(path);
        if (lineas.isEmpty()) return null;
        String l = lineas.get(lineas.size() - 1);
        String[] p = l.split(";");
        if (p.length < 5) return null;
        SimulacionConfigDTO dto = new SimulacionConfigDTO();
        dto.setEscenario(p[0]);
        dto.setPresasIniciales(Integer.parseInt(p[1]));
        dto.setDepredadoresIniciales(Integer.parseInt(p[2]));
        dto.setTerceraEspecieInicial(Integer.parseInt(p[3]));
        dto.setMaxTurnos(Integer.parseInt(p[4]));
        return dto;
    }
}
