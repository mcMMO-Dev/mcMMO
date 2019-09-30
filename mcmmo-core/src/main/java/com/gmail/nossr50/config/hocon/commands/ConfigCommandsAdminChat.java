package com.gmail.nossr50.config.hocon.commands;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigCommandsAdminChat {

    private static final String ADMIN_CHAT_PREFIX_DEFAULT = "&b[&f{0}&b]";
    private static final boolean USE_DISPLAY_NAMES_DEFAULT = true;
    @Setting(value = "Whether or not to use Display Names for admin chat." +
            "\nDisplay names are the current visible name of a player in the scoreboard, chat, and so on." +
            "\nThese names are modified by mods and are not necessarily the same nickname that a player has associated with their account." +
            "\nIf you turn this off, mcMMO will use a players registered nickname with their Minecraft account instead." +
            "\nDefault value: " + USE_DISPLAY_NAMES_DEFAULT)
    public boolean useDisplayNames = USE_DISPLAY_NAMES_DEFAULT;
    @Setting(value = "Admin-Chat-Prefix", comment = "Formatting use at the beginning of an admin chat message." +
            "\nYou can use & color codes here or type stuff like [[RED]]." +
            "\nDefault value: " + ADMIN_CHAT_PREFIX_DEFAULT)
    private String adminChatPrefix = ADMIN_CHAT_PREFIX_DEFAULT;

    public String getAdminChatPrefix() {
        return adminChatPrefix;
    }

    public boolean isUseDisplayNames() {
        return useDisplayNames;
    }
}