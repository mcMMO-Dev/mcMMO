package com.gmail.nossr50.util;

import static com.gmail.nossr50.util.MetadataService.NSK_SUPER_ABILITY_BOOSTED_ITEM;

import com.gmail.nossr50.mcMMO;
import java.util.List;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public final class ItemMetadataUtils {

    public static final @NotNull String LEGACY_ABILITY_TOOL_LORE = "mcMMO Ability Tool";

    private ItemMetadataUtils() {
        // private ctor
    }

    public static void setSuperAbilityBoostedItem(@NotNull ItemStack itemStack,
            int originalDigSpeed) {
        final ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            mcMMO.p.getLogger()
                    .severe("Can not assign persistent data to an item with null item metadata");
            return;
        }

        final PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

        dataContainer.set(NSK_SUPER_ABILITY_BOOSTED_ITEM, PersistentDataType.INTEGER,
                originalDigSpeed);

        itemStack.setItemMeta(itemMeta);
    }

    public static boolean isSuperAbilityBoosted(@NotNull ItemStack itemStack) {
        final ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            return false;
        }

        //Get container from entity
        final PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

        //If the key is present, the tool can be considered dig speed boosted
        return dataContainer.has(NSK_SUPER_ABILITY_BOOSTED_ITEM, PersistentDataType.INTEGER);
    }

    public static int getSuperAbilityToolOriginalDigSpeed(@NotNull ItemStack itemStack) {
        //Get container from entity
        final ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            return 0;
        }

        final PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        //Too lazy to make a custom data type for this stuff
        final Integer boostValue = dataContainer.get(NSK_SUPER_ABILITY_BOOSTED_ITEM,
                PersistentDataType.INTEGER);

        if (boostValue == null) {
            mcMMO.p.getLogger().severe("Value should never be null for a boosted item");
            return 0;
        }

        return Math.max(boostValue, 0);
    }

    public static void removeBonusDigSpeedOnSuperAbilityTool(@NotNull ItemStack itemStack) {
        final ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            return;
        }

        final PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        final Integer boostValue = dataContainer.get(NSK_SUPER_ABILITY_BOOSTED_ITEM,
                PersistentDataType.INTEGER);

        if (boostValue == null) {
            mcMMO.p.getLogger().severe("Value should never be null for a boosted item");
        }

        final int originalSpeed = boostValue == null ? 0 : Math.max(boostValue, 0);
        final Enchantment efficiency = mcMMO.p.getEnchantmentMapper().getEfficiency();

        if (itemMeta.hasEnchant(efficiency)) {
            itemMeta.removeEnchant(efficiency);
        }

        if (originalSpeed > 0) {
            itemMeta.addEnchant(efficiency, originalSpeed, true);
        }

        dataContainer.remove(NSK_SUPER_ABILITY_BOOSTED_ITEM); //Remove persistent data

        itemStack.setItemMeta(itemMeta);
    }

    public static boolean isLegacyAbilityTool(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            return false;
        }

        List<String> lore = itemMeta.getLore();

        if (lore == null || lore.isEmpty()) {
            return false;
        }

        return lore.contains(LEGACY_ABILITY_TOOL_LORE);
    }
}
