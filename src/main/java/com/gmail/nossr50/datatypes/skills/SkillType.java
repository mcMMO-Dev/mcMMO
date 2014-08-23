package com.gmail.nossr50.datatypes.skills;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.skills.AcrobaticsCommand;
import com.gmail.nossr50.commands.skills.AlchemyCommand;
import com.gmail.nossr50.commands.skills.ArcheryCommand;
import com.gmail.nossr50.commands.skills.AxesCommand;
import com.gmail.nossr50.commands.skills.ExcavationCommand;
import com.gmail.nossr50.commands.skills.FishingCommand;
import com.gmail.nossr50.commands.skills.HerbalismCommand;
import com.gmail.nossr50.commands.skills.MiningCommand;
import com.gmail.nossr50.commands.skills.RepairCommand;
import com.gmail.nossr50.commands.skills.SalvageCommand;
import com.gmail.nossr50.commands.skills.SkillCommand;
import com.gmail.nossr50.commands.skills.SmeltingCommand;
import com.gmail.nossr50.commands.skills.SwordsCommand;
import com.gmail.nossr50.commands.skills.TamingCommand;
import com.gmail.nossr50.commands.skills.UnarmedCommand;
import com.gmail.nossr50.commands.skills.WoodcuttingCommand;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.acrobatics.AcrobaticsManager;
import com.gmail.nossr50.skills.alchemy.AlchemyManager;
import com.gmail.nossr50.skills.archery.ArcheryManager;
import com.gmail.nossr50.skills.axes.AxesManager;
import com.gmail.nossr50.skills.excavation.ExcavationManager;
import com.gmail.nossr50.skills.fishing.FishingManager;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.repair.RepairManager;
import com.gmail.nossr50.skills.salvage.SalvageManager;
import com.gmail.nossr50.skills.smelting.SmeltingManager;
import com.gmail.nossr50.skills.swords.SwordsManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;
import com.gmail.nossr50.skills.woodcutting.WoodcuttingManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.google.common.collect.ImmutableList;

public class SkillType {
	public enum SkillUseType {
		COMBAT,
		GATHERING,
		MISC
	}

    public static List<String> skillNames = new ArrayList<String>();
    public static List<SkillType> skillList = new ArrayList<SkillType>();

    public static List<SkillType> childSkills = new ArrayList<SkillType>();
    public static List<SkillType> nonChildSkills = new ArrayList<SkillType>();

