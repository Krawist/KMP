package com.example.kmp.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.Playlist;

import java.util.List;

@Dao
public interface KmpDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMusique(Musique musique);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlayslist(Musique musique);

    @Query("SELECT * FROM musique WHERE isLiked = :isLiked ORDER BY titreMusique ASC")
    LiveData<List<Musique>> getAllFavoriteSongs(boolean isLiked);

    @Query("SELECT * FROM musique ORDER BY titreMusique ASC")
    LiveData<List<Musique>> getAllSongs();

    @Query("SELECT * FROM musique WHERE idAlbum = :idAlbum ORDER BY track ASC")
    LiveData<List<Musique>> getAllAlbumsSongs(int idAlbum);

    @Query("SELECT * FROM musique WHERE idArtiste = :idArtiste ORDER BY titreMusique ASC")
    LiveData<List<Musique>> getAllArtistesSongs(int idArtiste);



    @Query("SELECT * FROM playlist ORDER BY nomPlaylist ASC")
    LiveData<List<Playlist>> getAllPlaylists();

    @Query("DELETE FROM musique WHERE idMusique = :idMusique")
    void removeFromFavorite(int idMusique);

    @Query("DELETE FROM playlist WHERE idPlaylist = :idPlaylist")
    void deletePlaylist(int idPlaylist);
}
