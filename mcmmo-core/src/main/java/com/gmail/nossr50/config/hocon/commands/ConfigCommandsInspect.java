package com.gmail.nossr50.config.hocon.commands;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigCommandsInspect {

    private static final double INSPECT_MAX_DISTANCE_DEFAULT = 30.0D;
    private static final boolean LIMIT_INSPECT_RANGE_DEFAULT = false;
    private static final boolean ALLOW_OFFLINE_INSPECTION_DEFAULT = true;
    private static final String BYPASS_PERMISSION = "mcmmo.commands.mcrank.others.far";

    @Setting(value = "Inspect-Max-Distance", comment = "The maximum range at which players can inspect one another." +
            "\nIs only used if limit inspect range is turned on." +
            "\nDefault value: " + INSPECT_MAX_DISTANCE_DEFAULT)
    private double inspectCommandMaxDistance = INSPECT_MAX_DISTANCE_DEFAULT;

    @Setting(value = "Limit-Inspect-Range", comment = "Inspection is limited by the distance between players instead of always being usable." +
            "Permission to bypass this limit - " + BYPASS_PERMISSION
            + "\nDefault value: " + LIMIT_INSPECT_RANGE_DEFAULT)
    private boolean limitInspectRange = LIMIT_INSPECT_RANGE_DEFAULT;

    @Setting(value = "Allow-Offline-Inspection", comment = "If set to true players will be able to look at the profiles of anyone on the server whether they are connected or not." +
            "\nAdmins and the console can always check the profiles of offline players." +
            "\nDefault value: " + ALLOW_OFFLINE_INSPECTION_DEFAULT)
    private boolean allowInspectOnOfflinePlayers = ALLOW_OFFLINE_INSPECTION_DEFAULT;

    public double getInspectCommandMaxDistance() {
        return inspectCommandMaxDistance;
    }

    public boolean isLimitInspectRange() {
        return limitInspectRange;
    }

    public boolean isAllowInspectOnOfflinePlayers() {
        return allowInspectOnOfflinePlayers;
    }
}