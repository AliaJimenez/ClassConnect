package dao;

import Modelos.conexionlocal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/** Operaciones sobre estudiantes (tablas usuarios, alumnos, cursos, padres). */
public class EstudianteDAO {

    /** Fila resumida de un estudiante para las tablas de la interfaz. */
    public static class Fila {
        public int idEstudiante, idUsuario;
        public String matricula, nombre, apellido, correo, grado, seccion, direccion, edad, asistencia, promedio;

        public String getNombreCompleto() { return nombre + " " + apellido; }
    }

    private static final String SELECT_BASE =
            "SELECT a.id_estudiante, a.id_usuario, a.matricula, a.fecha_nacimiento, a.direccion, "
            + "u.nombre, u.apellido, u.correo, c.grado, c.seccion, "
            + "(SELECT ROUND(100 * SUM(s.estado IN ('presente','tarde','justificado')) / COUNT(*)) "
            + "   FROM asistencia s WHERE s.id_estudiante = a.id_estudiante) AS pct_asistencia, "
            + "(SELECT ROUND(AVG((n.calificacion + n.calificacion2 + n.calificacion3 + n.calificacion4) / 4), 1) "
            + "   FROM notas n WHERE n.id_estudiante = a.id_estudiante) AS promedio "
            + "FROM alumnos a "
            + "JOIN usuarios u ON u.id = a.id_usuario "
            + "JOIN cursos c ON c.id_curso = a.id_curso ";

    public List<Fila> listar() throws SQLException {
        return consultar(SELECT_BASE + "ORDER BY u.apellido, u.nombre", null, null);
    }

    /** @param seccion letra de la sección ("A", "B"...) o null para todas */
    public List<Fila> listarPorCurso(String grado, String seccion) throws SQLException {
        String sql = SELECT_BASE + "WHERE c.grado = ?" + (seccion != null ? " AND c.seccion = ?" : "")
                + " ORDER BY u.apellido, u.nombre";
        return consultar(sql, grado, seccion);
    }

