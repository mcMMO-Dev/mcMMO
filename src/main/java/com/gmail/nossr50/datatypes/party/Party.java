package com.gmail.nossr50.datatypes.party;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.Misc;
import com.google.common.base.Objects;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Party {
    private final @NotNull PersistentPartyData persistentPartyData;
    private final @NotNull PartyMemberManager partyMemberManager;
    private final @NotNull PartyExperienceManager partyExperienceManager;

    public Party(@NotNull PersistentPartyData persistentPartyData) {
        this.persistentPartyData = persistentPartyData;

        //Initialize Managers
        partyMemberManager = new PartyMemberManager();
        partyExperienceManager = new PartyExperienceManager();
    }

    public @NotNull PartyMemberManager getPartyMemberManager() {
        return partyMemberManager;
    }

    public @NotNull PartyExperienceManager getPartyExperienceManager() {
        return partyExperienceManager;
    }

    public Set<PartyMember> getPartyMembers() {
        return partyMemberManager.getPartyMembers();
    }

    public String getPartyName() {
        return persistentPartyData.getPartyName();
    }

    public void setLeader(UUID newPartyLeader) {
        this.partyMemberManager = ;
    }

    public boolean hasMember(Player player) {
        return hasMember(player.getUniqueId());
    }

    public boolean hasMember(OfflinePlayer offlinePlayer) {
        return hasMember(offlinePlayer.getUniqueId());
    }

    public boolean hasMember(UUID playerUUID) {
        for(PartyMember partyMember : getPartyMembers()) {
            if(partyMember.getUniqueId().equals(playerUUID))
                return true;
        }

        return false;
    }

    public boolean hasMember(String playerName) {
        for(PartyMember partyMember : getPartyMembers()) {
            if(partyMember.getName().equalsIgnoreCase(playerName))
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Party party = (Party) o;
        return Objects.equal(persistentPartyData, party.persistentPartyData);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(persistentPartyData);
    }
}
