package com.gmail.nossr50.util.sounds;

import com.gmail.nossr50.config.SoundConfig;
import com.gmail.nossr50.util.Misc;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SoundManager {
    private static Sound CRIPPLE_SOUND;

    private static final String ITEM_MACE_SMASH_GROUND = "ITEM_MACE_SMASH_GROUND";

    private static final String VALUE_OF = "valueOf";

    private static final String ORG_BUKKIT_SOUND = "org.bukkit.Sound";

    /**
     * Sends a sound to the player
     * @param soundType the type of sound
     */
    public static void sendSound(Player player, Location location, SoundType soundType) {
        if (SoundConfig.getInstance().getIsEnabled(soundType))
            player.playSound(location, getSound(soundType),
                    SoundCategory.MASTER, getVolume(soundType), getPitch(soundType));
    }

    public static void sendCategorizedSound(Location location, SoundType soundType, SoundCategory soundCategory) {
        if (SoundConfig.getInstance().getIsEnabled(soundType)) {
            final World world = location.getWorld();
            if (world != null) {
                world.playSound(location, getSound(soundType), soundCategory,
                        getVolume(soundType), getPitch(soundType));
            }
        }
    }

    public static void sendCategorizedSound(Location location, SoundType soundType, SoundCategory soundCategory,
                                            float pitchModifier) {
        if (SoundConfig.getInstance().getIsEnabled(soundType)) {
            final World world = location.getWorld();
            if (world != null) {
                float totalPitch = Math.min(2.0F, (getPitch(soundType) + pitchModifier));
                world.playSound(location, getSound(soundType), soundCategory, getVolume(soundType), totalPitch);
            }
        }
    }

    public static void sendCategorizedSound(Player player, Location location,
                                            SoundType soundType, SoundCategory soundCategory) {
        if (SoundConfig.getInstance().getIsEnabled(soundType))
            player.playSound(location, getSound(soundType), soundCategory, getVolume(soundType), getPitch(soundType));
    }

    public static void sendCategorizedSound(Player player, Location location,
                                            SoundType soundType, SoundCategory soundCategory, float pitchModifier) {
        float totalPitch = Math.min(2.0F, (getPitch(soundType) + pitchModifier));

        if (SoundConfig.getInstance().getIsEnabled(soundType))
            player.playSound(location, getSound(soundType), soundCategory, getVolume(soundType), totalPitch);
    }

    public static void worldSendSound(World world, Location location, SoundType soundType) {
        if (SoundConfig.getInstance().getIsEnabled(soundType))
            world.playSound(location, getSound(soundType), getVolume(soundType), getPitch(soundType));
    }

    public static void worldSendSoundMaxPitch(World world, Location location, SoundType soundType) {
        if (SoundConfig.getInstance().getIsEnabled(soundType))
            world.playSound(location, getSound(soundType), getVolume(soundType), 2.0F);
    }

    /**
     * All volume is multiplied by the master volume to get its final value
     * @param soundType target soundtype
     * @return the volume for this soundtype
     */
    private static float getVolume(SoundType soundType) {
        return SoundConfig.getInstance().getVolume(soundType) * SoundConfig.getInstance().getMasterVolume();
    }

    private static float getPitch(SoundType soundType) {
        if (soundType == SoundType.FIZZ)
            return getFizzPitch();
        else if (soundType == SoundType.POP)
            return getPopPitch();
        else
            return SoundConfig.getInstance().getPitch(soundType);
    }

    private static Sound getSound(SoundType soundType) {
        return switch (soundType) {
            case ANVIL -> Sound.BLOCK_ANVIL_PLACE;
            case ITEM_BREAK -> Sound.ENTITY_ITEM_BREAK;
            case POP -> Sound.ENTITY_ITEM_PICKUP;
            case CHIMAERA_WING -> Sound.ENTITY_BAT_TAKEOFF;
            case LEVEL_UP -> Sound.ENTITY_PLAYER_LEVELUP;
            case FIZZ -> Sound.BLOCK_FIRE_EXTINGUISH;
            case TOOL_READY -> Sound.ITEM_ARMOR_EQUIP_GOLD;
            case ROLL_ACTIVATED -> Sound.ENTITY_LLAMA_SWAG;
            case SKILL_UNLOCKED -> Sound.UI_TOAST_CHALLENGE_COMPLETE;
            case ABILITY_ACTIVATED_BERSERK, TIRED -> Sound.BLOCK_CONDUIT_AMBIENT;
            case ABILITY_ACTIVATED_GENERIC -> Sound.ITEM_TRIDENT_RIPTIDE_3;
            case DEFLECT_ARROWS, BLEED -> Sound.ENTITY_ENDER_EYE_DEATH;
            case GLASS -> Sound.BLOCK_GLASS_BREAK;
            case ITEM_CONSUMED -> Sound.ITEM_BOTTLE_EMPTY;
            case CRIPPLE -> getCrippleSound();
        };
    }

    private static Sound getCrippleSound() {
        if (CRIPPLE_SOUND != null) {
            return CRIPPLE_SOUND;
        }

        try {
            // Spigot changed the class from enum to interface around 1.21.3
            final Class<?> clazz = Class.forName(ORG_BUKKIT_SOUND);
            final Method valueOf = clazz.getMethod(VALUE_OF);
            CRIPPLE_SOUND = (Sound) valueOf.invoke(null, ITEM_MACE_SMASH_GROUND);
        } catch (IllegalArgumentException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException
                 | IllegalAccessException e) {
            CRIPPLE_SOUND = Sound.BLOCK_ANVIL_PLACE;
        }

        return CRIPPLE_SOUND;
    }

    public static float getFizzPitch() {
        return 2.6F + (Misc.getRandom().nextFloat() - Misc.getRandom().nextFloat()) * 0.8F;
    }

    public static float getPopPitch() {
        return ((Misc.getRandom().nextFloat() - Misc.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F;
    }

    public static float getKrakenPitch() {
        return (Misc.getRandom().nextFloat() - Misc.getRandom().nextFloat()) * 0.2F + 1.0F;
    }
}
