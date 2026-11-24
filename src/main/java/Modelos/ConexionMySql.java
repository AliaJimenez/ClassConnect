/*
Paquete de modelos para el Back end y la conexion a base de datos.
*/
package Modelos;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Compatibilidad con el código anterior: delega en {@link conexionlocal},
 * que obtiene la configuración desde variables de entorno.
 *
 * @author mikew
 */
public class ConexionMySql {

    public Connection conectar() {
        try {
            return conexionlocal.getConexion();
        } catch (SQLException sqlex) {
            System.out.println("Error al tratar de conectar: " + sqlex.getMessage());
            return null;
        }
    }
}
