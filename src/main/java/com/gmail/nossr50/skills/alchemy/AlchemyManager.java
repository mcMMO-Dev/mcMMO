package com.gmail.nossr50.skills.alchemy;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.config.potion.PotionConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;

public class AlchemyManager extends SkillManager {
    private final double LUCKY_MODIFIER = 4.0 / 3.0;

    public AlchemyManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.ALCHEMY);
    }

    public boolean canCatalysis() {
        return Permissions.secondaryAbilityEnabled(getPlayer(), SecondaryAbility.CATALYSIS);
    }

    public boolean canConcoctions() {
        return Permissions.secondaryAbilityEnabled(getPlayer(), SecondaryAbility.CONCOCTIONS);
    }

    public boolean canUseIngredient(ItemStack item) {
        for (ItemStack ingredient : getIngredients()) {
            if (item.isSimilar(ingredient)) {
                return true;
            }
        }
        return false;
    }

    public int getTier() {
        for (Alchemy.Tier tier : Alchemy.Tier.values()) {
            if (getSkillLevel() >= tier.getLevel()) {
                return tier.toNumerical();
            }
        }

        return 0;
    }

    public List<ItemStack> getIngredients() {
        switch (Alchemy.Tier.fromNumerical(getTier())) {
            case EIGHT:
                return PotionConfig.getInstance().concoctionsIngredientsTierEight;
            case SEVEN:
                return PotionConfig.getInstance().concoctionsIngredientsTierSeven;
            case SIX:
                return PotionConfig.getInstance().concoctionsIngredientsTierSix;
            case FIVE:
                return PotionConfig.getInstance().concoctionsIngredientsTierFive;
            case FOUR:
                return PotionConfig.getInstance().concoctionsIngredientsTierFour;
            case THREE:
                return PotionConfig.getInstance().concoctionsIngredientsTierThree;
            case TWO:
                return PotionConfig.getInstance().concoctionsIngredientsTierTwo;
            default:
                return PotionConfig.getInstance().concoctionsIngredientsTierOne;
        }
    }

    public String getIngredientList() {
        StringBuilder list = new StringBuilder();

        for (ItemStack ingredient : getIngredients()) {
            String string = StringUtils.getPrettyItemString(ingredient.getType()) + (ingredient.getDurability() != 0 ? ":" + ingredient.getDurability() : "");
            if (string.equals("Long Grass:2")) {
                string = "Fern";
            }
            else if (string.equals("Raw Fish:3")) {
                string = "Pufferfish";
            }

            list.append(", " + string);
        }
        return list.substring(2);
    }

    public double getBrewSpeed() {
        return Alchemy.calculateBrewSpeed(getSkillLevel());
    }

    public double getBrewSpeedLucky() {
        return LUCKY_MODIFIER * Alchemy.calculateBrewSpeed(getSkillLevel());
    }

    public void handlePotionBrewSuccesses(int amount) {
        applyXpGain((float) (ExperienceConfig.getInstance().getPotionXP() * amount));
    }
}
