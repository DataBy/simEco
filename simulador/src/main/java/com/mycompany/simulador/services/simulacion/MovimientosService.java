package com.mycompany.simulador.services.simulacion;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.simulador.interfaces.IMovimientosStrategy;
import com.mycompany.simulador.model.ecosystem.Celda;
import com.mycompany.simulador.model.ecosystem.Ecosistema;
import com.mycompany.simulador.model.species.Depredador;
import com.mycompany.simulador.model.species.Especie;
import com.mycompany.simulador.model.species.Presa;
import com.mycompany.simulador.model.species.TerceraEspecie;
import com.mycompany.simulador.utils.AleatorioUtils;
import com.mycompany.simulador.utils.MatrizUtils;

public class MovimientosService implements IMovimientosStrategy {

    @Override
    public void moverEspecies(Ecosistema e) {
        List<Celda> celdas = new ArrayList<>();
        for (Celda[] fila : e.getMatriz()) {
            for (Celda c : fila) {
                if (!c.estaVacia()) celdas.add(c);
            }
        }
        for (Celda origen : celdas) {
            Especie esp = origen.getEspecie();
            if (esp == null || !esp.isViva()) continue;
            if (esp instanceof Presa) {
                moverPresa(e, origen);
            } else if (esp instanceof Depredador || esp instanceof TerceraEspecie) {
                moverDepredadorLike(e, origen);
            }
            esp.incrementarTurnosSobrevividos();
        }
    }

    private void moverPresa(Ecosistema e, Celda origen) {
        List<Celda> libres = new ArrayList<>();
        for (var coord : MatrizUtils.vecinosOrtogonales(origen.getCoordenada())) {
            Celda c = e.getCelda(coord.getFila(), coord.getColumna());
            if (c.estaVacia()) libres.add(c);
        }
        if (libres.isEmpty()) return;
        Celda destino = AleatorioUtils.elegirAleatorio(libres);
        destino.setEspecie(origen.getEspecie());
        destino.getEspecie().setPosicion(destino.getCoordenada());
        origen.vaciar();
    }

    private void moverDepredadorLike(Ecosistema e, Celda origen) {
        Especie dep = origen.getEspecie();
        List<Celda> vecinos = new ArrayList<>();
        for (var coord : MatrizUtils.vecinosOrtogonales(origen.getCoordenada())) {
            vecinos.add(e.getCelda(coord.getFila(), coord.getColumna()));
        }
        Celda objetivoPresa = null;
        for (Celda c : vecinos) {
            if (!c.estaVacia() && c.getEspecie().getTipo() == Especie.Tipo.PRESA) {
                objetivoPresa = c;
                break;
            }
        }
        if (objetivoPresa != null) {
            objetivoPresa.setEspecie(dep);
            dep.setPosicion(objetivoPresa.getCoordenada());
            origen.vaciar();
            dep.setComioEnVentana(true);
            return;
        }
        List<Celda> libres = new ArrayList<>();
        for (Celda c : vecinos) {
            if (c.estaVacia()) libres.add(c);
        }
        if (libres.isEmpty()) return;
        Celda destino = AleatorioUtils.elegirAleatorio(libres);
        destino.setEspecie(dep);
        dep.setPosicion(destino.getCoordenada());
        origen.vaciar();
    }
}
