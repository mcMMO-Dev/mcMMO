package com.gmail.nossr50.config.sound;

public class SoundSetting {
    private boolean enabled;
    private float volume;
    private float pitch;

    public SoundSetting(boolean enabled) {
        this.enabled = enabled;
        volume = 0f;
        pitch = 0f;
    }

    public SoundSetting(boolean enabled, double pitch) {
        this.enabled = enabled;
        this.volume = 1.0f;
        this.pitch = (float) pitch;
    }

    public SoundSetting(boolean enabled, double volume, double pitch) {
        this.enabled = enabled;
        this.volume = (float) volume;
        this.pitch = (float) pitch;
    }

    public SoundSetting(double volume, double pitch) {
        this.enabled = true;
        this.volume = (float) volume;
        this.pitch = (float) pitch;
    }

    public SoundSetting(double volume) {
        this.enabled = true;
        this.volume = (float) volume;
        this.pitch = 1.0F;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = (float) volume;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = (float) pitch;
    }
}