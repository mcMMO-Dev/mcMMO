package com.gmail.nossr50.datatypes.skills;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.StringUtils;

public enum SubSkillType {
    /* !! Warning -- Do not let subskills share a name with any existing PrimarySkillType as it will clash with the static import !! */

    /* ACROBATICS */
    ACROBATICS_DODGE,
    ACROBATICS_ROLL,

    /* ALCHEMY */
    ALCHEMY_CATALYSIS(1),
    ALCHEMY_CONCOCTIONS(8),

    /* ARCHERY */
    ARCHERY_DAZE,
    ARCHERY_ARROW_RETRIEVAL,
    ARCHERY_SKILL_SHOT(20),

    /* Axes */
    AXES_ARMOR_IMPACT(20),
    AXES_AXE_MASTERY(4),
    AXES_CRITICAL_STRIKES,
    AXES_GREATER_IMPACT,
    AXES_SKULL_SPLITTER(1),

    /* Excavation */
    EXCAVATION_TREASURE_HUNTER,
    EXCAVATION_GIGA_DRILL_BREAKER(0),

    /* Fishing */
    FISHING_FISHERMANS_DIET,
    FISHING_TREASURE_HUNTER,
    FISHING_ICE_FISHING,
    FISHING_MAGIC_HUNTER,
    FISHING_MASTER_ANGLER,
    FISHING_SHAKE,

    /* Herbalism */
    HERBALISM_FARMERS_DIET,
    HERBALISM_GREEN_THUMB,
    HERBALISM_DOUBLE_DROPS,
    HERBALISM_HYLIAN_LUCK,
    HERBALISM_SHROOM_THUMB,
    HERBALISM_GREEN_TERRA,

    /* Mining */
    MINING_DOUBLE_DROPS,
    MINING_SUPER_BREAKER(1),
    MINING_BLAST_MINING(8),
    MINING_BIGGER_BOMBS,
    MINING_DEMOLITIONS_EXPERTISE,

    /* Repair */
    REPAIR_ARCANE_FORGING,
    REPAIR_REPAIR_MASTERY,
    REPAIR_SUPER_REPAIR,

    /* Salvage */
    SALVAGE_ADVANCED_SALVAGE,
    SALVAGE_ARCANE_SALVAGE,
    SALVAGE_UNDERSTANDING_THE_ART(8),

    /* Smelting */
    SMELTING_FLUX_MINING,
    SMELTING_FUEL_EFFICIENCY,
    SMELTING_SECOND_SMELT,
    SMELTING_UNDERSTANDING_THE_ART(8),

    /* Swords */
    SWORDS_BLEED,
    SWORDS_COUNTER_ATTACK,
    SWORDS_SERRATED_STRIKES(1),

    /* Taming */
    TAMING_BEAST_LORE,
    TAMING_CALL_OF_THE_WILD,
    TAMING_ENVIRONMENTALLY_AWARE(1),
    TAMING_FAST_FOOD_SERVICE(1),
    TAMING_GORE,
    TAMING_HOLY_HOUND(1),
    TAMING_SHARPENED_CLAWS(1),
    TAMING_SHOCK_PROOF(1),
    TAMING_THICK_FUR(1),
    TAMING_PUMMEL,

    /* Unarmed */
    UNARMED_BLOCK_CRACKER,
    UNARMED_ARROW_DEFLECT,
    UNARMED_DISARM,
    UNARMED_IRON_ARM_STYLE,
    UNARMED_IRON_GRIP,
    UNARMED_BERSERK(1),

    /* Woodcutting */
    WOODCUTTING_TREE_FELLER(5),
    WOODCUTTING_LEAF_BLOWER(3),
    WOODCUTTING_BARK_SURGEON(3),
    WOODCUTTING_NATURES_BOUNTY(3),
    WOODCUTTING_SPLINTER(3),
    WOODCUTTING_HARVEST_LUMBER(1);

    private final int numRanks;
    //TODO: SuperAbilityType should also contain flags for active by default? Not sure if it should work that way.

    /**
     * If our SubSkillType has more than 1 rank define it
     * @param numRanks The number of ranks our SubSkillType has
     */
    SubSkillType(int numRanks)
    {
        this.numRanks = numRanks;
    }

