package com.example.kmp.ViewModel;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.kmp.Activity.MainActivity;
import com.example.kmp.Activity.PermissionActivity;
import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.Album;
import com.example.kmp.Modeles.Artiste;
import com.example.kmp.Modeles.JOIN_MUSIQUE_PLAYLIST;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.Favori;
import com.example.kmp.Modeles.Playlist;
import com.example.kmp.Modeles.ThemeColor;
import com.example.kmp.Room.Repository;
import com.example.kmp.Service.PlayerService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KmpViewModel extends AndroidViewModel {

    public static final String PREFERENCE_FAVORITE_KEY = "favoris";

    private Repository repository;
    private MutableLiveData<List<Musique>> allSongs;
    private MutableLiveData<List<Musique>> favoriteSongs;
    private MutableLiveData<List<Playlist>> playlists;
    private MutableLiveData<List<Album>> allAlbums;
    private MutableLiveData<List<Artiste>> allArtistes;
    private MutableLiveData<Musique> currentPLayingMusic;
    private MutableLiveData<List<Musique>> listOfSongToPlay;
    private MutableLiveData<Integer> positionOfSongToPLay;
    private MutableLiveData<List<Musique>> allAlbumMusics;
    private MutableLiveData<List<Musique>> allArtistMusics;
    private MutableLiveData<Boolean> songIsPlaying;
    private MutableLiveData<Integer> playingSongPosition;
    private MutableLiveData<Integer> loopingMode;
    private MutableLiveData<Boolean> shuffleMode;
    private MutableLiveData<List<Musique>> playlistSongs;
    private MutableLiveData<List<Musique>> playingQueue;
    private MutableLiveData<ThemeColor> themeColor;

    private MutableLiveData<List<Integer>> favoriteSongsId;

    private static KmpViewModel INSTANCE = null;
    private HashMap pathOfSongAlbumArt;

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
        themeColor = new MutableLiveData<>();
        allSongs = new MutableLiveData<>();
        favoriteSongs = new MutableLiveData<>();
        allAlbums = new MutableLiveData<>();
        allArtistes = new MutableLiveData<>();
        allArtistMusics = new MutableLiveData<>();
        allAlbumMusics = new MutableLiveData<>();
        pathOfSongAlbumArt = new HashMap();
        favoriteSongsId = new MutableLiveData<>();

        loopingMode.setValue(PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(PlayerService.PREFERNCE_REPEAT_MODE_KEY, PlaybackStateCompat.REPEAT_MODE_NONE));

        shuffleMode.setValue(PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(PlayerService.PREFERNCE_SHUFFLE_MODE_KEY, false));

        songIsPlaying.setValue(false);

        loadLastComponent(context);

        refreshData(context);
    }

    public LiveData<List<Integer>> getFavoriteSongsId() {
        return favoriteSongsId;
    }

    public MutableLiveData<List<Playlist>> getPlaylists() {
        return playlists;
    }

    private void loadLastComponent(Context context) {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int lastPLayedSongPosition = sharedPreferences.getInt(PlayerService.PREFERENCES_LAST_PLAYED_SONG_POSITION_KEY,0);
        positionOfSongToPLay.setValue(lastPLayedSongPosition);

        int positionInLAstSong = sharedPreferences.getInt(PlayerService.PREFERNCE_POSITION_MILLI_LAST_SONG, 0);
        playingSongPosition.setValue(positionInLAstSong);

        List<Musique> list = new ArrayList<>();
        Musique[] array = gson.fromJson(sharedPreferences.getString(PlayerService.PREFERENCES_LAST_LOADED_PLAYLIST_KEY,null), Musique[].class);
        if(array!=null) {
            for (int i = 0; i < array.length; i++) {
                list.add(array[i]);
            }
        }
        listOfSongToPlay.setValue(list);

        List<Integer> list2 = new ArrayList<>();
        Integer[] array2 = gson.fromJson(sharedPreferences.getString(PREFERENCE_FAVORITE_KEY,null), Integer[].class);
        if(array2!=null) {
            for (int i = 0; i < array2.length; i++) {
                list2.add(array2[i]);
            }
        }
        favoriteSongsId.setValue(list2);

        list.clear();
        array = gson.fromJson(sharedPreferences.getString(PlayerService.PREFERENCES_LAST_ACTIVE_PLAYLIST_KEY, null), Musique[].class);
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

    public MutableLiveData<ThemeColor> getThemeColor() {
        return themeColor;
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
        allAlbumMusics.setValue(repository.loadAllAlbumSongs(context,album.getIdAlbum(),pathOfSongAlbumArt));
        //new UpdateMusicAsyncTask(context,allAlbumMusics).execute();

        return allAlbumMusics;
    }

    public LiveData<List<Musique>> getAllArtistMusics(Context context, Artiste artiste) {
        if(allArtistMusics.getValue()!=null){
            if(allArtistMusics.getValue().get(0).getIdAlbum()!=artiste.getIdArtiste())
                allArtistMusics.setValue(repository.loadAllArtistSongs(context,artiste.getIdArtiste(), pathOfSongAlbumArt));
        }else
            allArtistMusics.setValue(repository.loadAllArtistSongs(context,artiste.getIdArtiste(), pathOfSongAlbumArt));

        //new UpdateMusicAsyncTask(context,allArtistMusics).execute();

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

    public MutableLiveData<List<Musique>> getAllAlbumMusics() {
        return allAlbumMusics;
    }

    public MutableLiveData<List<Musique>> getAllArtistMusics() {
        return allArtistMusics;
    }

    public MutableLiveData<List<Musique>> getPlaylistSongs() {
        return playlistSongs;
    }

    public MutableLiveData<List<Musique>> getPlaylistSongs(Context context, Playlist playlist) {
        if(playlistSongs.getValue()!=null && !playlistSongs.getValue().isEmpty()){
            if(playlistSongs.getValue().get(0).getIdAlbum()!=playlist.getIdPlaylist())
                playlistSongs.setValue(repository.loadAllPlaylistSongs(context,playlist.getIdPlaylist(), pathOfSongAlbumArt));
        }else
            playlistSongs.setValue(repository.loadAllPlaylistSongs(context,playlist.getIdPlaylist(), pathOfSongAlbumArt));

        //new UpdateMusicAsyncTask(context,playlistSongs).execute();

        return playlistSongs;
    }

    public void loadPlaylistSong(Context context) {
        if(playlistSongs.getValue()!=null && !playlistSongs.getValue().isEmpty()) {
            for (Playlist playlist : playlists.getValue()) {
                playlist.setSongsOfPlaylist(repository.loadAllPlaylistSongs(context, playlist.getIdPlaylist(), pathOfSongAlbumArt));
            }
        }

        //new UpdateMusicAsyncTask(context,playlistSongs).execute();
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

    public MutableLiveData<List<Album>> getAllAlbums() {
        return allAlbums;
    }

    public MutableLiveData<List<Artiste>> getAllArtistes() {
        return allArtistes;
    }

    public static KmpViewModel getInstance(Application application, Context context){
        if(INSTANCE==null)
            INSTANCE = new KmpViewModel(application, context);

        return INSTANCE;
    }

    public void setPlayingSongPositionInMilli(int songProgressMillis, Context context) {
        getPlayingSongPosition().setValue(songProgressMillis);
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(PlayerService.PREFERNCE_POSITION_MILLI_LAST_SONG, songProgressMillis)
                .apply();
    }

    public boolean refreshData(Context context) {

        if(ContextCompat.checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            allAlbums.setValue(repository.loadAllAlbums(context, pathOfSongAlbumArt));
            allArtistes.setValue(repository.loadAllArtists(context));
            allSongs.setValue(repository.loadAllMusics(context, pathOfSongAlbumArt));
            playlists.setValue(repository.loadPlaylists(context));
            loadPlaylistSong(context);
            return true;
        }else{
            return false;
        }
    }

    public void addToFavorite(Context context, Integer favori){
        //repository.insertFavori(favori);
        List<Integer> list= favoriteSongsId.getValue();
        if(list==null)
            list = new ArrayList<>();

        list.add(new Integer(favori));
        favoriteSongsId.setValue(list);

        updateFavoriteSongId(context);

    }

    private void updateFavoriteSongId(Context context) {
        int[] array = new int[favoriteSongsId.getValue().size()];
        for(int i=0; i<favoriteSongsId.getValue().size(); i++){
            array[i] = favoriteSongsId.getValue().get(i);
        }

        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREFERENCE_FAVORITE_KEY,new Gson().toJson(array))
                .apply();
    }

    public void removeFromFavorite(Context context, int favori){
        //repository.removeFromFavori(favori);
        List<Integer> list= favoriteSongsId.getValue();
        if(list!=null)
            list.remove(new Integer(favori));

        favoriteSongsId.setValue(list);

        updateFavoriteSongId(context);
    }

    public void refreshFavoriteSong(Context context) {
        favoriteSongs.setValue(repository.loadFavoriteSong(context, favoriteSongsId.getValue(),pathOfSongAlbumArt));
        //new UpdateMusicAsyncTask(context,favoriteSongs).execute();
    }

    private class UpdateMusicAsyncTask extends AsyncTask<Void, Void, List<Musique>>{

        Context context;
        MutableLiveData<List<Musique>> mutableLiveData;
        List<Musique> data;
        public UpdateMusicAsyncTask(Context context, MutableLiveData<List<Musique>> mutableLiveData){
            this.context = context;
            this.mutableLiveData = mutableLiveData;
            this.data = mutableLiveData.getValue();
        }

        @Override
        protected List<Musique> doInBackground(Void... voids) {
            return Helper.updateMusic(data, context);
        }

        @Override
        protected void onPostExecute(List<Musique> musiques) {
            mutableLiveData.setValue(musiques);
        }
    }
}
