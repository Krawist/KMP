package com.example.kmp;

public interface Playback {

    public void setDataSource(String songPath);

    public boolean play();

    public boolean pause();

    public boolean stop();

    public boolean release();

    public void modifyVolume(float volume);

    public void seekTo(int milli);

    public boolean isPlaying();

    public int getCurrentPosition();

    public void setLooping(boolean bool);

    public int getDuration();

    public void loadMediaEffects();
}
