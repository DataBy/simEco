package com.mycompany.simulador.repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.simulador.config.AppConfig;
import com.mycompany.simulador.interfaces.IUsuariosRepository;
import com.mycompany.simulador.model.user.Usuario;
import com.mycompany.simulador.utils.ArchivoUtils;
import com.mycompany.simulador.utils.FechaUtils;
import com.mycompany.simulador.utils.LogUtils;

public class UsuarioRepositoryTXT implements IUsuariosRepository {

    private final Path path = AppConfig.ARCHIVO_USUARIOS;

    // ✔ ÚNICO CAMBIO necesario
    public UsuarioRepositoryTXT() {
        try {
            AppConfig.ensureDataFolder(); // crea /data si no existe

            if (!Files.exists(path)) {
                Files.createFile(path); // crea usuarios.txt si falta
            }

        } catch (Exception e) {
            throw new RuntimeException("Error inicializando archivo de usuarios: " + path, e);
        }
    }

    @Override
    public void guardar(Usuario usuario) {
        String linea = String.join(";",
                String.valueOf(usuario.getCedula()),
                usuario.getNombre(),
                FechaUtils.format(usuario.getFechaNacimiento()),
                usuario.getGenero(),
                usuario.getCorreo(),
                usuario.getContrasenaHash());

        List<String> lineas = new ArrayList<>();
        lineas.add(linea);

        ArchivoUtils.escribirLineas(path, lineas, true);
    }

    @Override
    public Usuario buscarPorCedula(int cedula) {
        List<String> lineas = ArchivoUtils.leerLineas(path);

        for (String l : lineas) {
            String[] p = l.split(";");
            if (p.length == 6 && Integer.parseInt(p[0]) == cedula) {
                Usuario u = new Usuario();
                u.setCedula(Integer.parseInt(p[0]));
                u.setNombre(p[1]);
                u.setFechaNacimiento(FechaUtils.parse(p[2]));
                u.setGenero(p[3]);
                u.setCorreo(p[4]);
                u.setContrasenaHash(p[5]);
                return u;
            }
        }
        return null;
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        List<String> lineas = ArchivoUtils.leerLineas(path);

        for (String l : lineas) {
            try {
                String[] p = l.split(";");
                if (p.length == 6) {
                    Usuario u = new Usuario();
                    u.setCedula(Integer.parseInt(p[0]));
                    u.setNombre(p[1]);
                    u.setFechaNacimiento(FechaUtils.parse(p[2]));
                    u.setGenero(p[3]);
                    u.setCorreo(p[4]);
                    u.setContrasenaHash(p[5]);
                    lista.add(u);
                }
            } catch (Exception e) {
                LogUtils.error("Línea inválida en usuarios.txt: " + l, e);
            }
        }
        return lista;
    }
}
