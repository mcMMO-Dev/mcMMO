package com.gmail.nossr50.datatypes.spout.huds;

public enum HudType {
    DISABLED,
    STANDARD,
    SMALL,
    RETRO;

    public HudType getNext() {
        return values()[(ordinal() + 1) % values().length];
    }
}
