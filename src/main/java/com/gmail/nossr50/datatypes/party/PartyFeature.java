package com.gmail.nossr50.datatypes.party;

import com.gmail.nossr50.commands.party.PartySubcommandType;
import com.gmail.nossr50.mcMMO;
import org.bukkit.entity.Player;

public enum PartyFeature {
    CHAT,
    TELEPORT,
    ALLIANCE,
    ITEM_SHARE,
    XP_SHARE;

    //TODO: hacky...
    public boolean hasPermission(mcMMO pluginRef, Player player) {
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

        return pluginRef.getPermissionTools().partySubcommand(player, partySubCommandType);
    }
}
