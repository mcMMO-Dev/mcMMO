package com.gmail.nossr50.skills.taming;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.EntityEffect;
import org.bukkit.entity.*;

public class Taming {
    public static int environmentallyAwareUnlockLevel = AdvancedConfig.getInstance().getEnviromentallyAwareUnlock();
    public static int holyHoundUnlockLevel            = RankUtils.getUnlockLevel(SubSkillType.TAMING_HOLY_HOUND);

    public static int    fastFoodServiceUnlockLevel      = RankUtils.getUnlockLevel(SubSkillType.TAMING_FAST_FOOD_SERVICE);
    public static double fastFoodServiceActivationChance = AdvancedConfig.getInstance().getFastFoodChance();

    public static int    goreBleedTicks    = 2; //Equivalent to rank 1 in Rupture
    public static double goreModifier      = AdvancedConfig.getInstance().getGoreModifier();

    public static int    sharpenedClawsUnlockLevel = RankUtils.getUnlockLevel(SubSkillType.TAMING_SHARPENED_CLAWS);
    public static double sharpenedClawsBonusDamage = AdvancedConfig.getInstance().getSharpenedClawsBonus();

    public static int    shockProofUnlockLevel = RankUtils.getUnlockLevel(SubSkillType.TAMING_SHOCK_PROOF);
    public static double shockProofModifier    = AdvancedConfig.getInstance().getShockProofModifier();

    public static int    thickFurUnlockLevel = RankUtils.getUnlockLevel(SubSkillType.TAMING_THICK_FUR);
    public static double thickFurModifier    = AdvancedConfig.getInstance().getThickFurModifier();

    public static boolean canPreventDamage(Tameable pet, AnimalTamer owner) {
        return pet.isTamed() && owner instanceof Player && pet instanceof Wolf;
    }

    public static double processThickFur(Wolf wolf, double damage) {
        wolf.playEffect(EntityEffect.WOLF_SHAKE);
        return damage / thickFurModifier;
    }

    public static void processThickFurFire(Wolf wolf) {
        wolf.playEffect(EntityEffect.WOLF_SMOKE);
        wolf.setFireTicks(0);
    }

    public static double processShockProof(Wolf wolf, double damage) {
        wolf.playEffect(EntityEffect.WOLF_SHAKE);
        return damage / shockProofModifier;
    }

    public static void processHolyHound(Wolf wolf, double damage) {
        double modifiedHealth = Math.min(wolf.getHealth() + damage, wolf.getMaxHealth());

        wolf.setHealth(modifiedHealth);
        wolf.playEffect(EntityEffect.WOLF_HEARTS);
    }

    protected static String getCallOfTheWildFailureMessage(EntityType type) {
        switch (type) {
            case OCELOT:
                return "Taming.Summon.Fail.Ocelot";

            case WOLF:
                return "Taming.Summon.Fail.Wolf";

            case HORSE:
                return "Taming.Summon.Fail.Horse";

            default:
                return "";
        }
    }
}