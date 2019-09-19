package com.example.kmp.Room;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.Album;
import com.example.kmp.Modeles.Artiste;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.Favori;
import com.example.kmp.Modeles.Playlist;

import java.util.HashMap;
import java.util.List;

public class Repository {

    public KmpDao dao;

    public Repository(Application application){
        KmpRoomDataBase database = KmpRoomDataBase.getDatabase(application);
        dao = database.getKmpDao();
    }


    /** load request **/
    public List<Album> loadAllAlbums(Context context, HashMap<Integer, String> pathOfSongAlbumArt) {
        return Helper.matchCursorToAlbums(Helper.getAllAlbum(context), pathOfSongAlbumArt);
    }

    public List<Artiste> loadAllArtists(Context context) {

        return Helper.matchCursorToArtists(Helper.getAllArtist(context));
    }

    public List<Playlist> loadPlaylists(Context context) {
        return Helper.matchCursorToPlaylist(Helper.getPlaylist(context));
    }

    public List<Musique> loadAllMusics(Context context, HashMap<Integer, String> pathOfSongAlbumArt){
        return Helper.matchBasicCursorToMusics(Helper.getAllMusic(context), pathOfSongAlbumArt);
    }

    public List<Musique> loadAllArtistSongs(Context context, int artisId, HashMap<Integer, String> pathOfSongAlbumArt){
        return Helper.matchBasicCursorToMusics(Helper.getAllArtistSongs(context,artisId),pathOfSongAlbumArt);
    }

    public List<Musique> loadAllPlaylistSongs(Context context, int idPlaylist, HashMap<Integer, String> pathOfSongAlbumArt){
        return Helper.matchPlaylistCursorToMusics(Helper.getPLaysListSongs(context,idPlaylist), pathOfSongAlbumArt);
    }

    public List<Musique> loadAllAlbumSongs(Context context, int albumId, HashMap<Integer, String> pathOfSongAlbumArt){
        return Helper.matchBasicCursorToMusics(Helper.getAlbumMusic(context,albumId), pathOfSongAlbumArt);
    }

    public List<Musique> loadFavoriteSong(Context context, List<Integer> favoriList, HashMap<Integer, String> pathOfSongAlbumArt) {
        return Helper.matchBasicCursorToMusics(Helper.getFavoriteSongs(context, favoriList), pathOfSongAlbumArt);
    }

    public LiveData<List<Favori>> getFavoriteSongsId(){
        return dao.getAllFavoriteSongs();
    }


    /** insert request */
    public void insertFavori(Favori favori){
        new InsertFavorisyncTask(dao).execute(favori);
    }

    /** delete request*/
    public void removeFromFavori(Favori favori){
        new DeleteFavoriAsyncTask(dao).execute(favori);
    }


    /** AsynTask for insert operation**/
    public class InsertFavorisyncTask extends AsyncTask<Favori, Void, Void>{

        KmpDao dao;
        public InsertFavorisyncTask(KmpDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Favori... favoris) {
            dao.insertFavori(favoris[0]);

            return null;
        }
    }

    public class DeleteFavoriAsyncTask extends AsyncTask<Favori, Void, Void>{

        KmpDao dao;
        public DeleteFavoriAsyncTask(KmpDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Favori... favoris) {
            dao.deleteFavori(favoris[0]);

            return null;
        }
    }
}
