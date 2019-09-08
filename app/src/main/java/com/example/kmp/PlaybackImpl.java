package com.example.kmp;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import com.example.kmp.Service.PlayerService;

import java.io.IOException;

import static android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_ALL;
import static android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_GROUP;
import static android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_ONE;

public class PlaybackImpl implements Playback, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer mediaPlayer = null;
    private PlayerService service;
    private Context context;
    private boolean isPrepared = false;

    public PlaybackImpl(PlayerService service, Context context){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        this.service = service;
        this.context = context;
    }

    public void setLooping(boolean looping){
        mediaPlayer.setLooping(looping);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(service.getRepeatMode()==REPEAT_MODE_ONE){
            service.loadMusic();
            play();
        }else if(service.isLastTrack()){
            if(service.getRepeatMode()==REPEAT_MODE_ALL|| service.getRepeatMode()==REPEAT_MODE_GROUP){
                service.playNextSong();
            }else{
                    stop();
                    service.updatePosition(service.getNextPosition());
                }
        }else {
            service.playNextSong();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(context,service.getString(R.string.une_erreur_est_survenue),Toast.LENGTH_SHORT).show();
        if(service.getPosition()==service.getPlayingQueueSize()-1){
            return true;
        }else{
            service.playNextSong();
            return true;
        }
    }

    @Override
    public void setDataSource(String songPath) {

        try {
            mediaPlayer.reset();
            mediaPlayer.setOnPreparedListener(null);
            if(songPath.startsWith("content://")){
                mediaPlayer.setDataSource(context, Uri.parse(songPath));
            }else
                mediaPlayer.setDataSource(songPath);

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
        }catch (IOException e){

        }

        isPrepared = true;
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public boolean play() {
        if(isPrepared){
            mediaPlayer.start();
            return true;
        }

        return false;
    }

    @Override
    public boolean pause() {
        if(isPrepared){
            mediaPlayer.pause();
            return true;
        }

        return false;
    }

    @Override
    public boolean stop() {
        if(isPrepared){
            mediaPlayer.stop();
        }

        return true;
    }

    @Override
    public boolean release() {
        mediaPlayer.release();

        return true;
    }

    @Override
    public void modifyVolume(float volume) {
        if(isPrepared){
            mediaPlayer.setVolume(volume, volume);
        }
    }

    @Override
    public void seekTo(int milli) {
        if(isPrepared){
            mediaPlayer.seekTo(milli);
        }
    }

    @Override
    public boolean isPlaying() {
        if(isPrepared)
            return mediaPlayer.isPlaying();
        else
            return false;
    }

    @Override
    public int getCurrentPosition() {
        if(isPrepared)
            return mediaPlayer.getCurrentPosition();
        else
            return 0;
    }

    @Override
    public int getDuration() {
        if(isPrepared){
            return mediaPlayer.getDuration();
        }

        return 0;
    }
}


