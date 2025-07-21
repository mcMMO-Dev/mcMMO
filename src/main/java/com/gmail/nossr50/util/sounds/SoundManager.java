package com.gmail.nossr50.util.sounds;

import com.gmail.nossr50.config.SoundConfig;
import com.gmail.nossr50.util.Misc;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SoundManager {
    private static Sound CRIPPLE_SOUND;

    private static final String ITEM_MACE_SMASH_GROUND = "ITEM_MACE_SMASH_GROUND";

    private static final String VALUE_OF = "valueOf";

    private static final String ORG_BUKKIT_SOUND = "org.bukkit.Sound";

    /**
     * Sends a sound to the player
     *
     * @param soundType the type of sound
     */
    public static void sendSound(Player player, Location location, SoundType soundType) {
        if (SoundConfig.getInstance().getIsEnabled(soundType)) {
            player.playSound(location, getSound(soundType),
                    SoundCategory.MASTER, getVolume(soundType), getPitch(soundType));
        }
    }

    public static void sendCategorizedSound(Location location, SoundType soundType,
            SoundCategory soundCategory) {
        if (SoundConfig.getInstance().getIsEnabled(soundType)) {
            final World world = location.getWorld();
            if (world != null) {
                world.playSound(location, getSound(soundType), soundCategory,
                        getVolume(soundType), getPitch(soundType));
            }
        }
    }

    public static void sendCategorizedSound(Location location, SoundType soundType,
            SoundCategory soundCategory,
            float pitchModifier) {
        if (SoundConfig.getInstance().getIsEnabled(soundType)) {
            final World world = location.getWorld();
            if (world != null) {
                float totalPitch = Math.min(2.0F, (getPitch(soundType) + pitchModifier));
                world.playSound(location, getSound(soundType), soundCategory, getVolume(soundType),
                        totalPitch);
            }
        }
    }

    public static void sendCategorizedSound(Player player, Location location,
            SoundType soundType, SoundCategory soundCategory) {
        if (SoundConfig.getInstance().getIsEnabled(soundType)) {
            player.playSound(location, getSound(soundType), soundCategory, getVolume(soundType),
                    getPitch(soundType));
        }
    }

    public static void sendCategorizedSound(Player player, Location location,
            SoundType soundType, SoundCategory soundCategory, float pitchModifier) {
        float totalPitch = Math.min(2.0F, (getPitch(soundType) + pitchModifier));

        if (SoundConfig.getInstance().getIsEnabled(soundType)) {
            player.playSound(location, getSound(soundType), soundCategory, getVolume(soundType),
                    totalPitch);
        }
    }

    public static void worldSendSound(World world, Location location, SoundType soundType) {
        if (SoundConfig.getInstance().getIsEnabled(soundType)) {
            world.playSound(location, getSound(soundType), getVolume(soundType),
                    getPitch(soundType));
        }
    }

    public static void worldSendSoundMaxPitch(World world, Location location, SoundType soundType) {
        if (SoundConfig.getInstance().getIsEnabled(soundType)) {
            world.playSound(location, getSound(soundType), getVolume(soundType), 2.0F);
        }
    }

    /**
     * All volume is multiplied by the master volume to get its final value
     *
     * @param soundType target soundtype
     * @return the volume for this soundtype
     */
    private static float getVolume(SoundType soundType) {
        return SoundConfig.getInstance().getVolume(soundType) * SoundConfig.getInstance()
                .getMasterVolume();
    }

    private static float getPitch(SoundType soundType) {
        return switch (soundType)
        {
            case FIZZ -> getFizzPitch();
            case POP -> getPopPitch();
            default -> SoundConfig.getInstance().getPitch(soundType);
        };
    }

    private static String getSound(SoundType soundType)
    {
        String sound = SoundConfig.getInstance().getSound(soundType);

        if (SoundConfig.getInstance().getCustomSoundEnabled())
        {
            return sound;
        }

        return SoundLookup.exists(sound)
                ? sound
                : soundType.id();
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
