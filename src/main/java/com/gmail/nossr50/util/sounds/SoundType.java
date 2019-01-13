package com.gmail.nossr50.util.sounds;

public enum SoundType {
    ANVIL,
    LEVEL_UP,
    FIZZ,
    ITEM_BREAK,
    POP,
    KRAKEN,
    CHIMAERA_WING;

    public boolean usesCustomPitch()
    {
        switch(this){
            case POP:
            case FIZZ:
            case KRAKEN:
                return true;
            default:
                return false;
        }
    }
}
