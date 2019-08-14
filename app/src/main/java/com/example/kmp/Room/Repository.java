package com.example.kmp.Room;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.Playlist;

import java.util.List;

public class Repository {

    public KmpDao dao;
    private LiveData<List<Musique>> favoriteMusique;
    private LiveData<List<Playlist>> playlists;

    public Repository(Application application){
        KmpRoomDataBase database = KmpRoomDataBase.getDatabase(application);
        dao = database.getKmpDao();
        favoriteMusique = dao.getAllFavoriteSongs(true);
        playlists =dao.getAllPlaylists();
    }

    public LiveData<List<Musique>> getAllAlbumSongs(Context context, int albumId){
       return dao.getAllAlbumsSongs(albumId);
    }

    public LiveData<List<Musique>> getFavoriteMusique() {
        return favoriteMusique;
    }

    public Cursor getPlaylists(Context context) {

        return Helper.getPlaylist(context);
    }

    public Cursor getPlalistSongs(Context context, int plalistId){

        return Helper.getPLaysListSongs(context, plalistId);

    }

    public Cursor loadAllSongs(Context context) {
        return  Helper.getAllMusic(context);
    }

    public Cursor loadAllAlbums(Context context) {
        return Helper.getAllAlbum(context);
    }

    public Cursor loadAllArtists(Context context) {

        return Helper.getAllArtist(context);
    }

    public LiveData<List<Musique>> getArtistSongs(Context context, int artisId){
        return dao.getAllArtistesSongs(artisId);
    }

    public void removeFromFavorite(Musique musique){
        new DeleteMusicAsyncTask(dao).execute(musique);
    }

    public void addToFavorite(Musique musique){
        new InsertSongAsyncTask(dao).execute(musique);
    }

    public class DeleteMusicAsyncTask extends AsyncTask<Musique, Void, Void>{

        KmpDao dao;

        public DeleteMusicAsyncTask(KmpDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Musique... musiques) {
            dao.removeFromFavorite(musiques[0].getIdMusique());
            return null;
        }
    }

    public class InsertSongAsyncTask extends AsyncTask<Musique, Void, Void>{

        KmpDao dao;
        public InsertSongAsyncTask(KmpDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Musique... musiques) {
            dao.insertMusique(musiques[0]);

            return null;
        }
    }

    public LiveData<List<Musique>> getAllSongs(){
        return dao.getAllSongs();
    }

    public void insertSong(Musique musique){
        new InsertSongAsyncTask(dao).execute(musique);
    }

}
