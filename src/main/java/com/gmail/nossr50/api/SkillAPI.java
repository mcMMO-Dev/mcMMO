package com.gmail.nossr50.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.gmail.nossr50.commands.skills.SkillCommand;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.SkillType.SkillUseType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.skills.SkillAbilityManager;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.child.ChildConfig;
import com.gmail.nossr50.skills.child.FamilyTree;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.commands.CommandRegistrationManager;
import com.gmail.nossr50.util.player.UserManager;
import com.google.common.collect.ImmutableList;

public final class SkillAPI {
    private SkillAPI() {}

    /**
     * Returns a list of strings with mcMMO's skills
     * This includes parent and child skills
     * </br>
     * This function is designed for API usage.
     *
     * @return a list of strings with valid skill names
     */
    public static List<String> getSkills() {
        return SkillType.getSkillNames();
    }

    /**
     * Returns a list of strings with mcMMO's skills
     * This only includes parent skills
     * </br>
     * This function is designed for API usage.
     *
     * @return a list of strings with valid skill names
     */
    public static List<String> getNonChildSkills() {
        return getStringListFromSkillList(SkillType.getNonChildSkills());
    }

    /**
     * Returns a list of strings with mcMMO's skills
     * This only includes child skills
     * </br>
     * This function is designed for API usage.
     *
     * @return a list of strings with valid skill names
     */
    public static List<String> getChildSkills() {
        return getStringListFromSkillList(SkillType.getChildSkills());
    }

    /**
     * Returns a list of strings with mcMMO's skills
     * This only includes combat skills
     * </br>
     * This function is designed for API usage.
     *
     * @return a list of strings with valid skill names
     */
    public static List<String> getCombatSkills() {
        return getStringListFromSkillList(SkillType.getCombatSkills());
    }

    /**
     * Returns a list of strings with mcMMO's skills
     * This only includes gathering skills
     * </br>
     * This function is designed for API usage.
     *
     * @return a list of strings with valid skill names
     */
    public static List<String> getGatheringSkills() {
        return getStringListFromSkillList(SkillType.getGatheringSkills());
    }

    /**
     * Returns a list of strings with mcMMO's skills
     * This only includes misc skills
     * </br>
     * This function is designed for API usage.
     *
     * @return a list of strings with valid skill names
     */
    public static List<String> getMiscSkills() {
        return getStringListFromSkillList(SkillType.getMiscSkills());
    }

    private static List<String> getStringListFromSkillList(List<SkillType> skillsTypes) {
        List<String> skills = new ArrayList<String>();

        for (SkillType skillType : skillsTypes) {
            skills.add(skillType.getName());
        }

        return skills;
    }
    
    /**
     * Creates a mcmmo skill
     * @param name the name of the skill in all caps
     * @param managerClass the manager class for the skill
     * @param commandClass the command handler for the skill
     * @param isChild whether the skill is a child
     * @param runescapeColor the runescape color for the skill
     * @param skillUseType what the skill use type of the skill is (COMBAT, GATHERING, MISC)
     * @param secondaryAbilities the list of secondary abilities the skill can use
     * @return the mcmmo skill created
     */
    public static SkillType createSkill(String name, Class<? extends SkillManager> managerClass, Class<? extends SkillCommand> commandClass, boolean isChild, Color runescapeColor, SkillUseType skillUseType, List<SecondaryAbility> secondaryAbilities) {
    	SkillType skill = SkillType.createSkill(name, managerClass, commandClass, isChild, runescapeColor, skillUseType, secondaryAbilities);
		CommandRegistrationManager.registerSkillCommandAndPassSkillToConstructor(skill);
    	return skill;
    }

    /**
     * Creates a mcmmo skill with an ability
     * @param name the name of the skill in all caps
     * @param managerClass the manager class for the skill
     * @param commandClass the command handler for the skill
     * @param isChild whether the skill is a child
     * @param runescapeColor the runescape color for the skill
     * @param skillUseType what the skill use type of the skill is (COMBAT, GATHERING, MISC)
     * @param ability the AbilityType attached to this skill
     * @param materials what tools can activate the ability
     * @param secondaryAbilities the list of secondary abilities the skill can use
     * @return the mcmmo skill created
     */
    public static SkillType createSkill(String name, Class<? extends SkillManager> managerClass, Class<? extends SkillCommand> commandClass, boolean isChild, Color runescapeColor, SkillUseType skillUseType, AbilityType ability, Material[] materials, List<SecondaryAbility> secondaryAbilities) {
    	SkillType skill = SkillType.createSkill(name, managerClass, commandClass, isChild, runescapeColor, skillUseType, ability, ToolType.createToolType(StringUtils.getCapitalized(name), materials), secondaryAbilities);
    	CommandRegistrationManager.registerSkillCommandAndPassSkillToConstructor(skill);
    	return skill;
    }

