package com.gmail.nossr50.datatypes.party;

import com.gmail.nossr50.commands.party.PartySubcommandType;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import org.bukkit.entity.Player;

public enum PartyFeature {
    CHAT,
    TELEPORT,
    ALLIANCE,
    ITEM_SHARE,
    XP_SHARE;

    public String getLocaleString() {
        return pluginRef.getLocaleManager().getString("Party.Feature." + StringUtils.getPrettyPartyFeatureString(this).replace(" ", ""));
    }

    public String getFeatureLockedLocaleString() {
        return pluginRef.getLocaleManager().getString("Ability.Generic.Template.Lock",
                pluginRef.getLocaleManager().getString("Party.Feature.Locked."
                                + StringUtils.getPrettyPartyFeatureString(this).replace(" ", ""),
                        pluginRef.getPartyManager().getPartyFeatureUnlockLevel(this)));
    }

    public boolean hasPermission(Player player) {
        PartySubcommandType partySubCommandType;
        switch (this) {
            case CHAT:
                partySubCommandType = PartySubcommandType.CHAT;
                break;
            case TELEPORT:
                partySubCommandType = PartySubcommandType.TELEPORT;
                break;
            case ALLIANCE:
                partySubCommandType = PartySubcommandType.ALLIANCE;
                break;
            case ITEM_SHARE:
                partySubCommandType = PartySubcommandType.ITEMSHARE;
                break;
            case XP_SHARE:
                partySubCommandType = PartySubcommandType.XPSHARE;
                break;
            default:
                return false;
        }


        return Permissions.partySubcommand(player, partySubCommandType);
    }
}
