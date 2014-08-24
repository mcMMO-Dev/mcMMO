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

    private static List<String> skillNames = new ArrayList<String>();
    private static List<SkillType> skillList = new ArrayList<SkillType>();

    private static List<SkillType> childSkills = new ArrayList<SkillType>();
    private static List<SkillType> nonChildSkills = new ArrayList<SkillType>();

    private static List<SkillType> combatSkills = new ArrayList<SkillType>();
    private static List<SkillType> gatheringSkills = new ArrayList<SkillType>();
    private static List<SkillType> miscSkills = new ArrayList<SkillType>();
    
	
    public static final SkillType acrobatics 	= createSkill("ACROBATICS" , AcrobaticsManager.class	, AcrobaticsCommand.class	, false, Color.WHITE, SkillUseType.MISC, ImmutableList.of(SecondaryAbility.dodge, SecondaryAbility.gracefullRoll, SecondaryAbility.roll));
    public static final SkillType alchemy	 	= createSkill("ALCHEMY"	 , AlchemyManager.class		, AlchemyCommand.class		, false, Color.FUCHSIA, SkillUseType.MISC, ImmutableList.of(SecondaryAbility.catalysis, SecondaryAbility.concoctions));
    public static final SkillType archery		= createSkill("ARCHERY"	 , ArcheryManager.class		, ArcheryCommand.class		, false, Color.MAROON, SkillUseType.COMBAT, ImmutableList.of(SecondaryAbility.daze, SecondaryAbility.retrieve, SecondaryAbility.skillShot));
    public static final SkillType axes	 		= createSkill("AXES"		 , AxesManager.class		, AxesCommand.class			, false, Color.AQUA, SkillUseType.COMBAT, AbilityType.skullSplitter, ToolType.AXE, ImmutableList.of(SecondaryAbility.armorImpact, SecondaryAbility.axeMastery, SecondaryAbility.criticalHit, SecondaryAbility.greaterImpact));
    public static final SkillType excavation 	= createSkill("EXCAVATION" , ExcavationManager.class	, ExcavationCommand.class	, false, Color.fromRGB(139, 69, 19), SkillUseType.GATHERING, AbilityType.gigaDrillBreaker, ToolType.SHOVEL, ImmutableList.of(SecondaryAbility.excavationTreasureHunter));
    public static final SkillType fishing	 	= createSkill("FISHING"	 , FishingManager.class		, FishingCommand.class		, false, Color.NAVY, SkillUseType.GATHERING, ImmutableList.of(SecondaryAbility.fishermansDiet, SecondaryAbility.fishingTreasureHunter, SecondaryAbility.iceFishing, SecondaryAbility.magicHunter, SecondaryAbility.masterAngler, SecondaryAbility.shake));
    public static final SkillType herbalism 	= createSkill("HERBALISM"  , HerbalismManager.class	, HerbalismCommand.class	, false, Color.GREEN, SkillUseType.GATHERING, AbilityType.greenTerra, ToolType.HOE, ImmutableList.of(SecondaryAbility.farmersDiet, SecondaryAbility.greenThumbPlant, SecondaryAbility.greenThumbBlock, SecondaryAbility.herbalismDoubleDrops, SecondaryAbility.hylianLuck, SecondaryAbility.shroomThumb));
    public static final SkillType mining 		= createSkill("MINING"	 , MiningManager.class		, MiningCommand.class		, false, Color.GRAY, SkillUseType.GATHERING, AbilityType.superBreaker, ToolType.PICKAXE, ImmutableList.of(SecondaryAbility.miningDoubleDrops));
    public static final SkillType repair 		= createSkill("REPAIR"	 , RepairManager.class		, RepairCommand.class		, false, Color.SILVER, SkillUseType.MISC, ImmutableList.of(SecondaryAbility.arcaneForging, SecondaryAbility.repairMastery, SecondaryAbility.superRepair));
    public static final SkillType salvage	 	= createSkill("SALVAGE"	 , SalvageManager.class		, SalvageCommand.class		, true, Color.ORANGE, SkillUseType.MISC, ImmutableList.of(SecondaryAbility.advancedSalvage, SecondaryAbility.arcaneSalvage));
    public static final SkillType smelting	 	= createSkill("SMELTING"	 , SmeltingManager.class	, SmeltingCommand.class		, true, Color.YELLOW, SkillUseType.MISC, ImmutableList.of(SecondaryAbility.fluxMining, SecondaryAbility.fuelEfficiency, SecondaryAbility.secondSmelt));
    public static final SkillType swords 		= createSkill("SWORDS"	 , SwordsManager.class		, SwordsCommand.class		, false, Color.fromRGB(178, 34, 34), SkillUseType.COMBAT, AbilityType.serratedStrikes, ToolType.SWORD, ImmutableList.of(SecondaryAbility.bleed, SecondaryAbility.counter));
    public static final SkillType taming 		= createSkill("TAMING"	 , TamingManager.class		, TamingCommand.class		, false, Color.PURPLE, SkillUseType.COMBAT, ImmutableList.of(SecondaryAbility.beastLore, SecondaryAbility.callOfTheWild, SecondaryAbility.enviromentallyAware, SecondaryAbility.fastFood, SecondaryAbility.gore, SecondaryAbility.holyHound, SecondaryAbility.sharpenedClaws, SecondaryAbility.shockProof, SecondaryAbility.thickFur));
    public static final SkillType unarmed	 	= createSkill("UNARMED"	 , UnarmedManager.class		, UnarmedCommand.class		, false, Color.BLACK, SkillUseType.COMBAT, AbilityType.berserk, ToolType.FISTS, ImmutableList.of(SecondaryAbility.blockCracker, SecondaryAbility.deflect, SecondaryAbility.disarm, SecondaryAbility.ironArm, SecondaryAbility.ironGrip));
    public static final SkillType woodcutting	= createSkill("WOODCUTTING", WoodcuttingManager.class	, WoodcuttingCommand.class	, false, Color.OLIVE, SkillUseType.GATHERING, AbilityType.treeFeller, ToolType.AXE, ImmutableList.of(SecondaryAbility.leafBlower, SecondaryAbility.woodcuttingDoubleDrops));

    
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
    	getSkillList().add(skill);
		if(skill.isChild) {
			getChildSkills().add(skill);
		}
		else {
			getNonChildSkills().add(skill);
		}
		switch(skill.skillUseType) {
			case COMBAT:
				getCombatSkills().add(skill);
				break;
			case GATHERING:
				getGatheringSkills().add(skill);
				break;
			default:
				getMiscSkills().add(skill);
				break;
		}
		getSkillNames().add(skill.name);
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
            for (SkillType type : getSkillList()) {
                if (skillName.equalsIgnoreCase(LocaleLoader.getString(StringUtils.getCapitalized(type.name) + ".SkillName"))) {
                    return type;
                }
            }
        }

        return getSkill(skillName);
    }
    
    public static SkillType getSkill(String skillName) {
        for (SkillType type : getSkillList()) {
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
        for (SkillType type : getSkillList()) {
            if (type.getSkillAbilities().contains(skillAbility)) {
                return type;
            }
        }
        return null;
    }

    public static SkillType byAbility(AbilityType ability) {
        for (SkillType type : getSkillList()) {
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
    	Collections.sort(getSkillNames());
    }

	public static List<String> getSkillNames() {
		return skillNames;
	}

	public static List<SkillType> getSkillList() {
		return skillList;
	}

	public static List<SkillType> getChildSkills() {
		return childSkills;
	}

	public static List<SkillType> getNonChildSkills() {
		return nonChildSkills;
	}

	public static List<SkillType> getCombatSkills() {
		return combatSkills;
	}

	public static List<SkillType> getGatheringSkills() {
		return gatheringSkills;
	}

	public static List<SkillType> getMiscSkills() {
		return miscSkills;
	}
}
