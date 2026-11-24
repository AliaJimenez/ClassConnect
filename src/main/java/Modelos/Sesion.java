package Modelos;

/**
 * Datos de la sesión activa, compartidos por todas las pantallas.
 * Se llenan al iniciar sesión y se limpian al cerrarla.
 */
public final class Sesion {

    private static Usuario usuario;
    /** Estudiante que consulta un padre/madre (su primer hijo registrado). */
    private static int idEstudiante = -1;

    private Sesion() {
    }

    public static void iniciar(Usuario u, int idEstudianteAsociado) {
        usuario = u;
        idEstudiante = idEstudianteAsociado;
    }

    public static void cerrar() {
        usuario = null;
        idEstudiante = -1;
    }

    public static Usuario getUsuario() { return usuario; }

    public static int getIdEstudiante() { return idEstudiante; }

    public static String getNombre(String porDefecto) {
        return usuario != null ? usuario.getNombreCompleto() : porDefecto;
    }
}
