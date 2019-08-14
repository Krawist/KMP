package com.example.kmp.Modeles;

import java.io.Serializable;

public class Artiste implements Serializable {
    private int idArtiste;
    private String nomArtiste;
    private int nombreAlbums;
    private int nombreMusique;

    public int getIdArtiste() {
        return idArtiste;
    }

    public void setIdArtiste(int idArtiste) {
        this.idArtiste = idArtiste;
    }

    public String getNomArtiste() {
        return nomArtiste;
    }

    public void setNomArtiste(String nomArtiste) {
        this.nomArtiste = nomArtiste;
    }

    public int getNombreAlbums() {
        return nombreAlbums;
    }

    public void setNombreAlbums(int nombreAlbums) {
        this.nombreAlbums = nombreAlbums;
    }

    public int getNombreMusique() {
        return nombreMusique;
    }

    public void setNombreMusique(int nombreMusique) {
        this.nombreMusique = nombreMusique;
    }
}
