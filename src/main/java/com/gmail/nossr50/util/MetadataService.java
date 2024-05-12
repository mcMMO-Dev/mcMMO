package com.gmail.nossr50.util;

import com.gmail.nossr50.mcMMO;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public final class MetadataService {
    static final @NotNull NamespacedKey NSK_SUPER_ABILITY_BOOSTED_ITEM;
    static final @NotNull NamespacedKey NSK_MOB_SPAWNER_MOB;
    static final @NotNull NamespacedKey NSK_EGG_MOB;
    static final @NotNull NamespacedKey NSK_NETHER_GATE_MOB;
    static final @NotNull NamespacedKey NSK_COTW_SUMMONED_MOB;
    static final @NotNull NamespacedKey NSK_PLAYER_BRED_MOB;
    static final @NotNull NamespacedKey NSK_PLAYER_TAMED_MOB;
    static final @NotNull NamespacedKey NSK_VILLAGER_TRADE_ORIGIN_ITEM;
    static final @NotNull NamespacedKey NSK_EXPLOITED_ENDERMEN;
    static final @NotNull NamespacedKey NSK_CONTAINER_UUID_MOST_SIG;
    static final @NotNull NamespacedKey NSK_CONTAINER_UUID_LEAST_SIG;

    private MetadataService() {
        // private ctor
    }

    static {
        NSK_SUPER_ABILITY_BOOSTED_ITEM = getNamespacedKey(MetadataConstants.METADATA_KEY_SUPER_ABILITY_BOOSTED_ITEM);
        NSK_MOB_SPAWNER_MOB = getNamespacedKey(MetadataConstants.METADATA_KEY_MOB_SPAWNER_MOB);
        NSK_EGG_MOB = getNamespacedKey(MetadataConstants.METADATA_KEY_EGG_MOB);
        NSK_NETHER_GATE_MOB = getNamespacedKey(MetadataConstants.METADATA_KEY_NETHER_PORTAL_MOB);
        NSK_COTW_SUMMONED_MOB = getNamespacedKey(MetadataConstants.METADATA_KEY_COTW_SUMMONED_MOB);
        NSK_PLAYER_BRED_MOB = getNamespacedKey(MetadataConstants.METADATA_KEY_PLAYER_BRED_MOB);
        NSK_PLAYER_TAMED_MOB = getNamespacedKey(MetadataConstants.METADATA_KEY_PLAYER_TAMED_MOB);
        NSK_VILLAGER_TRADE_ORIGIN_ITEM = getNamespacedKey(MetadataConstants.METADATA_KEY_VILLAGER_TRADE_ORIGIN_ITEM);
        NSK_EXPLOITED_ENDERMEN = getNamespacedKey(MetadataConstants.METADATA_KEY_EXPLOITED_ENDERMEN);
        NSK_CONTAINER_UUID_MOST_SIG = getNamespacedKey(MetadataConstants.METADATA_KEY_CONTAINER_UUID_MOST_SIG);
        NSK_CONTAINER_UUID_LEAST_SIG = getNamespacedKey(MetadataConstants.METADATA_KEY_CONTAINER_UUID_LEAST_SIG);
    }

    /**
     * Helper method to simplify generating namespaced keys
     *
     * @param key the {@link String} value of the key
     *
     * @return the generated {@link NamespacedKey}
     */
    public static @NotNull NamespacedKey getNamespacedKey(@NotNull String key) {
        return new NamespacedKey(mcMMO.p, key);
    }
}
