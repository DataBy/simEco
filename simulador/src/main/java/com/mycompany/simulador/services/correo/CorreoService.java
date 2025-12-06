package com.mycompany.simulador.services.correo;

import java.io.File;
import java.util.Properties;

import com.mycompany.simulador.config.AppConfig;
import com.mycompany.simulador.interfaces.ICorreoService;
import com.mycompany.simulador.utils.LogUtils;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class CorreoService implements ICorreoService {

    @Override
    public void enviarCorreoConAdjunto(String destinatario, String asunto,
                                       String cuerpo, File adjunto) {
        try {
            Session session = Session.getInstance(buildProps(), new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(AppConfig.SMTP_USUARIO, AppConfig.SMTP_CONTRASENA);
                }
            });
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(AppConfig.SMTP_USUARIO));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto, "UTF-8");

            MimeBodyPart texto = new MimeBodyPart();
            texto.setText(cuerpo, "UTF-8");

            MimeMultipart multi = new MimeMultipart();
            multi.addBodyPart(texto);

            if (adjunto != null && adjunto.exists()) {
                MimeBodyPart adj = new MimeBodyPart();
                adj.setDataHandler(new DataHandler(new FileDataSource(adjunto)));
                adj.setFileName(adjunto.getName());
                multi.addBodyPart(adj);
            }

            message.setContent(multi);
            Transport.send(message);
            LogUtils.info("Correo enviado a " + destinatario + " con adjunto: " +
                    (adjunto != null ? adjunto.getAbsolutePath() : "sin adjunto"));
        } catch (MessagingException e) {
            LogUtils.error("Error enviando correo", e);
            throw new RuntimeException("No se pudo enviar el correo: " + e.getMessage(), e);
        }
    }

    private Properties buildProps() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(AppConfig.SMTP_TLS));
        props.put("mail.smtp.host", AppConfig.SMTP_HOST);
        props.put("mail.smtp.port", String.valueOf(AppConfig.SMTP_PORT));
        return props;
    }
}
