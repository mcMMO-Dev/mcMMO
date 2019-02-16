package com.gmail.nossr50.core.skills.treasure;


public class EnchantmentTreasure {
    private Enchantment enchantment;
    private int level;

    public EnchantmentTreasure(Enchantment enchantment, int level) {
        this.setEnchantment(enchantment);
        this.setLevel(level);
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public void setEnchantment(Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