    public static List<SkillType> combatSkills = new ArrayList<SkillType>();
    public static List<SkillType> gatheringSkills = new ArrayList<SkillType>();
    public static List<SkillType> miscSkills = new ArrayList<SkillType>();
    
	
    public static final SkillType acrobatics 	= createSkill("ACROBATICS" , AcrobaticsManager.class	, AcrobaticsCommand.class	, false, Color.WHITE, SkillUseType.MISC, ImmutableList.of(SecondaryAbility.DODGE, SecondaryAbility.GRACEFUL_ROLL, SecondaryAbility.ROLL));
    public static final SkillType alchemy	 	= createSkill("ALCHEMY"	 , AlchemyManager.class		, AlchemyCommand.class		, false, Color.FUCHSIA, SkillUseType.MISC, ImmutableList.of(SecondaryAbility.CATALYSIS, SecondaryAbility.CONCOCTIONS));
    public static final SkillType archery		= createSkill("ARCHERY"	 , ArcheryManager.class		, ArcheryCommand.class		, false, Color.MAROON, SkillUseType.COMBAT, ImmutableList.of(SecondaryAbility.DAZE, SecondaryAbility.RETRIEVE, SecondaryAbility.SKILL_SHOT));
    public static final SkillType axes	 		= createSkill("AXES"		 , AxesManager.class		, AxesCommand.class			, false, Color.AQUA, SkillUseType.COMBAT, AbilityType.SKULL_SPLITTER, ToolType.AXE, ImmutableList.of(SecondaryAbility.ARMOR_IMPACT, SecondaryAbility.AXE_MASTERY, SecondaryAbility.CRITICAL_HIT, SecondaryAbility.GREATER_IMPACT));
    public static final SkillType excavation 	= createSkill("EXCAVATION" , ExcavationManager.class	, ExcavationCommand.class	, false, Color.fromRGB(139, 69, 19), SkillUseType.GATHERING, AbilityType.GIGA_DRILL_BREAKER, ToolType.SHOVEL, ImmutableList.of(SecondaryAbility.EXCAVATION_TREASURE_HUNTER));
    public static final SkillType fishing	 	= createSkill("FISHING"	 , FishingManager.class		, FishingCommand.class		, false, Color.NAVY, SkillUseType.GATHERING, ImmutableList.of(SecondaryAbility.FISHERMANS_DIET, SecondaryAbility.FISHING_TREASURE_HUNTER, SecondaryAbility.ICE_FISHING, SecondaryAbility.MAGIC_HUNTER, SecondaryAbility.MASTER_ANGLER, SecondaryAbility.SHAKE));
    public static final SkillType herbalism 	= createSkill("HERBALISM"  , HerbalismManager.class	, HerbalismCommand.class	, false, Color.GREEN, SkillUseType.GATHERING, AbilityType.GREEN_TERRA, ToolType.HOE, ImmutableList.of(SecondaryAbility.FARMERS_DIET, SecondaryAbility.GREEN_THUMB_PLANT, SecondaryAbility.GREEN_THUMB_BLOCK, SecondaryAbility.HERBALISM_DOUBLE_DROPS, SecondaryAbility.HYLIAN_LUCK, SecondaryAbility.SHROOM_THUMB));
    public static final SkillType mining 		= createSkill("MINING"	 , MiningManager.class		, MiningCommand.class		, false, Color.GRAY, SkillUseType.GATHERING, AbilityType.SUPER_BREAKER, ToolType.PICKAXE, ImmutableList.of(SecondaryAbility.MINING_DOUBLE_DROPS));
    public static final SkillType repair 		= createSkill("REPAIR"	 , RepairManager.class		, RepairCommand.class		, false, Color.SILVER, SkillUseType.MISC, ImmutableList.of(SecondaryAbility.ARCANE_FORGING, SecondaryAbility.REPAIR_MASTERY, SecondaryAbility.SUPER_REPAIR));
    public static final SkillType salvage	 	= createSkill("SALVAGE"	 , SalvageManager.class		, SalvageCommand.class		, true, Color.ORANGE, SkillUseType.MISC, ImmutableList.of(SecondaryAbility.ADVANCED_SALVAGE, SecondaryAbility.ARCANE_SALVAGE));
    public static final SkillType smelting	 	= createSkill("SMELTING"	 , SmeltingManager.class	, SmeltingCommand.class		, true, Color.YELLOW, SkillUseType.MISC, ImmutableList.of(SecondaryAbility.FLUX_MINING, SecondaryAbility.FUEL_EFFICIENCY, SecondaryAbility.SECOND_SMELT));
    public static final SkillType swords 		= createSkill("SWORDS"	 , SwordsManager.class		, SwordsCommand.class		, false, Color.fromRGB(178, 34, 34), SkillUseType.COMBAT, AbilityType.SERRATED_STRIKES, ToolType.SWORD, ImmutableList.of(SecondaryAbility.BLEED, SecondaryAbility.COUNTER));
    public static final SkillType taming 		= createSkill("TAMING"	 , TamingManager.class		, TamingCommand.class		, false, Color.PURPLE, SkillUseType.COMBAT, ImmutableList.of(SecondaryAbility.BEAST_LORE, SecondaryAbility.CALL_OF_THE_WILD, SecondaryAbility.ENVIROMENTALLY_AWARE, SecondaryAbility.FAST_FOOD, SecondaryAbility.GORE, SecondaryAbility.HOLY_HOUND, SecondaryAbility.SHARPENED_CLAWS, SecondaryAbility.SHOCK_PROOF, SecondaryAbility.THICK_FUR));
    public static final SkillType unarmed	 	= createSkill("UNARMED"	 , UnarmedManager.class		, UnarmedCommand.class		, false, Color.BLACK, SkillUseType.COMBAT, AbilityType.BERSERK, ToolType.FISTS, ImmutableList.of(SecondaryAbility.BLOCK_CRACKER, SecondaryAbility.DEFLECT, SecondaryAbility.DISARM, SecondaryAbility.IRON_ARM, SecondaryAbility.IRON_GRIP));
    public static final SkillType woodcutting	= createSkill("WOODCUTTING", WoodcuttingManager.class	, WoodcuttingCommand.class	, false, Color.OLIVE, SkillUseType.GATHERING, AbilityType.TREE_FELLER, ToolType.AXE, ImmutableList.of(SecondaryAbility.LEAF_BLOWER, SecondaryAbility.WOODCUTTING_DOUBLE_DROPS));

    
    private String name;
    private Class<? extends SkillManager> managerClass;
    private Class<? extends SkillCommand> commandClass;
    private boolean isChild;
    private Color runescapeColor;
    private SkillUseType skillUseType;
    private AbilityType ability;
    private ToolType tool;
    private List<SecondaryAbility> secondaryAbilities;
    
    public static SkillType createSkill(String name, Class<? extends SkillManager> managerClass, Class<? extends SkillCommand> commandClass, boolean isChild, Color runescapeColor, SkillUseType skillUseType, List<SecondaryAbility> secondaryAbilities) {
    	return createSkill(name, managerClass, commandClass, isChild, runescapeColor, skillUseType, null, null, secondaryAbilities);
    }
    
    public static SkillType createSkill(String name, Class<? extends SkillManager> managerClass, Class<? extends SkillCommand> commandClass, boolean isChild, Color runescapeColor, SkillUseType skillUseType, AbilityType ability, ToolType tool, List<SecondaryAbility> secondaryAbilities) {
    	SkillType skill = new SkillType(name, managerClass, commandClass, isChild, runescapeColor, skillUseType, ability, tool, secondaryAbilities);
    	skillList.add(skill);
		if(skill.isChild) {
			childSkills.add(skill);
		}
		else {
			nonChildSkills.add(skill);
		}
		switch(skill.skillUseType) {
			case COMBAT:
				combatSkills.add(skill);
				break;
			case GATHERING:
				gatheringSkills.add(skill);
				break;
			default:
				miscSkills.add(skill);
				break;
		}
		skillNames.add(skill.name);
    	return skill;
    }
    
