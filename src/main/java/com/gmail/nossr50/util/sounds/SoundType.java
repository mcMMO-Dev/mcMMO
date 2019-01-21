package com.gmail.nossr50.util.sounds;

public enum SoundType {
    ANVIL,
    LEVEL_UP,
    FIZZ,
    ITEM_BREAK,
    POP,
    KRAKEN,
    CHIMAERA_WING,
    ROLL_ACTIVATED,
    SKILL_UNLOCKED,
    DEFLECT_ARROWS,
    TOOL_READY,
    ABILITY_ACTIVATED_GENERIC,
    ABILITY_ACTIVATED_BERSERK,
    BLEED,
    TIRED;

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
