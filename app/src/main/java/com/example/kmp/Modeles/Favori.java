package com.example.kmp.Modeles;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Favori implements Serializable {

    @NonNull
    @PrimaryKey
    private int idMusique;

    public Favori(int idMusique) {
        this.idMusique = idMusique;
    }

    public int getIdMusique() {
        return idMusique;
    }

    public void setIdMusique(int idMusique) {
        this.idMusique = idMusique;
    }
}
