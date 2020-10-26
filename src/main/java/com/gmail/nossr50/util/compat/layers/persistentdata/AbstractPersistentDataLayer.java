package com.gmail.nossr50.util.compat.layers.persistentdata;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.compat.layers.AbstractCompatibilityLayer;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Furnace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public abstract class AbstractPersistentDataLayer extends AbstractCompatibilityLayer {

    protected final @NotNull NamespacedKey NSK_SUPER_ABILITY_BOOSTED_ITEM;
    protected final @NotNull NamespacedKey NSK_MOB_SPAWNER_MOB;
    protected final @NotNull NamespacedKey NSK_EGG_MOB;
    protected final @NotNull NamespacedKey NSK_NETHER_GATE_MOB;
    protected final @NotNull NamespacedKey NSK_COTW_SUMMONED_MOB;
    protected final @NotNull NamespacedKey NSK_PLAYER_BRED_MOB;
    protected final @NotNull NamespacedKey NSK_PLAYER_TAMED_MOB;
    protected final @NotNull NamespacedKey NSK_VILLAGER_TRADE_ORIGIN_ITEM;
    protected final @NotNull NamespacedKey NSK_EXPLOITED_ENDERMEN;

    //Never change these constants
    public final @NotNull String STR_SUPER_ABILITY_BOOSTED_ITEM = "super_ability_boosted";
    public final @NotNull String STR_MOB_SPAWNER_MOB = "mcmmo_mob_spawner_mob";
    public final @NotNull String STR_EGG_MOB = "mcmmo_egg_mob";
    public final @NotNull String STR_NETHER_PORTAL_MOB = "mcmmo_nethergate_mob";
    public final @NotNull String STR_COTW_SUMMONED_MOB = "mcmmo_cotw_summoned_mob";
    public final @NotNull String STR_PLAYER_BRED_MOB = "mcmmo_player_bred_mob";
    public final @NotNull String STR_PLAYER_TAMED_MOB = "mcmmo_player_tamed_mob";
    public final @NotNull String STR_VILLAGER_TRADE_ORIGIN_ITEM = "mcmmo_villager_trade_origin_item";
    public final @NotNull String STR_EXPLOITED_ENDERMEN = "mcmmo_exploited_endermen";

    /*
     * Don't modify these keys
     */
    public final @NotNull String STR_FURNACE_UUID_MOST_SIG = "furnace_uuid_most_sig";
    public final @NotNull String STR_FURNACE_UUID_LEAST_SIG = "furnace_uuid_least_sig";

    protected final @NotNull NamespacedKey NSK_FURNACE_UUID_MOST_SIG;
    protected final @NotNull NamespacedKey NSK_FURNACE_UUID_LEAST_SIG;

    public final @NotNull String LEGACY_ABILITY_TOOL_LORE = "mcMMO Ability Tool";

    protected static final byte SIMPLE_FLAG_VALUE = (byte) 0x1;

    public AbstractPersistentDataLayer() {
        NSK_SUPER_ABILITY_BOOSTED_ITEM = getNamespacedKey(STR_SUPER_ABILITY_BOOSTED_ITEM);
        NSK_MOB_SPAWNER_MOB = getNamespacedKey(STR_MOB_SPAWNER_MOB);
        NSK_EGG_MOB = getNamespacedKey(STR_EGG_MOB);
        NSK_NETHER_GATE_MOB = getNamespacedKey(STR_NETHER_PORTAL_MOB);
        NSK_COTW_SUMMONED_MOB = getNamespacedKey(STR_COTW_SUMMONED_MOB);
        NSK_PLAYER_BRED_MOB = getNamespacedKey(STR_PLAYER_BRED_MOB);
        NSK_PLAYER_TAMED_MOB = getNamespacedKey(STR_PLAYER_TAMED_MOB);
        NSK_VILLAGER_TRADE_ORIGIN_ITEM = getNamespacedKey(STR_VILLAGER_TRADE_ORIGIN_ITEM);
        NSK_EXPLOITED_ENDERMEN = getNamespacedKey(STR_EXPLOITED_ENDERMEN);
        NSK_FURNACE_UUID_MOST_SIG = getNamespacedKey(STR_FURNACE_UUID_MOST_SIG);
        NSK_FURNACE_UUID_LEAST_SIG = getNamespacedKey(STR_FURNACE_UUID_LEAST_SIG);

        initializeLayer();
    }


    /**
     * Helper method to simplify generating namespaced keys
     * @param key the {@link String} value of the key
     * @return the generated {@link NamespacedKey}
     */
    private @NotNull NamespacedKey getNamespacedKey(@NotNull String key) {
        return new NamespacedKey(mcMMO.p, key);
    }

    /**
     * Whether or not a target {@link LivingEntity} has a specific mcMMO mob flags
     * @param flag the type of mob flag to check for
     * @param livingEntity the living entity to check for metadata
     * @return true if the mob has metadata values for target {@link MobMetaFlagType}
     */
    public abstract boolean hasMobFlag(@NotNull MobMetaFlagType flag, @NotNull LivingEntity livingEntity);

    /**
     * Whether or not a target {@link LivingEntity} has any mcMMO mob flags
     * @param livingEntity the living entity to check for metadata
     * @return true if the mob has any mcMMO mob related metadata values
     */
    public abstract boolean hasMobFlags(@NotNull LivingEntity livingEntity);

    /**
     * Copies all mcMMO mob flags from one {@link LivingEntity} to another {@link LivingEntity}
     * This does not clear existing mcMMO mob flags on the target
     * @param sourceEntity entity to copy from
     * @param targetEntity entity to copy to
     */
    public abstract void addMobFlags(@NotNull LivingEntity sourceEntity, @NotNull LivingEntity targetEntity);

    /**
     * Adds a mob flag to a {@link LivingEntity} which effectively acts a true/false boolean
     * Existence of the flag can be considered a true value, non-existence can be considered false for all intents and purposes
     * @param flag the desired flag to assign
     * @param livingEntity the target living entity
     */
    public abstract void flagMetadata(@NotNull MobMetaFlagType flag, @NotNull LivingEntity livingEntity);

    /**
     * Removes a specific mob flag from target {@link LivingEntity}
     * @param flag desired flag to remove
     * @param livingEntity the target living entity
     */
    public abstract void removeMobFlag(@NotNull MobMetaFlagType flag, @NotNull LivingEntity livingEntity);

    /**
     * Remove all mcMMO related mob flags from the target {@link LivingEntity}
     * @param livingEntity target entity
     */
    public void removeMobFlags(@NotNull LivingEntity livingEntity) {
        for(MobMetaFlagType flag : MobMetaFlagType.values()) {
            removeMobFlag(flag, livingEntity);
        }
    }

    public abstract @Nullable UUID getFurnaceOwner(@NotNull Furnace furnace);

    public abstract void setFurnaceOwner(@NotNull Furnace furnace, @NotNull UUID uuid);

    public abstract void setSuperAbilityBoostedItem(@NotNull ItemStack itemStack, int originalDigSpeed);

    public abstract boolean isSuperAbilityBoosted(@NotNull ItemStack itemStack);

    public abstract int getSuperAbilityToolOriginalDigSpeed(@NotNull ItemStack itemStack);

    public abstract void removeBonusDigSpeedOnSuperAbilityTool(@NotNull ItemStack itemStack);

    public boolean isLegacyAbilityTool(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta == null)
            return false;

        List<String> lore = itemMeta.getLore();

        if(lore == null || lore.isEmpty())
            return false;

        return lore.contains(LEGACY_ABILITY_TOOL_LORE);
    }

    public @NotNull String getLegacyAbilityToolLore() {
        return LEGACY_ABILITY_TOOL_LORE;
    }
}