    SubSkillType()
    {
        this.numRanks = 0;
    }

    public int getNumRanks()
    {
        return numRanks;
    }

    /**
     * !!! This relies on the immutable lists in PrimarySkillType being populated !!!
     * If we add skills, those immutable lists need to be updated
     * @return
     */
    public PrimarySkillType getParentSkill() { return PrimarySkillType.bySecondaryAbility(this); }

    /**
     * Returns the root address for this skill in the advanced.yml file
     * @return the root address for this skill in advanced.yml
     */
    public String getAdvConfigAddress() {
        return "Skills." + StringUtils.getCapitalized(getParentSkill().toString()) + "." + getConfigName(toString());
    }

    /**
     * Returns the root address for this skill in the rankskills.yml file
     * @return the root address for this skill in rankskills.yml
     */
    public String getRankConfigAddress() {
        return StringUtils.getCapitalized(getParentSkill().toString()) + "." + getConfigName(toString());
    }

    /**
     * Get the string representation of the permission node for this subskill
     * @return the permission node for this subskill
     */
    public String getPermissionNodeAddress()
    {
        //TODO: This could be optimized
        return "mcmmo.ability." + getParentSkill().toString().toLowerCase() + "." + getConfigName(toString()).toLowerCase();
    }

    /**
     * Returns the name of the skill as it is used in advanced.yml and other config files
     * @return the yaml identifier for this skill
     */
    private String getConfigName(String subSkillName) {
        /*
         * Our ENUM constants name is something like PREFIX_SUB_SKILL_NAME
         * We need to remove the prefix and then format the subskill to follow the naming conventions of our yaml configs
         *
         * So this method uses this kind of formatting
         * "PARENTSKILL_COOL_SUBSKILL_ULTRA" -> "Cool Subskill Ultra" - > "CoolSubskillUltra"
         *
         */


        /*
         * Find where to begin our substring (after the prefix)
         */
        String endResult = "";
        int subStringIndex = getSubStringIndex(subSkillName);

        /*
         * Split the string up so we can capitalize each part
         */
        String subskillNameWithoutPrefix = subSkillName.substring(subStringIndex);
        if(subskillNameWithoutPrefix.contains("_"))
        {
            String splitStrings[] = subskillNameWithoutPrefix.split("_");

            for(String string : splitStrings)
            {
                endResult += StringUtils.getCapitalized(string);
            }
        } else {
            endResult += StringUtils.getCapitalized(subskillNameWithoutPrefix);
        }

        return endResult;
    }

    /**
     * Returns the name of the parent skill from the Locale file
     * @return The parent skill as defined in the locale
     */
    public String getParentNiceNameLocale()
    {
        return LocaleLoader.getString(StringUtils.getCapitalized(getParentSkill().toString())+".SkillName");
    }

    /**
     * Gets the "nice" name of the subskill without spaces
     * @param subSkillType target subskill
     * @return the "nice" name without spaces
     */
    public String getNiceNameNoSpaces(SubSkillType subSkillType)
    {
        return getConfigName(subSkillType.toString());
    }

    /**
     * This finds the substring index for our SubSkillType's name after its parent name prefix
     * @param subSkillName The name to process
     * @return The value of the substring index after our parent's prefix
     */
    private int getSubStringIndex(String subSkillName) {
        char[] enumNameCharArray = subSkillName.toCharArray();
        int subStringIndex = 0;

        //Find where to start our substring for this constants name
        for (int i = 0; i < enumNameCharArray.length; i++) {
            if (enumNameCharArray[i] == '_') {
                subStringIndex = i + 1; //Start the substring after this char

                break;
            }
        }
        return subStringIndex;
    }

    public String getLocaleKeyRoot()
    {
        return StringUtils.getCapitalized(getParentSkill().toString())+".SubSkill."+getConfigName(toString());
    }

    public String getLocaleName()
    {
        return LocaleLoader.getString(getLocaleKeyRoot()+".Name");
    }

    public String getLocaleDescription()
    {
        return LocaleLoader.getString(getLocaleKeyRoot()+".Description");
    }
}
