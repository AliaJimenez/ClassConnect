package dao;

import Modelos.conexionlocal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Avisos y eventos escolares (tabla avisos_eventos). */
public class EventoDAO {

    public static class Evento {
        public final String titulo, tipo;
        public final LocalDate fecha;

        Evento(String titulo, String tipo, LocalDate fecha) {
            this.titulo = titulo;
            this.tipo = tipo == null ? "aviso" : tipo;
            this.fecha = fecha;
        }
    }

    /** Próximos eventos del curso del estudiante (o generales), a partir de hoy. */
    public List<Evento> proximos(int idEstudiante, int limite) throws SQLException {
        List<Evento> lista = new ArrayList<>();
        String sql = "SELECT e.titulo, e.tipo, e.fecha_evento FROM avisos_eventos e "
                + "WHERE e.fecha_evento >= CURDATE() AND (e.id_curso IS NULL OR e.id_curso = "
                + "(SELECT id_curso FROM alumnos WHERE id_estudiante = ?)) ORDER BY e.fecha_evento LIMIT ?";
        Connection con = conexionlocal.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            ps.setInt(2, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Evento(rs.getString(1), rs.getString(2), rs.getDate(3).toLocalDate()));
                }
            }
        }
        return lista;
    }
}
