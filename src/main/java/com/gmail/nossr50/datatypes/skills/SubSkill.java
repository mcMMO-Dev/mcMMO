package com.gmail.nossr50.datatypes.skills;

import com.gmail.nossr50.util.StringUtils;

import com.gmail.nossr50.datatypes.skills.PrimarySkill.*;
public enum SubSkill {
    /* !! Warning -- Do not let subskills share a name with any existing PrimarySkill as it will clash with the static import !! */

    /* ACROBATICS */
    ACROBATICS_DODGE,
    ACROBATICS_GRACEFUL_ROLL,
    ACROBATICS_ROLL,

    /* ALCHEMY */
    ALCHEMY_CATALYSIS,
    ALCHEMY_CONCOCTIONS,

    /* ARCHERY */
    ARCHERY_DAZE,
    ARCHERY_RETRIEVE,
    ARCHERY_SKILL_SHOT,

    /* Axes */
    AXES_ARMOR_IMPACT,
    AXES_AXE_MASTERY,
    AXES_CRITICAL_HIT,
    AXES_GREATER_IMPACT,

    /* Excavation */
    EXCAVATION_TREASURE_HUNTER,

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

    /* Mining */
    MINING_DOUBLE_DROPS,

    /* Repair */
    REPAIR_ARCANE_FORGING,
    REPAIR_REPAIR_MASTERY,
    REPAIR_SUPER_REPAIR,

    /* Salvage */
    SALVAGE_ADVANCED_SALVAGE,
    SALVAGE_ARCANE_SALVAGE,

    /* Smelting */
    SMELTING_FLUX_MINING,
    SMELTING_FUEL_EFFICIENCY,
    SMELTING_SECOND_SMELT,

    /* Swords */
    SWORDS_BLEED,
    SWORDS_COUNTER,

    /* Taming */
    TAMING_BEAST_LORE,
    TAMING_CALL_OF_THE_WILD,
    TAMING_ENVIRONMENTALLY_AWARE,
    TAMING_FAST_FOOD,
    TAMING_GORE,
    TAMING_HOLY_HOUND,
    TAMING_SHARPENED_CLAWS,
    TAMING_SHOCK_PROOF,
    TAMING_THICK_FUR,
    TAMING_PUMMEL,

    /* Unarmed */
    UNARMED_BLOCK_CRACKER,
    UNARMED_DEFLECT,
    UNARMED_DISARM,
    UNARMED_IRON_ARM,
    UNARMED_IRON_GRIP,

    /* Woodcutting */
    WOODCUTTING_TREE_FELLER,
    WOODCUTTING_LEAF_BLOWER,
    WOODCUTTING_SURGEON,
    WOODCUTTING_NATURES_BOUNTY,
    WOODCUTTING_SPLINTER,
    WOODCUTTING_DOUBLE_DROPS;

    /**
     * !!! This relies on the immutable lists in PrimarySkill being populated !!!
     * If we add skills, those immutable lists need to be updated
     * @return
     */
    public PrimarySkill getParentSkill() { return PrimarySkill.bySecondaryAbility(this); }

    /**
     * Returns the permission root address for the advanced.yml for this subskill
     * @return permission root address in advanced.yml for this subskill
     */
    public String getAdvConfigAddress() {
        return "Skills." + StringUtils.getCapitalized(getParentSkill().toString()) + "." + getConfigName(toString());
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
        char[] enumNameCharArray = subSkillName.toString().toCharArray();
        int subStringIndex = 0;

        //Find where to start our substring for this constants name
        for (int i = 0; i < enumNameCharArray.length; i++) {
            if(enumNameCharArray[i] == '_')
            {
                subStringIndex = i+1; //Start the substring after this char

                break;
            }
        }

        /*
         * Split the string up so we can capitalize each part
         */
        String subskillNameWithoutPrefix = subSkillName.toString().substring(subStringIndex);
        String splitStrings[] = subskillNameWithoutPrefix.split("_");

        for(String string : splitStrings)
        {
            endResult += StringUtils.getCapitalized(string);
        }

        return endResult;
    }
}
