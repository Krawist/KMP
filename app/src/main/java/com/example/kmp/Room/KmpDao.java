package com.example.kmp.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.kmp.Modeles.Favori;

import java.util.List;

@Dao
public interface KmpDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavori(Favori favori);


    /** delete request**/
    @Delete
    void deleteFavori(Favori idFavori);

    /** SELECT REQUEST */
    @Query("SELECT * FROM favori")
    LiveData<List<Favori>> getAllFavoriteSongs();
}
