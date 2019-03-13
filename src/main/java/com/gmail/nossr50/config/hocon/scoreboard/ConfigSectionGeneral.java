package com.gmail.nossr50.config.hocon.scoreboard;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionGeneral {

    /* DEFAULT VALUES */
    private static final boolean USE_SCOREBOARDS_DEFAULT = false;
    private static final boolean POWER_LEVEL_DISPLAY_DEFAULT = false;
    private static final boolean SHOW_PLAYER_STATS_SCOREBOARD_AFTER_LOGIN_DEFAULT = false;
    private static final boolean USE_RAINBOW_SKILL_COLORING_DEFAULT = true;
    private static final boolean USE_SUPER_ABILITY_NAME_INSTEAD_OF_GENERIC = true;

    private static final int SHOW_TIPS_LIMIT_DEFAULT = 10;

    /*
     * CONFIG NODES
     */

    @Setting(value = "Use_Scoreboards", comment = "Whether or not mcMMO should use make use of scoreboards." +
            "\nPersonally, I find scoreboards quite ugly, so I've disabled them by default." +
            "\nMost of their functionality has been replaced by the new XP bars (Boss Bars)" +
            "\nIf you still wish to use scoreboards, you can, just turn this setting on." +
            "\nDefault value: "+ USE_SCOREBOARDS_DEFAULT)
    private boolean useScoreboards = USE_SCOREBOARDS_DEFAULT;

    @Setting(value = "Display_Power_Levels_Below_Player_Names",
            comment = "Whether or not Player power levels should be displayed below " +
                    "their username (above their 3d model in the world)" +
                    "\nAlthough it doesn't seem related to scoreboards, displaying a power level for a Player is done" +
                    " through the use of scoreboards." +
                    "\nThis is off by default because a lot of Plugins for Minecraft make use of editing" +
                    " a players \"nameplate\" and that can cause compatibility issues" +
                    "\nDefault value: "+ POWER_LEVEL_DISPLAY_DEFAULT)
    private boolean powerLevelTags = POWER_LEVEL_DISPLAY_DEFAULT;

    @Setting(value = "Show_Stats_Scoreboard_On_Player_Login", comment = "Shows the player the /mcstats scoreboard" +
            " display after they login." +
            "\nDefault value: "+ SHOW_PLAYER_STATS_SCOREBOARD_AFTER_LOGIN_DEFAULT)
    private boolean showStatsAfterLogin = SHOW_PLAYER_STATS_SCOREBOARD_AFTER_LOGIN_DEFAULT;

    @Setting(value = "Show_Scoreboard_Tips_Only_This_Many_Times", comment = "This determines how many times players are" +
            " given tips about how to use the scoreboard system before they are never tipped again." +
            "\nPlayers are given tips once per login session." +
            "\nDefault value: "+ SHOW_TIPS_LIMIT_DEFAULT)
    private int tipsAmount = SHOW_TIPS_LIMIT_DEFAULT;

    @Setting(value ="Use_Rainbow_Styling_For_Skill_Names", comment = "If true, skills names will use rainbow style" +
            " colorings instead of having the same color" +
            "\nDefault value: "+ USE_RAINBOW_SKILL_COLORING_DEFAULT)
    private boolean useRainbows = USE_RAINBOW_SKILL_COLORING_DEFAULT;

    @Setting(value = "Use_Super_Ability_Name_Instead_Of_Generic_Name",
            comment = "If true, scoreboards displaying super ability cooldowns will use the super abilities name " +
                    "instead of using a generic word from the locale, which by default in the locale is defined as " +
                    "\"Ability\". The locale key for this entry is - Scoreboard.Misc.Ability " +
                    "\nExample: If true Tree Feller will be shown instead of Super Ability with default en_us locale entries" +
                    "\nDefault value: "+ USE_SUPER_ABILITY_NAME_INSTEAD_OF_GENERIC)
    private boolean useAbilityNameInsteadOfGeneric = USE_SUPER_ABILITY_NAME_INSTEAD_OF_GENERIC;

    public boolean isUseScoreboards() {
        return useScoreboards;
    }

    public boolean isPowerLevelTags() {
        return powerLevelTags;
    }

    public boolean isShowStatsAfterLogin() {
        return showStatsAfterLogin;
    }

    public int getTipsAmount() {
        return tipsAmount;
    }

    public boolean isUseRainbows() {
        return useRainbows;
    }

    public boolean isUseAbilityNameInsteadOfGeneric() {
        return useAbilityNameInsteadOfGeneric;
    }
}
