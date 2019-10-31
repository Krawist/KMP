package com.example.kmp.Modeles;

public class MusicEffect {

    private boolean isActif = false;
    private short equalizerEffectIndex = 0;
    private int soundPitch = 0;
    private int soundSpeed;

    public boolean isActif() {
        return isActif;
    }

    public int getSoundPitch() {
        return soundPitch;
    }

    public void setSoundPitch(int soundPitch) {
        this.soundPitch = soundPitch;
    }

    public int getSoundSpeed() {
        return soundSpeed;
    }

    public void setSoundSpeed(int soundSpeed) {
        this.soundSpeed = soundSpeed;
    }

    public void setActif(boolean actif) {
        isActif = actif;
    }

    public short getEqualizerEffectIndex() {
        return equalizerEffectIndex;
    }

    public void setEqualizerEffectIndex(short equalizerEffectIndex) {
        this.equalizerEffectIndex = equalizerEffectIndex;
    }
}
