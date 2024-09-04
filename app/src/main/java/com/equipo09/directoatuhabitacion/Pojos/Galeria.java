package com.equipo09.directoatuhabitacion.Pojos;

public class Galeria {

    String uID, descipcion, nombreUsuario, fechaPublicacion, foto, fotoPerfilUsuario;

    public Galeria() {
    }

    public Galeria(String uID, String descipcion, String nombreUsuario, String fechaPublicacion, String foto, String fotoPerfilUsuario) {
        this.uID = uID;
        this.descipcion = descipcion;
        this.nombreUsuario = nombreUsuario;
        this.fechaPublicacion = fechaPublicacion;
        this.foto = foto;
        this.fotoPerfilUsuario = fotoPerfilUsuario;
    }

    public Galeria(String uID, String descipcion, String nombreUsuario, String fechaPublicacion, String foto) {
        this.uID = uID;
        this.descipcion = descipcion;
        this.nombreUsuario = nombreUsuario;
        this.fechaPublicacion = fechaPublicacion;
        this.foto = foto;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getDescipcion() {
        return descipcion;
    }

    public void setDescipcion(String descipcion) {
        this.descipcion = descipcion;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(String fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getFotoPerfilUsuario() {
        return fotoPerfilUsuario;
    }
    public void setFotoPerfilUsuario(String fotoPerfilUsuario) {
        this.fotoPerfilUsuario = fotoPerfilUsuario;
    }
}
