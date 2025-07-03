package com.gmail.nossr50.datatypes.party;

import static com.gmail.nossr50.util.text.ConfigStringUtils.getConfigPartyFeatureString;

import com.gmail.nossr50.commands.party.PartySubcommandType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import org.bukkit.entity.Player;

public enum PartyFeature {
    CHAT,
    TELEPORT,
    ALLIANCE,
    ITEM_SHARE,
    XP_SHARE;

    public String getLocaleString() {
        return LocaleLoader.getString("Party.Feature." + getConfigPartyFeatureString(this));
    }

    public String getFeatureLockedLocaleString() {
        return LocaleLoader.getString("Ability.Generic.Template.Lock",
                LocaleLoader.getString("Party.Feature.Locked." + getConfigPartyFeatureString(this),
                        mcMMO.p.getGeneralConfig().getPartyFeatureUnlockLevel(this)));
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
