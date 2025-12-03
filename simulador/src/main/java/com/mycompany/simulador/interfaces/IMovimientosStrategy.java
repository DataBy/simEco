package com.mycompany.simulador.interfaces;

import com.mycompany.simulador.model.ecosystem.Ecosistema;
import java.util.function.Consumer;

public interface IMovimientosStrategy {
    void moverEspecies(Ecosistema ecosistema);

    default void moverEspecies(Ecosistema ecosistema, Consumer<Ecosistema> stepCallback) {
        moverEspecies(ecosistema);
    }
}
