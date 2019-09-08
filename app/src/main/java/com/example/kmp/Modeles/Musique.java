package com.example.kmp.Modeles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Musique implements Serializable {

    @NonNull
    @PrimaryKey
    private int idMusique;
    private int idAlbum;
    private int idArtiste;
    private String titreMusique;
    private String titreAlbum;
    private int duration;
    private String pochette;
    private String nomArtiste;
    private String paroleMusique;
    private String path;
    private int track;
    private int size;
    private int bookmark;
    private boolean isLiked;

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public int getIdMusique() {
        return idMusique;
    }

    public void setIdMusique(int idMusique) {
        this.idMusique = idMusique;
    }

    public int getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(int idAlbum) {
        this.idAlbum = idAlbum;
    }

    public int getIdArtiste() {
        return idArtiste;
    }

    public void setIdArtiste(int idArtiste) {
        this.idArtiste = idArtiste;
    }

    public String getTitreMusique() {
        return titreMusique;
    }

    public void setTitreMusique(String titreMusique) {
        this.titreMusique = titreMusique;
    }

    public String getTitreAlbum() {
        return titreAlbum;
    }

    public void setTitreAlbum(String titreAlbum) {
        this.titreAlbum = titreAlbum;
    }

    public int getDuration() {
        return duration;
    }

    public String getPochette() {
        return pochette;
    }

    public void setPochette(String pochette) {
        this.pochette = pochette;
    }

    public String getNomArtiste() {
        return nomArtiste;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }



    public void setNomArtiste(String nomArtiste) {
        this.nomArtiste = nomArtiste;
    }

    public String getParoleMusique() {
        return paroleMusique;
    }

    public void setParoleMusique(String paroleMusique) {
        this.paroleMusique = paroleMusique;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getTrack() {
        return track;
    }

    public void setTrack(int track) {
        this.track = track;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getBookmark() {
        return bookmark;
    }

    public void setBookmark(int bookmark) {
        this.bookmark = bookmark;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(this==obj) {
            return true;
        }

        if(obj==null || getClass()!=obj.getClass()) return false;

        Musique musique = (Musique)obj;
        if (idMusique != musique.getIdMusique() ||
                track != musique.getTrack() ||
                duration != musique.getDuration() ||
                idAlbum != musique.getIdAlbum() ||
                idArtiste != musique.getIdArtiste() ||
                titreMusique != musique.getTitreMusique() ||
                path != musique.getPath()) {

            return false;
        }

        return true;
    }
}
