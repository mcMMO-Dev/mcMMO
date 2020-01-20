package com.gmail.nossr50.core;

import com.gmail.nossr50.mcmmo.api.platform.util.MetadataKey;

import org.bukkit.metadata.FixedMetadataValue;

/**
 * This is a convenience class which stores the string representations of our metadata keys
 */
public class MetadataConstants {

    /* Metadata Values */
    public static final MetadataKey<Boolean> FISH_HOOK_REF_METAKEY = new MetadataKey<>("mcMMO: Fish Hook Tracker");
    public static final MetadataKey<Boolean> DODGE_TRACKER        = new MetadataKey<>("mcMMO: Dodge Tracker");
    public static final MetadataKey<Boolean> CUSTOM_DAMAGE_METAKEY = new MetadataKey<>("mcMMO: Custom Damage");
    public final static MetadataKey<Boolean> UNNATURAL_MOB_METAKEY = new MetadataKey<>("mcMMO: Spawned Entity");
    public final static MetadataKey<Boolean> PISTON_TRACKING_METAKEY = new MetadataKey<>("mcMMO: Piston Tracking");
    public final static MetadataKey<Boolean> FURNACE_TRACKING_METAKEY = new MetadataKey<>("mcMMO: Tracked Furnace");
    public final static MetadataKey<Boolean> TNT_TRACKING_METAKEY = new MetadataKey<>("mcMMO: Tracked TNT");
    public final static MetadataKey<Boolean> SPAWNED_FIREWORKS_METAKEY = new MetadataKey<>("mcMMO: Funfetti");
    public final static MetadataKey<Boolean> SAFE_TNT_METAKEY = new MetadataKey<>("mcMMO: Safe TNT");
    public final static MetadataKey<String> CUSTOM_NAME_METAKEY = new MetadataKey<>("mcMMO: Custom Name");
    public final static MetadataKey<Boolean> NAME_VISIBILITY_METAKEY = new MetadataKey<>("mcMMO: Name Visibility");
    public final static MetadataKey<Boolean> DROPPED_ITEM_TRACKING_METAKEY = new MetadataKey<>("mcMMO: Tracked Item");
    public final static MetadataKey<Boolean> INFINITE_ARROW_METAKEY = new MetadataKey<>("mcMMO: Infinite Arrow");
    public final static MetadataKey<Boolean> BOW_FORCE_METAKEY = new MetadataKey<>("mcMMO: Bow Force");
    public final static MetadataKey<Boolean> ARROW_DISTANCE_METAKEY = new MetadataKey<>("mcMMO: Arrow Distance");
    public final static MetadataKey<Boolean> ARROW_TRACKER_METAKEY = new MetadataKey<>("mcMMO: Arrow Tracker");
    public final static MetadataKey<Boolean> BONUS_DROPS_METAKEY = new MetadataKey<>("mcMMO: Bonus Drops");
    public final static MetadataKey<Boolean> DISARMED_ITEM_METAKEY = new MetadataKey<>("mcMMO: Disarmed Item");
    public final static MetadataKey<Boolean> PLAYER_DATA_METAKEY = new MetadataKey<>("mcMMO: Player Data");
    public final static MetadataKey<Boolean> GREEN_THUMB_METAKEY = new MetadataKey<>("mcMMO: Green Thumb");
    public final static MetadataKey<Boolean> DATABASE_PROCESSING_COMMAND_METAKEY = new MetadataKey<>("mcMMO: Processing Database Command");
    public final static MetadataKey<Boolean> PETS_ANIMAL_TRACKING_METAKEY = new MetadataKey<>("mcMMO: Pet Animal");
    public static final MetadataKey<Boolean> COTW_TEMPORARY_SUMMON = new MetadataKey<>("mcMMO: COTW Entity");

}
