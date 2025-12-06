package com.mycompany.simulador.services.simulacion;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.simulador.config.Constantes;
import com.mycompany.simulador.dto.SimulacionConfigDTO;
import com.mycompany.simulador.model.ecosystem.Celda;
import com.mycompany.simulador.model.ecosystem.Ecosistema;
import com.mycompany.simulador.model.ecosystem.Turno;
import com.mycompany.simulador.model.species.Depredador;
import com.mycompany.simulador.model.species.Especie;
import com.mycompany.simulador.model.species.Presa;
import com.mycompany.simulador.model.species.TerceraEspecie;
import com.mycompany.simulador.utils.AleatorioUtils;
import com.mycompany.simulador.utils.MatrizUtils;

public class EcosistemaService {

    public Ecosistema crearEcosistema(SimulacionConfigDTO config) {
        Ecosistema e = new Ecosistema();
        if (config.getMatrizPersonalizada() != null) {
            poblarDesdeMatriz(e, config.getMatrizPersonalizada());
            return e;
        }
        poblarEspecies(e, config.getPresasIniciales(), Especie.Tipo.PRESA);
        poblarEspecies(e, config.getDepredadoresIniciales(), Especie.Tipo.DEPREDADOR);
        poblarEspecies(e, config.getTerceraEspecieInicial(), Especie.Tipo.TERCERA_ESPECIE);
        return e;
    }

    private void poblarEspecies(Ecosistema e, int cantidad, Especie.Tipo tipo) {
        int colocados = 0;
        while (colocados < cantidad) {
            int fila = AleatorioUtils.enteroEnRango(0, Constantes.MATRIZ_FILAS - 1);
            int col = AleatorioUtils.enteroEnRango(0, Constantes.MATRIZ_COLUMNAS - 1);
            Celda c = e.getCelda(fila, col);
            if (!c.estaVacia()) continue;
            Especie esp;
            switch (tipo) {
                case PRESA -> esp = new Presa("Presa");
                case DEPREDADOR -> esp = new Depredador("Depredador");
                default -> esp = new TerceraEspecie("Tercera");
            }
            e.colocarEspecie(esp, fila, col);
            colocados++;
        }
    }

    public Turno calcularTurno(int numero, Ecosistema e) {
        int presas = 0, depredadores = 0, tercera = 0, ocupadas = 0;
        for (Celda[] fila : e.getMatriz()) {
            for (Celda c : fila) {
                if (!c.estaVacia()) {
                    ocupadas++;
                    Especie esp = c.getEspecie();
                    if (esp.getTipo() == Especie.Tipo.PRESA) presas++;
                    else if (esp.getTipo() == Especie.Tipo.DEPREDADOR) depredadores++;
                    else tercera++;
                }
            }
        }
        return new Turno(numero, presas, depredadores, tercera, ocupadas);
    }

    public boolean estaExtinto(Ecosistema e) {
        int vivos = 0;
        for (Celda[] fila : e.getMatriz()) {
            for (Celda c : fila) {
                if (!c.estaVacia()) vivos++;
            }
        }
        return vivos == 0;
    }

    private void poblarDesdeMatriz(Ecosistema e, char[][] matriz) {
        if (matriz == null) return;
        int filas = Math.min(Constantes.MATRIZ_FILAS, matriz.length);
        for (int i = 0; i < filas; i++) {
            int cols = Math.min(Constantes.MATRIZ_COLUMNAS, matriz[i].length);
            for (int j = 0; j < cols; j++) {
                char val = matriz[i][j];
                Especie especie = switch (val) {
                    case 'P' -> new Presa("Presa");
                    case 'D' -> new Depredador("Depredador");
                    case 'T' -> new TerceraEspecie("Tercera");
                    default -> null;
                };
                if (especie != null) {
                    e.colocarEspecie(especie, i, j);
                }
            }
        }
    }

    public char[][] construirMatrizSimbolos(Ecosistema e) {
        char[][] m = new char[Constantes.MATRIZ_FILAS][Constantes.MATRIZ_COLUMNAS];
        for (int i = 0; i < Constantes.MATRIZ_FILAS; i++) {
            for (int j = 0; j < Constantes.MATRIZ_COLUMNAS; j++) {
                Celda c = e.getCelda(i, j);
                if (c.estaVacia()) m[i][j] = '.';
                else {
                    Especie.Tipo t = c.getEspecie().getTipo();
                    m[i][j] = switch (t) {
                        case PRESA -> 'P';
                        case DEPREDADOR -> 'D';
                        case TERCERA_ESPECIE -> 'T';
                    };
                }
            }
        }
        return m;
    }

    public List<Celda> obtenerCeldasVecinasLibres(Ecosistema e, Celda origen) {
        List<Celda> libres = new ArrayList<>();
        for (var coord : MatrizUtils.vecinosOrtogonales(origen.getCoordenada())) {
            Celda c = e.getCelda(coord.getFila(), coord.getColumna());
            if (c.estaVacia()) libres.add(c);
        }
        return libres;
    }
}
