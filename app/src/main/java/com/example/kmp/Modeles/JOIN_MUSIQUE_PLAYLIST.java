package com.example.kmp.Modeles;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(primaryKeys = {"idPlaylist","idMusique"})
public class JOIN_MUSIQUE_PLAYLIST implements Serializable {

    private int idPlaylist;
    private int idMusique;

    public JOIN_MUSIQUE_PLAYLIST(int idPlaylist, int idMusique) {
        this.idPlaylist = idPlaylist;
        this.idMusique = idMusique;
    }

    public int getIdPlaylist() {
        return idPlaylist;
    }

    public void setIdPlaylist(int idPlaylist) {
        this.idPlaylist = idPlaylist;
    }

    public int getIdMusique() {
        return idMusique;
    }

    public void setIdMusique(int idMusique) {
        this.idMusique = idMusique;
    }
}

