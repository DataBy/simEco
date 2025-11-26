package com.mycompany.simulador.services.correo;

import java.io.File;

import com.mycompany.simulador.config.AppConfig;
import com.mycompany.simulador.interfaces.ICorreoService;
import com.mycompany.simulador.utils.LogUtils;

public class CorreoService implements ICorreoService {

    @Override
    public void enviarCorreoConAdjunto(String destinatario, String asunto,
                                       String cuerpo, File adjunto) {
        LogUtils.info("Simulando envío de correo a " + destinatario +
                " desde " + AppConfig.SMTP_USUARIO +
                " con adjunto: " + (adjunto != null ? adjunto.getAbsolutePath() : "sin adjunto"));
    }
}
