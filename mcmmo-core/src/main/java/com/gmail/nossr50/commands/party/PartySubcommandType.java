package com.gmail.nossr50.commands.party;

public enum PartySubcommandType {
    JOIN,
    ACCEPT,
    CREATE,
    HELP,
    INFO,
    QUIT,
    XPSHARE,
    ITEMSHARE,
    INVITE,
    KICK,
    DISBAND,
    OWNER,
    LOCK,
    UNLOCK,
    PASSWORD,
    RENAME,
    TELEPORT,
    CHAT,
    ALLIANCE;

    public static PartySubcommandType getSubcommand(String commandName) {
        for (PartySubcommandType command : values()) {
            if (command.name().equalsIgnoreCase(commandName)) {
                return command;
            }
        }

        if (commandName.equalsIgnoreCase("?")) {
            return HELP;
        } else if (commandName.equalsIgnoreCase("q") || commandName.equalsIgnoreCase("leave")) {
            return QUIT;
        } else if (commandName.equalsIgnoreCase("leader")) {
            return OWNER;
        } else if (commandName.equalsIgnoreCase("xpshare") || commandName.equalsIgnoreCase("shareexp") || commandName.equalsIgnoreCase("sharexp")) {
            return XPSHARE;
        } else if (commandName.equalsIgnoreCase("shareitem") || commandName.equalsIgnoreCase("shareitems")) {
            return ITEMSHARE;
        } else if (commandName.equalsIgnoreCase("ally")) {
            return ALLIANCE;
        }

        return null;
    }
}
