package com.gmail.nossr50.config.hocon.sound;

public class SoundSetting {
    private boolean enabled;
    private double volume;
    private double pitch;

    public SoundSetting(boolean enabled) {
        this.enabled = enabled;
        volume = 0f;
        pitch = 0f;
    }

    public SoundSetting(boolean enabled, double pitch) {
        this.enabled = enabled;
        this.volume = 1.0f;
        this.pitch = pitch;
    }

    public SoundSetting(boolean enabled, double volume, double pitch) {
        this.enabled = enabled;
        this.volume = volume;
        this.pitch = pitch;
    }

    public SoundSetting(double volume, double pitch) {
        this.volume = volume;
        this.pitch = pitch;
    }

    public SoundSetting(double volume) {
        this.volume = volume;
        this.pitch = 1.0F;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }
}