/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos;

/**
 *
 * @author alian
 */
public class Calificacion {

    private int idCalificacion;
    private int idAlumno;
    private String nombreAsignatura;
    private double calificacion;
    private String periodo;
    private String observacion;

    // Constructor con los datos que muestra la interfaz
    public Calificacion(String nombreAsignatura, double calificacion, String periodo, String observacion) {
        this.nombreAsignatura = nombreAsignatura;
        this.calificacion = calificacion;
        this.periodo = periodo;
        this.observacion = observacion;
    }

    // Constructor completo (opcional)
    public Calificacion(int idCalificacion, int idAlumno, String nombreAsignatura,
                        double calificacion, String periodo, String observacion) {
        this.idCalificacion = idCalificacion;
        this.idAlumno = idAlumno;
        this.nombreAsignatura = nombreAsignatura;
        this.calificacion = calificacion;
        this.periodo = periodo;
        this.observacion = observacion;
    }

    // ====== GETTERS & SETTERS ======

    public int getIdCalificacion() {
        return idCalificacion;
    }

    public void setIdCalificacion(int idCalificacion) {
        this.idCalificacion = idCalificacion;
    }

    public int getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(int idAlumno) {
        this.idAlumno = idAlumno;
    }

    public String getNombreAsignatura() {
        return nombreAsignatura;
    }

    public void setNombreAsignatura(String nombreAsignatura) {
        this.nombreAsignatura = nombreAsignatura;
    }

    public double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(double calificacion) {
        this.calificacion = calificacion;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
