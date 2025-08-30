package com.gmail.nossr50.util.sounds;

import com.gmail.nossr50.config.SoundConfig;
import com.gmail.nossr50.util.Misc;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SoundManager {

    private static final Map<SoundType, Sound> sounds = new ConcurrentHashMap<>();
    private static final String NULL_FALLBACK_ID = null;

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

    private static Sound getSound(SoundType soundType)
    {
        if (sounds.containsKey(soundType)) {
            return sounds.get(soundType);
        }

        final String soundId = SoundConfig.getInstance().getSound(soundType);
        Sound sound;
        if (soundId != null && !soundId.isEmpty()) {
            sound = SoundRegistryUtils.getSound(soundId, soundType.id());
        } else {
            sound = SoundRegistryUtils.getSound(soundType.id(), NULL_FALLBACK_ID);
        }

        if (sound != null) {
            sounds.putIfAbsent(soundType, sound);
            return sound;
        }

        throw new RuntimeException("Could not find Sound for SoundType: " + soundType);
    }

    public static float getFizzPitch() {
        return 2.6F + (Misc.getRandom().nextFloat() - Misc.getRandom().nextFloat()) * 0.8F;
    }

    public static float getPopPitch() {
        return ((Misc.getRandom().nextFloat() - Misc.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F;
    }
}
