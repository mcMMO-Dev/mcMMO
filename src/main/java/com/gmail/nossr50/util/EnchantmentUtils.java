package com.gmail.nossr50.util;

import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class EnchantmentUtils {

    private static final HashMap<String, Enchantment> legacyEnchantments = new HashMap<>();

    static {
        // backwards compatibility for looking up legacy bukkit enums
        addLegacyEnchantmentLookup("SHARPNESS", "DAMAGE_ALL");
        addLegacyEnchantmentLookup("POWER", "ARROW_DAMAGE");
        addLegacyEnchantmentLookup("FIRE_PROTECTION", "PROTECTION_FIRE");
        addLegacyEnchantmentLookup("FEATHER_FALLING", "PROTECTION_FALL");
        addLegacyEnchantmentLookup("PROTECTION", "PROTECTION_ENVIRONMENTAL");
        addLegacyEnchantmentLookup("BLAST_PROTECTION", "PROTECTION_EXPLOSIONS");
        addLegacyEnchantmentLookup("PROJECTILE_PROTECTION", "PROTECTION_PROJECTILE");
        addLegacyEnchantmentLookup("RESPIRATION", "OXYGEN");
        addLegacyEnchantmentLookup("INFINITY", "ARROW_INFINITE");
        addLegacyEnchantmentLookup("AQUA_AFFINITY", "WATER_WORKER");
        addLegacyEnchantmentLookup("UNBREAKING", "DURABILITY");
        addLegacyEnchantmentLookup("SMITE", "DAMAGE_UNDEAD");
        addLegacyEnchantmentLookup("BANE_OF_ARTHROPODS", "DAMAGE_ARTHROPODS");
        addLegacyEnchantmentLookup("EFFICIENCY", "DIG_SPEED");
        addLegacyEnchantmentLookup("FORTUNE", "LOOT_BONUS_BLOCKS");
        addLegacyEnchantmentLookup("LOOTING", "LOOT_BONUS_MOBS");
        addLegacyEnchantmentLookup("PUNCH", "ARROW_KNOCKBACK");
        addLegacyEnchantmentLookup("FLAME", "ARROW_FIRE");
    }

    /**
     * Method to get an {@link Enchantment} using it's Vanilla Minecraft name or Bukkit enum name
     *
     * @param enchantmentName Vanilla or Bukkit name of enchantment
     *
     * @return Enchantment or null if no enchantment was found
     */
    @SuppressWarnings("deprecation")
    public static @Nullable Enchantment getByName(String enchantmentName) {
        if (legacyEnchantments.containsKey(enchantmentName)) {
            return legacyEnchantments.get(enchantmentName);
        }

        return Enchantment.getByName(enchantmentName);
    }

    @SuppressWarnings("deprecation")
    private static void addLegacyEnchantmentLookup(String enchantmentName, String legacyBukkitName) {
        if (Enchantment.getByName(legacyBukkitName) != null) {
            legacyEnchantments.put(enchantmentName, Enchantment.getByName(legacyBukkitName));
        }
    }
}
