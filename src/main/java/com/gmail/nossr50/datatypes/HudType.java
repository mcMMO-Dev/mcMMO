package com.gmail.nossr50.datatypes;

public enum HudType {
    DISABLED,
    STANDARD,
    SMALL,
    RETRO;

    public HudType getNext() {
        return values()[(ordinal() + 1) % values().length];
    }
}
