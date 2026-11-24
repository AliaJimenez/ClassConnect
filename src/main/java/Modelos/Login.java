/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos;

/**
 *
 * @author mikew
 */
public class Login {
    
    String correo;
    String clave;

    public Login() {
    }

    public Login(String correo, String clave) {
        this.correo = correo;
        this.clave = clave;
    }

   

    public String getUsuario() {
        return correo;
    }

    public void setUsuario(String correo) {
        this.correo = correo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
    
    
}
