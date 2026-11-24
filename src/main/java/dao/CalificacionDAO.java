package dao;

import Modelos.Calificacion;
import Modelos.conexionlocal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Calificaciones (tabla notas). Cada fila guarda las cuatro notas del año de una materia:
 * calificacion = 1er trimestre, calificacion2 = 2do trimestre, calificacion3 = 3er trimestre,
 * calificacion4 = examen final. Una nota en 0 significa "pendiente".
 */
public class CalificacionDAO {

    public static final String[] PERIODOS = {"1er trimestre", "2do trimestre", "3er trimestre", "Examen final"};

    /** Una calificación por materia y período (solo las ya registradas). */
    public List<Calificacion> obtenerPorEstudiante(int idEstudiante) {
        List<Calificacion> lista = new ArrayList<>();
        String sql = "SELECT asig.nombre_asignatura, n.calificacion, n.calificacion2, n.calificacion3, n.calificacion4, n.observacion "
                + "FROM notas n JOIN asignaturas asig ON n.id_asignatura = asig.id_asignatura "
                + "WHERE n.id_estudiante = ? ORDER BY asig.nombre_asignatura";
        try {
            Connection con = conexionlocal.getConexion();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idEstudiante);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String obs = rs.getString("observacion") == null ? "" : rs.getString("observacion");
                        double[] notas = {rs.getDouble(2), rs.getDouble(3), rs.getDouble(4), rs.getDouble(5)};
                        for (int i = 0; i < notas.length; i++) {
                            if (notas[i] > 0) {
                                lista.add(new Calificacion(rs.getString(1), notas[i], PERIODOS[i], obs));
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /** Promedio del estudiante; si periodo es null se usa todo el año. */
    public double obtenerPromedio(int idEstudiante, String periodo) {
        return obtenerPromediosPorMateria(idEstudiante, periodo).values().stream()
                .mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    public Calificacion obtenerMejorMateria(int idEstudiante, String periodo) {
        return extremo(idEstudiante, periodo, true);
    }

    public Calificacion obtenerPeorMateria(int idEstudiante, String periodo) {
        return extremo(idEstudiante, periodo, false);
    }

    private Calificacion extremo(int idEstudiante, String periodo, boolean mejor) {
        Map<String, Double> promedios = obtenerPromediosPorMateria(idEstudiante, periodo);
        Comparator<Map.Entry<String, Double>> cmp = Map.Entry.comparingByValue();
        return promedios.entrySet().stream()
                .max(mejor ? cmp : cmp.reversed())
                .map(e -> new Calificacion(e.getKey(), Math.round(e.getValue() * 10) / 10.0, periodo, ""))
                .orElse(null);
    }

    /** Promedio por materia en el período indicado (o del año si es null). */
    public Map<String, Double> obtenerPromediosPorMateria(int idEstudiante, String periodo) {
        Map<String, List<Double>> notas = new LinkedHashMap<>();
        for (Calificacion c : obtenerPorEstudiante(idEstudiante)) {
            if (periodo == null || periodo.equalsIgnoreCase(c.getPeriodo())) {
                notas.computeIfAbsent(c.getNombreAsignatura(), k -> new ArrayList<>()).add(c.getCalificacion());
            }
        }
        Map<String, Double> promedios = new LinkedHashMap<>();
        notas.forEach((materia, lista) ->
                promedios.put(materia, lista.stream().mapToDouble(Double::doubleValue).average().orElse(0)));
        return promedios;
    }

    /**
     * Notas para la pantalla del docente: una fila por materia del curso del estudiante
     * con {materia, nota1, nota2, nota3, nota4} (0 = pendiente).
     */
    public List<Object[]> obtenerNotasDocente(int idEstudiante) throws SQLException {
        List<Object[]> filas = new ArrayList<>();
        String sql = "SELECT asig.nombre_asignatura, COALESCE(n.calificacion,0), COALESCE(n.calificacion2,0), "
                + "COALESCE(n.calificacion3,0), COALESCE(n.calificacion4,0) "
                + "FROM alumnos a JOIN asignaturas asig ON asig.id_curso = a.id_curso "
                + "LEFT JOIN notas n ON n.id_asignatura = asig.id_asignatura AND n.id_estudiante = a.id_estudiante "
                + "WHERE a.id_estudiante = ? ORDER BY asig.nombre_asignatura";
        Connection con = conexionlocal.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filas.add(new Object[]{rs.getString(1), rs.getDouble(2), rs.getDouble(3), rs.getDouble(4), rs.getDouble(5)});
                }
            }
        }
        return filas;
    }

    /** Guarda las cuatro notas de una materia del curso del estudiante. */
    public void guardarNotas(int idEstudiante, String materia, double n1, double n2, double n3, double n4)
            throws SQLException {
        Connection con = conexionlocal.getConexion();
        int idAsignatura;
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT asig.id_asignatura FROM alumnos a JOIN asignaturas asig ON asig.id_curso = a.id_curso "
                + "WHERE a.id_estudiante = ? AND asig.nombre_asignatura = ?")) {
            ps.setInt(1, idEstudiante);
            ps.setString(2, materia);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("La materia " + materia + " no pertenece al curso del estudiante");
                idAsignatura = rs.getInt(1);
            }
        }
        int actualizadas;
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE notas SET calificacion=?, calificacion2=?, calificacion3=?, calificacion4=? "
                + "WHERE id_estudiante=? AND id_asignatura=?")) {
            ps.setDouble(1, n1); ps.setDouble(2, n2); ps.setDouble(3, n3); ps.setDouble(4, n4);
            ps.setInt(5, idEstudiante); ps.setInt(6, idAsignatura);
            actualizadas = ps.executeUpdate();
        }
        if (actualizadas == 0) {
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO notas (id_estudiante, id_asignatura, calificacion, calificacion2, calificacion3, calificacion4) "
                    + "VALUES (?,?,?,?,?,?)")) {
                ps.setInt(1, idEstudiante); ps.setInt(2, idAsignatura);
                ps.setDouble(3, n1); ps.setDouble(4, n2); ps.setDouble(5, n3); ps.setDouble(6, n4);
                ps.executeUpdate();
            }
        }
    }
}
