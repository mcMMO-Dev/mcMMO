package com.gmail.nossr50.config.hocon.scoreboard;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionCooldownBoard {

    /* DEFAULT VALUES*/
    private static final boolean PRINT_TO_CHAT_DEFAULT = true;
    private static final boolean USE_THIS_SCOREBOARD_DEFAULT = true;
    private static final int DISPLAY_TIME_SECONDS_DEFAULT = 40;

    /*
     * CONFIG NODES
     */

    @Setting(value = "Show_Command_Output_In_Chat",
            comment = "Should the commands normal chat output be shown in chat?" +
                    "\nIf you feel that the scoreboard does a good enough job at conveying the information, you can set this to false." +
                    "\nIf you'd like to show the chat anyways, turn this to true, the scoreboard will still be shown if its enabled." +
                    "\nDefault value: "+PRINT_TO_CHAT_DEFAULT)
    private boolean printToChat = PRINT_TO_CHAT_DEFAULT;

    @Setting(value = "Show_Scoreboard",
            comment = "Whether or not you wish to enable the display of this scoreboard." +
                    "\nScoreboards are shown when the associated command is executed from the player." +
                    "\nThis setting will only work if \"Use_Scoreboards\" is set to true, which is found elsewhere in this configuration file." +
                    "\nDefault value: "+USE_THIS_SCOREBOARD_DEFAULT)
    private boolean useThisBoard = USE_THIS_SCOREBOARD_DEFAULT;

    @Setting(value = "Display_Time_In_Seconds",
            comment = "How long to keep the scoreboard on a players screen after it is first shown." +
                    "\nThis setting is not related to the command that keeps scoreboards on screen." +
                    "\nDefault value: "+DISPLAY_TIME_SECONDS_DEFAULT)
    private int displayTimeInSeconds = DISPLAY_TIME_SECONDS_DEFAULT;

    /*
     * GETTER BOILERPLATE
     */

    public boolean isPrintToChat() {
        return printToChat;
    }

    public boolean isUseThisBoard() {
        return useThisBoard;
    }

    public int getDisplayTimeInSeconds() {
        return displayTimeInSeconds;
    }
}
