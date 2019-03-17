package com.gmail.nossr50.config.hocon.party;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionPartyTeleportCommand {

    public static final int PTP_COOLDOWN_DEFAULT = 120;
    public static final int PTP_WARMUP_DEFAULT = 5;
    public static final int PTP_RECENTLY_HURT_COOLDOWN_DEFAULT = 60;
    public static final boolean PTP_ACCEPT_REQUIRED_DEFAULT = true;
    public static final int PTP_REQUEST_TIMEOUT = 300;
    public static final boolean PTP_WORLD_BASED_PERMISSIONS_DEFAULT = false;

    @Setting(value = "PTP-Cooldown", comment = "How many seconds a player must wait between usages of PTP." +
            "\nDefault value: "+PTP_COOLDOWN_DEFAULT)
    private int ptpCooldown = PTP_COOLDOWN_DEFAULT;

    @Setting(value = "PTP-Warmup", comment = "How many seconds a player must stand still for a PTP to be successful." +
            "\nDefault value: "+PTP_WARMUP_DEFAULT)
    private int ptpWarmup = PTP_WARMUP_DEFAULT;

    @Setting(value = "PTP-Hurt-Cooldown", comment = "How many seconds a player must wait from last taking damage in order to use PTP." +
            "\nDefault value: "+PTP_RECENTLY_HURT_COOLDOWN_DEFAULT)
    private int ptpRecentlyHurtCooldown = PTP_RECENTLY_HURT_COOLDOWN_DEFAULT;

    @Setting(value = "PTP-Requires-Accept", comment = "If a player tries to use PTP to another party member," +
            " that party member must then accept his request or the PTP will not execute." +
            "\nDefault value: "+PTP_ACCEPT_REQUIRED_DEFAULT)
    private boolean ptpAcceptRequired = PTP_ACCEPT_REQUIRED_DEFAULT;

    @Setting(value = "PTP-Request-Timeout", comment = "How many seconds before a PTP request will become invalid." +
            "\nDefault value: "+PTP_REQUEST_TIMEOUT)
    private int ptpRequestTimeout = PTP_REQUEST_TIMEOUT;

    @Setting(value = "PTP-Require-World-Based-Permissions", comment = "If true, players need to use a special permission node in order to use PTP on that world or to that world." +
            "\nExample: Pretend the world is named \"mouth-fedora-planet\"" +
            "\nThe permission node a player would need to use PTP for that world, would be..." +
            "\nRequired Permission Node Example: 'mcmmo.commands.ptp.world.mouth-fedora-planet'" +
            "\nNote: 'mcmmo.commands.ptp.world.*' would allow a player to use PTP on all worlds, to and from")
    private boolean ptpWorldBasedPermissions = PTP_WORLD_BASED_PERMISSIONS_DEFAULT;

    public int getPtpCooldown() {
        return ptpCooldown;
    }

    public int getPtpWarmup() {
        return ptpWarmup;
    }

    public int getPtpRecentlyHurtCooldown() {
        return ptpRecentlyHurtCooldown;
    }

    public boolean isPtpAcceptRequired() {
        return ptpAcceptRequired;
    }

    public int getPtpRequestTimeout() {
        return ptpRequestTimeout;
    }

    public boolean isPtpWorldBasedPermissions() {
        return ptpWorldBasedPermissions;
    }
}