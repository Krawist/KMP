package com.example.kmp.Service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import com.example.kmp.Modeles.Musique;
import com.example.kmp.Notification.PlayingNotification;
import com.example.kmp.Notification.PlayingNotificationImpl;
import com.example.kmp.Playback;
import com.example.kmp.PlaybackImpl;
import com.example.kmp.R;
import com.example.kmp.Receiver.MediaButtonIntentReceiver;
import com.example.kmp.ViewModel.KmpViewModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_ALL;
import static android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_GROUP;
import static android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_INVALID;
import static android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_NONE;
import static android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_ONE;


public class PlayerService extends MediaBrowserServiceCompat implements AudioManager.OnAudioFocusChangeListener {

    public static final String PACKAGE_NAME = "com.example.kmp";
    public static final String MUSIC_PACKAGE_NAME = "com.android.music";
    public static final String ACTION_TOGGLE_PAUSE = PACKAGE_NAME + ".togglepause";
    public static final String ACTION_PLAY = PACKAGE_NAME + ".play";
    public static final String ACTION_PLAY_PLAYLIST = PACKAGE_NAME + ".play.playlist";
    public static final String ACTION_PAUSE = PACKAGE_NAME + ".pause";
    public static final String ACTION_STOP = PACKAGE_NAME + ".stop";
    public static final String ACTION_SKIP = PACKAGE_NAME + ".skip";
    public static final String ACTION_REWIND = PACKAGE_NAME + ".rewind";
    public static final String ACTION_QUIT = PACKAGE_NAME + ".quitservice";
    public static final String ACTION_PENDING_QUIT = PACKAGE_NAME + ".pendingquitservice";
    public static final String INTENT_EXTRA_PLAYLIST = PACKAGE_NAME + "intentextra.playlist";
    public static final String INTENT_EXTRA_SHUFFLE_MODE = PACKAGE_NAME + ".intentextra.shufflemode";
    public static final String SERVICE_CREATE = "com.example.kmp.KMP_SERVICE_CREATED";
    public static final String SERVICE_DESTROYED= "com.example.kmp.KMP_SERVICE_DESTROYED";
    public static final String PREFERENCES_LAST_PLAYED_SONG_POSITION_KEY = PACKAGE_NAME + "position_dernier_song";
    public static final String PREFERENCES_LAST_LOADED_PLAYLIST_KEY = PACKAGE_NAME + "derniere_playlist_chargee";
    public static final String PREFERENCES_LAST_ACTIVE_PLAYLIST_KEY = PACKAGE_NAME + "derniere_playlist_actuelle";
    public static final String PREFERNCE_SHUFFLE_MODE_KEY = PACKAGE_NAME + "shuffle_mode";
    public static final String PREFERNCE_REPEAT_MODE_KEY = PACKAGE_NAME + "repeat_mode";
    public static final String PREFERNCE_POSITION_MILLI_LAST_SONG = PACKAGE_NAME + "duree_dernier_song_joué";


