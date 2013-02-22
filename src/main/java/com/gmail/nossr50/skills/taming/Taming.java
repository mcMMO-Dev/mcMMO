package com.gmail.nossr50.skills.taming;

import org.bukkit.EntityEffect;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Permissions;

public class Taming {
    public static int environmentallyAwareUnlockLevel = AdvancedConfig.getInstance().getEnviromentallyAwareUnlock();
    public static int holyHoundUnlockLevel = AdvancedConfig.getInstance().getHolyHoundUnlock();

    public static double fastFoodServiceActivationChance = AdvancedConfig.getInstance().getFastFoodChance();
    public static int fastFoodServiceUnlockLevel = AdvancedConfig.getInstance().getFastFoodUnlock();

    public static double goreMaxChance = AdvancedConfig.getInstance().getGoreChanceMax();
    public static int goreBleedTicks = AdvancedConfig.getInstance().getGoreBleedTicks();
    public static int goreMaxBonusLevel = AdvancedConfig.getInstance().getGoreMaxBonusLevel();
    public static int goreModifier = AdvancedConfig.getInstance().getGoreModifier();

    public static int sharpenedClawsUnlockLevel = AdvancedConfig.getInstance().getSharpenedClawsUnlock();
    public static int sharpenedClawsBonusDamage = AdvancedConfig.getInstance().getSharpenedClawsBonus();

    public static int shockProofUnlockLevel = AdvancedConfig.getInstance().getShockProofUnlock();
    public static int shockProofModifier = AdvancedConfig.getInstance().getShockProofModifier();

    public static int thickFurUnlockLevel = AdvancedConfig.getInstance().getThickFurUnlock();
    public static int thickFurModifier = AdvancedConfig.getInstance().getThickFurModifier();

    public static int wolfXp = Config.getInstance().getTamingXPWolf();
    public static int ocelotXp = Config.getInstance().getTamingXPOcelot();

    public static boolean canPreventDamage(Tameable pet, AnimalTamer owner) {
        return pet.isTamed() && owner instanceof Player && pet instanceof Wolf;
    }

    public static boolean canUseThickFur(Player player) {
        return SkillTools.unlockLevelReached(player, SkillType.TAMING, thickFurUnlockLevel) && Permissions.thickFur(player);
    }

    public static boolean canUseEnvironmentallyAware(Player player) {
        return SkillTools.unlockLevelReached(player, SkillType.TAMING, environmentallyAwareUnlockLevel) && Permissions.environmentallyAware(player);
    }

    public static boolean canUseShockProof(Player player) {
        return SkillTools.unlockLevelReached(player, SkillType.TAMING, shockProofUnlockLevel) && Permissions.shockProof(player);
    }

    public static boolean canUseHolyHound(Player player) {
        return SkillTools.unlockLevelReached(player, SkillType.TAMING, holyHoundUnlockLevel) && Permissions.holyHound(player);
    }

    public static int processThickFur(Wolf wolf, int damage) {
        wolf.playEffect(EntityEffect.WOLF_SHAKE);
        return damage / thickFurModifier;
    }

    public static void processThickFurFire(Wolf wolf) {
        wolf.playEffect(EntityEffect.WOLF_SMOKE);
        wolf.setFireTicks(0);
    }

    public static void processEnvironmentallyAware(Player player, Wolf wolf, int damage) {
        if (damage > wolf.getHealth()) {
            return;
        }

        wolf.teleport(player);
        player.sendMessage(LocaleLoader.getString("Taming.Listener.Wolf"));
    }

    public static int processShockProof(Wolf wolf, int damage) {
        wolf.playEffect(EntityEffect.WOLF_SHAKE);
        return damage / shockProofModifier;
    }

    public static void processHolyHound(Wolf wolf, int damage) {
        int modifiedHealth = Math.min(wolf.getHealth() + damage, wolf.getMaxHealth());

        wolf.setHealth(modifiedHealth);
        wolf.playEffect(EntityEffect.WOLF_HEARTS);
    }
}
