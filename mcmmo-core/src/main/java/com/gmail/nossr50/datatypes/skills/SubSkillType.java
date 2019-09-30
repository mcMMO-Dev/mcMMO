package com.gmail.nossr50.datatypes.skills;

import com.gmail.nossr50.config.HOCONUtil;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.StringUtils;

public enum SubSkillType {
    /* !! Warning -- Do not let subskills share a name with any existing PrimarySkillType as it will clash with the static import !! */

    /* ACROBATICS */
    ACROBATICS_DODGE(1),
    ACROBATICS_ROLL,

    /* ALCHEMY */

    /* ARCHERY */
    ARCHERY_ARROW_RETRIEVAL(1),
    ARCHERY_DAZE,
    ARCHERY_SKILL_SHOT(20),
    ARCHERY_ARCHERY_LIMIT_BREAK(10),

    /* Axes */
    AXES_ARMOR_IMPACT(20),
    AXES_AXE_MASTERY(4),
    AXES_AXES_LIMIT_BREAK(10),
    AXES_CRITICAL_STRIKES(1),
    AXES_GREATER_IMPACT(1),
    AXES_SKULL_SPLITTER(1),

    /* Excavation */
    EXCAVATION_ARCHAEOLOGY(8),
    EXCAVATION_GIGA_DRILL_BREAKER(1),

    /* Fishing */
    FISHING_FISHERMANS_DIET(5),
    FISHING_ICE_FISHING(1),
    FISHING_MAGIC_HUNTER(1),
    FISHING_MASTER_ANGLER(1),
    FISHING_TREASURE_HUNTER(8),
    FISHING_INNER_PEACE(3),
    FISHING_SHAKE(1),

    /* Herbalism */
    HERBALISM_DOUBLE_DROPS(1),
    HERBALISM_FARMERS_DIET(5),
    HERBALISM_GREEN_TERRA(1),
    HERBALISM_GREEN_THUMB(4),
    HERBALISM_HYLIAN_LUCK,
    HERBALISM_SHROOM_THUMB,

    /* Mining */
    MINING_BIGGER_BOMBS(1),
    MINING_BLAST_MINING(8),
    MINING_DEMOLITIONS_EXPERTISE(1),
    MINING_DOUBLE_DROPS(1),
    MINING_SUPER_BREAKER(1),

    /* Repair */
    REPAIR_ARCANE_FORGING(8),
    REPAIR_REPAIR_MASTERY(1),
    REPAIR_SUPER_REPAIR(1),

    /* Salvage */
    SALVAGE_SCRAP_COLLECTOR(8),
    SALVAGE_ARCANE_SALVAGE(8),

    /* Smelting */
    SMELTING_FUEL_EFFICIENCY(3),
    SMELTING_SECOND_SMELT,
    SMELTING_UNDERSTANDING_THE_ART(8),

    /* Swords */
    SWORDS_COUNTER_ATTACK(1),
    SWORDS_RUPTURE(4),
    SWORDS_SERRATED_STRIKES(1),
    SWORDS_STAB(2),
    SWORDS_SWORDS_LIMIT_BREAK(10),

    /* Taming */
    TAMING_BEAST_LORE(1),
    TAMING_CALL_OF_THE_WILD(1),
    TAMING_ENVIRONMENTALLY_AWARE(1),
    TAMING_FAST_FOOD_SERVICE(1),
    TAMING_GORE(1),
    TAMING_HOLY_HOUND(1),
    TAMING_PUMMEL(1),
    TAMING_SHARPENED_CLAWS(1),
    TAMING_SHOCK_PROOF(1),
    TAMING_THICK_FUR(1),

    /* Unarmed */
    UNARMED_ARROW_DEFLECT(1),
    UNARMED_BERSERK(1),
    UNARMED_BLOCK_CRACKER,
    UNARMED_DISARM(1),
    UNARMED_IRON_ARM_STYLE(5),
    UNARMED_IRON_GRIP(1),
    UNARMED_UNARMED_LIMIT_BREAK(10),

    /* Woodcutting */
    /*    WOODCUTTING_BARK_SURGEON(3),*/
    WOODCUTTING_HARVEST_LUMBER(1),
    WOODCUTTING_LEAF_BLOWER(1),
    /*    WOODCUTTING_NATURES_BOUNTY(3),
        WOODCUTTING_SPLINTER(3),*/
    WOODCUTTING_TREE_FELLER(1);

    private final int numRanks;
    //TODO: SuperAbilityType should also contain flags for active by default? Not sure if it should work that way.

    /**
     * If our SubSkillType has more than 1 rank define it
     *
     * @param numRanks The number of ranks our SubSkillType has
     */
    SubSkillType(int numRanks) {
        this.numRanks = numRanks;
    }

    SubSkillType() {
        this.numRanks = 0;
    }

    public int getNumRanks() {
        return numRanks;
    }

    /**
     * !!! This relies on the immutable lists in PrimarySkillType being populated !!!
     * If we add skills, those immutable lists need to be updated
     *
     * @return
     */
    public PrimarySkillType getParentSkill(mcMMO pluginRef) {
        return pluginRef.getSkillTools().getPrimarySkillBySubSkill(this);
    }

    /**
     * Get the string representation of the permission node for this subskill
     *
     * @return the permission node for this subskill
     */
    public String getPermissionNodeAddress(mcMMO pluginRef) {
        //TODO: This could be optimized
        return "mcmmo.ability." + getParentSkill(pluginRef).toString().toLowerCase() + "." + getConfigName(toString()).toLowerCase();
    }

