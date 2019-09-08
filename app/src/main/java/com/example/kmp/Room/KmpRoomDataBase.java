package com.example.kmp.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.kmp.Modeles.Favori;
import com.example.kmp.Modeles.JOIN_MUSIQUE_PLAYLIST;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.Playlist;

@Database(entities = {Musique.class, Playlist.class, Favori.class, JOIN_MUSIQUE_PLAYLIST.class}, version = 10)
public abstract class KmpRoomDataBase extends RoomDatabase {

    private static volatile KmpRoomDataBase INSTANCE;
    private static final String DATABASE_NAME = "kmp_mobile_database";

    public static KmpRoomDataBase getDatabase(final Context context){
        if(INSTANCE==null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),KmpRoomDataBase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return INSTANCE;
    }

    public abstract KmpDao getKmpDao();
}
