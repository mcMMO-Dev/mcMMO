package com.gmail.nossr50.datatypes.skills;

public enum AbilityToolType {
    SKULL_SPLITTER_TOOL("Axes.Ability.Lower", "Axes.Ability.Ready"),
    BERSERK_TOOL( "Unarmed.Ability.Lower", "Unarmed.Ability.Ready"),
    GREEN_TERRA_TOOL("Herbalism.Ability.Lower", "Herbalism.Ability.Ready"),
    SUPER_BREAKER_TOOL("Mining.Ability.Lower", "Mining.Ability.Ready"),
    GIGA_DRILL_BREAKER_TOOL("Excavation.Ability.Lower", "Excavation.Ability.Ready"),
    SERRATED_STRIKES_TOOL("Swords.Ability.Lower", "Swords.Ability.Ready"),
    TREE_FELLER_TOOL("Axes.Ability.Lower", "Axes.Ability.Ready"),
    ARCHERY_TOOL("Archery.Ability.Lower", "Archery.Ability.Ready"),
    SUPER_SHOTGUN_TOOL("Crossbows.Ability.Lower", "Crossbows.Ability.Ready"),
    TRIDENTS_TOOL("Tridents.Ability.Lower", "Tridents.Ability.Ready");

    private final String lowerToolLocaleKey;
    private final String raiseToolLocaleKey;

    AbilityToolType(String lowerToolLocaleKey, String raiseToolLocaleKey) {
        this.lowerToolLocaleKey = lowerToolLocaleKey;
        this.raiseToolLocaleKey = raiseToolLocaleKey;
    }

    public String getLowerToolLocaleKey() {
        return lowerToolLocaleKey;
    }

    public String getRaiseToolLocaleKey() {
        return raiseToolLocaleKey;
    }
}
