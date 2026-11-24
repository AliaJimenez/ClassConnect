package Modelos;

import dao.UsuarioDAO;

/**
 * Autenticación contra la tabla usuarios y apertura de la sesión.
 *
 * @author mikew
 */
public class LoginDB {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * @return el usuario autenticado, o null si las credenciales no son válidas
     * @throws Exception si no se puede conectar a la base de datos
     */
    public Usuario iniciarSesion(String correo, String clave) throws Exception {
        Usuario u = usuarioDAO.autenticar(correo, clave);
        if (u != null) {
            int idEstudiante = "padre".equals(u.getRol()) ? usuarioDAO.obtenerPrimerHijo(u.getId()) : -1;
            Sesion.iniciar(u, idEstudiante);
        }
        return u;
    }
}
