package com.gmail.nossr50.metadata;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.MetadataConstants;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class MetadataService {
    private final @NotNull mcMMO pluginRef;

    protected static final @NotNull NamespacedKey NSK_SUPER_ABILITY_BOOSTED_ITEM;
    protected static final @NotNull NamespacedKey NSK_MOB_SPAWNER_MOB;
    protected static final @NotNull NamespacedKey NSK_EGG_MOB;
    protected static final @NotNull NamespacedKey NSK_NETHER_GATE_MOB;
    protected static final @NotNull NamespacedKey NSK_COTW_SUMMONED_MOB;
    protected static final @NotNull NamespacedKey NSK_PLAYER_BRED_MOB;
    protected static final @NotNull NamespacedKey NSK_PLAYER_TAMED_MOB;
    protected static final @NotNull NamespacedKey NSK_VILLAGER_TRADE_ORIGIN_ITEM;
    protected static final @NotNull NamespacedKey NSK_EXPLOITED_ENDERMEN;
    protected static final @NotNull NamespacedKey NSK_FURNACE_UUID_MOST_SIG;
    protected static final @NotNull NamespacedKey NSK_FURNACE_UUID_LEAST_SIG;

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
        NSK_FURNACE_UUID_MOST_SIG = getNamespacedKey(MetadataConstants.METADATA_KEY_FURNACE_UUID_MOST_SIG);
        NSK_FURNACE_UUID_LEAST_SIG = getNamespacedKey(MetadataConstants.METADATA_KEY_FURNACE_UUID_LEAST_SIG);
    }

    private final @NotNull ItemMetadataService itemMetadataService;
    private final @NotNull MobMetadataService mobMetadataService;
    private final @NotNull BlockMetadataService blockMetadataService;

    public MetadataService(@NotNull mcMMO pluginRef) {
        this.pluginRef = pluginRef;

        blockMetadataService = new BlockMetadataService(pluginRef);
        mobMetadataService = new MobMetadataService(pluginRef);
        itemMetadataService = new ItemMetadataService(pluginRef);
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

    public @NotNull ItemMetadataService getItemMetadataService() {
        return itemMetadataService;
    }

    public @NotNull MobMetadataService getMobMetadataService() {
        return mobMetadataService;
    }

    public @NotNull BlockMetadataService getBlockMetadataService() {
        return blockMetadataService;
    }
}
