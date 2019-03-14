package com.gmail.nossr50.config.hocon.scoreboard;

import com.gmail.nossr50.config.ConfigConstants;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigScoreboard {

    /*
     * CONFIG NODES
     */

    @Setting(value = "General-Settings", comment = "Settings that apply to all scoreboards or don't fall into other categories")
    private ConfigSectionGeneral configSectionGeneral = new ConfigSectionGeneral();

    @Setting(value = "Scoreboard-Specific-Settings", comment = "Settings for individual scoreboard displays")
    private ConfigSectionScoreboardTypes configSectionScoreboardTypes = new ConfigSectionScoreboardTypes();

    /*
     * GETTER BOILERPLATE
     */

    public ConfigSectionGeneral getConfigSectionGeneral() {
        return configSectionGeneral;
    }

    public ConfigSectionScoreboardTypes getConfigSectionScoreboardTypes() {
        return configSectionScoreboardTypes;
    }

    /*
     * HELPER METHODS
     */

    public boolean getScoreboardsEnabled() {
        return configSectionGeneral.isUseScoreboards();
    }

    public boolean getPowerLevelTagsEnabled() {
        return configSectionGeneral.isPowerLevelTags();
    }

    public boolean getShowStatsAfterLogin() {
        return configSectionGeneral.isShowStatsAfterLogin();
    }

    public int getTipsAmount() {
        return configSectionGeneral.getTipsAmount();
    }

    public boolean getUseRainbowSkillStyling() {
        return configSectionGeneral.isUseRainbows();
    }

    public boolean getUseAbilityNamesOverGenerics() {
        return configSectionGeneral.isUseAbilityNameInsteadOfGeneric();
    }

    public boolean isScoreboardEnabled(ScoreboardManager.SidebarType sidebarType)
    {
        switch(sidebarType)
        {
            case TOP_BOARD:
                return getConfigSectionScoreboardTypes().getConfigSectionTopBoard().isUseThisBoard();
            case RANK_BOARD:
                return getConfigSectionScoreboardTypes().getConfigSectionRankBoard().isUseThisBoard();
            case STATS_BOARD:
                return getConfigSectionScoreboardTypes().getConfigSectionStatsBoard().isUseThisBoard();
            case COOLDOWNS_BOARD:
                return getConfigSectionScoreboardTypes().getConfigSectionCooldownBoard().isUseThisBoard();
            case SKILL_BOARD:
                return getConfigSectionScoreboardTypes().getConfigSectionSkillBoard().isUseThisBoard();
            default:
                return false;
        }
    }

    public boolean isScoreboardPrinting(ScoreboardManager.SidebarType sidebarType)
    {
        switch(sidebarType)
        {
            case TOP_BOARD:
                return getConfigSectionScoreboardTypes().getConfigSectionTopBoard().isPrintToChat();
            case RANK_BOARD:
                return getConfigSectionScoreboardTypes().getConfigSectionRankBoard().isPrintToChat();
            case STATS_BOARD:
                return getConfigSectionScoreboardTypes().getConfigSectionStatsBoard().isPrintToChat();
            case COOLDOWNS_BOARD:
                return getConfigSectionScoreboardTypes().getConfigSectionCooldownBoard().isPrintToChat();
            //NOTE: SKILL_BOARD does not have a setting for this because it is always printed to chat
            default:
                return false;
        }
    }

    public int getScoreboardDisplayTime(ScoreboardManager.SidebarType sidebarType)
    {
        switch(sidebarType)
        {
            case TOP_BOARD:
                return getConfigSectionScoreboardTypes().getConfigSectionTopBoard().getDisplayTimeInSeconds();
            case RANK_BOARD:
                return getConfigSectionScoreboardTypes().getConfigSectionRankBoard().getDisplayTimeInSeconds();
            case STATS_BOARD:
                return getConfigSectionScoreboardTypes().getConfigSectionStatsBoard().getDisplayTimeInSeconds();
            case COOLDOWNS_BOARD:
                return getConfigSectionScoreboardTypes().getConfigSectionCooldownBoard().getDisplayTimeInSeconds();
            case SKILL_BOARD:
                return getConfigSectionScoreboardTypes().getConfigSectionSkillBoard().getDisplayTimeInSeconds();
            default:
                return 20;
        }
    }
}
