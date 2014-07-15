package com.gmail.nossr50.datatypes.skills.alchemy;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

public class AlchemyPotion {
    private short dataValue;
    private String name;
    private List<String> lore;
    private List<PotionEffect> effects;
    private Map<ItemStack, Short> children;

    public AlchemyPotion(short dataValue, String name, List<String> lore, List<PotionEffect> effects, Map<ItemStack, Short> children) {
        this.dataValue = dataValue;
        this.lore = lore;
        this.name = name;
        this.effects = effects;
        this.children = children;
    }

    public String toString() {
        return "AlchemyPotion{" + dataValue + ", " + name + ", Effects[" + effects.size() + "], Children[" + children.size() + "]}";
    }

    public ItemStack toItemStack(int amount) {
        ItemStack potion = new ItemStack(Material.POTION, amount, this.getDataValue());
        PotionMeta meta = (PotionMeta) potion.getItemMeta();

        if (this.getName() != null) {
            meta.setDisplayName(this.getName());
        }

        if (this.getLore() != null && !this.getLore().isEmpty()) {
            meta.setLore(this.getLore());
        }

        if (!this.getEffects().isEmpty()) {
            for (PotionEffect effect : this.getEffects()) {
                meta.addCustomEffect(effect, true);
            }
        }

        potion.setItemMeta(meta);
        return potion;
    }

    public Potion toPotion(int amount) {
        return Potion.fromItemStack(this.toItemStack(amount));
    }

    public short getDataValue() {
        return dataValue;
    }

    public void setDataValue(short data_value) {
        this.dataValue = data_value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public List<PotionEffect> getEffects() {
        return effects;
    }

    public void setEffects(List<PotionEffect> effects) {
        this.effects = effects;
    }

    public Map<ItemStack, Short> getChildren() {
        return children;
    }

    public void setChildren(Map<ItemStack, Short> children) {
        this.children = children;
    }

    public Short getChildDataValue(ItemStack ingredient) {
        if (!children.isEmpty()) {
            for (Entry<ItemStack, Short> child : children.entrySet()) {
                if (ingredient.isSimilar(child.getKey())) {
                    return child.getValue();
                }
            }
        }
        return -1;
    }
}
