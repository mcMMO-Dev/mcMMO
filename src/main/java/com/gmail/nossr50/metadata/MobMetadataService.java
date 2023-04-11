package com.gmail.nossr50.metadata;

import com.gmail.nossr50.api.exceptions.IncompleteNamespacedKeyRegister;
import com.gmail.nossr50.config.PersistentDataConfig;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.MetadataConstants;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.WeakHashMap;

import static com.gmail.nossr50.metadata.MetadataService.*;

//TODO: Use SpawnReason where appropriate instead of MobMetaFlagType
public class MobMetadataService {
    private final @NotNull WeakHashMap<Entity, HashSet<MobMetaFlagType>> mobRegistry; //transient data
    private final @NotNull EnumMap<MobMetaFlagType, NamespacedKey> mobFlagKeyMap; //used for persistent data
    private final @NotNull mcMMO pluginRef;
    private boolean isUsingPersistentData = false;

    public MobMetadataService(@NotNull mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        mobFlagKeyMap = new EnumMap<>(MobMetaFlagType.class);
        mobRegistry = new WeakHashMap<>();
        initMobFlagKeyMap();

        for (MobMetaFlagType metaFlagType : MobMetaFlagType.values()) {
            if (PersistentDataConfig.getInstance().isMobPersistent(metaFlagType))
                isUsingPersistentData = true;
        }
    }

    /**
     * Registers the namespaced keys required by the API (CB/Spigot)
     * Used primarily for persistent data
     */
    private void initMobFlagKeyMap() throws IncompleteNamespacedKeyRegister {
        for (MobMetaFlagType mobMetaFlagType : MobMetaFlagType.values()) {
            switch (mobMetaFlagType) {
                case MOB_SPAWNER_MOB -> mobFlagKeyMap.put(mobMetaFlagType, NSK_MOB_SPAWNER_MOB);
                case EGG_MOB -> mobFlagKeyMap.put(mobMetaFlagType, NSK_EGG_MOB);
                case NETHER_PORTAL_MOB -> mobFlagKeyMap.put(mobMetaFlagType, NSK_NETHER_GATE_MOB);
                case COTW_SUMMONED_MOB -> mobFlagKeyMap.put(mobMetaFlagType, NSK_COTW_SUMMONED_MOB);
                case PLAYER_BRED_MOB -> mobFlagKeyMap.put(mobMetaFlagType, NSK_PLAYER_BRED_MOB);
                case EXPLOITED_ENDERMEN -> mobFlagKeyMap.put(mobMetaFlagType, NSK_EXPLOITED_ENDERMEN);
                case PLAYER_TAMED_MOB -> mobFlagKeyMap.put(mobMetaFlagType, NSK_PLAYER_TAMED_MOB);
                default -> throw new IncompleteNamespacedKeyRegister("missing namespaced key register for type: " + mobMetaFlagType);
            }
        }
    }

    /**
     * Whether a target {@link LivingEntity} has a specific mcMMO mob flags
     *
     * @param flag         the type of mob flag to check for
     * @param livingEntity the living entity to check for metadata
     *
     * @return true if the mob has metadata values for target {@link MobMetaFlagType}
     */
    public boolean hasMobFlag(@NotNull MobMetaFlagType flag, @NotNull LivingEntity livingEntity) {
        if (PersistentDataConfig.getInstance().isMobPersistent(flag)) {
            return livingEntity.getPersistentDataContainer().has(mobFlagKeyMap.get(flag), PersistentDataType.BYTE);
        } else {
            if (mobRegistry.containsKey(livingEntity)) {
                return mobRegistry.get(livingEntity).contains(flag);
            }

            return false;
        }
    }

    /**
     * Whether a target {@link LivingEntity} has any mcMMO mob flags
     *
     * @param livingEntity the living entity to check for metadata
     *
     * @return true if the mob has any mcMMO mob related metadata values
     */
    public boolean hasMobFlags(@NotNull LivingEntity livingEntity) {
        if (isUsingPersistentData) {
            for (MobMetaFlagType metaFlagType : MobMetaFlagType.values()) {
                if (hasMobFlag(metaFlagType, livingEntity))
                    return true;
            }

            return false;
        } else {
            return mobRegistry.containsKey(livingEntity) && mobRegistry.get(livingEntity).size() > 0;
        }
    }

    /**
     * Copies all mcMMO mob flags from one {@link LivingEntity} to another {@link LivingEntity}
     * This does not clear existing mcMMO mob flags on the target
     *
     * @param sourceEntity entity to copy from
     * @param targetEntity entity to copy to
     */
    public void addMobFlags(@NotNull LivingEntity sourceEntity, @NotNull LivingEntity targetEntity) {
        if (!hasMobFlags(sourceEntity))
            return;

        if (isUsingPersistentData) {
            for (MobMetaFlagType flag : MobMetaFlagType.values()) {
                if (hasMobFlag(flag, sourceEntity)) {
                    flagMetadata(flag, targetEntity);
                }
            }
        } else {
            HashSet<MobMetaFlagType> flags = new HashSet<>(mobRegistry.get(sourceEntity));
            mobRegistry.put(targetEntity, flags);
        }
    }

    /**
     * Adds a mob flag to a {@link LivingEntity} which effectively acts a true/false boolean
     * Existence of the flag can be considered a true value, non-existence can be considered false for all intents and purposes
     *
     * @param flag         the desired flag to assign
     * @param livingEntity the target living entity
     */
    public void flagMetadata(@NotNull MobMetaFlagType flag, @NotNull LivingEntity livingEntity) {
        if (PersistentDataConfig.getInstance().isMobPersistent(flag)) {
            if (!hasMobFlag(flag, livingEntity)) {
                PersistentDataContainer persistentDataContainer = livingEntity.getPersistentDataContainer();
                persistentDataContainer.set(mobFlagKeyMap.get(flag), PersistentDataType.BYTE, MetadataConstants.SIMPLE_FLAG_VALUE);
            }
        } else {
            HashSet<MobMetaFlagType> flags = mobRegistry.getOrDefault(livingEntity, new HashSet<>());
            flags.add(flag); // add the new flag
            mobRegistry.put(livingEntity, flags); //update registry
        }
    }

    /**
     * Removes a specific mob flag from target {@link LivingEntity}
     *
     * @param flag         desired flag to remove
     * @param livingEntity the target living entity
     */
    public void removeMobFlag(@NotNull MobMetaFlagType flag, @NotNull LivingEntity livingEntity) {
        if (PersistentDataConfig.getInstance().isMobPersistent(flag)) {
            if (hasMobFlag(flag, livingEntity)) {
                PersistentDataContainer persistentDataContainer = livingEntity.getPersistentDataContainer();
                persistentDataContainer.remove(mobFlagKeyMap.get(flag));
            }
        } else {
            if (mobRegistry.containsKey(livingEntity)) {
                mobRegistry.get(livingEntity).remove(flag);

                if (mobRegistry.get(livingEntity).size() == 0)
                    mobRegistry.remove(livingEntity);
            }
        }
    }

    /**
     * Remove all mcMMO related mob flags from the target {@link LivingEntity}
     *
     * @param livingEntity target entity
     */
    public void removeMobFlags(@NotNull LivingEntity livingEntity) {
        if (isUsingPersistentData) {
            for (MobMetaFlagType flag : MobMetaFlagType.values()) {
                removeMobFlag(flag, livingEntity);
            }
        } else {
            mobRegistry.remove(livingEntity);
        }
    }
}
