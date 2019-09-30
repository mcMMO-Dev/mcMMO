package com.gmail.nossr50.config.hocon.commands;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigCommandsSkills {

    private static final boolean SEND_BLANK_LINES_DEFAULT = true;

    @Setting(value = "Send-Blank-Lines", comment = "If set to true, mcMMO will send a few blank lines when players use skill commands to make them more readable.")
    private boolean sendBlankLines = SEND_BLANK_LINES_DEFAULT;

    public boolean isSendBlankLines() {
        return sendBlankLines;
    }
}
