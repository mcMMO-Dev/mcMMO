package com.gmail.nossr50.commands;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class CommandConstants {
    public static final List<String> TELEPORT_SUBCOMMANDS = ImmutableList.of("toggle", "accept", "acceptany", "acceptall");
    public static final List<String> ALLIANCE_SUBCOMMANDS = ImmutableList.of("invite", "accept", "disband");
    public static final List<String> TRUE_FALSE_OPTIONS = ImmutableList.of("on", "off", "true", "false", "enabled", "disabled");
    public static final List<String> RESET_OPTIONS = ImmutableList.of("clear", "reset");
}
