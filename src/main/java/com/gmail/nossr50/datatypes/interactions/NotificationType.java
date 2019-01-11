package com.gmail.nossr50.datatypes.interactions;

/**
 * This class helps define the types of information interactions we will have with players
 */
public enum NotificationType {
    XP_GAIN("ExperienceGain"),
    SUBSKILL_UNLOCKED("SubSkillUnlocked"),
    LEVEL_UP_MESSAGE("LevelUps"),
    SUBSKILL_MESSAGE("SubSkillInteraction"),
    TOOL("ToolReady"),
    UNSKILLED("LevelRequirementNotMet"),
    ABILITY_COOLDOWN("AbilityCoolDown"),
    SUPER_ABILITY("SuperAbilityInteraction");

    final String niceName;

    NotificationType(String niceName)
    {
        this.niceName = niceName;
    }

    @Override
    public String toString() {
        return niceName;
    }}
