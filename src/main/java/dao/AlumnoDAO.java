/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author alian
 */
import Modelos.Alumno;
import Modelos.conexionlocal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AlumnoDAO {

    public Alumno obtenerPorId(int idEstudiante) {
        Alumno alu = null;
        
        // Unimos tabla 'alumnos' con 'usuarios' para saber el nombre
        // Unimos tabla 'alumnos' con 'cursos' para saber el grado
        String sql = "SELECT a.id_estudiante, u.nombre, u.apellido, u.correo, " +
                     "a.matricula, a.direccion, c.grado, c.seccion " +
                     "FROM alumnos a " +
                     "INNER JOIN usuarios u ON a.id_usuario = u.id " +
                     "INNER JOIN cursos c ON a.id_curso = c.id_curso " +
                     "WHERE a.id_estudiante = ?";
        
        try {
            Connection con = conexionlocal.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idEstudiante);
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()) {
                alu = new Alumno();
                alu.setIdEstudiante(rs.getInt("id_estudiante"));
                alu.setNombre(rs.getString("nombre"));
                alu.setApellido(rs.getString("apellido"));
                alu.setCorreo(rs.getString("correo"));
                alu.setMatricula(rs.getString("matricula"));
                alu.setDireccion(rs.getString("direccion"));
                alu.setGrado(rs.getString("grado"));
                alu.setSeccion(rs.getString("seccion"));
            }
        } catch (Exception e) {
            System.out.println("Error buscando alumno: " + e.getMessage());
            e.printStackTrace();
        }
        return alu;
    }
}