package com.gmail.nossr50.api;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.runnables.skills.BleedTimerTask;
import com.gmail.nossr50.skills.SkillAbilityManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;

public final class AbilityAPI {
    private AbilityAPI() {}

    public static boolean berserkEnabled(Player player) {
        return abilityEnabled(player, AbilityType.berserk);
    }

    public static boolean gigaDrillBreakerEnabled(Player player) {
        return abilityEnabled(player, AbilityType.gigaDrillBreaker);
    }

    public static boolean greenTerraEnabled(Player player) {
        return abilityEnabled(player, AbilityType.greenTerra);
    }

    public static boolean serratedStrikesEnabled(Player player) {
        return abilityEnabled(player, AbilityType.serratedStrikes);
    }

    public static boolean skullSplitterEnabled(Player player) {
        return abilityEnabled(player, AbilityType.skullSplitter);
    }

    public static boolean superBreakerEnabled(Player player) {
        return abilityEnabled(player, AbilityType.superBreaker);
    }

    public static boolean treeFellerEnabled(Player player) {
        return abilityEnabled(player, AbilityType.treeFeller);
    }
    
    public static boolean abilityEnabled(Player player, AbilityType ability) {
    	return UserManager.getPlayer(player).getAbilityMode(ability);
    }

    public static boolean isAnyAbilityEnabled(Player player) {
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        for (AbilityType ability : AbilityType.getAbilities()) {
            if (mcMMOPlayer.getAbilityMode(ability)) {
                return true;
            }
        }

        return false;
    }

    public static void resetCooldowns(Player player) {
        UserManager.getPlayer(player).resetCooldowns();
    }

    public static void setBerserkCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(AbilityType.berserk, cooldown);
    }

    public static void setGigaDrillBreakerCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(AbilityType.gigaDrillBreaker, cooldown);
    }

    public static void setGreenTerraCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(AbilityType.greenTerra, cooldown);
    }

    public static void setSerratedStrikesCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(AbilityType.serratedStrikes, cooldown);
    }

    public static void setSkullSplitterCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(AbilityType.skullSplitter, cooldown);
    }

    public static void setSuperBreakerCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(AbilityType.superBreaker, cooldown);
    }

    public static void setTreeFellerCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(AbilityType.treeFeller, cooldown);
    }
    
    public static void setAbilityCooldown(Player player, AbilityType ability, long cooldown) {
    	UserManager.getPlayer(player).setAbilityDATS(ability, cooldown);
    }

    public static boolean isBleeding(LivingEntity entity) {
        return BleedTimerTask.isBleeding(entity);
    }
    
    public static AbilityType createAbility(String name, String skillName, final Material... materials) {
    	return createAbility(name, skillName, 240, materials);
    }
    
    public static AbilityType createAbility(String name, String skillName) {
    	return createAbility(name, skillName, 240);
    }
    
    public static AbilityType createAbility(String name, String skillName, int cooldown, final Material... materials) {
    	AbilityType ability = AbilityType.createAbility(name, skillName, materials);
    	Config.getInstance().createAbility(ability, cooldown);
		return ability;
    }
    
    public static AbilityType createAbility(String name, String skillName, int cooldown) {
    	AbilityType ability = AbilityType.createAbility(name, skillName);
    	Config.getInstance().createAbility(ability, cooldown);
		return ability;
    }
    
    public static SecondaryAbility createSecondaryAbility(String name, String skillName) {
    	return createSecondaryAbility(name, skillName, 0, 100);
    }
    
    public static SecondaryAbility createSecondaryAbility(String name, String skillName, int maxBonusLevel, double maxChance) {
    	SecondaryAbility ability = new SecondaryAbility(name);
    	AdvancedConfig.getInstance().createNewSkill(ability, skillName, maxBonusLevel, maxChance);
    	return ability;
    }
    
    public static boolean hasSecondaryAbilityPermissions(Player player, SecondaryAbility ability) {
    	return Permissions.secondaryAbilityEnabled(player, ability);
    }
    
    public static boolean wasSecondaryAbilityActivationSuccessful(SecondaryAbility skillAbility, Player player, SkillType skill) {
    	return SkillUtils.activationSuccessful(skillAbility, player, skill);
    }
    
    public static void activateSkillAbility(SkillType skill, Player player, BlockState blockState) {
    	SkillAbilityManager abilityManager = SkillAPI.getSkillAbilityManager(skill, player);
		if(abilityManager != null) {
    		abilityManager.doAbilityPreparationCheck(blockState);
    	}
    }
    
    public static void activateSkillAbility(SkillType skill, Player player) {
    	SkillAbilityManager abilityManager = SkillAPI.getSkillAbilityManager(skill, player);
		if(abilityManager != null) {
    		abilityManager.doAbilityPreparationCheck(null);
    	}
    }
}
