package com.example.kmp.Room;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.BaseMusique;
import com.example.kmp.Modeles.JOIN_MUSIQUE_PLAYLIST;
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
    public Cursor loadAllAlbums(Context context) {
        return Helper.getAllAlbum(context);
    }

    public Cursor loadAllArtists(Context context) {

        return Helper.getAllArtist(context);
    }

    public Cursor loadPlaylists(Context context) {
        return Helper.getPlaylist(context);
    }


    /** insert request */
    public void insertSong(BaseMusique musique){
        new InsertSongAsyncTask(dao).execute(musique);
    }

    public void insertFavori(Favori favori){
        new InsertFavorisyncTask(dao).execute(favori);
    }

    public void insertPlaylist(Playlist playlist){
        new InsertPlaylistAsyncTask(dao).execute(playlist);
    }

    public void addSongToPlaylist(JOIN_MUSIQUE_PLAYLIST join_musique_playlist){
        new InsertJoinMusiquePlaylist(dao).execute(join_musique_playlist);
    }


    /** get from local database request **/
    public LiveData<List<Musique>> getAllAlbumSongs(int albumId){
        return dao.getAllAlbumsSongs(albumId);
    }

    public LiveData<List<Musique>> getFavoriteMusique() {
        return dao.getAllFavoriteSongs();
    }

    public LiveData<List<Musique>> getAllSongs(){
        return dao.getAllSongs();
    }

    public LiveData<List<Musique>> getArtistSongs(int artisId){
        return dao.getAllArtistesSongs(artisId);
    }

    public LiveData<List<Musique>> getUserPlayListSong(int idPlaylist){
        return dao.getUserPlaylistSong(idPlaylist);
    }

    /** delete request*/
    public void removeFromFavori(Favori favori){
        new DeleteFavoriAsyncTask(dao).execute(favori);
    }

    public void deleteMusique(BaseMusique musique){
        new DeleteMusiqueAsyncTask(dao).execute(musique);
    }

    public void removeSonToPlaylist(JOIN_MUSIQUE_PLAYLIST join_musique_playlist){
        new DeleteJoinMusique(dao).execute(join_musique_playlist);
    }

    /** AsynTask for insert operation**/
    public class InsertSongAsyncTask extends AsyncTask<BaseMusique, Void, Void>{

        KmpDao dao;
        public InsertSongAsyncTask(KmpDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(BaseMusique... musiques) {
            dao.insertMusique(musiques[0]);

            return null;
        }
    }

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

    public class InsertPlaylistAsyncTask extends AsyncTask<Playlist, Void, Void>{

        KmpDao dao;
        public InsertPlaylistAsyncTask(KmpDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Playlist... playlists) {
            dao.insertPlayslist(playlists[0]);

            return null;
        }
    }

    public class InsertJoinMusiquePlaylist extends AsyncTask<JOIN_MUSIQUE_PLAYLIST, Void, Void>{

        KmpDao dao;
        public InsertJoinMusiquePlaylist(KmpDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(JOIN_MUSIQUE_PLAYLIST... join_musique_playlists) {
            dao.addSongToPlaylist(join_musique_playlists[0]);

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

    public class DeleteMusiqueAsyncTask extends AsyncTask<BaseMusique, Void, Void>{

        KmpDao dao;
        public DeleteMusiqueAsyncTask(KmpDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(BaseMusique... musiques) {
            dao.deleteMusique(musiques[0]);

            return null;
        }
    }

    public class DeleteJoinMusique extends AsyncTask<JOIN_MUSIQUE_PLAYLIST, Void, Void>{

        KmpDao dao;
        public DeleteJoinMusique(KmpDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(JOIN_MUSIQUE_PLAYLIST... musiques) {
            dao.removeSongToPlaylist(musiques[0]);

            return null;
        }
    }


}
