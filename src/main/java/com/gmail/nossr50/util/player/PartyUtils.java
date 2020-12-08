package com.gmail.nossr50.util.player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.datatypes.party.PartyMember;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PartyUtils {
    public static boolean isAllowed(@NotNull Party party, @NotNull PartyFeature partyFeature) {
        return party.getPartyExperienceManager().getLevel() >= Config.getInstance().getPartyFeatureUnlockLevel(partyFeature);
    }

    /**
     * Makes a formatted list of party members based on the perspective of a target player
     * Players that are hidden will be shown as offline (formatted in the same way)
     * Party leader will be formatted a specific way as well
     *
     * @param party target party
     * @param partyMember this player will be used for POV styling
     * @return formatted list of party members from the POV of a player
     */
    public String createMembersList(@NotNull Party party, @NotNull PartyMember partyMember) {
        StringBuilder memberList = new StringBuilder();
        List<String> coloredNames = new ArrayList<>();

        //Party member should always be online when this code is executed
        Player player = partyMember.getOfflinePlayer().getPlayer();

        if(player == null)
            return "NULL PARTY LIST";

        for(PartyMember otherPartyMember : party.getPartyMembers()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(otherPartyMember.getUniqueId());

            if(offlinePlayer.isOnline() && player.canSee((Player) offlinePlayer)) {
                ChatColor onlineColor = party.getPartyMemberManager().get.getUniqueId().equals(otherPartyMember.getUniqueId()) ? ChatColor.GOLD : ChatColor.GREEN;
                coloredNames.add(onlineColor + offlinePlayer.getName());
            } else {
                coloredNames.add(ChatColor.DARK_GRAY + members.get(playerUUID));
            }
        }

        buildChatMessage(memberList, coloredNames.toArray(new String[0]));
        return memberList.toString();
    }
}
