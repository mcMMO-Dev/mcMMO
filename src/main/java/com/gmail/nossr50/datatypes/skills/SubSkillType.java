package com.gmail.nossr50.datatypes.skills;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.text.StringUtils;
import java.util.Locale;

public enum SubSkillType {
    /* !! Warning -- Do not let subskills share a name with any existing PrimarySkillType as it will clash with the static import !! */

    /* ACROBATICS */
    ACROBATICS_DODGE(1),
    ACROBATICS_ROLL,

    /* ALCHEMY */
    ALCHEMY_CATALYSIS(1),
    ALCHEMY_CONCOCTIONS(8),

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

    /* CROSSBOWS */
    CROSSBOWS_CROSSBOWS_LIMIT_BREAK(10),
    CROSSBOWS_TRICK_SHOT(3),
    CROSSBOWS_POWERED_SHOT(20),

    /* Excavation */
    EXCAVATION_ARCHAEOLOGY(8),
    EXCAVATION_GIGA_DRILL_BREAKER(1),

    /* Fishing */
    FISHING_FISHERMANS_DIET(5),
    FISHING_ICE_FISHING(1),
    FISHING_MAGIC_HUNTER(1),
    FISHING_MASTER_ANGLER(8),
    FISHING_TREASURE_HUNTER(8),
    FISHING_SHAKE(8),

    /* Herbalism */
    HERBALISM_DOUBLE_DROPS(1),
    HERBALISM_VERDANT_BOUNTY(1),
    HERBALISM_FARMERS_DIET(5),
    HERBALISM_GREEN_TERRA(1),
    HERBALISM_GREEN_THUMB(4),
    HERBALISM_HYLIAN_LUCK,
    HERBALISM_SHROOM_THUMB,

    /* Maces */
    MACES_MACES_LIMIT_BREAK(10),
    MACES_CRUSH(4),
    MACES_CRIPPLE(4),

    /* Mining */
    MINING_BIGGER_BOMBS(1),
    MINING_BLAST_MINING(8),
    MINING_DEMOLITIONS_EXPERTISE(1),
    MINING_DOUBLE_DROPS(1),
    MINING_SUPER_BREAKER(1),
    MINING_MOTHER_LODE(1),

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

    /* Spears */
    SPEARS_SPEARS_LIMIT_BREAK(10),
    SPEARS_MOMENTUM(10),
    SPEARS_SPEAR_MASTERY(8),

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

    /* Tridents */
    TRIDENTS_IMPALE(10),
    TRIDENTS_TRIDENTS_LIMIT_BREAK(10),

    /* Unarmed */
    UNARMED_ARROW_DEFLECT(1),
    UNARMED_BERSERK(1),
    UNARMED_BLOCK_CRACKER,
    UNARMED_DISARM(1),
    UNARMED_STEEL_ARM_STYLE(20),
    UNARMED_IRON_GRIP(1),
    UNARMED_UNARMED_LIMIT_BREAK(10),

    /* Woodcutting */
    WOODCUTTING_KNOCK_ON_WOOD(2),
    WOODCUTTING_HARVEST_LUMBER(1),
    WOODCUTTING_LEAF_BLOWER(1),
    WOODCUTTING_TREE_FELLER(1),
    WOODCUTTING_CLEAN_CUTS(1);

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
     * !!! This relies on the immutable lists in PrimarySkillType being populated !!! If we add
     * skills, those immutable lists need to be updated
     *
     * @return the parent skill of this subskill
     */
    public PrimarySkillType getParentSkill() {
        return mcMMO.p.getSkillTools().getPrimarySkillBySubSkill(this);
    }

    /**
     * Returns the root address for this skill in the advanced.yml file
     *
     * @return the root address for this skill in advanced.yml
     */
    public String getAdvConfigAddress() {
        return "Skills." + StringUtils.getCapitalized(getParentSkill().toString()) + "."
                + getConfigName(toString());
    }

    /**
     * Returns the root address for this skill in the rankskills.yml file
     *
     * @return the root address for this skill in rankskills.yml
     */
    public String getRankConfigAddress() {
        return StringUtils.getCapitalized(getParentSkill().toString()) + "." + getConfigName(
                toString());
    }

    /**
     * Get the string representation of the permission node for this subskill
     *
     * @return the permission node for this subskill
     */
    public String getPermissionNodeAddress() {
        //TODO: This could be optimized
        return "mcmmo.ability." + getParentSkill().toString().toLowerCase(Locale.ENGLISH) + "."
                + getConfigName(toString()).toLowerCase(Locale.ENGLISH);
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
        String subskillNameWithoutPrefix = subSkillName.substring(subStringIndex);
        if (subskillNameWithoutPrefix.contains("_")) {
            String[] splitStrings = subskillNameWithoutPrefix.split("_");

            for (String string : splitStrings) {
                endResult.append(StringUtils.getCapitalized(string));
            }
        } else {
            endResult.append(StringUtils.getCapitalized(subskillNameWithoutPrefix));
        }

        return endResult.toString();
    }

    public String getWikiUrl() {
        // remove the text before the first underscore
        int subStringIndex = getSubStringIndex(name());
        String afterPrefix = name().substring(subStringIndex);
        // replace _ or spaces with -
        return afterPrefix.replace("_", "-").replace(" ", "-").toLowerCase(Locale.ENGLISH);
    }

    /**
     * Returns the name of the parent skill from the Locale file
     *
     * @return The parent skill as defined in the locale
     */
    public String getParentNiceNameLocale() {
        return LocaleLoader.getString(
                StringUtils.getCapitalized(getParentSkill().toString()) + ".SkillName");
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

    public String getLocaleKeyRoot() {
        return StringUtils.getCapitalized(getParentSkill().toString()) + ".SubSkill."
                + getConfigName(toString());
    }

    public String getLocaleName() {
        return getFromLocaleSubAddress(".Name");
    }

    public String getLocaleDescription() {
        return getFromLocaleSubAddress(".Description");
    }

    public String getLocaleStatDescription() {
        return getFromLocaleSubAddress(".Stat");
    }

    public String getLocaleKeyStatDescription() {
        return getLocaleKeyFromSubAddress(".Stat");
    }

    public String getLocaleStatExtraDescription() {
        return getFromLocaleSubAddress(".Stat.Extra");
    }

    public String getLocaleKeyStatExtraDescription() {
        return getLocaleKeyFromSubAddress(".Stat.Extra");
    }

    public String getLocaleStat(String... vars) {
        return LocaleLoader.getString("Ability.Generic.Template", (Object[]) vars);
    }

    public String getCustomLocaleStat(String... vars) {
        return LocaleLoader.getString("Ability.Generic.Template.Custom", (Object[]) vars);
    }

    private String getFromLocaleSubAddress(String s) {
        return LocaleLoader.getString(getLocaleKeyRoot() + s);
    }

    private String getLocaleKeyFromSubAddress(String s) {
        return getLocaleKeyRoot() + s;
    }
}
