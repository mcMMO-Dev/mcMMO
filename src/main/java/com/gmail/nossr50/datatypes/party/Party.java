package com.gmail.nossr50.datatypes.party;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.Misc;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class Party {
    private final String partyName;
    private String partyPassword;
    private boolean partyLock;

    private final PartyMemberManager partyMemberManager;
    private final PartyItemShareManager partyItemShareManager;
    private final PartyExperienceManager partyExperienceManager;
    private final PartyAllianceManager partyAllianceManager;

    public Party(Persistent)

    public HashSet<PartyMember> getPartyMembers() {
        return partyMemberManager.getPartyMembers();
    }

    public String getPartyName() {
        return partyName;
    }

    public String getPartyPassword() {
        return partyPassword;
    }

    public boolean isLocked() {
        return partyLock;
    }

    public Party getAlly() {
        return ally;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public void setLeader(UUID newPartyLeader) {
        this.partyMemberManager = ;
    }

    public void setPartyPassword(String partyPassword) {
        this.partyPassword = partyPassword;
    }

    public void setPartyLock(boolean partyLock) {
        this.partyLock = partyLock;
    }

    public boolean hasMember(Player player) {
        for(PartyMember partyMember : getPartyMembers()) {
            if(partyMember.getUniqueId().equals(player.getUniqueId()))
                return true;
        }

        return false;
    }

    /**
     * Makes a formatted list of party members based on the perspective of a target player
     * Players that are hidden will be shown as offline (formatted in the same way)
     * Party leader will be formatted a specific way as well
     * @param player target player to use as POV
     * @return formatted list of party members from the POV of a player
     */
    public String createMembersList(Player player) {
        /* BUILD THE PARTY LIST WITH FORMATTING */
        boolean useDisplayNames = Config.getInstance().getPartyDisplayNames();

        StringBuilder formattedPartyMemberList = new StringBuilder();

        PartyMember partyLeader = getLeader();

        //First add the party leader
        memberList.append(PARTY_LEADER_PREFIX);

        return memberList.toString();
    }

    /**
     * Get the near party members.
     *
     * @param mmoPlayer The player to check
     * @return the near party members
     */
    public List<Player> getNearMembers(McMMOPlayer mmoPlayer) {
        List<Player> nearMembers = new ArrayList<>();
        Party party = mmoPlayer.getParty();

        if (party != null) {
            Player player = mmoPlayer.getPlayer();
            double range = Config.getInstance().getPartyShareRange();

            for (PartyMember partyMember : party.getPartyMembers()) {
                if (!player.getUniqueId().equals(partyMember.getOfflinePlayer().getUniqueId())
                        && partyMember.getOfflinePlayer().isOnline()
                        && partyMember.getOfflinePlayer().getPlayer() != null
                        && Misc.isNear(player.getLocation(), partyMember.getOfflinePlayer().getPlayer().getLocation(), range)) {
                    nearMembers.add(partyMember.getOfflinePlayer().getPlayer());
                }
            }
        }

        return nearMembers;
    }

    @Override
    public boolean equals(Object o) {
    }

    @Override
    public int hashCode() {
        return ;
    }
}
