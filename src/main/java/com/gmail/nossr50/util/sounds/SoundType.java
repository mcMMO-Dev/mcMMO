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
    ABILITY_ACTIVATED_GENERIC("minecraft:item.trident.riptide_3"),
    BLEED("minecraft:entity.ender_eye.death"),
    GLASS("minecraft:block.glass.break"),
    ITEM_CONSUMED("minecraft:item.bottle.empty"),
    // The mace smash sound only exists on 1.21+, where Cripple can trigger; on older
    // versions the unresolvable ID makes the sound a silent no-op instead of an anvil noise
    CRIPPLE("minecraft:item.mace.smash_ground");
    
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