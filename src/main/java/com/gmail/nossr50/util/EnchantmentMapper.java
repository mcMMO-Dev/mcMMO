package com.gmail.nossr50.util;

import com.gmail.nossr50.mcMMO;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

public class EnchantmentMapper {
    private final mcMMO pluginRef;
    private final Enchantment efficiency;
    private final Enchantment unbreaking;
    private final Enchantment infinity;
    private final Enchantment featherFalling;
    private final Enchantment luckOfTheSea;

    public EnchantmentMapper(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        this.efficiency = resolve("efficiency", "Efficiency");
        this.unbreaking = resolve("unbreaking", "Unbreaking");
        this.infinity = resolve("infinity", "Infinity");
        this.featherFalling = resolve("feather_falling", "Feather Falling");
        this.luckOfTheSea = resolve("luck_of_the_sea", "Luck of the Sea");
    }

    private @NotNull Enchantment resolve(@NotNull String key, @NotNull String displayName) {
        final Enchantment enchantment = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(key));

        if (enchantment == null) {
            pluginRef.getLogger().severe("Unable to find the " + displayName + " enchantment, "
                    + "mcMMO will not function properly.");
            throw new IllegalStateException(
                    "Unable to find the " + displayName + " enchantment");
        }

        return enchantment;
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
