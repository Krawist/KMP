package com.example.kmp.ViewModel;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.Album;
import com.example.kmp.Modeles.Artiste;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.Playlist;
import com.example.kmp.Room.Repository;
import com.example.kmp.Service.PlayerService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class KmpViewModel extends AndroidViewModel {

    private Repository repository;
    private LiveData<List<Musique>> allSongs;
    private LiveData<List<Musique>> favoriteSongs;
    private MutableLiveData<List<Playlist>> playlists;
    private MutableLiveData<Cursor> allAlbums;
    private MutableLiveData<Cursor> allArtistes;
    private MutableLiveData<Musique> currentPLayingMusic;
    private MutableLiveData<List<Musique>> listOfSongToPlay;
    private MutableLiveData<Integer> positionOfSongToPLay;
    private LiveData<List<Musique>> allAlbumMusics;
    private LiveData<List<Musique>> allArtistMusics;
    private MutableLiveData<Boolean> songIsPlaying;
    private MutableLiveData<Integer> playingSongPosition;
    private MutableLiveData<Integer> loopingMode;
    private MutableLiveData<Boolean> shuffleMode;
    private MutableLiveData<List<Musique>> playlistSongs;
    private MutableLiveData<List<Musique>> playingQueue;


    private static KmpViewModel INSTANCE = null;

    private KmpViewModel(Application application, Context context){
        super(application);
        repository = new Repository(application);
        allAlbums = new MutableLiveData<>();
        allArtistes = new MutableLiveData<>();
        currentPLayingMusic = new MutableLiveData<>();
        listOfSongToPlay = new MutableLiveData<>();
        positionOfSongToPLay = new MutableLiveData<>();
        playingSongPosition = new MutableLiveData<>();
        songIsPlaying = new MutableLiveData<>();
        loopingMode = new MutableLiveData<>();
        shuffleMode = new MutableLiveData<>();
        playlistSongs = new MutableLiveData<>();
        playlists = new MutableLiveData<>();
        playingQueue = new MutableLiveData<>();

        loopingMode.setValue(PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(PlayerService.PREFERNCE_REPEAT_MODE_KEY, PlaybackStateCompat.REPEAT_MODE_NONE));

        shuffleMode.setValue(PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(PlayerService.PREFERNCE_SHUFFLE_MODE_KEY, false));

        songIsPlaying.setValue(false);

        loadLastComponent(context);

        refreshData(context);
    }

    public MutableLiveData<List<Playlist>> getPlaylists() {
        return playlists;
    }

    private void loadLastComponent(Context context) {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int lastPLayedSongPosition = sharedPreferences.getInt(PlayerService.PREFERENCES_LAST_PLAYED_SONG_POSITION_KEY,0);
        positionOfSongToPLay.setValue(lastPLayedSongPosition);

        List<Musique> list = new ArrayList<>();
        Musique[] array = gson.fromJson(sharedPreferences.getString(PlayerService.PREFERENCES_LAST_LOADED_PLAYLIST_KEY,null),Musique[].class);

        if(array!=null) {
            for (int i = 0; i < array.length; i++) {
                list.add(array[i]);
            }
        }
        listOfSongToPlay.setValue(list);

        list.clear();
        array = gson.fromJson(sharedPreferences.getString(PlayerService.PREFERENCES_LAST_ACTIVE_PLAYLIST_KEY, null),Musique[].class);
        if(array!=null){
            for (int i = 0; i < array.length; i++) {
                list.add(array[i]);
            }
        }
        playingQueue.setValue(list);

        if(playingQueue.getValue()!=null){
            try {
                Musique musique = playingQueue.getValue().get(lastPLayedSongPosition);
                currentPLayingMusic.setValue(musique);
            }catch (IndexOutOfBoundsException e){
                //currentPLayingMusic.setValue(playingQueue.getValue().get(0));
            }
            //playingSongPosition.setValue(currentPLayingMusic.getValue().getBookmark());
        }

    }

    public MutableLiveData<List<Musique>> getPlayingQueue() {
        return playingQueue;
    }

    public MutableLiveData<Boolean> getShuffleMode() {
        return shuffleMode;
    }

    public MutableLiveData<Boolean> getSongIsPlaying() {
        return songIsPlaying;
    }

    public MutableLiveData<Integer> getLoopingMode() {
        return loopingMode;
    }

    public void setLoopingMode(Context context, int loopingMode) {
       this.loopingMode.setValue(loopingMode);
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(PlayerService.PREFERNCE_REPEAT_MODE_KEY,loopingMode)
                .commit();

    }

    public void setShuffleMode(Context context, boolean shuffle){
        this.shuffleMode.setValue(shuffle);
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PlayerService.PREFERNCE_SHUFFLE_MODE_KEY,shuffle)
                .commit();
    }

    public MutableLiveData<Integer> getPlayingSongPosition() {
        return playingSongPosition;
    }

    public LiveData<List<Musique>> getAllAlbumMusics(Context context, Album album) {

        allAlbumMusics = repository.getAllAlbumSongs(context, album.getIdAlbum());

        return allAlbumMusics;
    }

    public LiveData<List<Musique>> getAllArtistMusics(Context context, Artiste artiste) {

        allArtistMusics = repository.getArtistSongs(context, artiste.getIdArtiste());

        return allAlbumMusics;
    }

    public LiveData<List<Musique>> getAllAlbumMusics() {
        return allAlbumMusics;
    }

    public LiveData<List<Musique>> getAllArtistMusics() {
        return allArtistMusics;
    }

    public MutableLiveData<List<Musique>> getListOfSongToPlay() {
        return listOfSongToPlay;
    }

    public MutableLiveData<Integer> getPositionOfSongToPLay() {
        return positionOfSongToPLay;
    }

    public MutableLiveData<Musique> getCurrentPLayingMusic() {
        return currentPLayingMusic;
    }

    public LiveData<List<Musique>> getArtistSongs(Context context, int artistId){
        allArtistMusics = repository.getArtistSongs(context, artistId);
        //new UpdateMusicAsyncTask(context, allArtistMusics).execute();
        return allArtistMusics;
    }

    public void removeSongFromFavorite(Musique musique){
        repository.removeFromFavorite(musique);
    }

    public void addToFromFavorite(Musique musique){
        repository.addToFavorite(musique);
    }

    public void setPlayingList(List<Musique> musiques, Context context){
        listOfSongToPlay.setValue(musiques);
    }

    public LiveData<List<Musique>> getAllSongs() {
        return allSongs;
    }

    public LiveData<List<Musique>> getFavoriteSongs() {
        return favoriteSongs;
    }

    public MutableLiveData<Cursor> getAllAlbums() {
        return allAlbums;
    }

    public MutableLiveData<Cursor> getAllArtistes() {
        return allArtistes;
    }

    public static KmpViewModel getInstance(Application application, Context context){
        if(INSTANCE==null)
            INSTANCE = new KmpViewModel(application, context);

        return INSTANCE;
    }

    public void setPlayingSongPositionInMilli(int songProgressMillis, Context context) {
        getPlayingSongPosition().setValue(songProgressMillis);
    }

    public void refreshData(Context context) {
        if(ContextCompat.checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){

            allSongs = repository.getAllSongs();
            allAlbums.setValue(repository.loadAllAlbums(context));
            allArtistes.setValue(repository.loadAllArtists(context));
            favoriteSongs = repository.getFavoriteMusique();
            playlists.setValue(Helper.matchCursorToPlaylist(repository.getPlaylists(context)));

            new UpdateMusicAsyncTask(context, Helper.matchCursorToMusics(Helper.getAllMusic(context))).execute();

        }else{

        }
    }

    private void insertMusique(Musique musique){
        repository.insertSong(musique);
    }

    private class UpdateMusicAsyncTask extends AsyncTask<Void, Void, List<Musique>>{

        Context context;
        List<Musique> musiqueList;
        public UpdateMusicAsyncTask(Context context, List<Musique> musiques ){
            this.context = context;
            this.musiqueList = musiques;
        }

        @Override
        protected List<Musique> doInBackground(Void... voids) {
            return Helper.updateMusic(musiqueList, context);
        }

        @Override
        protected void onPostExecute(List<Musique> musiques) {

            for (Musique musique:
                    musiques) {
                musique.setLiked(false);
                insertMusique(musique);
            }
        }
    }

}
