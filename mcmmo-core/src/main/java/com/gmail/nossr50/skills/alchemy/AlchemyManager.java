package com.gmail.nossr50.skills.alchemy;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;

public class AlchemyManager extends SkillManager {

    public AlchemyManager(mcMMO pluginRef,  McMMOPlayer mcMMOPlayer) {
        super(pluginRef, mcMMOPlayer, PrimarySkillType.ALCHEMY);
    }

//
//    public int getTier() {
//        return RankUtils.getRank(getPlayer(), SubSkillType.ALCHEMY_CONCOCTIONS);
//    }
//
//    public List<ItemStack> getIngredients() {
//        return PotionManager.getInstance().getIngredients(getTier());
//    }
//
//    public String getIngredientList() {
//        StringBuilder list = new StringBuilder();
//
//        for (ItemStack ingredient : getIngredients()) {
//            String string = StringUtils.getPrettyItemString(ingredient.getType());
//
//            list.append(", ").append(string);
//        }
//
//        return list.substring(2);
//    }
//
//    public double calculateBrewSpeed(boolean isLucky) {
//        int skillLevel = getSkillLevel();
//
//        if (skillLevel < Alchemy.catalysisUnlockLevel) {
//            return Alchemy.catalysisMinSpeed;
//        }
//
//        double LUCKY_MODIFIER = 4.0 / 3.0;
//        return Math.min(Alchemy.catalysisMaxSpeed, Alchemy.catalysisMinSpeed + (Alchemy.catalysisMaxSpeed - Alchemy.catalysisMinSpeed) * (skillLevel - Alchemy.catalysisUnlockLevel) / (Alchemy.catalysisMaxBonusLevel - Alchemy.catalysisUnlockLevel)) * (isLucky ? LUCKY_MODIFIER : 1.0);
//    }
//
//    public void handlePotionBrewSuccesses(PotionStage potionStage, int amount) {
//        //TODO: This code disturbs me
//        applyXpGain((float) (mcMMO.getConfigManager().getConfigExperience().getExperienceAlchemy().getPotionXPByStage(potionStage.toNumerical()) * amount), XPGainReason.PVE, XPGainSource.PASSIVE);
//    }
}