package com.gmail.nossr50.party;

import com.gmail.nossr50.commands.party.PartySubCommandType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.text.StringUtils;
import org.bukkit.entity.Player;

public enum PartyFeature {
    CHAT,
    TELEPORT,
    XP_SHARE;

    public String getLocaleString() {
        return LocaleLoader.getString("Party.Feature." + StringUtils.getPrettyPartyFeatureString(this).replace(" ", ""));
    }

    public String getFeatureLockedLocaleString() {
        return LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Party.Feature.Locked." + StringUtils.getPrettyPartyFeatureString(this).replace(" ", ""), mcMMO.p.getGeneralConfig().getPartyFeatureUnlockLevel(this)));
    }

    public boolean hasPermission(Player player) {
        PartySubCommandType partySubCommandType;
        switch (this) {
            case CHAT:
                partySubCommandType = PartySubCommandType.CHAT;
                break;
            case TELEPORT:
                partySubCommandType = PartySubCommandType.TELEPORT;
                break;
            case XP_SHARE:
                partySubCommandType = PartySubCommandType.XPSHARE;
                break;
            default:
                return false;
        }


        return Permissions.partySubcommand(player, partySubCommandType);
    }
}
