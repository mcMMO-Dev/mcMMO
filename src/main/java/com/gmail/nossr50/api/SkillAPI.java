package com.gmail.nossr50.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;

import com.gmail.nossr50.commands.skills.SkillCommand;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.datatypes.skills.SkillType.SkillUseType;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.commands.CommandRegistrationManager;
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
    
    public static SkillType createSkill(String name, Class<? extends SkillManager> managerClass, Class<? extends SkillCommand> commandClass, boolean isChild, Color runescapeColor, SkillUseType skillUseType, List<SecondaryAbility> secondaryAbilities) {
    	SkillType skill = SkillType.createSkill(name, managerClass, commandClass, isChild, runescapeColor, skillUseType, secondaryAbilities);
		CommandRegistrationManager.registerSkillCommandAndPassSkillToConstructor(skill);
    	return skill;
    }
    
    public static SkillType createSkill(String name, Class<? extends SkillManager> managerClass, Class<? extends SkillCommand> commandClass, boolean isChild, Color runescapeColor, SkillUseType skillUseType, AbilityType ability, ToolType tool, List<SecondaryAbility> secondaryAbilities) {
    	SkillType skill = SkillType.createSkill(name, managerClass, commandClass, isChild, runescapeColor, skillUseType, ability, tool, secondaryAbilities);
    	CommandRegistrationManager.registerSkillCommandAndPassSkillToConstructor(skill);
    	return skill;
    }
    
    public static SkillType createSkill(String name, Class<? extends SkillManager> managerClass, Class<? extends SkillCommand> commandClass, boolean isChild, Color runescapeColor, SkillUseType skillUseType, SecondaryAbility... secondaryAbilities) {
    	SkillType skill = SkillType.createSkill(name, managerClass, commandClass, isChild, runescapeColor, skillUseType, ImmutableList.copyOf(secondaryAbilities));
    	CommandRegistrationManager.registerSkillCommandAndPassSkillToConstructor(skill);
    	return skill;
    }
    
    public static SkillType createSkill(String name, Class<? extends SkillManager> managerClass, Class<? extends SkillCommand> commandClass, boolean isChild, Color runescapeColor, SkillUseType skillUseType, AbilityType ability, ToolType tool, SecondaryAbility... secondaryAbilities) {
    	SkillType skill = SkillType.createSkill(name, managerClass, commandClass, isChild, runescapeColor, skillUseType, ability, tool, ImmutableList.copyOf(secondaryAbilities));
    	CommandRegistrationManager.registerSkillCommandAndPassSkillToConstructor(skill);
    	return skill;
    }
    
    public static void loadNewSkills() {
    	SkillType.setUpSkillTypes();
    }
}
