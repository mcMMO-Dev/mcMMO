package com.gmail.nossr50.skills.alchemy;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.config.skills.alchemy.PotionConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.alchemy.PotionStage;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.text.StringUtils;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AlchemyManager extends SkillManager {
    private final double LUCKY_MODIFIER = 4.0 / 3.0;

    public AlchemyManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, PrimarySkillType.ALCHEMY);
    }

    public int getTier() {
        return RankUtils.getRank(getPlayer(), SubSkillType.ALCHEMY_CONCOCTIONS);
    }

    public List<ItemStack> getIngredients() {
        return PotionConfig.getInstance().getIngredients(getTier());
    }

    public String getIngredientList() {
        StringBuilder list = new StringBuilder();

        for (ItemStack ingredient : getIngredients()) {
            String string = StringUtils.getPrettyItemString(ingredient.getType());

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
        applyXpGain((float) (ExperienceConfig.getInstance().getPotionXP(potionStage) * amount), XPGainReason.PVE, XPGainSource.PASSIVE);
    }
}