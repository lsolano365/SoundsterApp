package com.uco.soundsterapp.model;

public class Usuario {

    String id;
    String nombreCompleto;
    String correo;
    String contraseña;
    String descripcion;

    public Usuario() {

    }

    public Usuario(String id, String nombreCompleto, String correo, String contraseña, String descripcion) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.contraseña = contraseña;
        this.descripcion = descripcion;
    }

    public Usuario(String id, String nombreCompleto, String correo, String contraseña) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.contraseña = contraseña;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
