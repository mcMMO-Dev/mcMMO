package com.gmail.nossr50.datatypes.skills;

import com.gmail.nossr50.util.StringUtils;

public enum SuperAbilityType {
    BERSERK,
    SUPER_BREAKER,
    GIGA_DRILL_BREAKER,
    GREEN_TERRA,
    SKULL_SPLITTER,
    TREE_FELLER,
    SERRATED_STRIKES,
    BLAST_MINING;

    /*
     * Defining their associated SubSkillType definitions
     * This is a bit of a band-aid fix until the new skill system is in place
     */
    static {
        BERSERK.subSkillTypeDefinition = SubSkillType.UNARMED_BERSERK;
        SUPER_BREAKER.subSkillTypeDefinition = SubSkillType.MINING_SUPER_BREAKER;
        GIGA_DRILL_BREAKER.subSkillTypeDefinition = SubSkillType.EXCAVATION_GIGA_DRILL_BREAKER;
        GREEN_TERRA.subSkillTypeDefinition = SubSkillType.HERBALISM_GREEN_TERRA;
        SKULL_SPLITTER.subSkillTypeDefinition = SubSkillType.AXES_SKULL_SPLITTER;
        TREE_FELLER.subSkillTypeDefinition = SubSkillType.WOODCUTTING_TREE_FELLER;
        SERRATED_STRIKES.subSkillTypeDefinition = SubSkillType.SWORDS_SERRATED_STRIKES;
        BLAST_MINING.subSkillTypeDefinition = SubSkillType.MINING_BLAST_MINING;
    }

    private SubSkillType subSkillTypeDefinition;

    /**
     * Grabs the associated SubSkillType definition for this SuperAbilityType
     *
     * @return the matching SubSkillType definition for this SuperAbilityType
     */
    public SubSkillType getSubSkillTypeDefinition() {
        return subSkillTypeDefinition;
    }

    @Override
    public String toString() {
        String baseString = name();
        String[] substrings = baseString.split("_");
        String formattedString = "";

        int size = 1;

        for (String string : substrings) {
            formattedString = formattedString.concat(StringUtils.getCapitalized(string));

            if (size < substrings.length) {
                formattedString = formattedString.concat("_");
            }

            size++;
        }

        return formattedString;
    }
}
