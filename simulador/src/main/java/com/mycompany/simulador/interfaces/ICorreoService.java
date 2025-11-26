package com.mycompany.simulador.interfaces;

import java.io.File;

public interface ICorreoService {

    void enviarCorreoConAdjunto(String destinatario,
                                String asunto,
                                String cuerpo,
                                File adjunto);
}
