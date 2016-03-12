package com.gmail.nossr50.util;

import java.util.HashMap;

import org.bukkit.enchantments.Enchantment;

public class EnchantmentUtils {

    private static final HashMap<String, Enchantment> enchants = new HashMap<String, Enchantment>();

    static {
        enchants.put("SHARPNESS", Enchantment.DAMAGE_ALL);
        enchants.put("POWER", Enchantment.ARROW_DAMAGE);
        enchants.put("FIRE_PROTECTION", Enchantment.PROTECTION_FIRE);
        enchants.put("FEATHER_FALLING", Enchantment.PROTECTION_FALL);
        enchants.put("PROTECTION", Enchantment.PROTECTION_ENVIRONMENTAL);
        enchants.put("BLAST_PROTECTION", Enchantment.PROTECTION_EXPLOSIONS);
        enchants.put("PROJECTILE_PROTECTION", Enchantment.PROTECTION_PROJECTILE);
        enchants.put("RESPIRATION", Enchantment.OXYGEN);
        enchants.put("INFINITY", Enchantment.ARROW_INFINITE);
        enchants.put("AQUA_AFFINITY", Enchantment.WATER_WORKER);
        enchants.put("UNBREAKING", Enchantment.DURABILITY);
        enchants.put("SMITE", Enchantment.DAMAGE_UNDEAD);
        enchants.put("BANE_OF_ARTHROPODS", Enchantment.DAMAGE_ARTHROPODS);
        enchants.put("EFFICIENCY", Enchantment.DIG_SPEED);
        enchants.put("FIRE_ASPECT", Enchantment.FIRE_ASPECT);
        enchants.put("SILK_TOUCH", Enchantment.SILK_TOUCH);
        enchants.put("FORTUNE", Enchantment.LOOT_BONUS_BLOCKS);
        enchants.put("LOOTING", Enchantment.LOOT_BONUS_MOBS);
        enchants.put("PUNCH", Enchantment.ARROW_KNOCKBACK);
        enchants.put("FLAME", Enchantment.ARROW_FIRE);
        enchants.put("KNOCKBACK", Enchantment.KNOCKBACK);
        enchants.put("THORNS", Enchantment.THORNS);
        enchants.put("MENDING", Enchantment.MENDING);
        enchants.put("DEPTH_STRIDER", Enchantment.DEPTH_STRIDER);
        enchants.put("FROST_WALKER", Enchantment.FROST_WALKER);
    }

    /**
     * Method to get an {@link Enchantment} using it's Vanilla Minecraft name or Bukkit enum name
     *
     * @param enchantmentName Vanilla or Bukkit name of enchantment
     *
     * @return Enchantment or null if no enchantment was found
     */
    public static Enchantment getByName(String enchantmentName) {
        if (enchants.containsKey(enchantmentName)) {
            return enchants.get(enchantmentName);
        }

        return Enchantment.getByName(enchantmentName);
    }
}
