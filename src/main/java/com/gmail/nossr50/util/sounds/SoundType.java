package com.gmail.nossr50.util.sounds;

public enum SoundType {
    ANVIL,
    LEVEL_UP,
    FIZZ,
    ITEM_BREAK,
    POP,
    CHIMAERA_WING,
    ROLL_ACTIVATED,
    SKILL_UNLOCKED,
    DEFLECT_ARROWS,
    TOOL_READY,
    ABILITY_ACTIVATED_GENERIC,
    ABILITY_ACTIVATED_BERSERK,
    BLEED,
    GLASS,
    ITEM_CONSUMED,
    CRIPPLE,
    TIRED;

    public boolean usesCustomPitch() {
        switch (this) {
            case POP:
            case FIZZ:
                return true;
            default:
                return false;
        }
    }
}
