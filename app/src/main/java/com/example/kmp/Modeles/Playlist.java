package com.example.kmp.Modeles;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity
public class Playlist implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int idPlaylist;
    private String nomPlaylist;
    private String pochette;
    private int nombreSong;
    private List<Musique> songsOfPlaylist;

    public List<Musique> getSongsOfPlaylist() {
        return songsOfPlaylist;
    }

    public void setSongsOfPlaylist(List<Musique> songsOfPlaylist) {
        this.songsOfPlaylist = songsOfPlaylist;
    }

    public int getNombreSong() {
        return nombreSong;
    }

    public void setNombreSong(int nombreSong) {
        this.nombreSong = nombreSong;
    }

    public int getIdPlaylist() {
        return idPlaylist;
    }

    public void setIdPlaylist(int idPlaylist) {
        this.idPlaylist = idPlaylist;
    }

    public String getNomPlaylist() {
        return nomPlaylist;
    }

    public void setNomPlaylist(String nomPlaylist) {
        this.nomPlaylist = nomPlaylist;
    }

    public String getPochette() {
        return pochette;
    }

    public void setPochette(String pochette) {
        this.pochette = pochette;
    }

}
