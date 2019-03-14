package com.gmail.nossr50.config.hocon.scoreboard;

import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionSkillBoard {

    /* DEFAULT VALUES*/
    private static final boolean USE_THIS_SCOREBOARD_DEFAULT = true;
    private static final int DISPLAY_TIME_SECONDS_DEFAULT = 30;
    private static final boolean SHOW_BOARD_ON_PLAYER_LEVELUP = true;
    private static final int SHOW_BOARD_ON_LEVELUP_TIME = 5;

    /*
     * CONFIG NODES
     */

    @Setting(value = "Show-Scoreboard",
            comment = "Whether or not you wish to enable the display of this scoreboard." +
                    "\nScoreboards are shown when the associated command is executed from the player." +
                    "\nThis setting will only work if \"Use_Scoreboards\" is set to true," +
                    " which is found elsewhere in this configuration file." +
                    "\nDefault value: "+USE_THIS_SCOREBOARD_DEFAULT)
    private boolean useThisBoard = USE_THIS_SCOREBOARD_DEFAULT;

    @Setting(value = "Display-Time-In-Seconds",
            comment = "How long to keep the scoreboard on a players screen after it is first shown." +
                    "\nThis setting is not related to the command that keeps scoreboards on screen." +
                    "\nDefault value: "+DISPLAY_TIME_SECONDS_DEFAULT)
    private int displayTimeInSeconds = DISPLAY_TIME_SECONDS_DEFAULT;

    @Setting(value = "Show-Board-On-Player-Level-Up",
            comment = "Show a skill scoreboard when the player levels up in that skill" +
                    "\nDefault value: "+SHOW_BOARD_ON_PLAYER_LEVELUP)
    private boolean showBoardOnPlayerLevelUp = SHOW_BOARD_ON_PLAYER_LEVELUP;

    @Setting(value = "Level-Up-Display-Time", comment = "How long to show a skill scoreboard when a player levels up?" +
            "\nIs only shown if Show_Board_On_Player_Level_Up is true" +
            "\nDefault value: "+SHOW_BOARD_ON_LEVELUP_TIME)
    private int showBoardOnPlayerLevelUpTime = SHOW_BOARD_ON_LEVELUP_TIME;

    /*
     * GETTER BOILERPLATE
     */

    public boolean isUseThisBoard() {
        return useThisBoard;
    }

    public int getDisplayTimeInSeconds() {
        return displayTimeInSeconds;
    }

    public boolean getShowBoardOnPlayerLevelUp() {
        return showBoardOnPlayerLevelUp;
    }

    public int getShowBoardOnPlayerLevelUpTime() {
        return showBoardOnPlayerLevelUpTime;
    }
}
