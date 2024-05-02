package com.gmail.nossr50.datatypes.skills.alchemy;

import com.gmail.nossr50.mcMMO;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import static com.gmail.nossr50.util.PotionUtil.samePotionType;
import static java.util.Objects.requireNonNull;

public class AlchemyPotion {
    private final ItemStack potion;
    private final Map<ItemStack, String> alchemyPotionChildren;

    public AlchemyPotion(ItemStack potion, Map<ItemStack, String> alchemyPotionChildren) {
        this.potion = requireNonNull(potion, "potion cannot be null");
        this.alchemyPotionChildren = requireNonNull(alchemyPotionChildren, "alchemyPotionChildren cannot be null");
    }

    public @NotNull ItemStack toItemStack(int amount) {
        final ItemStack potion = new ItemStack(this.potion);
        potion.setAmount(Math.max(1, amount));
        return potion;
    }

    public Map<ItemStack, String> getAlchemyPotionChildren() {
        return alchemyPotionChildren;
    }

    public @Nullable AlchemyPotion getChild(@NotNull ItemStack ingredient) {
        if (!alchemyPotionChildren.isEmpty()) {
            for (Entry<ItemStack, String> child : alchemyPotionChildren.entrySet()) {
                if (ingredient.isSimilar(child.getKey())) {
                    return mcMMO.p.getPotionConfig().getPotion(child.getValue());
                }
            }
        }
        return null;
    }

    public boolean isSimilarPotion(@NotNull ItemStack otherPotion) {
        requireNonNull(otherPotion, "otherPotion cannot be null");
        // TODO: Investigate?
        // We currently don't compare base potion effects, likely because they are derived from the potion type
        if (otherPotion.getType() != potion.getType() || !otherPotion.hasItemMeta()) {
            return false;
        }

        final PotionMeta otherPotionMeta = (PotionMeta) otherPotion.getItemMeta();

        // all custom effects must be present
        for (var effect : getAlchemyPotionMeta().getCustomEffects()) {
            if (!otherPotionMeta.hasCustomEffect(effect.getType())) {
                return false;
            }
        }

        if (!samePotionType(getAlchemyPotionMeta(), otherPotionMeta)) {
            return false;
        }

        if (!otherPotionMeta.hasLore() && getAlchemyPotionMeta().hasLore()
                || !getAlchemyPotionMeta().hasLore() && otherPotionMeta.hasLore()) {
            return false;
        }

        if (otherPotionMeta.hasLore() && getAlchemyPotionMeta().hasLore()
                && !otherPotionMeta.getLore().equals(getAlchemyPotionMeta().getLore())) {
            return false;
        }

        if (!otherPotionMeta.hasDisplayName() && getAlchemyPotionMeta().hasDisplayName()) {
            return false;
        }

        var alchemyPotionName = getAlchemyPotionMeta().hasDisplayName() ? getAlchemyPotionMeta().getDisplayName() : null;

        return (alchemyPotionName == null && !otherPotionMeta.hasDisplayName()) || otherPotionMeta.getDisplayName().equals(alchemyPotionName);
    }

    public PotionMeta getAlchemyPotionMeta() {
        return (PotionMeta) potion.getItemMeta();
    }

    public boolean isSplash() {
        return potion.getType() == Material.SPLASH_POTION;
    }

    public boolean isLingering() {
        return potion.getType() == Material.LINGERING_POTION;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlchemyPotion that = (AlchemyPotion) o;
        return Objects.equals(potion, that.potion) && Objects.equals(alchemyPotionChildren, that.alchemyPotionChildren);
    }

    @Override
    public int hashCode() {
        return Objects.hash(potion, alchemyPotionChildren);
    }

    @Override
    public String toString() {
        return "AlchemyPotion{" +
                "potion=" + potion +
                ", alchemyPotionChildren=" + alchemyPotionChildren +
                '}';
    }
}
