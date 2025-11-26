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

public class ReproduccionService implements IReproduccionStrategy {

    @Override
    public void reproducir(Ecosistema e) {
        List<Celda> snapshot = new ArrayList<>();
        for (Celda[] fila : e.getMatriz()) {
            for (Celda c : fila) {
                if (!c.estaVacia()) snapshot.add(c);
            }
        }
        for (Celda c : snapshot) {
            Especie esp = c.getEspecie();
            if (esp == null || !esp.isViva()) continue;
            if (esp instanceof Presa &&
                esp.getTurnosSobrevividos() >= Constantes.TURNOS_SOBREVIVIR_REPRO_PRESA) {
                reproducirEnVecinoVacio(e, c, new Presa("Presa"));
            } else if (esp instanceof Depredador && esp.isComioEnVentana()) {
                reproducirEnVecinoVacio(e, c, new Depredador("Depredador"));
            }
        }
    }

    private void reproducirEnVecinoVacio(Ecosistema e, Celda origen, Especie cría) {
        List<Celda> libres = new ArrayList<>();
        for (var coord : MatrizUtils.vecinosOrtogonales(origen.getCoordenada())) {
            Celda c = e.getCelda(coord.getFila(), coord.getColumna());
            if (c.estaVacia()) libres.add(c);
        }
        if (libres.isEmpty()) return;
        Celda destino = AleatorioUtils.elegirAleatorio(libres);
        destino.setEspecie(cría);
        cría.setPosicion(destino.getCoordenada());
    }
}