    private SkillType(String name, Class<? extends SkillManager> managerClass, Class<? extends SkillCommand> commandClass, boolean isChild, Color runescapeColor, SkillUseType skillUseType, List<SecondaryAbility> secondaryAbilities) {
        this(name, managerClass, commandClass, isChild, runescapeColor, skillUseType, null, null, secondaryAbilities);
    }

    private SkillType(String name, Class<? extends SkillManager> managerClass, Class<? extends SkillCommand> commandClass, boolean isChild, Color runescapeColor, SkillUseType skillUseType, AbilityType ability, ToolType tool, List<SecondaryAbility> secondaryAbilities) {
    	this.name = name;
    	this.managerClass = managerClass;
    	this.commandClass = commandClass;
    	this.isChild = isChild;
        this.runescapeColor = runescapeColor;
        this.skillUseType = skillUseType;
        this.ability = ability;
        this.tool = tool;
        this.secondaryAbilities = secondaryAbilities;
    }

    public Class<? extends SkillManager> getManagerClass() {
        return managerClass;
    }

    public Class<? extends SkillCommand> getCommandClass() {
        return commandClass;
    }

    public AbilityType getAbility() {
        return ability;
    }

    /**
     * Get the max level of this skill.
     *
     * @return the max level of this skill
     */
    public int getMaxLevel() {
        return Config.getInstance().getLevelCap(this);
    }

    public boolean getPVPEnabled() {
        return Config.getInstance().getPVPEnabled(this);
    }

    public boolean getPVEEnabled() {
        return Config.getInstance().getPVEEnabled(this);
    }

    public boolean getDoubleDropsDisabled() {
        return Config.getInstance().getDoubleDropsDisabled(this);
    }

    public boolean getHardcoreStatLossEnabled() {
        return Config.getInstance().getHardcoreStatLossEnabled(this);
    }

    public void setHardcoreStatLossEnabled(boolean enable) {
        Config.getInstance().setHardcoreStatLossEnabled(this, enable);
    }

    public boolean getHardcoreVampirismEnabled() {
        return Config.getInstance().getHardcoreVampirismEnabled(this);
    }

    public void setHardcoreVampirismEnabled(boolean enable) {
        Config.getInstance().setHardcoreVampirismEnabled(this, enable);
    }

    public ToolType getTool() {
        return tool;
    }

    public List<SecondaryAbility> getSkillAbilities() {
        return secondaryAbilities;
    }

    public double getXpModifier() {
        return ExperienceConfig.getInstance().getFormulaSkillModifier(this);
    }

    public static SkillType getSkillFromLocalized(String skillName) {
        if (!Config.getInstance().getLocale().equalsIgnoreCase("en_US")) {
            for (SkillType type : skillList) {
                if (skillName.equalsIgnoreCase(LocaleLoader.getString(StringUtils.getCapitalized(type.name) + ".SkillName"))) {
                    return type;
                }
            }
        }

        return getSkill(skillName);
    }
    
    public static SkillType getSkill(String skillName) {
        for (SkillType type : skillList) {
            if (type.name.equalsIgnoreCase(skillName)) {
                return type;
            }
        }

        if (!skillName.equalsIgnoreCase("all")) {
            mcMMO.p.getLogger().warning("Invalid mcMMO skill (" + skillName + ")"); //TODO: Localize
        }

        return null;
    }

    public boolean isChildSkill() {
        return isChild;
    }

    public static SkillType bySecondaryAbility(SecondaryAbility skillAbility) {
        for (SkillType type : skillList) {
            if (type.getSkillAbilities().contains(skillAbility)) {
                return type;
            }
        }
        return null;
    }

    public static SkillType byAbility(AbilityType ability) {
        for (SkillType type : skillList) {
            if (type.getAbility() == ability) {
                return type;
            }
        }

        return null;
    }

    public String getName()
    {
    	return this.name;
    }
    
    @Override
    public String toString()
    {
    	return getName();
    }
    
    public String getLocalizedName() {
        return Config.getInstance().getLocale().equalsIgnoreCase("en_US") ? StringUtils.getCapitalized(this.toString()) : StringUtils.getCapitalized(LocaleLoader.getString(StringUtils.getCapitalized(this.toString()) + ".SkillName"));
    }

    public boolean getPermissions(Player player) {
        return Permissions.skillEnabled(player, this);
    }

    public void celebrateLevelUp(Player player) {
        ParticleEffectUtils.fireworkParticleShower(player, runescapeColor);
    }

    public boolean shouldProcess(Entity target) {
        return (target instanceof Player || (target instanceof Tameable && ((Tameable) target).isTamed())) ? getPVPEnabled() : getPVEEnabled();
    }
    
    public static void setUpSkillTypes()
    {
    	Collections.sort(skillNames);
    }
}
