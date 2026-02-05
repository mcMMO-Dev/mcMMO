package com.gmail.nossr50.util;

import java.util.List;
import java.util.Set;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

/**
 * Stores our constants related to metadata
 */
public class MetadataConstants {
    public static final @NotNull Set<String> MOB_METADATA_KEYS = Set.of(
            MetadataConstants.METADATA_KEY_MOB_SPAWNER_MOB,
            MetadataConstants.METADATA_KEY_EGG_MOB,
            MetadataConstants.METADATA_KEY_NETHER_PORTAL_MOB,
            MetadataConstants.METADATA_KEY_COTW_SUMMONED_MOB,
            MetadataConstants.METADATA_KEY_PLAYER_BRED_MOB,
            MetadataConstants.METADATA_KEY_PLAYER_TAMED_MOB,
            MetadataConstants.METADATA_KEY_EXPLOITED_ENDERMEN,
            MetadataConstants.METADATA_KEY_CUSTOM_NAME,
            MetadataConstants.METADATA_KEY_RUPTURE,
            MetadataConstants.METADATA_KEY_EXPLOSION_FROM_RUPTURE,
            MetadataConstants.METADATA_KEY_OLD_NAME_KEY,
            MetadataConstants.METADATA_KEY_DODGE_TRACKER
    );

    public static final @NotNull List<String> ARROW_METADATA_KEYS = List.of(
            MetadataConstants.METADATA_KEY_INF_ARROW,
            MetadataConstants.METADATA_KEY_BOW_FORCE,
            MetadataConstants.METADATA_KEY_ARROW_DISTANCE,
            MetadataConstants.METADATA_KEY_SPAWNED_ARROW,
            MetadataConstants.METADATA_KEY_MULTI_SHOT_ARROW,
            MetadataConstants.METADATA_KEY_BOUNCE_COUNT,
            MetadataConstants.METADATA_KEY_CROSSBOW_PROJECTILE
    );

    public static final @NotNull String METADATA_KEY_REPLANT = "mcMMO: Recently Replanted";
    public static final @NotNull String METADATA_KEY_CROSSBOW_PROJECTILE = "mcMMO: Crossbow Projectile";
    public static final @NotNull String METADATA_KEY_SPAWNED_ARROW = "mcMMO: Spawned Arrow";
    public static final @NotNull String METADATA_KEY_MULTI_SHOT_ARROW = "mcMMO: Multi-shot Arrow";
    public static final @NotNull String METADATA_KEY_BOUNCE_COUNT = "mcMMO: Arrow Bounce Count";
    public static final @NotNull String METADATA_KEY_EXPLOSION_FROM_RUPTURE = "mcMMO: Rupture Explosion";
    public static final @NotNull String METADATA_KEY_FISH_HOOK_REF = "mcMMO: Fish Hook Tracker";
    public static final @NotNull String METADATA_KEY_DODGE_TRACKER = "mcMMO: Dodge Tracker";
    public static final @NotNull String METADATA_KEY_CUSTOM_DAMAGE = "mcMMO: Custom Damage";
    public static final @NotNull String METADATA_KEY_TRAVELING_BLOCK = "mcMMO: Traveling Block";
    public static final @NotNull String METADATA_KEY_TRACKED_TNT = "mcMMO: Tracked TNT";
    public static final @NotNull String METADATA_KEY_NAME_VISIBILITY = "mcMMO: Name Visibility";
    public static final @NotNull String METADATA_KEY_INF_ARROW = "mcMMO: Infinite Arrow";
    public static final @NotNull String METADATA_KEY_TRACKED_ARROW = "mcMMO: Tracked Arrow";
    public static final @NotNull String METADATA_KEY_BOW_FORCE = "mcMMO: Bow Force";
    public static final @NotNull String METADATA_KEY_ARROW_DISTANCE = "mcMMO: Arrow Distance";
    public static final @NotNull String METADATA_KEY_BONUS_DROPS = "mcMMO: Double Drops";
    public static final @NotNull String METADATA_KEY_DISARMED_ITEM = "mcMMO: Disarmed Item";
    public static final @NotNull String METADATA_KEY_PLAYER_DATA = "mcMMO: Player Data";
    public static final @NotNull String METADATA_KEY_DATABASE_COMMAND = "mcMMO: Processing Database Command";
    // the value of these two keys have "furnace" to keep supporting legacy data
    public static final @NotNull String METADATA_KEY_CONTAINER_UUID_MOST_SIG = "furnace_uuid_most_sig";
    public static final @NotNull String METADATA_KEY_CONTAINER_UUID_LEAST_SIG = "furnace_uuid_least_sig";
    public static final @NotNull String METADATA_KEY_SUPER_ABILITY_BOOSTED_ITEM = "super_ability_boosted";
    public static final @NotNull String METADATA_KEY_MOB_SPAWNER_MOB = "mcmmo_mob_spawner_mob";
    public static final @NotNull String METADATA_KEY_EGG_MOB = "mcmmo_egg_mob";
    public static final @NotNull String METADATA_KEY_NETHER_PORTAL_MOB = "mcmmo_nethergate_mob";
    public static final @NotNull String METADATA_KEY_COTW_SUMMONED_MOB = "mcmmo_cotw_summoned_mob";
    public static final @NotNull String METADATA_KEY_PLAYER_BRED_MOB = "mcmmo_player_bred_mob";
    public static final @NotNull String METADATA_KEY_PLAYER_TAMED_MOB = "mcmmo_player_tamed_mob";
    public static final @NotNull String METADATA_KEY_VILLAGER_TRADE_ORIGIN_ITEM = "mcmmo_villager_trade_origin_item";
    public static final @NotNull String METADATA_KEY_EXPLOITED_ENDERMEN = "mcmmo_exploited_endermen";
    public static final @NotNull String METADATA_KEY_CUSTOM_NAME = "mcmmo_custom_name";
    public static final @NotNull String METADATA_KEY_OLD_NAME_KEY = "mcmmo_old_name";
    public static final @NotNull String METADATA_KEY_RUPTURE = "mcmmo_rupture";
    public static final byte SIMPLE_FLAG_VALUE = (byte) 0x1;
    public static FixedMetadataValue MCMMO_METADATA_VALUE;
}
