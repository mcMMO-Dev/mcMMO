package com.gmail.nossr50.util.commands;

public enum CommandTabCompleteMode {
    TRANSLATED,
    BOTH,
    CANONICAL;

    public static CommandTabCompleteMode fromConfig(String value) {
        if (value == null) {
            return BOTH;
        }

        for (CommandTabCompleteMode mode : values()) {
            if (mode.name().equalsIgnoreCase(value)) {
                return mode;
            }
        }

        return BOTH;
    }
}
