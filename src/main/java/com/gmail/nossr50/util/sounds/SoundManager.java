package com.gmail.nossr50.util.sounds;

import com.gmail.nossr50.config.hocon.sound.SoundSetting;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Misc;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SoundManager {

    private final mcMMO pluginRef;

    public SoundManager(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    /**
     * Sends a sound to the player
     *
     * @param soundType the type of sound
     */
    public void sendSound(Player player, Location location, SoundType soundType) {
        if (pluginRef.getConfigManager().getConfigSound().isSoundEnabled(soundType))
            player.playSound(location, getSound(soundType), SoundCategory.MASTER, getVolume(soundType), getPitch(soundType));
    }

    public void sendCategorizedSound(Player player, Location location, SoundType soundType, SoundCategory soundCategory) {
        if (pluginRef.getConfigManager().getConfigSound().isSoundEnabled(soundType))
            player.playSound(location, getSound(soundType), soundCategory, getVolume(soundType), getPitch(soundType));
    }

    public void sendCategorizedSound(Player player, Location location, SoundType soundType, SoundCategory soundCategory, float pitchModifier) {
        float totalPitch = Math.min(2.0F, (getPitch(soundType) + pitchModifier));

        if (pluginRef.getConfigManager().getConfigSound().isSoundEnabled(soundType))
            player.playSound(location, getSound(soundType), soundCategory, getVolume(soundType), totalPitch);
    }

    public void worldSendSound(World world, Location location, SoundType soundType) {
        if (pluginRef.getConfigManager().getConfigSound().isSoundEnabled(soundType))
            world.playSound(location, getSound(soundType), getVolume(soundType), getPitch(soundType));
    }

    /**
     * All volume is multiplied by the master volume to get its final value
     *
     * @param soundType target soundtype
     * @return the volume for this soundtype
     */
    private float getVolume(SoundType soundType) {
        SoundSetting soundSetting = pluginRef.getConfigManager().getConfigSound().getSoundSetting(soundType);
        return soundSetting.getVolume() * (float) pluginRef.getConfigManager().getConfigSound().getMasterVolume();
    }

    private float getPitch(SoundType soundType) {
        if (soundType == SoundType.FIZZ)
            return getFizzPitch();
        else if (soundType == SoundType.POP)
            return getPopPitch();
        else {
            SoundSetting soundSetting = pluginRef.getConfigManager().getConfigSound().getSoundSetting(soundType);
            return soundSetting.getPitch();
        }
    }

    private Sound getSound(SoundType soundType) {
        switch (soundType) {
            case ANVIL:
                return Sound.BLOCK_ANVIL_PLACE;
            case ITEM_BREAK:
                return Sound.ENTITY_ITEM_BREAK;
            case POP:
                return Sound.ENTITY_ITEM_PICKUP;
            case CHIMAERA_WING:
                return Sound.ENTITY_BAT_TAKEOFF;
            case LEVEL_UP:
                return Sound.ENTITY_PLAYER_LEVELUP;
            case FIZZ:
                return Sound.BLOCK_FIRE_EXTINGUISH;
            case TOOL_READY:
                return Sound.ITEM_ARMOR_EQUIP_GOLD;
            case ROLL_ACTIVATED:
                return Sound.ENTITY_LLAMA_SWAG;
            case SKILL_UNLOCKED:
                return Sound.UI_TOAST_CHALLENGE_COMPLETE;
            case ABILITY_ACTIVATED_BERSERK:
            case TIRED:
                return Sound.BLOCK_CONDUIT_AMBIENT;
            case ABILITY_ACTIVATED_GENERIC:
                return Sound.ITEM_TRIDENT_RIPTIDE_3;
            case DEFLECT_ARROWS:
            case BLEED:
                return Sound.ENTITY_ENDER_EYE_DEATH;
            default:
                return null;
        }
    }

    public float getFizzPitch() {
        return 2.6F + (Misc.getRandom().nextFloat() - Misc.getRandom().nextFloat()) * 0.8F;
    }

    public float getPopPitch() {
        return ((Misc.getRandom().nextFloat() - Misc.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F;
    }
}
