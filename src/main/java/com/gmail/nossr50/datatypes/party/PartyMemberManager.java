package com.gmail.nossr50.datatypes.party;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.Misc;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PartyMemberManager {

    private final @NotNull PersistentPartyData persistentPartyData;
    
    public PartyMemberManager(@NotNull PersistentPartyData persistentPartyData) {
        this.persistentPartyData = persistentPartyData;
    }

    /**
     * Grab all party members for this party
     *
     * @return all party members
     */
    public @NotNull Set<PartyMember> getPartyMembers() {
        return persistentPartyData.getPartyMembers();
    }

    /**
     * Add a {@link PartyMember} to this {@link Party} with a designated rank
     * If you are adding a {@link PartyMemberRank} of Leader, any existing party leaders will be demoted to a regular member of the party
     *
     * @param playerUUID target player's uuid
     * @param partyMemberRank target rank
     */
    public void addPartyMember(@NotNull UUID playerUUID, @NotNull PartyMemberRank partyMemberRank) {
        //TODO: Prevent adding multiple leaders
        //TODO: Call event
        persistentPartyData.getPartyMembers().add(new PartyMember(playerUUID, partyMemberRank));
    }

    /**
     * Get party members that are "Visible" to a target {@link Player}
     *
     * @param player target {@link Player}
     * @return returns a {@link HashSet<PartyMember>} which are visible to the player
     */
    public @NotNull HashSet<PartyMember> getVisibleMembers(@NotNull Player player)
    {
        HashSet<PartyMember> visibleMembers = new HashSet<>();

        for(PartyMember partyMember : persistentPartyData.getPartyMembers())
        {
            if(partyMember.getOfflinePlayer().getPlayer() == null)
                continue;

            if(player.canSee(partyMember.getOfflinePlayer().getPlayer()))
                visibleMembers.add(partyMember);
        }

        return visibleMembers;
    }

    /**
     * Change the leader of a party to the provided UUID
     *
     * @param playerUUID the UUID of the new party leader
     */
    public void changeLeader(@NotNull UUID playerUUID) {
        //TODO: implementation
    }

    public boolean hasMember(@NotNull UUID playerUUID) {
        for(PartyMember partyMember : persistentPartyData.getPartyMembers()) {
            if(partyMember.getUniqueId().equals(playerUUID))
                return true;
        }
        return false;
    }

    public boolean hasMember(@NotNull Player player) {
        return hasMember(player.getUniqueId());
    }

    public boolean hasMember(@NotNull OfflinePlayer offlinePlayer) {
        return hasMember(offlinePlayer.getUniqueId());
    }

    /**
     * Checks for a party member by player name, this method is unreliable and should be avoided.
     * Not case sensitive
     *
     * @param playerName target player name
     * @return true if the a matching player is found
     * @deprecated Unreliable, use UUID instead
     */
    @Deprecated
    public boolean hasMember(@NotNull String playerName) {
        for(PartyMember partyMember : getPartyMembers()) {
            if(partyMember.getName().equalsIgnoreCase(playerName))
                return true;
        }

        return false;
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

    public @NotNull PartyMember getPartyLeader() {
        return persistentPartyData.getPartyLeader();
    }
}
