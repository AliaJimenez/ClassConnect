package Modelos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Punto único de acceso a la base de datos.
 * La configuración se lee de variables de entorno para no guardar credenciales en el código:
 *   CLASSCONNECT_DB_URL  (por defecto jdbc:mysql://localhost:3306/sistema_escolar)
 *   CLASSCONNECT_DB_USER (por defecto root)
 *   CLASSCONNECT_DB_PASS (por defecto vacío)
 */
public class conexionlocal {

    private static final String URL = env("CLASSCONNECT_DB_URL",
            "jdbc:mysql://localhost:3306/sistema_escolar?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
    private static final String USER = env("CLASSCONNECT_DB_USER", "root");
    private static final String PASS = env("CLASSCONNECT_DB_PASS", "");

    private static Connection conexion;

    private conexionlocal() {
    }

    public static synchronized Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed() || !conexion.isValid(2)) {
            conexion = DriverManager.getConnection(URL, USER, PASS);
        }
        return conexion;
    }

    private static String env(String nombre, String porDefecto) {
        String valor = System.getenv(nombre);
        return (valor == null || valor.isBlank()) ? porDefecto : valor;
    }
}
