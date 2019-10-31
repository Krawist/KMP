package com.example.kmp.Modeles;

public class MusicEffect {

    private boolean isActif = false;
    private short equalizerEffectIndex = 0;
    private float soundPitch = 1.0f;
    private float soundSpeed = 1.0f;

    public boolean isActif() {
        return isActif;
    }

    public float getSoundPitch() {
        return soundPitch;
    }

    public void setSoundPitch(float soundPitch) {
        this.soundPitch = soundPitch;
    }

    public float getSoundSpeed() {
        return soundSpeed;
    }

    public void setSoundSpeed(float soundSpeed) {
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
