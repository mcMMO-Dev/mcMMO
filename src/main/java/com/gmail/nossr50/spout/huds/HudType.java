package com.gmail.nossr50.spout.huds;

public enum HudType {
    DISABLED,
    STANDARD,
    SMALL,
    RETRO;

    public HudType getNext() {
        return values()[(ordinal() + 1) % values().length];
    }
}
