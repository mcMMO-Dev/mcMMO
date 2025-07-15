package com.gmail.nossr50.skills.alchemy;

import static com.gmail.nossr50.util.text.ConfigStringUtils.getMaterialConfigString;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.alchemy.PotionStage;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.skills.RankUtils;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class AlchemyManager extends SkillManager {
    private final double LUCKY_MODIFIER = 4.0 / 3.0;

    public AlchemyManager(McMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.ALCHEMY);
    }

    public int getTier() {
        return RankUtils.getRank(getPlayer(), SubSkillType.ALCHEMY_CONCOCTIONS);
    }

    public List<ItemStack> getIngredients() {
        return mcMMO.p.getPotionConfig().getIngredients(getTier());
    }

    public String getIngredientList() {
        StringBuilder list = new StringBuilder();

        for (ItemStack ingredient : getIngredients()) {
            String string = getMaterialConfigString(ingredient.getType());

            list.append(", ").append(string);
        }

        return list.substring(2);
    }

    public double calculateBrewSpeed(boolean isLucky) {
        int skillLevel = getSkillLevel();

        if (skillLevel < RankUtils.getUnlockLevel(SubSkillType.ALCHEMY_CATALYSIS)) {
            return Alchemy.catalysisMinSpeed;
        }

        return Math.min(Alchemy.catalysisMaxSpeed, Alchemy.catalysisMinSpeed +
                (Alchemy.catalysisMaxSpeed - Alchemy.catalysisMinSpeed) * (skillLevel
                        - RankUtils.getUnlockLevel(SubSkillType.ALCHEMY_CATALYSIS)) / (
                        Alchemy.catalysisMaxBonusLevel - RankUtils.getUnlockLevel(
                                SubSkillType.ALCHEMY_CATALYSIS))) * (isLucky ? LUCKY_MODIFIER
                : 1.0);
    }

    /**
     * Handle the XP gain for a successful potion brew.
     *
     * @param potionStage The potion stage, this is used to determine the XP gain.
     * @param amount The amount of potions brewed.
     */
    public void handlePotionBrewSuccesses(PotionStage potionStage, int amount) {
        applyXpGain((float) (ExperienceConfig.getInstance().getPotionXP(potionStage) * amount),
                XPGainReason.PVE, XPGainSource.PASSIVE);
    }
}