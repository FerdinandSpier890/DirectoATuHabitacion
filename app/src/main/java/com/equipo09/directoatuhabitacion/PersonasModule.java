package com.equipo09.directoatuhabitacion;

import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class PersonasModule {

    private String uID;
    private String Nombre;
    private String Apellidos;
    private String Teléfono;
    private String Biografía;
    private String FotoPerfil;

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getApellidos() {
        return Apellidos;
    }

    public void setApellidos(String apellidos) {
        Apellidos = apellidos;
    }

    public String getTeléfono() {
        return Teléfono;
    }

    public void setTeléfono(String teléfono) {
        Teléfono = teléfono;
    }

    public String getBiografía() {
        return Biografía;
    }

    public void setBiografía(String biografía) {
        Biografía = biografía;
    }

    public String getFotoPerfil() {
        return FotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        FotoPerfil = fotoPerfil;
    }

    @NonNull
    @Override
    public String toString() {
        return Nombre;
    }
}