    private static final String MEDIA_SESSION_LOG_TAG = PlayerService.class.getSimpleName();
    private IBinder binder = new LocalBinder();
    private Context context;
    private PlaybackStateCompat.Builder stateBuilder;
    private int mediaSessionFalgs = MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS;
    private MediaSessionCompat mediaSession;
    private MediaSessionCompat.Callback sessionCallBack;
    private AudioManager audioManager;
    boolean becomingNoisyReceiverRegistered;
    private final BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action!=null && action.equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
                pause();
            }
        }
    };
    private PowerManager.WakeLock wakeLock;
    private int position;
    private int nextPosition;
    private List<Musique> playingQueue;
    private List<Musique> originalPlayingQueue;
    private int repeatMode;
    private Playback playback;
    private boolean pausedByTransientLossOfFocus;
    private IntentFilter becomingNoisyReceiverIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    KmpViewModel model;
    UpdateSeekBarPosition seekBarPositionRunnable = new UpdateSeekBarPosition();
    Handler seekbarHandler = new Handler();
    private static final int SEEK_BAR_POSITION_REFRESH_DELAY = 500;
    private PlayingNotification playingNotification;

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange){

            case AudioManager.AUDIOFOCUS_LOSS:
                pause();
                break;

            case AudioManager.AUDIOFOCUS_GAIN:
                playback.modifyVolume(1);
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                pause();
                break;


            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                playback.modifyVolume(0.5f);
                break;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        wakeLock.setReferenceCounted(false);

        context = getApplicationContext();
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY|
                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_SEEK_TO);

        setupMediaSession();

        sendBroadcast(new Intent(SERVICE_CREATE));

        configureViewModel();

        mediaSession.setActive(true);

        playback = new PlaybackImpl(this,context);

        initNotification();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(becomingNoisyReceiverRegistered){
            unregisterReceiver(becomingNoisyReceiver);
            becomingNoisyReceiverRegistered = false;
        }
        mediaSession.setActive(false);
        quit();
        releaseResources();
        wakeLock.release();
        sendBroadcast(new Intent(SERVICE_DESTROYED));


    }

    public void setShuffle(){
        model.setShuffleMode(context,true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent!=null){
            String action = intent.getAction();
            if(action!=null){
                switch (action){

                    case ACTION_PLAY:
                        play();
                        break;

                    case ACTION_PLAY_PLAYLIST:
                        loadMusic();
                        play();
                        initialisePlayingQueue();
                        updatePlayListPreference();
                        break;

                    case ACTION_TOGGLE_PAUSE:
                        if(isPlaying())
                            pause();
                        else
                            play();
                        break;

                    case ACTION_PAUSE:
                        pause();
                        break;

                    case ACTION_SKIP:
                        playNextSong();
                        break;

                    case ACTION_REWIND:
                        playPreviousSong();
                        break;

                    default:
                        preparePlaying();
                        break;

                }
            }
        }
        return START_NOT_STICKY;
    }

    private void initialisePlayingQueue() {
        boolean isShuffleMode = getShuffleMode();
        if(isShuffleMode){
            shuffleOriginalList();
        }else{
            model.getPlayingQueue().setValue(new ArrayList<Musique>(getOriginalPlayList()));
        }
    }

    private void shuffleOriginalList() {
        List<Musique> list = new ArrayList<>(getOriginalPlayList());
        List<Musique> newPlayingQueue = new ArrayList<>();
        Random rand = new Random();
        while (!list.isEmpty()){
            int n = rand.nextInt(list.size());
            newPlayingQueue.add(list.get(n));
            list.remove(n);
        }

        model.getPlayingQueue().setValue(newPlayingQueue);
    }

    private void preparePlaying() {
        loadMusic();
        seetTo(model.getPlayingSongPosition().getValue());
    }

    private void initNotification() {
        playingNotification = new PlayingNotificationImpl();
        playingNotification.init(this);
    }

    private void updateNotification(){
        if(playingNotification!=null && getCurrentSong()!=null && getCurrentSong().getIdMusique()!= -1){
            playingNotification.update();
        }
    }

    private void configureViewModel() {
        model = KmpViewModel.getInstance(getApplication(),context);
    }

    private AudioManager getAudioManager() {
        if (audioManager == null) {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        }
        return audioManager;
    }

    private void setupMediaSession(){
        ComponentName mediaButtonReceiverComponentName = new ComponentName(getApplicationContext(), MediaButtonIntentReceiver.class);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent(mediaButtonReceiverComponentName);

        PendingIntent mediaButtonReceiverPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, mediaButtonIntent, 0);

        mediaSession = new MediaSessionCompat(context,MEDIA_SESSION_LOG_TAG,mediaButtonReceiverComponentName,mediaButtonReceiverPendingIntent);

        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
                return MediaButtonIntentReceiver.handleIntent(PlayerService.this, mediaButtonEvent);
            }

            @Override
            public void onPlay() {
                play();
            }

            @Override
            public void onPause() {
                pause();
            }

            @Override
            public void onSkipToNext() {
                playNextSong();
            }

            @Override
            public void onSkipToPrevious() {
                playPreviousSong();
            }

            @Override
            public void onStop() {
                stop();
            }

            @Override
            public void onSeekTo(long pos) {
                seetTo(pos);
            }
        });

        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);

        mediaSession.setMediaButtonReceiver(mediaButtonReceiverPendingIntent);
    }

    public  void play() {
        synchronized (this) {
            if (requestFocus()) {
                boolean isPlaying = playback.play();
                if(!isPlaying) {
                    loadMusic();
                    play();
                }
            }

            updateMediaSessionMetaData();

            updateMediaSessionPlaybackState();

            model.getSongIsPlaying().setValue(true);

            mediaSession.setActive(true);

            initNotification();

            updateNotification();

            registerReceiver(true);

            seekBarPositionRunnable.run();
        }
    }

    public  void pause(){
        pausedByTransientLossOfFocus = false;
        playback.pause();
        model.getSongIsPlaying().setValue(false);

        mediaSession.setActive(false);

        updateNotification();
    }

    public  void playNextSong(){

        updatePosition(getNextPosition());
        loadMusic();
        play();
    }

    public  void playPreviousSong(){

        updatePosition(getPreviousPosition());
        loadMusic();
        play();
    }

    public void loadMusic() {
        pause();
        playback.setDataSource(getCurrentSong().getPath());
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(PREFERENCES_LAST_PLAYED_SONG_POSITION_KEY, getPosition())
                .apply();
    }

    public  void stop(){
        playback.stop();
        stopForeground(false);
        model.getSongIsPlaying().setValue(false);
        playback.release();
    }

    private void registerReceiver(boolean register){
        if(register){
            if(!becomingNoisyReceiverRegistered){
                IntentFilter filter = new IntentFilter();
                filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
                registerReceiver(becomingNoisyReceiver,filter);
            }
        }else{
            unregisterReceiver(becomingNoisyReceiver);
        }
        becomingNoisyReceiverRegistered = register;
    }

    private void updatePlayListPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        Musique[] playingQueueArray = new Musique[getPlayingQueueSize()];
        Musique[] orinalPlaylistArray = new Musique[getOriginalPlaylistSize()];
        // on transforme les tableaux en arrays pour mieux stocker
        for(int i=0; i<getPlayingQueueSize(); i++){
            playingQueueArray[i] = getPlayingQueue().get(i);
        }
        for(int i=0; i<getOriginalPlaylistSize(); i++){
            orinalPlaylistArray[i] = getOriginalPlayList().get(i);
        }

        String playingQueueAsString = gson.toJson(playingQueueArray);
        String originalPlayListAsString = gson.toJson(orinalPlaylistArray);

        sharedPreferences.edit()
                .putString(PREFERENCES_LAST_ACTIVE_PLAYLIST_KEY, playingQueueAsString)
                .putString(PREFERENCES_LAST_LOADED_PLAYLIST_KEY, originalPlayListAsString)
                .apply();
    }

    private List<Musique> getOriginalPlayList(){
        return model.getListOfSongToPlay().getValue();
    }

    public void updatePosition(int newCurrentPosotion) {
        model.getPositionOfSongToPLay().setValue(newCurrentPosotion);
        model.getCurrentPLayingMusic().setValue(getPlayingQueue().get(newCurrentPosotion));
    }

    private boolean requestFocus() {
        return (getAudioManager().requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
    }

    private void updateMediaSessionPlaybackState() {
        mediaSession.setPlaybackState(
                new PlaybackStateCompat.Builder()
                        .setState(isPlaying() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED,
                                getPosition(), 1)
                        .build());
    }

    public List<Musique> getPlayingQueue(){
        return model.getPlayingQueue().getValue();
    }

    private void updateMediaSessionMetaData() {
        final Musique song = getCurrentSong();

        if (song.getIdMusique() == -1) {
            mediaSession.setMetadata(null);
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(song.getPochette());
        if(bitmap==null)
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

        final MediaMetadataCompat.Builder metaData = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getNomArtiste())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, song.getNomArtiste())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.getTitreAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getTitreMusique())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.getDuration())
                .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, getPosition() + 1)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //metaData.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, getPlayingQueueSize());
        }
        mediaSession.setMetadata(metaData.build());
    }

    public int getPlayingQueueSize() {
        if(getPlayingQueue()!=null){
            return getPlayingQueue().size();
        }
        return 0;
    }

    public Musique getCurrentSong() {
        return model.getCurrentPLayingMusic().getValue();
    }

    public Musique getSongAt(int position) {
        return model.getPlayingQueue().getValue().get(position);
    }

    public boolean isLastTrack() {
        return getPosition() == getOriginalPlaylistSize() - 1;
    }

    public int getDuration(){
        return playback.getDuration();
    }

    public int getRepeatMode() {
        return model.getLoopingMode().getValue();
    }

    public int getNextPosition() {
        int newPosition = (getPosition()+1)%getPlayingQueueSize();
        if(newPosition>=getPlayingQueueSize())
            newPosition = 0;

        return newPosition;
    }

    public int getPreviousPosition(){
        int newPosition = (getPosition()-1)%getPlayingQueueSize();
        if(newPosition<0)
            newPosition = newPosition+getPlayingQueueSize();

        return newPosition;
    }

    private int getOriginalPlaylistSize(){
        if(model.getListOfSongToPlay().getValue()!=null)
            return model.getListOfSongToPlay().getValue().size();
        else
            return 0;
    }

    public void seetTo(long pos){
        playback.seekTo((int)pos);
    }

    public boolean isPlaying(){
       return playback.isPlaying();
    }

    public void quit(){
        pause();
        playingNotification.stop();
        getAudioManager().abandonAudioFocus(this);
        stopSelf();
        releaseResources();
    }

    public boolean getShuffleMode(){
        return model.getShuffleMode().getValue();
    }

    public int getPosition() {
        return model.getPositionOfSongToPLay().getValue();
    }

    private long getSongProgressMillis() {
        return playback.getCurrentPosition();
    }

    public void playPauseMusic() {
        if(isPlaying())
            pause();
        else
            play();
    }

    public void changeShuffleMode() {
        model.setShuffleMode(context, !getShuffleMode());
        boolean isShuffle = getShuffleMode();
        if(isShuffle){
            shuffleOriginalList();
        }else{
            model.getPlayingQueue().setValue(new ArrayList<Musique>(getOriginalPlayList()));
        }
        updatePlayListPreference();
    }

    public void changeRepeatMode() {
        int repeatMode = getNewRepeatMode();
        model.setLoopingMode(context, repeatMode);
    }

    private int getNewRepeatMode() {
        switch (getRepeatMode()){

            case REPEAT_MODE_ALL:
            case REPEAT_MODE_GROUP:
                return REPEAT_MODE_ONE;
            case REPEAT_MODE_ONE:
                return REPEAT_MODE_NONE;
            case REPEAT_MODE_NONE:
            case REPEAT_MODE_INVALID:
                return REPEAT_MODE_ALL;
        }

        return REPEAT_MODE_NONE;
    }

    private void abandonFocusOnAudioOutput(){
        audioManager.abandonAudioFocus(PlayerService.this);
    }

    private void releaseResources(){
        abandonFocusOnAudioOutput();
        seekBarPositionRunnable = null;
        playback.release();
    }

    public void setRepeatMode(final int repeatMode) {
       switch (repeatMode){
           case REPEAT_MODE_ONE:
               playback.setLooping(true);
               break;

           default:
               playback.setLooping(false);
       }
    }

    public void addSong(int position, Musique song) {
        List<Musique> playlist = getPlayingQueue();
        playlist.add(position,song);

        Toast.makeText(context,"Le song sera joué juste après celui en cours",Toast.LENGTH_LONG).show();
    }

    public void addSong(Musique song) {
        getPlayingQueue().add(song);
        getOriginalPlayList().add(song);
        Toast.makeText(context,"Le song sera joué juste aprèsla liste de lecture en cours",Toast.LENGTH_LONG).show();
    }

    public void addSongs(int position, List<Musique> songs) {
        getPlayingQueue().addAll(position, songs);
        getOriginalPlayList().addAll(position, songs);
        Toast.makeText(context,"cette liste sera joué juste après le song en cours",Toast.LENGTH_LONG).show();
    }

    public void addSongs(List<Musique> songs) {
        getPlayingQueue().addAll(songs);
        getOriginalPlayList().addAll(songs);
        Toast.makeText(context,"cette liste sera joué juste après celle en cours",Toast.LENGTH_LONG).show();
    }

    public void removeSong(int position) {
/*        if (getShuffleMode() == SHUFFLE_MODE_NONE) {
            playingQueue.remove(position);
            originalPlayingQueue.remove(position);
        } else {
            originalPlayingQueue.remove(playingQueue.remove(position));
        }*/
    }

    public void moveSong(int from, int to) {
 /*       if (from == to) return;
        final int currentPosition = getPosition();
        Musique songToMove = playingQueue.remove(from);
        playingQueue.add(to, songToMove);
        if (getShuffleMode() == SHUFFLE_MODE_NONE) {
            Musique tmpSong = originalPlayingQueue.remove(from);
            originalPlayingQueue.add(to, tmpSong);
        }
        if (from > currentPosition && to <= currentPosition) {
            position = currentPosition + 1;
        } else if (from < currentPosition && to >= currentPosition) {
            position = currentPosition - 1;
        } else if (from == currentPosition) {
            position = to;
        }*/
    }

    public void playSongAt(final int position) {
        model.getPositionOfSongToPLay().setValue(position);
        model.getCurrentPLayingMusic().setValue(model.getListOfSongToPlay().getValue().get(position));
        loadMusic();
        play();
    }

    private void sendPublicIntent(@NonNull final String what) {
        final Intent intent = new Intent(what.replace(PACKAGE_NAME, MUSIC_PACKAGE_NAME));

        final Musique song = getCurrentSong();

        intent.putExtra("id", song.getIdMusique());

        intent.putExtra("artist", song.getNomArtiste());
        intent.putExtra("album", song.getTitreAlbum());
        intent.putExtra("track", song.getTitreMusique());

        intent.putExtra("duration", song.getDuration());
        intent.putExtra("position", (long) getSongProgressMillis());

        intent.putExtra("playing", isPlaying());

        intent.putExtra("scrobbling_source", PACKAGE_NAME);

        //sendStickyBroadcast(intent);
    }


    public class LocalBinder extends Binder {
        @NonNull
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    private class UpdateSeekBarPosition implements Runnable{
        @Override
        public void run() {
            model.setPlayingSongPositionInMilli((int)getSongProgressMillis(), context);
            seekbarHandler.postDelayed(this, SEEK_BAR_POSITION_REFRESH_DELAY);
        }

    }
}
