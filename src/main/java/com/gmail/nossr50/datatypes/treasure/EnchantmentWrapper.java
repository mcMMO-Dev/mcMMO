package com.gmail.nossr50.datatypes.treasure;

import com.google.common.base.Objects;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

public class EnchantmentWrapper {
    private final @NotNull Enchantment enchantment;
    private final int enchantmentLevel;

    public EnchantmentWrapper(@NotNull Enchantment enchantment, int enchantmentLevel) {
        this.enchantment = enchantment;
        this.enchantmentLevel = enchantmentLevel;
    }

    public @NotNull Enchantment getEnchantment() {
        return enchantment;
    }

    public int getEnchantmentLevel() {
        return enchantmentLevel;
    }

    @Override
    public String toString() {
        return "EnchantmentWrapper{" +
                "enchantment=" + enchantment +
                ", enchantmentLevel=" + enchantmentLevel +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EnchantmentWrapper that = (EnchantmentWrapper) o;
        return enchantmentLevel == that.enchantmentLevel && Objects.equal(enchantment,
                that.enchantment);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(enchantment, enchantmentLevel);
    }
}
