package dao;

import Modelos.conexionlocal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

/** Registro y consulta de asistencia (tabla asistencia). */
public class AsistenciaDAO {

    /** Estado y observación de un día. */
    public static class Registro {
        public final String estado;
        public final String observacion;

        Registro(String estado, String observacion) {
            this.estado = estado;
            this.observacion = observacion == null ? "" : observacion;
        }
    }

    /** Guarda (o reemplaza) la asistencia de un estudiante en una fecha. Estados: presente, ausente, tarde, justificado. */
    public void guardar(int idEstudiante, LocalDate fecha, String estado, String observacion) throws SQLException {
        Connection con = conexionlocal.getConexion();
        try (PreparedStatement del = con.prepareStatement("DELETE FROM asistencia WHERE id_estudiante = ? AND fecha = ?")) {
            del.setInt(1, idEstudiante);
            del.setDate(2, Date.valueOf(fecha));
            del.executeUpdate();
        }
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO asistencia (id_estudiante, fecha, estado, observacion) VALUES (?,?,?,?)")) {
            ps.setInt(1, idEstudiante);
            ps.setDate(2, Date.valueOf(fecha));
            ps.setString(3, estado);
            ps.setString(4, observacion);
            ps.executeUpdate();
        }
    }

    /** Registros del mes, ordenados por fecha. */
    public Map<LocalDate, Registro> obtenerMes(int idEstudiante, YearMonth mes) throws SQLException {
        Map<LocalDate, Registro> mapa = new LinkedHashMap<>();
        Connection con = conexionlocal.getConexion();
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT fecha, estado, observacion FROM asistencia WHERE id_estudiante = ? AND fecha BETWEEN ? AND ? ORDER BY fecha")) {
            ps.setInt(1, idEstudiante);
            ps.setDate(2, Date.valueOf(mes.atDay(1)));
            ps.setDate(3, Date.valueOf(mes.atEndOfMonth()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    mapa.put(rs.getDate("fecha").toLocalDate(), new Registro(rs.getString("estado"), rs.getString("observacion")));
                }
            }
        }
        return mapa;
    }

    /** Marca una falta como justificada con el motivo indicado. */
    public void justificar(int idEstudiante, LocalDate fecha, String motivo) throws SQLException {
        guardar(idEstudiante, fecha, "justificado", motivo);
    }

    /** [presentes (incluye tarde y justificado), total registrados] de una fecha. */
    public int[] resumenDia(LocalDate fecha) throws SQLException {
        Connection con = conexionlocal.getConexion();
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT COALESCE(SUM(estado <> 'ausente'),0), COUNT(*) FROM asistencia WHERE fecha = ?")) {
            ps.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new int[]{rs.getInt(1), rs.getInt(2)};
            }
        }
    }

    /** [días con registro, días asistidos] de todo el historial del estudiante. */
    public int[] resumenEstudiante(int idEstudiante) throws SQLException {
        Connection con = conexionlocal.getConexion();
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT COUNT(*), COALESCE(SUM(estado <> 'ausente'),0) FROM asistencia WHERE id_estudiante = ?")) {
            ps.setInt(1, idEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new int[]{rs.getInt(1), rs.getInt(2)};
            }
        }
    }
}
