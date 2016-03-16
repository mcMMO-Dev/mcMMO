package com.gmail.nossr50.datatypes.skills.alchemy;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;

import com.gmail.nossr50.config.skills.alchemy.PotionConfig;

public class AlchemyPotion {
    private Material material;
    private PotionData data;
    private String name;
    private List<String> lore;
    private List<PotionEffect> effects;
    private Map<ItemStack, String> children;

    public AlchemyPotion(Material material, PotionData data, String name, List<String> lore, List<PotionEffect> effects, Map<ItemStack, String> children) {
        this.material = material;
        this.data = data;
        this.lore = lore;
        this.name = name;
        this.effects = effects;
        this.children = children;
    }

    public String toString() {
        return "AlchemyPotion{" + data + ", " + name + ", Effects[" + effects.size() + "], Children[" + children.size() + "]}";
    }

    public ItemStack toItemStack(int amount) {
        ItemStack potion = new ItemStack(material, amount);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();

        meta.setBasePotionData(data);
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

    public Material getMaterial() {
        return material;
    }

    public Potion toPotion(int amount) {
        return Potion.fromItemStack(this.toItemStack(amount));
    }

    public PotionData getData() {
        return data;
    }

    public void setData(PotionData data) {
        this.data = data;
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

    public Map<ItemStack, String> getChildren() {
        return children;
    }

    public void setChildren(Map<ItemStack, String> children) {
        this.children = children;
    }

    public AlchemyPotion getChild(ItemStack ingredient) {
        if (!children.isEmpty()) {
            for (Entry<ItemStack, String> child : children.entrySet()) {
                if (ingredient.isSimilar(child.getKey())) {
                    return PotionConfig.getInstance().getPotion(child.getValue());
                }
            }
        }
        return null;
    }

    public boolean isSimilar(ItemStack item) {
        if (item.getType() != material) {
            return false;
        }
        if (!item.hasItemMeta()) {
            return false;
        }
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        PotionData that = meta.getBasePotionData();
        if (data.getType() != that.getType()) {
            return false;
        }
        if (data.isExtended() != that.isExtended()) {
            return false;
        }
        if (data.isUpgraded() != that.isUpgraded()) {
            return false;
        }
        for (PotionEffect effect : effects) {
            if (!meta.hasCustomEffect(effect.getType())) {
                return false;
            }
        }
        if (!meta.hasLore() && !lore.isEmpty()) {
            return false;
        }
        if (!(lore.isEmpty() && !meta.hasLore()) && !meta.getLore().equals(lore)) {
            return false;
        }
        if (!meta.hasDisplayName() && name != null) {
            return false;
        }
        return (name == null && !meta.hasDisplayName()) || meta.getDisplayName().equals(name);
    }
}
