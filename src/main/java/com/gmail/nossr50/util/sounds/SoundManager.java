package com.gmail.nossr50.util.sounds;

import com.gmail.nossr50.config.SoundConfig;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.ReflectionUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoundManager {

    private static final Map<SoundType, Sound> soundCache = new ConcurrentHashMap<>();
    private static final Set<SoundType> unresolvedSoundTypes = ConcurrentHashMap.newKeySet();
    private static final String NULL_FALLBACK_ID = null;
    private static Sound crippleSound;
    private static boolean crippleSoundResolved;
    private static final String ITEM_MACE_SMASH_GROUND = "ITEM_MACE_SMASH_GROUND";
    private static final String ORG_BUKKIT_SOUND = "org.bukkit.Sound";

    /**
     * Receives the resolved sound and final volume/pitch when a sound is enabled and resolvable.
     */
    @FunctionalInterface
    private interface SoundDispatch {
        void play(Sound sound, float volume, float pitch);
    }

    /**
     * Shared funnel for every send method: checks the enabled toggle, resolves the sound, and
     * hands volume/pitch to the dispatch. Pitch is supplied lazily so random pitches (FIZZ/POP)
     * are only rolled when the sound actually plays.
     */
    private static void playIfEnabled(SoundType soundType, Supplier<Float> pitch,
            SoundDispatch dispatch) {
        if (!SoundConfig.getInstance().getIsEnabled(soundType)) {
            return;
        }

        final Sound sound = getSound(soundType);
        if (sound == null) {
            return;
        }

        dispatch.play(sound, getVolume(soundType), pitch.get());
    }

    private static void playIfEnabled(SoundType soundType, SoundDispatch dispatch) {
        playIfEnabled(soundType, () -> getPitch(soundType), dispatch);
    }

    private static @NotNull Supplier<Float> modifiedPitch(SoundType soundType,
            float pitchModifier) {
        return () -> Math.min(2.0F, getPitch(soundType) + pitchModifier);
    }

    /**
     * Sends a sound to the player
     *
     * @param soundType the type of sound
     */
    public static void sendSound(Player player, Location location, SoundType soundType) {
        playIfEnabled(soundType, (sound, volume, pitch) ->
                player.playSound(location, sound, SoundCategory.MASTER, volume, pitch));
    }

    public static void sendCategorizedSound(Location location, SoundType soundType,
            SoundCategory soundCategory) {
        playIfEnabled(soundType, (sound, volume, pitch) -> {
            final World world = location.getWorld();
            if (world != null) {
                world.playSound(location, sound, soundCategory, volume, pitch);
            }
        });
    }

    public static void sendCategorizedSound(Location location, SoundType soundType,
            SoundCategory soundCategory, float pitchModifier) {
        playIfEnabled(soundType, modifiedPitch(soundType, pitchModifier),
                (sound, volume, pitch) -> {
                    final World world = location.getWorld();
                    if (world != null) {
                        world.playSound(location, sound, soundCategory, volume, pitch);
                    }
                });
    }

    public static void sendCategorizedSound(Player player, Location location,
            SoundType soundType, SoundCategory soundCategory) {
        playIfEnabled(soundType, (sound, volume, pitch) ->
                player.playSound(location, sound, soundCategory, volume, pitch));
    }

    public static void sendCategorizedSound(Player player, Location location,
            SoundType soundType, SoundCategory soundCategory, float pitchModifier) {
        playIfEnabled(soundType, modifiedPitch(soundType, pitchModifier),
                (sound, volume, pitch) ->
                        player.playSound(location, sound, soundCategory, volume, pitch));
    }

    public static void worldSendSound(World world, Location location, SoundType soundType) {
        playIfEnabled(soundType, (sound, volume, pitch) ->
                world.playSound(location, sound, volume, pitch));
    }

    public static void worldSendSoundMaxPitch(World world, Location location, SoundType soundType) {
        playIfEnabled(soundType, () -> 2.0F, (sound, volume, pitch) ->
                world.playSound(location, sound, volume, pitch));
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

    private static @Nullable Sound getSound(SoundType soundType) {
        final String soundId = SoundConfig.getInstance().getSound(soundType);

        // Legacy versions use a different lookup method
        if (SoundRegistryUtils.useLegacyLookup()) {
            return getSoundLegacyCustom(soundId, soundType);
        }

        if (soundCache.containsKey(soundType)) {
            return soundCache.get(soundType);
        }

        Sound sound;
        if (soundId != null && !soundId.isEmpty()) {
            sound = SoundRegistryUtils.getSound(soundId, soundType.id());
        } else {
            sound = SoundRegistryUtils.getSound(soundType.id(), NULL_FALLBACK_ID);
        }

        if (sound != null) {
            soundCache.putIfAbsent(soundType, sound);
            return sound;
        }

        warnOnceAboutUnresolvedSound(soundType);
        return null;
    }

    private static @Nullable Sound getSoundLegacyCustom(String id, SoundType soundType) {
        if (soundCache.containsKey(soundType)) {
            return soundCache.get(soundType);
        }

        // Try to look up a custom legacy sound
        if (id != null && !id.isEmpty()) {
            Sound sound;
            if (Sound.class.isEnum()) {
                // Sound is only an ENUM in legacy versions
                // Use reflection to loop through the values, finding the first enum matching our ID
                try {
                    Method method = Sound.class.getMethod("getKey");
                    for (Object legacyEnumEntry : Sound.class.getEnumConstants()) {
                        // This enum extends Keyed which adds the getKey() method
                        // we need to invoke this method to get the NamespacedKey and compare to our ID
                        if (method.invoke(legacyEnumEntry).toString().equals(id)) {
                            sound = (Sound) legacyEnumEntry;
                            soundCache.putIfAbsent(soundType, sound);
                            return sound;
                        }
                    }
                } catch (NoSuchMethodException | InvocationTargetException |
                         IllegalAccessException e) {
                    // Ignore
                }
            }
            warnOnceAboutUnresolvedSound(soundType);
        }
        // Failsafe -- we haven't found a matching sound
        final Sound sound = getSoundLegacyFallBack(soundType);
        if (sound == null) {
            return null;
        }
        soundCache.putIfAbsent(soundType, sound);
        return sound;
    }

    private static void warnOnceAboutUnresolvedSound(SoundType soundType) {
        if (unresolvedSoundTypes.add(soundType)) {
            mcMMO.p.getLogger().warning("Could not resolve a sound for " + soundType
                    + ", it will not be played. Check the " + soundType + " entry in sounds.yml.");
        }
    }

    private static @Nullable Sound getSoundLegacyFallBack(SoundType soundType) {
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

    private static @Nullable Sound getCrippleSound() {
        if (crippleSoundResolved) {
            return crippleSound;
        }

        // Reflective because Spigot changed Sound from enum to interface around 1.21.3; null
        // when the sound doesn't exist (before Minecraft 1.21, where Cripple can't trigger)
        crippleSound = ReflectionUtils.staticValueOf(ORG_BUKKIT_SOUND, ITEM_MACE_SMASH_GROUND);
        crippleSoundResolved = true;

        return crippleSound;
    }

    public static float getFizzPitch() {
        return 2.6F + (Misc.getRandom().nextFloat() - Misc.getRandom().nextFloat()) * 0.8F;
    }

    public static float getPopPitch() {
        return ((Misc.getRandom().nextFloat() - Misc.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F;
    }
}
