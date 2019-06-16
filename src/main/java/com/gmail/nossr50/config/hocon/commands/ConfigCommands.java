package com.gmail.nossr50.config.hocon.commands;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigCommands {

    @Setting(value = "Admin-Chat", comment = "Settings related to the admin chat command and chatting modes.")
    private ConfigCommandsAdminChat adminChat = new ConfigCommandsAdminChat();

    @Setting(value = "Inspect", comment = "Settings related to the inspect command.")
    private ConfigCommandsInspect inspect = new ConfigCommandsInspect();

    @Setting(value = "Skills", comment = "Settings related to skill commands like /mining or /herbalism")
    private ConfigCommandsSkills skills = new ConfigCommandsSkills();

    @Setting(value = "Misc", comment = "Various settings for commands that don't fit into other categories.")
    private ConfigCommandsMisc misc = new ConfigCommandsMisc();

    public ConfigCommandsMisc getMisc() {
        return misc;
    }

    public ConfigCommandsAdminChat getAdminChat() {
        return adminChat;
    }

    public String getAdminChatPrefix() {
        return getAdminChat().getAdminChatPrefix();
    }

    public boolean isUseDisplayNames() {
        return getAdminChat().isUseDisplayNames();
    }

    public ConfigCommandsInspect getInspect() {
        return inspect;
    }

    public double getInspectCommandMaxDistance() {
        return getInspect().getInspectCommandMaxDistance();
    }

    public boolean isLimitInspectRange() {
        return getInspect().isLimitInspectRange();
    }

    public boolean isAllowInspectOnOfflinePlayers() {
        return getInspect().isAllowInspectOnOfflinePlayers();
    }

    public ConfigCommandsSkills getSkills() {
        return skills;
    }

    public boolean isSendBlankLines() {
        return skills.isSendBlankLines();
    }
}
