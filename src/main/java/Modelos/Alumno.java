/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos;

/**
 *
 * @author alian
 */
public class Alumno {
    private int idEstudiante;
    private String nombre;     // Viene de tabla 'usuarios'
    private String apellido;   // Viene de tabla 'usuarios'
    private String matricula;  // Viene de tabla 'alumnos'
    private String direccion;  // Viene de tabla 'alumnos'
    private String grado;      // Viene de tabla 'cursos'
    private String seccion;    // Viene de tabla 'cursos'
    private String correo;     // Viene de tabla 'usuarios'

    public Alumno() {}

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
    
    public String getGradoCompleto() {
        return grado + " - Sec. " + seccion;
    }

    // Getters y Setters
    public int getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(int idEstudiante) { this.idEstudiante = idEstudiante; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getGrado() { return grado; }
    public void setGrado(String grado) { this.grado = grado; }
    public String getSeccion() { return seccion; }
    public void setSeccion(String seccion) { this.seccion = seccion; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
}