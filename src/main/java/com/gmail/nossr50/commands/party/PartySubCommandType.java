package com.gmail.nossr50.commands.party;

public enum PartySubCommandType {
    JOIN,
    ACCEPT,
    CREATE,
    HELP,
    INFO,
    QUIT,
    XPSHARE,
    INVITE,
    KICK,
    DISBAND,
    OWNER,
    RENAME,
    TELEPORT,
    CHAT,
    PROMOTE,
    DEMOTE;

    public static PartySubCommandType getSubcommand(String commandName) {
        for (PartySubCommandType command : values()) {
            if (command.name().equalsIgnoreCase(commandName)) {
                return command;
            }
        }

        if (commandName.equalsIgnoreCase("?")) {
            return HELP;
        }
        else if (commandName.equalsIgnoreCase("q") || commandName.equalsIgnoreCase("leave")) {
            return QUIT;
        }
        else if (commandName.equalsIgnoreCase("leader")) {
            return OWNER;
        }
        else if (commandName.equalsIgnoreCase("xpshare") || commandName.equalsIgnoreCase("shareexp") || commandName.equalsIgnoreCase("sharexp")) {
            return XPSHARE;
        } else {
            return null;
        }
    }
}
