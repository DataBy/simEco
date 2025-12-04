package com.mycompany.simulador.services.simulacion;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.simulador.config.Constantes;
import com.mycompany.simulador.interfaces.IReproduccionStrategy;
import com.mycompany.simulador.model.ecosystem.Celda;
import com.mycompany.simulador.model.ecosystem.Ecosistema;
import com.mycompany.simulador.model.species.Depredador;
import com.mycompany.simulador.model.species.Especie;
import com.mycompany.simulador.model.species.Presa;
import com.mycompany.simulador.utils.AleatorioUtils;
import com.mycompany.simulador.utils.MatrizUtils;
import com.mycompany.simulador.utils.SimLogger;

public class ReproduccionService implements IReproduccionStrategy {

    @Override
    public void reproducir(Ecosistema e) {
        // Reproducción deshabilitada para que cada turno solo cambie una pieza (el movimiento).
        // Esto evita que aparezcan nuevas presas/depredadores sobre elementos del escenario.
    }

    private boolean reproducirEnVecinoVacio(Ecosistema e, Celda origen, Especie cria, String tipo) {
        List<Celda> libres = new ArrayList<>();
        for (var coord : MatrizUtils.vecinosOrtogonales(origen.getCoordenada())) {
            Celda c = e.getCelda(coord.getFila(), coord.getColumna());
            if (c.estaVacia()) libres.add(c);
        }
        if (libres.isEmpty()) {
            SimLogger.log(tipo + " en " + coord(origen) + " intenta reproducirse, pero no hay espacio libre");
            return false;
        }
        Celda destino = AleatorioUtils.elegirAleatorio(libres);
        destino.setEspecie(cria);
        cria.setPosicion(destino.getCoordenada());
        SimLogger.log(tipo + " en " + coord(origen) + " se reproduce en " + coord(destino));
        return true;
    }

    private String coord(Celda c) {
        return "(" + c.getCoordenada().getFila() + "," + c.getCoordenada().getColumna() + ")";
    }
}
