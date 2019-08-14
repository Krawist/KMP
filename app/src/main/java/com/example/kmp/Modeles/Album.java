package com.example.kmp.Modeles;

import java.io.Serializable;

public class Album implements Serializable {

    private int idAlbum;
    private String pochette;
    private String titreAlbum;
    private String nomArtiste;
    private int nombreMusique;

    public String getPochette() {
        return pochette;
    }

    public void setPochette(String pochette) {
        this.pochette = pochette;
    }

    public int getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(int idAlbum) {
        this.idAlbum = idAlbum;
    }

    public String getTitreAlbum() {
        return titreAlbum;
    }

    public void setTitreAlbum(String titreAlbum) {
        this.titreAlbum = titreAlbum;
    }

    public String getNomArtiste() {
        return nomArtiste;
    }

    public void setNomArtiste(String nomArtiste) {
        this.nomArtiste = nomArtiste;
    }

    public int getNombreMusique() {
        return nombreMusique;
    }

    public void setNombreMusique(int nombreMusique) {
        this.nombreMusique = nombreMusique;
    }
}
