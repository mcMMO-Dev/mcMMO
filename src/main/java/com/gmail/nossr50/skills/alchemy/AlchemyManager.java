package com.gmail.nossr50.skills.alchemy;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.config.skills.alchemy.PotionConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.XPGainReason;
import com.gmail.nossr50.datatypes.skills.alchemy.PotionStage;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.StringUtils;

public class AlchemyManager extends SkillManager {
    private final double LUCKY_MODIFIER = 4.0 / 3.0;

    public AlchemyManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.ALCHEMY);
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
        return PotionConfig.getInstance().getIngredients(getTier());
    }

    public String getIngredientList() {
        StringBuilder list = new StringBuilder();

        for (ItemStack ingredient : getIngredients()) {
            short durability = ingredient.getDurability();

            String string = StringUtils.getPrettyItemString(ingredient.getType()) + (durability != 0 ? ":" + durability : "");

            if (string.equals("Long Grass:2")) {
                string = "Fern";
            }
            else if (string.equals("Raw Fish:3")) {
                string = "Pufferfish";
            }

            list.append(", ").append(string);
        }

        return list.substring(2);
    }

    public double calculateBrewSpeed(boolean isLucky) {
        int skillLevel = getSkillLevel();

        if (skillLevel < Alchemy.catalysisUnlockLevel) {
            return Alchemy.catalysisMinSpeed;
        }

        return Math.min(Alchemy.catalysisMaxSpeed, Alchemy.catalysisMinSpeed + (Alchemy.catalysisMaxSpeed - Alchemy.catalysisMinSpeed) * (skillLevel - Alchemy.catalysisUnlockLevel) / (Alchemy.catalysisMaxBonusLevel - Alchemy.catalysisUnlockLevel)) * (isLucky ? LUCKY_MODIFIER : 1.0);
    }

    public void handlePotionBrewSuccesses(PotionStage potionStage, int amount) {
        applyXpGain((float) (ExperienceConfig.getInstance().getPotionXP(potionStage) * amount), XPGainReason.PVE);
    }
}
