package com.gmail.nossr50.config.hocon.party;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigParty {

    @Setting(value = "Party-Chat",
            comment = "Settings related to the display, formatting, and misc settings related to party chat.")
    private ConfigSectionPartyChat partyChat = new ConfigSectionPartyChat();

    @Setting(value = "Party-Combat",
            comment = "Settings related to combat interactions for parties.")
    private ConfigSectionPartyCombat partyCombat = new ConfigSectionPartyCombat();

    @Setting(value = "Party-General",
            comment = "Settings for player parties that don't fit neatly into other categories.")
    private ConfigSectionPartyGeneral partyGeneral = new ConfigSectionPartyGeneral();

    @Setting(value = "Party-Scheduled-Cleanups",
            comment = "Settings related to automatic removal of players who haven't connected to the server in a long time.")
    private ConfigSectionPartyCleanup partyCleanup = new ConfigSectionPartyCleanup();

    @Setting(value = "Party-XP", comment = "Settings related to leveling parties.")
    private ConfigSectionPartyXP partyXP = new ConfigSectionPartyXP();

    @Setting(value = "Party-Commands", comment = "Settings related to various party commands.")
    private ConfigSectionPartyCommands partyCommands = new ConfigSectionPartyCommands();

    @Setting(value = "Party-Item-Share", comment = "Settings related to sharing items dropped from monsters in a party.")
    private ConfigSectionPartyItemShare partyItemShare = new ConfigSectionPartyItemShare();

    public int getPartySizeLimit() {
        return partyGeneral.getPartySizeLimit();
    }

    public boolean isPartySizeCapped() {
        return partyGeneral.isPartySizeCapped();
    }

    public ConfigSectionPartyTeleportCommand getPTP() {
        return partyCommands.getPartyTeleportCommand();
    }

    public ConfigSectionPartyCleanup getPartyCleanup() {
        return partyCleanup;
    }

    public ConfigSectionPartyChat getPartyChat() {
        return partyChat;
    }

    public ConfigSectionPartyCombat getPartyCombat() {
        return partyCombat;
    }

    public ConfigSectionPartyGeneral getPartyGeneral() {
        return partyGeneral;
    }

    public ConfigSectionPartyXP getPartyXP() {
        return partyXP;
    }

    public ConfigSectionPartyItemShare getPartyItemShare() {
        return partyItemShare;
    }

    public ConfigSectionPartyCommands getPartyCommands() {
        return partyCommands;
    }

    public String getPartyChatPrefixFormat() {
        return partyChat.getPartyChatPrefixFormat();
    }

    public String getPartyChatPrefixAlly() {
        return partyChat.getPartyChatPrefixAlly();
    }

    public boolean isPartyLeaderColoredGold() {
        return partyChat.isPartyLeaderColoredGold();
    }

    public boolean isPartyDisplayNamesEnabled() {
        return partyChat.isPartyDisplayNamesEnabled();
    }

    public boolean isPartyFriendlyFireEnabled() {
        return partyCombat.isPartyFriendlyFire();
    }

    /*


    public int getPTPCommandCooldown() {
        return getIntValue(COMMANDS, PTP, COOLDOWN);
    }

    public int getPTPCommandWarmup() {
        return getIntValue(COMMANDS, PTP, WARMUP);
    }

    public int getPTPCommandRecentlyHurtCooldown() {
        return getIntValue(COMMANDS, PTP, RECENTLY_HURT + COOLDOWN);
    }

    public int getPTPCommandTimeout() {
        return getIntValue(COMMANDS, PTP, REQUEST_TIMEOUT);
    }

    public boolean getPTPCommandConfirmRequired() {
        return getBooleanValue(COMMANDS, PTP, ACCEPT_REQUIRED);
    }

    public boolean getPTPCommandWorldPermissions() {
        return getBooleanValue(COMMANDS, PTP, WORLD_BASED_PERMISSIONS);
    }

     */
}
