package com.gmail.nossr50.util.sounds;

public enum SoundType {
    ANVIL("minecraft:block.anvil.place"),
    ITEM_BREAK("minecraft:entity.item.break"),
    POP("minecraft:entity.item.pickup"),
    CHIMAERA_WING("minecraft:entity.bat.takeoff"),
    LEVEL_UP("minecraft:entity.player.levelup"),
    FIZZ("minecraft:block.fire.extinguish"),
    TOOL_READY("minecraft:item.armor.equip_gold"),
    ROLL_ACTIVATED("minecraft:entity.llama.swag"),
    SKILL_UNLOCKED("minecraft:ui.toast.challenge_complete"),
    ABILITY_ACTIVATED_BERSERK("minecraft:block.conduit.ambient"),
    TIRED("minecraft:block.conduit.ambient"),
    ABILITY_ACTIVATED_GENERIC("minecraft:item.trident.riptide_3"),
    DEFLECT_ARROWS("minecraft:entity.ender_eye.death"),
    BLEED("minecraft:entity.ender_eye.death"),
    GLASS("minecraft:block.glass.break"),
    ITEM_CONSUMED("minecraft:item.bottle.empty"),
    CRIPPLE("minecraft:block.anvil.place");
    
    private final String soundRegistryId;

    SoundType(String soundRegistryId) {
        this.soundRegistryId = soundRegistryId;
    }

    public String id() {
        return soundRegistryId;
    }
    
    public boolean usesCustomPitch()
    {
        return switch (this) {
            case POP, FIZZ -> true;
            default -> false;
        };
    }
}