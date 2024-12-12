package com.gmail.nossr50.util;

import com.gmail.nossr50.mcMMO;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class EnchantmentMapper {
    private final mcMMO pluginRef;
    private final Enchantment efficiency;
    private final Enchantment unbreaking;
    private final Enchantment infinity;
    private final Enchantment featherFalling;
    private final Enchantment luckOfTheSea;

    public EnchantmentMapper(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        this.efficiency = initEfficiency();
        this.unbreaking = initUnbreaking();
        this.infinity = initInfinity();
        this.featherFalling = initFeatherFalling();
        this.luckOfTheSea = initLuckOfTheSea();
    }
    
    private static @Nullable Enchantment mockSpigotMatch(@NotNull String input) {
        // Replicates match() behaviour for older versions lacking this API
        final String filtered = input.toLowerCase(Locale.ROOT).replaceAll("\\s+", "_");
        final NamespacedKey namespacedKey = NamespacedKey.fromString(filtered);
        return (namespacedKey != null) ? Registry.ENCHANTMENT.get(namespacedKey) : null;
    }

    private Enchantment initLuckOfTheSea() {
        if (mockSpigotMatch("luck_of_the_sea") != null) {
            return mockSpigotMatch("luck_of_the_sea");
        }

        // Look for the enchantment by name
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (enchantment.getKey().getKey().equalsIgnoreCase("LUCK_OF_THE_SEA")
                    || enchantment.getKey().getKey().equalsIgnoreCase("LUCK")
                    || enchantment.getName().equalsIgnoreCase("LUCK_OF_THE_SEA")
                    || enchantment.getName().equalsIgnoreCase("LUCK")) {
                return enchantment;
            }
        }

        pluginRef.getLogger().severe("Unable to find the Luck of the Sea enchantment, " +
                "mcMMO will not function properly.");
        throw new IllegalStateException("Unable to find the Luck of the Sea enchantment");
    }

    private Enchantment initFeatherFalling() {
        if (mockSpigotMatch("feather_falling") != null) {
            return mockSpigotMatch("feather_falling");
        }

        // Look for the enchantment by name
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (enchantment.getKey().getKey().equalsIgnoreCase("FEATHER_FALLING")
                    || enchantment.getKey().getKey().equalsIgnoreCase("PROTECTION_FALL")
                    || enchantment.getName().equalsIgnoreCase("FEATHER_FALLING")
                    || enchantment.getName().equalsIgnoreCase("PROTECTION_FALL")) {
                return enchantment;
            }
        }

        pluginRef.getLogger().severe("Unable to find the Feather Falling enchantment, " +
                "mcMMO will not function properly.");
        throw new IllegalStateException("Unable to find the Feather Falling enchantment");
    }

    private Enchantment initInfinity() {
        if (mockSpigotMatch("infinity") != null) {
            return mockSpigotMatch("infinity");
        }

        // Look for the enchantment by name
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (enchantment.getKey().getKey().equalsIgnoreCase("INFINITY")
                    || enchantment.getKey().getKey().equalsIgnoreCase("ARROW_INFINITE")
                    || enchantment.getName().equalsIgnoreCase("INFINITY")
                    || enchantment.getName().equalsIgnoreCase("ARROW_INFINITE")) {
                return enchantment;
            }
        }

        pluginRef.getLogger().severe("Unable to find the Infinity enchantment, " +
                "mcMMO will not function properly.");
        throw new IllegalStateException("Unable to find the Infinity enchantment");
    }

    private Enchantment initEfficiency() {
        if (mockSpigotMatch("efficiency") != null) {
            return mockSpigotMatch("efficiency");
        }

        // Look for the enchantment by name
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (enchantment.getKey().getKey().equalsIgnoreCase("EFFICIENCY")
                    || enchantment.getKey().getKey().equalsIgnoreCase("DIG_SPEED")
                    || enchantment.getName().equalsIgnoreCase("EFFICIENCY")
                    || enchantment.getName().equalsIgnoreCase("DIG_SPEED")) {
                return enchantment;
            }
        }

        pluginRef.getLogger().severe("Unable to find the Efficiency enchantment, " +
                "mcMMO will not function properly.");
        throw new IllegalStateException("Unable to find the Efficiency enchantment");
    }

    private Enchantment initUnbreaking() {
        if (mockSpigotMatch("unbreaking") != null) {
            return mockSpigotMatch("unbreaking");
        }

        // Look for the enchantment by name
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (enchantment.getKey().getKey().equalsIgnoreCase("UNBREAKING")
                    || enchantment.getKey().getKey().equalsIgnoreCase("DURABILITY")
                    || enchantment.getName().equalsIgnoreCase("UNBREAKING")
                    || enchantment.getName().equalsIgnoreCase("DURABILITY")) {
                return enchantment;
            }
        }

        pluginRef.getLogger().severe("Unable to find the Unbreaking enchantment, " +
                "mcMMO will not function properly.");
        throw new IllegalStateException("Unable to find the Unbreaking enchantment");
    }

    /**
     * Get the efficiency enchantment
     *
     * @return The efficiency enchantment
     */
    public Enchantment getEfficiency() {
        return efficiency;
    }

    public Enchantment getUnbreaking() {
        return unbreaking;
    }

    public Enchantment getInfinity() {
        return infinity;
    }

    public Enchantment getFeatherFalling() {
        return featherFalling;
    }

    public Enchantment getLuckOfTheSea() {
        return luckOfTheSea;
    }
}
