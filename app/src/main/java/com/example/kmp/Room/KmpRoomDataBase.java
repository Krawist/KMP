package com.example.kmp.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.kmp.Modeles.Musique;

@Database(entities = {Musique.class}, version = 12)
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
