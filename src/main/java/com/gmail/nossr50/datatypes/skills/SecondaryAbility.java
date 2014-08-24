package com.gmail.nossr50.datatypes.skills;

public class SecondaryAbility {
	/* ACROBATICS */
    public static final SecondaryAbility dodge						= new SecondaryAbility("DODGE");
    public static final SecondaryAbility gracefullRoll				= new SecondaryAbility("GRACEFUL_ROLL");
    public static final SecondaryAbility roll						= new SecondaryAbility("ROLL");

    /* ALCHEMY */
    public static final SecondaryAbility catalysis					= new SecondaryAbility("CATALYSIS");
    public static final SecondaryAbility concoctions				= new SecondaryAbility("CONCOCTIONS");

    /* ARCHERY */
    public static final SecondaryAbility daze						= new SecondaryAbility("DAZE");
    public static final SecondaryAbility retrieve					= new SecondaryAbility("RETRIEVE");
    public static final SecondaryAbility skillShot					= new SecondaryAbility("SKILL_SHOT");

    /* Axes */
    public static final SecondaryAbility armorImpact				= new SecondaryAbility("ARMOR_IMPACT");
    public static final SecondaryAbility axeMastery					= new SecondaryAbility("AXE_MASTERY");
    public static final SecondaryAbility criticalHit				= new SecondaryAbility("CRITICAL_HIT");
    public static final SecondaryAbility greaterImpact				= new SecondaryAbility("GREATER_IMPACT");

    /* Excavation */
    public static final SecondaryAbility excavationTreasureHunter	= new SecondaryAbility("TREASURE_HUNTER");

    /* Fishing */
    public static final SecondaryAbility fishermansDiet				= new SecondaryAbility("FISHERMANS_DIET");
    public static final SecondaryAbility fishingTreasureHunter		= new SecondaryAbility("TREASURE_HUNTER");
    public static final SecondaryAbility iceFishing					= new SecondaryAbility("ICE_FISHING");
    public static final SecondaryAbility magicHunter				= new SecondaryAbility("MAGIC_HUNTER");
    public static final SecondaryAbility masterAngler				= new SecondaryAbility("MASTER_ANGLER");
    public static final SecondaryAbility shake						= new SecondaryAbility("SHAKE");

    /* Herbalism */
    public static final SecondaryAbility farmersDiet				= new SecondaryAbility("FARMERS_DIET");
    public static final SecondaryAbility greenThumbPlant			= new SecondaryAbility("GREEN_THUMB");
    public static final SecondaryAbility greenThumbBlock			= new SecondaryAbility("GREEN_THUMB");
    public static final SecondaryAbility herbalismDoubleDrops		= new SecondaryAbility("DOUBLE_DROPS");
    public static final SecondaryAbility hylianLuck					= new SecondaryAbility("HYLIAN_LUCK");
    public static final SecondaryAbility shroomThumb				= new SecondaryAbility("SHROOM_THUMB");

    /* Mining */ 
    public static final SecondaryAbility miningDoubleDrops			= new SecondaryAbility("DOUBLE_DROPS");

    /* Repair */
    public static final SecondaryAbility arcaneForging				= new SecondaryAbility("ARCANE_FORGING");
    public static final SecondaryAbility repairMastery				= new SecondaryAbility("REPAIR_MASTERY");
    public static final SecondaryAbility superRepair				= new SecondaryAbility("SUPER_REPAIR");

    /* Salvage */
    public static final SecondaryAbility advancedSalvage			= new SecondaryAbility("ADVANCED_SALVAGE");
    public static final SecondaryAbility arcaneSalvage				= new SecondaryAbility("ARCANE_SALVAGE");

    /* Smelting */
    public static final SecondaryAbility fluxMining					= new SecondaryAbility("FLUX_MINING");
    public static final SecondaryAbility fuelEfficiency				= new SecondaryAbility("FUEL_EFFICIENCY");
    public static final SecondaryAbility secondSmelt				= new SecondaryAbility("SECOND_SMELT");

    /* Swords */
    public static final SecondaryAbility bleed						= new SecondaryAbility("BLEED");
    public static final SecondaryAbility counter					= new SecondaryAbility("COUNTER");

    /* Taming */
    public static final SecondaryAbility beastLore					= new SecondaryAbility("BEAST_LORE");
    public static final SecondaryAbility callOfTheWild				= new SecondaryAbility("CALL_OF_THE_WILD");
    public static final SecondaryAbility enviromentallyAware		= new SecondaryAbility("ENVIROMENTALLY_AWARE");
    public static final SecondaryAbility fastFood					= new SecondaryAbility("FAST_FOOD");
    public static final SecondaryAbility gore						= new SecondaryAbility("GORE");
    public static final SecondaryAbility holyHound					= new SecondaryAbility("HOLY_HOUND");
    public static final SecondaryAbility sharpenedClaws				= new SecondaryAbility("SHARPENED_CLAWS");
    public static final SecondaryAbility shockProof					= new SecondaryAbility("SHOCK_PROOF");
    public static final SecondaryAbility thickFur					= new SecondaryAbility("THICK_FUR");

    /* Unarmed */
    public static final SecondaryAbility blockCracker				= new SecondaryAbility("BLOCK_CRACKER");
    public static final SecondaryAbility deflect					= new SecondaryAbility("DEFLECT");
    public static final SecondaryAbility disarm						= new SecondaryAbility("DISARM");
    public static final SecondaryAbility ironArm					= new SecondaryAbility("IRON_ARM");
    public static final SecondaryAbility ironGrip					= new SecondaryAbility("IRON_GRIP");

    /* Woodcutting */
    public static final SecondaryAbility leafBlower					= new SecondaryAbility("LEAF_BLOWER");
    public static final SecondaryAbility woodcuttingDoubleDrops		= new SecondaryAbility("DOUBLE_DROPS");
    
    private String name;
    
    private SecondaryAbility(String name) {
    	this.name = name;
    }

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
