package dao;

import Modelos.Usuario;
import Modelos.conexionlocal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public Usuario autenticar(String correo, String clave) throws SQLException {
        String sql = "SELECT id, nombre, apellido, correo, rol FROM usuarios WHERE correo = ? AND pass = ?";
        Connection con = conexionlocal.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            ps.setString(2, clave);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(rs.getInt("id"), rs.getString("nombre"), rs.getString("apellido"),
                            rs.getString("correo"), rs.getString("rol"));
                }
            }
        }
        return null;
    }

    /** Primer estudiante vinculado a un usuario con rol padre, o -1 si no tiene. */
    public int obtenerPrimerHijo(int idUsuarioPadre) throws SQLException {
        String sql = "SELECT pe.id_estudiante FROM padres p "
                + "JOIN padre_estudiante pe ON pe.id_padre = p.id_padre "
                + "WHERE p.id_usuario = ? ORDER BY pe.id_estudiante LIMIT 1";
        Connection con = conexionlocal.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuarioPadre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }
}
