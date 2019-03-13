package com.gmail.nossr50.config.hocon.scoreboard;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionScoreboardTypes {

    /*
     * CONFIG NODES
     */

    @Setting(value = "Rank_Scoreboard", comment = "Settings for /mcrank")
    private ConfigSectionRankBoard configSectionRankBoard = new ConfigSectionRankBoard();

    @Setting(value = "Top_Scoreboard", comment = "Settings for /mctop")
    private ConfigSectionTopBoard configSectionTopBoard = new ConfigSectionTopBoard();

    @Setting(value = "Stats_Scoreboard", comment = "Settings for /mcstats")
    private ConfigSectionStatsBoard configSectionStatsBoard = new ConfigSectionStatsBoard();

    @Setting(value = "Inspect_Scoreboard", comment = "Settings for /inspect")
    private ConfigSectionInspectBoard configSectionInspectBoard = new ConfigSectionInspectBoard();

    @Setting(value = "Cooldown_Scoreboard", comment = "Settings for /mccooldown")
    private ConfigSectionCooldownBoard configSectionCooldownBoard = new ConfigSectionCooldownBoard();

    @Setting(value = "Skill_Scoreboard_Settings",
            comment = "Settings for /<skillname> (e.g. /mining, /unarmed)" +
                    "\nNo \"print\" option is given here; the information will always be displayed in chat." +
                    "\nThe functionality of this scoreboard overlaps heavily with the new XP bars," +
                    " so these scoreboards are disabled by default")
    private ConfigSectionSkillBoard configSectionSkillBoard = new ConfigSectionSkillBoard();

    /*
     * GETTER BOILERPLATE
     */

    public ConfigSectionRankBoard getConfigSectionRankBoard() {
        return configSectionRankBoard;
    }

    public ConfigSectionTopBoard getConfigSectionTopBoard() {
        return configSectionTopBoard;
    }

    public ConfigSectionStatsBoard getConfigSectionStatsBoard() {
        return configSectionStatsBoard;
    }

    public ConfigSectionInspectBoard getConfigSectionInspectBoard() {
        return configSectionInspectBoard;
    }

    public ConfigSectionCooldownBoard getConfigSectionCooldownBoard() {
        return configSectionCooldownBoard;
    }

    public ConfigSectionSkillBoard getConfigSectionSkillBoard() {
        return configSectionSkillBoard;
    }
}
