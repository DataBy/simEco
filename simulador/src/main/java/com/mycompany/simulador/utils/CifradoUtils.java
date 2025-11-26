package com.mycompany.simulador.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class CifradoUtils {

    private CifradoUtils() {}

    public static String hashSHA256(String textoPlano) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(textoPlano.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            LogUtils.error("No se pudo crear hash SHA-256", e);
            return textoPlano;
        }
    }

    public static boolean verificar(String textoPlano, String hash) {
        return hashSHA256(textoPlano).equals(hash);
    }
}
