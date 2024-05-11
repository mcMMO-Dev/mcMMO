package com.gmail.nossr50.datatypes.skills.alchemy;

import com.gmail.nossr50.mcMMO;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import static com.gmail.nossr50.util.PotionUtil.samePotionType;
import static java.util.Objects.requireNonNull;

public class AlchemyPotion {
    private final @NotNull ItemStack potionItemstack;
    private final @NotNull Map<ItemStack, String> alchemyPotionChildren;

    public AlchemyPotion(@NotNull ItemStack potionItemStack, @NotNull Map<ItemStack, String> alchemyPotionChildren) {
        this.potionItemstack = requireNonNull(potionItemStack, "potionItemStack cannot be null");
        this.alchemyPotionChildren = requireNonNull(alchemyPotionChildren, "alchemyPotionChildren cannot be null");
    }

    public @NotNull ItemStack toItemStack(int amount) {
        final ItemStack clone = potionItemstack.clone();
        clone.setAmount(Math.max(1, amount));
        return clone;
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
        if (otherPotion.getType() != potionItemstack.getType() || !otherPotion.hasItemMeta()) {
            return false;
        }

        final PotionMeta otherPotionMeta = (PotionMeta) otherPotion.getItemMeta();
        // compare custom effects on both potions, this has to be done in two traversals
        // comparing thisPotionMeta -> otherPotionMeta and otherPotionMeta -> thisPotionMeta
        if (hasDifferingCustomEffects(getAlchemyPotionMeta(), otherPotionMeta)
                || hasDifferingCustomEffects(otherPotionMeta, getAlchemyPotionMeta())) {
            return false;
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

        return true;
    }

    private boolean hasDifferingCustomEffects(PotionMeta potionMeta, PotionMeta otherPotionMeta) {
        for (int i = 0; i < potionMeta.getCustomEffects().size(); i++) {
            var effect = potionMeta.getCustomEffects().get(i);

            // One has an effect the other does not, they are not the same potion
            if (!otherPotionMeta.hasCustomEffect(effect.getType())) {
                return true;
            }

            var otherEffect = otherPotionMeta.getCustomEffects().get(i);
            // Amplifier or duration are not equal, they are not the same potion
            if (effect.getAmplifier() != otherEffect.getAmplifier()
                    || effect.getDuration() != otherEffect.getDuration()) {
                return true;
            }
        }
        return false;
    }

    public PotionMeta getAlchemyPotionMeta() {
        return (PotionMeta) potionItemstack.getItemMeta();
    }

    public boolean isSplash() {
        return potionItemstack.getType() == Material.SPLASH_POTION;
    }

    public boolean isLingering() {
        return potionItemstack.getType() == Material.LINGERING_POTION;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlchemyPotion that = (AlchemyPotion) o;
        return Objects.equals(potionItemstack, that.potionItemstack) && Objects.equals(alchemyPotionChildren, that.alchemyPotionChildren);
    }

    @Override
    public int hashCode() {
        return Objects.hash(potionItemstack, alchemyPotionChildren);
    }

    @Override
    public String toString() {
        return "AlchemyPotion{" +
                "potion=" + potionItemstack +
                ", alchemyPotionChildren=" + alchemyPotionChildren +
                '}';
    }
}
