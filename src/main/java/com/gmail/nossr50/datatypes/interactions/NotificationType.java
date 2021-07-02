package com.gmail.nossr50.datatypes.interactions;

import org.jetbrains.annotations.NotNull;

/**
 * This class helps define the types of information interactions we will have with players
 */
public enum NotificationType {
    XP_GAIN("ExperienceGain"),
    HARDCORE_MODE("HardcoreMode"),
    NO_PERMISSION("NoPermission"),
    SUBSKILL_UNLOCKED("SubSkillUnlocked"),
    LEVEL_UP_MESSAGE("LevelUps"),
    HOLIDAY("Holiday"),
    SUBSKILL_MESSAGE("SubSkillInteraction"),
    SUBSKILL_MESSAGE_FAILED("SubSkillFailed"),
    TOOL("ToolReady"),
    REQUIREMENTS_NOT_MET("RequirementsNotMet"),
    ABILITY_OFF("AbilityOff"),
    ABILITY_COOLDOWN("AbilityCoolDown"),
    ABILITY_REFRESHED("AbilityRefreshed"),
    SUPER_ABILITY("SuperAbilityInteraction"),
    SUPER_ABILITY_ALERT_OTHERS("SuperAbilityAlertOthers"),
    ITEM_MESSAGE("ItemMessage"),
    CHAT_ONLY("ChatOnly"),
    PARTY_MESSAGE("PartyMessage");

    private final String niceName;

    NotificationType(@NotNull String niceName) {
        this.niceName = niceName;
    }

    @Override
    public @NotNull String toString() {
        return niceName;
    }
}
