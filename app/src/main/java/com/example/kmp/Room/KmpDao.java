package com.example.kmp.Room;

import android.graphics.Paint;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.kmp.Modeles.BaseMusique;
import com.example.kmp.Modeles.Favori;
import com.example.kmp.Modeles.JOIN_MUSIQUE_PLAYLIST;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.Playlist;

import java.util.List;

@Dao
public interface KmpDao {


    /** insert request*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMusique(BaseMusique musique);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlayslist(Playlist playlist);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavori(Favori favori);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addSongToPlaylist(JOIN_MUSIQUE_PLAYLIST join_musique_playlist);


    /** delete request**/
    @Delete
    void deleteFavori(Favori favori);

    @Delete
    void deletePlaylist(Playlist playlist);

    @Delete
    void deleteMusique(BaseMusique musique);

    @Delete
    void removeSongToPlaylist(JOIN_MUSIQUE_PLAYLIST join_musique_playlist);



    /** SELECT REQUEST */
    @Query("SELECT * ,  (basemusique.idMusique IN (SELECT * FROM favori)) AS 'isLiked' FROM BaseMusique, favori WHERE BaseMusique.idMusique = favori.idMusique ORDER BY titreMusique ASC")
    LiveData<List<Musique>> getAllFavoriteSongs();

    /*    @Query("SELECT *, IF((SELECT count(idMusique) FROM favori WHERE idMusique = baseMusique.idMusique)>0, 'vrai', 'faux') isLike")*/

    @Query("SELECT * ,  (idMusique IN (SELECT * FROM favori)) AS 'isLiked' FROM BaseMusique ORDER BY titreMusique ASC")
    LiveData<List<Musique>> getAllSongs();

    @Query("SELECT * ,  (idMusique IN (SELECT * FROM favori)) AS 'isLiked' FROM BaseMusique WHERE idAlbum = :idAlbum ORDER BY track ASC")
    LiveData<List<Musique>> getAllAlbumsSongs(int idAlbum);

    @Query("SELECT * ,  (idMusique IN (SELECT * FROM favori)) AS 'isLiked' FROM BaseMusique WHERE idArtiste = :idArtiste ORDER BY titreMusique ASC")
    LiveData<List<Musique>> getAllArtistesSongs(int idArtiste);

    @Query("SELECT * FROM playlist ORDER BY nomPlaylist ASC")
    LiveData<List<Playlist>> getAllPlaylists();

    @Query("SELECT * ,  (basemusique.idMusique IN (SELECT * FROM favori)) AS 'isLiked' FROM  basemusique, JOIN_MUSIQUE_PLAYLIST WHERE JOIN_MUSIQUE_PLAYLIST.idMusique = baseMusique.idMusique AND JOIN_MUSIQUE_PLAYLIST.idPlaylist = :idPlaylist")
    LiveData<List<Musique>> getUserPlaylistSong(int idPlaylist);
}
