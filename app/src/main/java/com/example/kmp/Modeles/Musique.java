package com.example.kmp.Modeles;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class Musique extends BaseMusique implements Serializable {

    private boolean isLiked;

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
}
