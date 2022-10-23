package com.equipo09.directoatuhabitacion.Pojos;

public class Galeria {

    String uID, descipcion, foto, usuario;

    public Galeria() {
    }

    public Galeria(String uID, String usuario, String descipcion, String foto) {
        this.uID = uID;
        this.usuario = usuario;
        this.descipcion = descipcion;
        this.foto = foto;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getDescipcion() {
        return descipcion;
    }

    public void setDescipcion(String descipcion) {
        this.descipcion = descipcion;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
