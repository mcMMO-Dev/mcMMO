package com.gmail.nossr50.datatypes.skills.alchemy;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.PotionUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import static com.gmail.nossr50.util.PotionUtil.samePotionType;
import static java.util.Objects.requireNonNull;

public class AlchemyPotion {
    private final @NotNull String potionConfigName;
    private final @NotNull ItemStack potionItemStack;
    private final @NotNull ItemMeta potionItemMeta;
    private final @NotNull Map<ItemStack, String> alchemyPotionChildren;

    public AlchemyPotion(@NotNull String potionConfigName, @NotNull ItemStack potionItemStack,
                         @NotNull Map<ItemStack, String> alchemyPotionChildren) {
        this.potionConfigName = requireNonNull(potionConfigName, "potionConfigName cannot be null");
        this.potionItemStack = requireNonNull(potionItemStack, "potionItemStack cannot be null");
        this.alchemyPotionChildren = requireNonNull(alchemyPotionChildren, "alchemyPotionChildren cannot be null");
        this.potionItemMeta = requireNonNull(potionItemStack.getItemMeta(), "potionItemMeta cannot be null"); // The potion item meta should never be null because it is a potion, but if it is null, then something went terribly wrong
    }

    public @NotNull ItemStack toItemStack(int amount) {
        final ItemStack clone = potionItemStack.clone();
        clone.setAmount(Math.max(1, amount));
        return clone;
    }

    public @NotNull Map<ItemStack, String> getAlchemyPotionChildren() {
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
        return isSimilarPotion(otherPotion, otherPotion.getItemMeta());
    }

    public boolean isSimilarPotion(@NotNull ItemStack otherPotion, @Nullable ItemMeta otherMeta) {
        requireNonNull(otherPotion, "otherPotion cannot be null");

        if (otherPotion.getType() != potionItemStack.getType()) {
            return false;
        }

        // no potion meta, no match
        if (otherMeta == null) {
            return false;
        }

        /*
         * Compare custom effects on both potions.
         */

        final PotionMeta otherPotionMeta = (PotionMeta) otherMeta;
        // compare custom effects on both potions, this has to be done in two traversals
        // comparing thisPotionMeta -> otherPotionMeta and otherPotionMeta -> thisPotionMeta
        if (hasDifferingCustomEffects(getAlchemyPotionMeta(), otherPotionMeta)
                || hasDifferingCustomEffects(otherPotionMeta, getAlchemyPotionMeta())) {
            return false;
        }

        if (!samePotionType(getAlchemyPotionMeta(), otherPotionMeta)) {
            return false;
        }

        // Legacy only comparison, compare PotionData
        if (!PotionUtil.isPotionDataEqual(getAlchemyPotionMeta(), otherPotionMeta)) {
            return false;
        }

        /*
         * If one potion has lore and the other does not, then they are not the same potion.
         * If both have lore, compare the lore.
         * If neither have lore, they may be the same potion.
         */
        if (!otherPotionMeta.hasLore() && getAlchemyPotionMeta().hasLore()
                || !getAlchemyPotionMeta().hasLore() && otherPotionMeta.hasLore()) {
            return false;
        }

        return !otherPotionMeta.hasLore() || !getAlchemyPotionMeta().hasLore()
                || otherPotionMeta.getLore().equals(getAlchemyPotionMeta().getLore());
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
        return (PotionMeta) potionItemMeta;
    }

    public boolean isSplash() {
        return potionItemStack.getType() == Material.SPLASH_POTION;
    }

    public boolean isLingering() {
        return potionItemStack.getType() == Material.LINGERING_POTION;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlchemyPotion that = (AlchemyPotion) o;
        return Objects.equals(potionConfigName, that.potionConfigName) && Objects.equals(potionItemStack, that.potionItemStack) && Objects.equals(alchemyPotionChildren, that.alchemyPotionChildren);
    }

    @Override
    public int hashCode() {
        return Objects.hash(potionConfigName, potionItemStack, alchemyPotionChildren);
    }

    @Override
    public String toString() {
        return "AlchemyPotion{" +
                "potionConfigName='" + potionConfigName + '\'' +
                ", potionItemStack=" + potionItemStack +
                ", alchemyPotionChildren=" + alchemyPotionChildren +
                '}';
    }
}
