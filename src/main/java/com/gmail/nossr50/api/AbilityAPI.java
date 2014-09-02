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
    
    /**
     * Creates a new ability to be used with a skill that is effective on certain blocks with a default cooldown of 240
     * 
     * @param name the name of the ability
     * @param skillName the name of the skill (used for localization only)
     * @param materials the materials that the skill is effective on (ability will only activate when one of those blocks is entered)
     * @return the ability created
     */
    public static AbilityType createAbility(String name, String skillName, final Material... materials) {
    	return createAbility(name, skillName, 240, materials);
    }

    /**
     * Creates a new ability to be used with a skill with a default cooldown of 240
     * 
     * @param name the name of the ability
     * @param skillName the name of the skill (used for localization only)
     * @return the ability created
     */
    public static AbilityType createAbility(String name, String skillName) {
    	return createAbility(name, skillName, 240);
    }

    /**
     * Creates a new ability to be used with a skill that is effective on certain blocks with a specified default cooldown
     * 
     * @param name the name of the ability
     * @param skillName the name of the skill (used for localization only)
     * @param materials the materials that the skill is effective on
     * @param cooldown the default cooldown of the ability
     * @return the ability created
     */
    public static AbilityType createAbility(String name, String skillName, int cooldown, final Material... materials) {
    	AbilityType ability = AbilityType.createAbility(name, skillName, materials);
    	Config.getInstance().createAbility(ability, cooldown);
		return ability;
    }

    /**
     * Creates a new ability to be used with a skill with a specified default cooldown
     * 
     * @param name the name of the ability
     * @param skillName the name of the skill (used for localization only)
     * @param cooldown the default cooldown of the ability
     * @return the ability created
     */
    public static AbilityType createAbility(String name, String skillName, int cooldown) {
    	AbilityType ability = AbilityType.createAbility(name, skillName);
    	Config.getInstance().createAbility(ability, cooldown);
		return ability;
    }

    /**
     * Creates a secondary ability to be used with a skill with a max bonus level of 0 and max chance of 100
     * 
     * @param name the name of the ability
     * @param skillName the name of the skill (used for config only)
     * @return the secondary ability created
     */
    public static SecondaryAbility createSecondaryAbility(String name, String skillName) {
    	return createSecondaryAbility(name, skillName, 0, 100);
    }
    
    /**
     * Creates a secondary ability to be used with a skill with specified default config
     * 
     * @param name the name of the ability
     * @param skillName the name of the skill (used for config only)
     * @param maxBonuslevel the level where the skill is at it's maximum chance
     * @param maxChance the maximum chance of the skill
     * @return the secondary ability created
     */
    public static SecondaryAbility createSecondaryAbility(String name, String skillName, int maxBonusLevel, double maxChance) {
    	SecondaryAbility ability = new SecondaryAbility(name);
    	AdvancedConfig.getInstance().createNewSkill(ability, skillName, maxBonusLevel, maxChance);
    	return ability;
    }
    
    /**
     * Checks if the specified player has permission to use the specified ability
     * @param player the player to check the permission for
     * @param ability the ability that the player wants to use
     * @return true if the player has permission and false if they don't
     */
    public static boolean hasSecondaryAbilityPermissions(Player player, SecondaryAbility ability) {
    	return Permissions.secondaryAbilityEnabled(player, ability);
    }
    
    /**
     * Checks if a secondary ability's chances of activating would succeed
     * @param skillAbility the secondary ability to check for
     * @param player the player who wants to use the secondary ability
     * @return true if the player can successfully use the ability this time
     */
    public static boolean wasSecondaryAbilityActivationSuccessful(SecondaryAbility skillAbility, Player player) {
    	return SkillUtils.activationSuccessful(skillAbility, player);
    }
    
    /**
     * Activates an ability for a player
     * @param skill the skill that the player is trying to use the ability for
     * @param player the player trying to use the ability
     * @param blockState a blockstate that must be passed to check if the ability is allowed to use it
     */
    public static void activateSkillAbility(SkillType skill, Player player, BlockState blockState) {
    	SkillAbilityManager abilityManager = SkillAPI.getSkillAbilityManager(skill, player);
		if(abilityManager != null) {
    		abilityManager.doAbilityPreparationCheck(blockState);
    	}
    }

    /**
     * Activates an ability for a player
     * @param skill the skill that the player is trying to use the ability for
     * @param player the player trying to use the ability
     */
    public static void activateSkillAbility(SkillType skill, Player player) {
    	SkillAbilityManager abilityManager = SkillAPI.getSkillAbilityManager(skill, player);
		if(abilityManager != null) {
    		abilityManager.doAbilityPreparationCheck(null);
    	}
    }
    
    /**
     * Checks if an ability will activate when passed this blockstate
     * @param ability the ability to check for
     * @param blockState the blockstate to see if the ability will activate for it
     * @return whether the ability will activate when passed this blockstate
     */
    public static boolean abilityBlockCheck(AbilityType ability, BlockState blockState) {
    	return ability.blockCheck(blockState);
    }
}