    private List<Fila> consultar(String sql, String grado, String seccion) throws SQLException {
        List<Fila> lista = new ArrayList<>();
        Connection con = conexionlocal.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (grado != null) ps.setString(1, grado);
            if (seccion != null) ps.setString(2, seccion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Fila f = new Fila();
                    f.idEstudiante = rs.getInt("id_estudiante");
                    f.idUsuario = rs.getInt("id_usuario");
                    f.matricula = rs.getString("matricula");
                    f.nombre = rs.getString("nombre");
                    f.apellido = rs.getString("apellido");
                    f.correo = rs.getString("correo");
                    f.grado = rs.getString("grado");
                    f.seccion = rs.getString("seccion");
                    f.direccion = rs.getString("direccion") == null ? "" : rs.getString("direccion");
                    Date nac = rs.getDate("fecha_nacimiento");
                    f.edad = nac == null ? "-" : Period.between(nac.toLocalDate(), LocalDate.now()).getYears() + " años";
                    String pct = rs.getString("pct_asistencia");
                    f.asistencia = pct == null ? "-" : pct + "%";
                    String prom = rs.getString("promedio");
                    f.promedio = prom == null ? "-" : prom;
                    lista.add(f);
                }
            }
        }
        return lista;
    }

    /**
     * Registra un estudiante y, si se indica, vincula a su padre/madre
     * (lo crea con la contraseña inicial "pass123" si su correo no existe).
     */
    public void registrar(String nombre, String apellido, String matricula, LocalDate fechaNacimiento,
                          String grado, String seccion, String direccion,
                          String nombrePadre, String correoPadre, String telefonoPadre) throws SQLException {
        Connection con = conexionlocal.getConexion();
        boolean autoCommit = con.getAutoCommit();
        con.setAutoCommit(false);
        try {
            int idCurso = obtenerOCrearCurso(con, grado, seccion);
            int idUsuario = insertarUsuario(con, nombre, apellido,
                    matricula.toLowerCase() + "@estudiantes.classconnect.edu", null, "estudiante");

            int idEstudiante;
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO alumnos (id_usuario, id_curso, matricula, fecha_nacimiento, direccion) VALUES (?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, idUsuario);
                ps.setInt(2, idCurso);
                ps.setString(3, matricula);
                ps.setDate(4, Date.valueOf(fechaNacimiento));
                ps.setString(5, direccion);
                ps.executeUpdate();
                idEstudiante = clave(ps);
            }

            if (correoPadre != null && !correoPadre.isBlank()) {
                int idPadre = obtenerOCrearPadre(con, nombrePadre, correoPadre, telefonoPadre);
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO padre_estudiante (id_padre, id_estudiante, parentesco) VALUES (?,?,'tutor')")) {
                    ps.setInt(1, idPadre);
                    ps.setInt(2, idEstudiante);
                    ps.executeUpdate();
                }
            }
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(autoCommit);
        }
    }

    public void actualizar(int idUsuario, int idEstudiante, String nombre, String apellido, String direccion)
            throws SQLException {
        Connection con = conexionlocal.getConexion();
        try (PreparedStatement ps = con.prepareStatement("UPDATE usuarios SET nombre = ?, apellido = ? WHERE id = ?")) {
            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setInt(3, idUsuario);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = con.prepareStatement("UPDATE alumnos SET direccion = ? WHERE id_estudiante = ?")) {
            ps.setString(1, direccion);
            ps.setInt(2, idEstudiante);
            ps.executeUpdate();
        }
    }

    /** Elimina al estudiante junto con sus notas y asistencias. */
    public void eliminar(int idEstudiante, int idUsuario) throws SQLException {
        Connection con = conexionlocal.getConexion();
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM notas WHERE id_estudiante = ?")) {
            ps.setInt(1, idEstudiante);
            ps.executeUpdate();
        }
        // asistencia, padre_estudiante y alumnos se eliminan en cascada
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM usuarios WHERE id = ?")) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }

    public boolean existeMatricula(String matricula) throws SQLException {
        Connection con = conexionlocal.getConexion();
        try (PreparedStatement ps = con.prepareStatement("SELECT 1 FROM alumnos WHERE matricula = ?")) {
            ps.setString(1, matricula);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int contarEstudiantes() throws SQLException {
        return contar("SELECT COUNT(*) FROM alumnos");
    }

    /** Materias sin nota final (calificacion4 = 0). */
    public int contarNotasPendientes() throws SQLException {
        return contar("SELECT COUNT(*) FROM notas WHERE calificacion4 = 0");
    }

    private int contar(String sql) throws SQLException {
        Connection con = conexionlocal.getConexion();
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int obtenerOCrearCurso(Connection con, String grado, String seccion) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT id_curso FROM cursos WHERE grado = ? AND seccion = ? AND anio_escolar = ?")) {
            ps.setString(1, grado);
            ps.setString(2, seccion);
            ps.setInt(3, LocalDate.now().getYear());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO cursos (grado, anio_escolar, seccion) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, grado);
            ps.setInt(2, LocalDate.now().getYear());
            ps.setString(3, seccion);
            ps.executeUpdate();
            return clave(ps);
        }
    }

    private int obtenerOCrearPadre(Connection con, String nombreCompleto, String correo, String telefono)
            throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT p.id_padre FROM padres p JOIN usuarios u ON u.id = p.id_usuario WHERE u.correo = ?")) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        String nombre = nombreCompleto == null || nombreCompleto.isBlank() ? "Padre/Madre" : nombreCompleto.trim();
        String apellido = "";
        int espacio = nombre.indexOf(' ');
        if (espacio > 0) {
            apellido = nombre.substring(espacio + 1);
            nombre = nombre.substring(0, espacio);
        }
        int idUsuario = insertarUsuario(con, nombre, apellido, correo, "pass123", "padre");
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO padres (id_usuario, telefono) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idUsuario);
            ps.setString(2, telefono);
            ps.executeUpdate();
            return clave(ps);
        }
    }

    private int insertarUsuario(Connection con, String nombre, String apellido, String correo, String pass, String rol)
            throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO usuarios (nombre, apellido, correo, pass, rol) VALUES (?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setString(3, correo);
            ps.setString(4, pass);
            ps.setString(5, rol);
            ps.executeUpdate();
            return clave(ps);
        }
    }

    private static int clave(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.getGeneratedKeys()) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