    /**
     * Creates a mcmmo skill with an ability
     * @param name the name of the skill in all caps
     * @param managerClass the manager class for the skill
     * @param commandClass the command handler for the skill
     * @param isChild whether the skill is a child
     * @param runescapeColor the runescape color for the skill
     * @param skillUseType what the skill use type of the skill is (COMBAT, GATHERING, MISC)
     * @param ability the AbilityType attached to this skill
     * @param secondaryAbilities the list of secondary abilities the skill can use
     * @return the mcmmo skill created
     */
    public static SkillType createSkill(String name, Class<? extends SkillManager> managerClass, Class<? extends SkillCommand> commandClass, boolean isChild, Color runescapeColor, SkillUseType skillUseType, AbilityType ability, List<SecondaryAbility> secondaryAbilities) {
    	SkillType skill = SkillType.createSkill(name, managerClass, commandClass, isChild, runescapeColor, skillUseType, ability, ToolType.createToolType(StringUtils.getCapitalized(name)), secondaryAbilities);
    	CommandRegistrationManager.registerSkillCommandAndPassSkillToConstructor(skill);
    	return skill;
    }

    /**
     * Creates a mcmmo skill
     * @param name the name of the skill in all caps
     * @param managerClass the manager class for the skill
     * @param commandClass the command handler for the skill
     * @param isChild whether the skill is a child
     * @param runescapeColor the runescape color for the skill
     * @param skillUseType what the skill use type of the skill is (COMBAT, GATHERING, MISC)
     * @param secondaryAbilities the list of secondary abilities the skill can use
     * @return the mcmmo skill created
     */
    public static SkillType createSkill(String name, Class<? extends SkillManager> managerClass, Class<? extends SkillCommand> commandClass, boolean isChild, Color runescapeColor, SkillUseType skillUseType, SecondaryAbility... secondaryAbilities) {
    	return createSkill(name, managerClass, commandClass, isChild, runescapeColor, skillUseType, ImmutableList.copyOf(secondaryAbilities));
    }

    /**
     * Creates a mcmmo skill with an ability
     * @param name the name of the skill in all caps
     * @param managerClass the manager class for the skill
     * @param commandClass the command handler for the skill
     * @param isChild whether the skill is a child
     * @param runescapeColor the runescape color for the skill
     * @param skillUseType what the skill use type of the skill is (COMBAT, GATHERING, MISC)
     * @param ability the AbilityType attached to this skill
     * @param materials what tools can activate the ability
     * @param secondaryAbilities the list of secondary abilities the skill can use
     * @return the mcmmo skill created
     */
    public static SkillType createSkill(String name, Class<? extends SkillManager> managerClass, Class<? extends SkillCommand> commandClass, boolean isChild, Color runescapeColor, SkillUseType skillUseType, AbilityType ability, Material[] materials, SecondaryAbility... secondaryAbilities) {
    	return createSkill(name, managerClass, commandClass, isChild, runescapeColor, skillUseType, ability, materials, ImmutableList.copyOf(secondaryAbilities));
    }

    /**
     * Creates a mcmmo skill with an ability
     * @param name the name of the skill in all caps
     * @param managerClass the manager class for the skill
     * @param commandClass the command handler for the skill
     * @param isChild whether the skill is a child
     * @param runescapeColor the runescape color for the skill
     * @param skillUseType what the skill use type of the skill is (COMBAT, GATHERING, MISC)
     * @param ability the AbilityType attached to this skill
     * @param secondaryAbilities the list of secondary abilities the skill can use
     * @return the mcmmo skill created
     */
    public static SkillType createSkill(String name, Class<? extends SkillManager> managerClass, Class<? extends SkillCommand> commandClass, boolean isChild, Color runescapeColor, SkillUseType skillUseType, AbilityType ability, SecondaryAbility... secondaryAbilities) {
    	return createSkill(name, managerClass, commandClass, isChild, runescapeColor, skillUseType, ability, ImmutableList.copyOf(secondaryAbilities));
    }
    
    /**
     * Gets the skill manager for the skill from the specified player
     * @param skill the skill the skill manager is for
     * @param player who's skill manager it is
     * @return a skill manager for the skill owned by the player
     */
    public static SkillManager getSkillManager(SkillType skill, Player player) {
    	return UserManager.getPlayer(player).getSkillManager(skill);
    }
    
    /**
     * Gets the skill manager cast to SkillAbilityManager
     * @param skill the skill the skill manager is for
     * @param player who's skill manager it is
     * @return a skill ability manager for the skill owned by the player
     */
    public static SkillAbilityManager getSkillAbilityManager(SkillType skill, Player player) {
    	SkillManager skillManager = getSkillManager(skill, player);
    	if(skillManager instanceof SkillAbilityManager) {
    		return (SkillAbilityManager)skillManager;
    	}
    	return null;
    }
    
    /**
     * Registers parents for the child
     * @param childSkill the child to add parents for
     * @param parentSkills the parents to add
     */
    public static void registerParents(SkillType childSkill, SkillType... parentSkills) {
    	if(ChildConfig.getInstance() != null) {
    		ChildConfig.getInstance().addParents(childSkill, parentSkills);
    	}
    }
    
    /**
     * Loads the new skills added
     */
    public static void loadNewSkills() {
    	SkillType.setUpSkillTypes();
    }
}
