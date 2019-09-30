package com.gmail.nossr50.config.hocon.party;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionPartyCommands {

    public static final String HTTPS_MCMMO_ORG_WIKI_PERMISSIONS = "https://mcmmo.org/wiki/Permissions";

    @Setting(value = "Party-Teleport-Command",
            comment = "This command allows party members to teleport to one another after a small delay." +
                    "\nYou can disable this command by negating following permission node: mcmmo.commands.ptp" +
                    "\nThere are many permission nodes related to PTP (Party-Teleport)" +
                    "\nFor a list of permission nodes and a small description of what they do, checkout our wiki" +
                    "\nwiki permission page - " + HTTPS_MCMMO_ORG_WIKI_PERMISSIONS)
    private ConfigSectionPartyTeleportCommand partyTeleportCommand = new ConfigSectionPartyTeleportCommand();

    public ConfigSectionPartyTeleportCommand getPartyTeleportCommand() {
        return partyTeleportCommand;
    }
}