    /**
     * Returns the name of the sub-skill as it is used in our HOCON configs
     *
     * @return the yaml identifier for this skill
     */
    public String getHoconFriendlyConfigName() {
        /*
         * Our ENUM constants name is something like PREFIX_SUB_SKILL_NAME
         * We need to remove the prefix and then format the subskill to follow the naming conventions of our yaml configs
         *
         * So this method uses this kind of formatting
         * "PARENTSKILL_COOL_SUBSKILL_ULTRA" -> "Cool Subskill Ultra" - > "Cool-Subskill-Ultra"
         *
         */

        /*
         * Find where to begin our substring (after the prefix)
         */
        int subStringIndex = getSubStringIndex(toString());

        /*
         * Split the string up so we can capitalize each part
         */
        String withoutPrefix = toString().substring(subStringIndex);

        //Grab the HOCON friendly version of the string and return it
        return HOCONUtil.serializeENUMName(withoutPrefix);
    }

    /**
     * Returns the name of the skill as it is used in advanced.yml and other config files
     *
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
        StringBuilder endResult = new StringBuilder();
        int subStringIndex = getSubStringIndex(subSkillName);

        /*
         * Split the string up so we can capitalize each part
         */
        String withoutPrefix = subSkillName.substring(subStringIndex);
        if (withoutPrefix.contains("_")) {
            String[] splitStrings = withoutPrefix.split("_");

            for (String string : splitStrings) {
                endResult.append(StringUtils.getCapitalized(string));
            }
        } else {
            endResult.append(StringUtils.getCapitalized(withoutPrefix));
        }

        return endResult.toString();
    }

    public String getWikiName(String subSkillName) {
        /*
         * Find where to begin our substring (after the prefix)
         */
        StringBuilder endResult = new StringBuilder();
        int subStringIndex = getSubStringIndex(subSkillName);

        /*
         * Split the string up so we can capitalize each part
         */
        String subskillNameWithoutPrefix = subSkillName.substring(subStringIndex);
        if (subskillNameWithoutPrefix.contains("_")) {
            String[] splitStrings = subskillNameWithoutPrefix.split("_");

            for (int i = 0; i < splitStrings.length; i++) {
                if (i + 1 >= splitStrings.length)
                    endResult.append(StringUtils.getCapitalized(splitStrings[i]));
                else {
                    endResult.append(StringUtils.getCapitalized(splitStrings[i]));
                    endResult.append("_");
                }
            }
        } else {
            endResult.append(StringUtils.getCapitalized(subskillNameWithoutPrefix));
        }

        return endResult.toString();
    }

    /**
     * Returns the name of the parent skill from the LocaleManager file
     *
     * @return The parent skill as defined in the locale
     */
    public String getParentNiceNameLocale(mcMMO pluginRef) {
        return pluginRef.getLocaleManager().getString(StringUtils.getCapitalized(getParentSkill(pluginRef).toString()) + ".SkillName");
    }

    /**
     * Gets the "nice" name of the subskill without spaces
     *
     * @param subSkillType target subskill
     * @return the "nice" name without spaces
     */
    public String getNiceNameNoSpaces(SubSkillType subSkillType) {
        return getConfigName(subSkillType.toString());
    }

    /**
     * This finds the substring index for our SubSkillType's name after its parent name prefix
     *
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

    public String getLocaleKeyRoot(mcMMO pluginRef) {
        return StringUtils.getCapitalized(getParentSkill(pluginRef).toString()) + ".SubSkill." + getConfigName(toString());
    }

    public String getLocaleName(mcMMO pluginRef) {
        return getFromLocaleSubAddress(pluginRef,".Name");
    }

    public String getLocaleDescription(mcMMO pluginRef) {
        return getFromLocaleSubAddress(pluginRef,".Description");
    }

    public String getLocaleStatDescription(mcMMO pluginRef) {
        return getFromLocaleSubAddress(pluginRef,".Stat");
    }

    public String getLocaleKeyStatDescription(mcMMO pluginRef) {
        return getLocaleKeyFromSubAddress(pluginRef, ".Stat");
    }

    public String getLocaleStatExtraDescription(mcMMO pluginRef) {
        return getFromLocaleSubAddress(pluginRef,".Stat.Extra");
    }

    public String getLocaleKeyStatExtraDescription(mcMMO pluginRef) {
        return getLocaleKeyFromSubAddress(pluginRef, ".Stat.Extra");
    }

    public String getLocaleStat(mcMMO pluginRef, String... vars) {
        String statMsg = pluginRef.getLocaleManager().getString("Ability.Generic.Template", (Object[]) vars);
        return statMsg;
    }

    public String getCustomLocaleStat(mcMMO pluginRef, String... vars) {
        String statMsg = pluginRef.getLocaleManager().getString("Ability.Generic.Template.Custom", (Object[]) vars);
        return statMsg;
    }

    private String getFromLocaleSubAddress(mcMMO pluginRef, String s) {
        return pluginRef.getLocaleManager().getString(getLocaleKeyRoot(pluginRef) + s);
    }

    private String getLocaleKeyFromSubAddress(mcMMO pluginRef, String subAddress) {
        return getLocaleKeyRoot(pluginRef) + subAddress;
    }
}
