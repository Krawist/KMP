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

import java.util.List;

public class Repository {

    public KmpDao dao;

    public Repository(Application application){
        KmpRoomDataBase database = KmpRoomDataBase.getDatabase(application);
        dao = database.getKmpDao();
    }


    /** load request **/
    public List<Album> loadAllAlbums(Context context) {
        return Helper.matchCursorToAlbums(Helper.getAllAlbum(context));
    }

    public List<Artiste> loadAllArtists(Context context) {

        return Helper.matchCursorToArtists(Helper.getAllArtist(context));
    }

    public List<Playlist> loadPlaylists(Context context) {
        return Helper.matchCursorToPlaylist(Helper.getPlaylist(context));
    }

    public List<Musique> loadAllMusics(Context context){
        return Helper.matchBasicCursorToMusics(Helper.getAllMusic(context));
    }

    public List<Musique> loadAllArtistSongs(Context context, int artisId){
        return Helper.matchBasicCursorToMusics(Helper.getAllArtistSongs(context,artisId));
    }

    public List<Musique> loadAllPlaylistSongs(Context context, int idPlaylist){
        return Helper.matchBasicCursorToMusics(Helper.getPLaysListSongs(context,idPlaylist));
    }

    public List<Musique> loadAllAlbumSongs(Context context, int albumId){
        return Helper.matchBasicCursorToMusics(Helper.getAlbumMusic(context,albumId));
    }

    public List<Musique> loadFavoriteSong(Context context, List<Favori> favoriList) {
        return Helper.matchBasicCursorToMusics(Helper.getFavoriteSongs(context, favoriList));
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
