package com.gmail.nossr50.util;

import com.gmail.nossr50.api.exceptions.IncompleteNamespacedKeyRegister;
import com.gmail.nossr50.config.PersistentDataConfig;
import com.gmail.nossr50.metadata.MobMetaFlagType;
import com.google.common.collect.MapMaker;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.gmail.nossr50.util.MetadataService.*;

public final class MobMetadataUtils {
    private static final @NotNull ConcurrentMap<Entity, Set<MobMetaFlagType>> mobRegistry; // transient data
    private static final @NotNull EnumMap<MobMetaFlagType, NamespacedKey> mobFlagKeyMap; // used for persistent data
    private static boolean isUsingPersistentData = false;

    private MobMetadataUtils() {
        // private constructor to prevent instantiation
    }

    static {
        mobFlagKeyMap = new EnumMap<>(MobMetaFlagType.class);
        // Using Guava for a concurrent weak hash map
        // IMPORTANT: This type of map uses == for comparison over .equals(),
        // which is a violation of map contract
        mobRegistry = new MapMaker()
                .weakKeys()
                .concurrencyLevel(4)
                .makeMap();

        initMobFlagKeyMap();

        for (MobMetaFlagType metaFlagType : MobMetaFlagType.values()) {
            if (PersistentDataConfig.getInstance().isMobPersistent(metaFlagType)) {
                isUsingPersistentData = true;
                break;
            }
        }
    }

    /**
     * Registers the namespaced keys required by the API (CB/Spigot)
     * Used primarily for persistent data
     */
    private static void initMobFlagKeyMap() throws IncompleteNamespacedKeyRegister {
        for (MobMetaFlagType mobMetaFlagType : MobMetaFlagType.values()) {
            switch (mobMetaFlagType) {
                case MOB_SPAWNER_MOB -> mobFlagKeyMap.put(mobMetaFlagType, NSK_MOB_SPAWNER_MOB);
                case EGG_MOB -> mobFlagKeyMap.put(mobMetaFlagType, NSK_EGG_MOB);
                case NETHER_PORTAL_MOB -> mobFlagKeyMap.put(mobMetaFlagType, NSK_NETHER_GATE_MOB);
                case COTW_SUMMONED_MOB -> mobFlagKeyMap.put(mobMetaFlagType, NSK_COTW_SUMMONED_MOB);
                case PLAYER_BRED_MOB -> mobFlagKeyMap.put(mobMetaFlagType, NSK_PLAYER_BRED_MOB);
                case EXPLOITED_ENDERMEN -> mobFlagKeyMap.put(mobMetaFlagType, NSK_EXPLOITED_ENDERMEN);
                case PLAYER_TAMED_MOB -> mobFlagKeyMap.put(mobMetaFlagType, NSK_PLAYER_TAMED_MOB);
                default -> throw new IncompleteNamespacedKeyRegister("Missing namespaced key register for type: " + mobMetaFlagType);
            }
        }
    }

    /**
     * Checks if a {@link LivingEntity} has a specific mcMMO mob flag.
     *
     * @param flag         the type of mob flag to check for
     * @param livingEntity the living entity to check
     * @return true if the mob has the specified metadata flag
     */
    public static boolean hasMobFlag(@NotNull MobMetaFlagType flag, @NotNull LivingEntity livingEntity) {
        if (PersistentDataConfig.getInstance().isMobPersistent(flag)) {
            return livingEntity.getPersistentDataContainer().has(mobFlagKeyMap.get(flag), PersistentDataType.BYTE);
        } else {
            final Set<MobMetaFlagType> flags = mobRegistry.get(livingEntity);
            return flags != null && flags.contains(flag);
        }
    }

    /**
     * Checks if a {@link LivingEntity} has any mcMMO mob flags.
     *
     * @param livingEntity the living entity to check
     * @return true if the mob has any mcMMO mob-related metadata flags
     */
    public static boolean hasMobFlags(@NotNull LivingEntity livingEntity) {
        if (isUsingPersistentData) {
            for (MobMetaFlagType metaFlagType : MobMetaFlagType.values()) {
                if (hasMobFlag(metaFlagType, livingEntity)) {
                    return true;
                }
            }
            return false;
        } else {
            final Set<MobMetaFlagType> flags = mobRegistry.get(livingEntity);
            return flags != null && !flags.isEmpty();
        }
    }

    /**
     * Copies all mcMMO mob flags from one {@link LivingEntity} to another.
     * This does not clear existing mcMMO mob flags on the target.
     *
     * @param sourceEntity entity to copy from
     * @param targetEntity entity to copy to
     */
    public static void addMobFlags(@NotNull LivingEntity sourceEntity, @NotNull LivingEntity targetEntity) {
        if (!hasMobFlags(sourceEntity)) {
            return;
        }

        if (isUsingPersistentData) {
            for (MobMetaFlagType flag : MobMetaFlagType.values()) {
                if (hasMobFlag(flag, sourceEntity)) {
                    flagMetadata(flag, targetEntity);
                }
            }
        } else {
            Set<MobMetaFlagType> sourceFlags = mobRegistry.get(sourceEntity);
            if (sourceFlags != null) {
                Set<MobMetaFlagType> targetFlags = mobRegistry.computeIfAbsent(targetEntity, k -> ConcurrentHashMap.newKeySet());
                targetFlags.addAll(sourceFlags);
            }
        }
    }

    /**
     * Adds a mob flag to a {@link LivingEntity}.
     * The existence of the flag acts as a true value; non-existence is false.
     *
     * @param flag         the desired flag to assign
     * @param livingEntity the target living entity
     */
    public static void flagMetadata(@NotNull MobMetaFlagType flag, @NotNull LivingEntity livingEntity) {
        if (PersistentDataConfig.getInstance().isMobPersistent(flag)) {
            if (!hasMobFlag(flag, livingEntity)) {
                PersistentDataContainer persistentDataContainer = livingEntity.getPersistentDataContainer();
                persistentDataContainer.set(mobFlagKeyMap.get(flag), PersistentDataType.BYTE, MetadataConstants.SIMPLE_FLAG_VALUE);
            }
        } else {
            final Set<MobMetaFlagType> flags = mobRegistry.computeIfAbsent(livingEntity, k -> ConcurrentHashMap.newKeySet());
            flags.add(flag);
        }
    }

    /**
     * Removes a specific mob flag from a {@link LivingEntity}.
     *
     * @param flag         the flag to remove
     * @param livingEntity the target living entity
     */
    public static void removeMobFlag(@NotNull MobMetaFlagType flag, @NotNull LivingEntity livingEntity) {
        if (PersistentDataConfig.getInstance().isMobPersistent(flag)) {
            if (hasMobFlag(flag, livingEntity)) {
                PersistentDataContainer persistentDataContainer = livingEntity.getPersistentDataContainer();
                persistentDataContainer.remove(mobFlagKeyMap.get(flag));
            }
        } else {
            final Set<MobMetaFlagType> flags = mobRegistry.get(livingEntity);
            if (flags != null) {
                flags.remove(flag);
                if (flags.isEmpty()) {
                    mobRegistry.remove(livingEntity, flags);
                }
            }
        }
    }

    /**
     * Removes all mcMMO-related mob flags from a {@link LivingEntity}.
     *
     * @param livingEntity the target entity
     */
    public static void removeMobFlags(@NotNull LivingEntity livingEntity) {
        if (isUsingPersistentData) {
            for (MobMetaFlagType flag : MobMetaFlagType.values()) {
                removeMobFlag(flag, livingEntity);
            }
        } else {
            mobRegistry.remove(livingEntity);
        }
    }
}
