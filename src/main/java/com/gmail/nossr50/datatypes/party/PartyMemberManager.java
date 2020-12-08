package com.gmail.nossr50.datatypes.party;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.dirtydata.DirtySet;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.Misc;
import com.neetgames.mcmmo.exceptions.InvalidPlayerException;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PartyMemberManager {

    private final @NotNull PersistentPartyData persistentPartyData;
    private final @NotNull HashMap<UUID, PartyMember> partyMemberMap;
    private @Nullable PartyMember partyLeaderRef;
    
    public PartyMemberManager(@NotNull PersistentPartyData persistentPartyData) {
        this.persistentPartyData = persistentPartyData;
        this.partyMemberMap = new HashMap<>();
        initPartyLeaderRef();
    }

    /**
     * Grab all party members for this party
     *
     * @return all party members
     */
    public @NotNull DirtySet<PartyMember> getPartyMembers() {
        return persistentPartyData.getPartyMembers();
    }

    /**
     * Grab a specific {@link PartyMember} by {@link UUID}
     *
     * @param playerUUID target UUID
     * @return the party member if they exist, otherwise null
     */
    public @Nullable PartyMember getPartyMember(@NotNull UUID playerUUID) {
        return partyMemberMap.get(playerUUID);
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
        PartyMember partyMember = new PartyMember(playerUUID, partyMemberRank);
        persistentPartyData.getPartyMembers().add(partyMember);
        partyMemberMap.put(playerUUID, partyMember);
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
    public void changeLeader(@NotNull UUID playerUUID) throws RuntimeException {
        if(hasMember(playerUUID)) {
            //TODO: implementation
                for(PartyMember partyMember : getPartyMembers()) {
                    if (partyMember.getPartyMemberRank() == PartyMemberRank.LEADER) {
                        partyMember.setPartyMemberRank(PartyMemberRank.MEMBER);
                    }
                }

            partyLeaderRef = partyMemberMap.get(playerUUID);
            partyLeaderRef.setPartyMemberRank(PartyMemberRank.LEADER);
        } else {
            throw new RuntimeException();
        }
    }

    private void initPartyLeaderRef() {
        for(PartyMember partyMember : getPartyMembers()) {
            if(partyMember.getPartyMemberRank() == PartyMemberRank.LEADER) {
                partyLeaderRef = partyMember;
                break;
            }
        }
    }

    /**
     * Retrieves a party leader, if one doesn't exist a "random" player is forcibly promoted to leader
     * Random being the first player in the set
     *
     * @return the party leader
     */
    public @NotNull PartyMember getPartyLeader() {
        if(partyLeaderRef == null) {
            //The first player in a party is now the leader
            partyLeaderRef = (PartyMember) getPartyMembers().unwrapSet().toArray()[0];
            partyLeaderRef.setPartyMemberRank(PartyMemberRank.LEADER);
        }

        return partyLeaderRef;
    }

    public boolean hasMember(@NotNull UUID playerUUID) {
        return partyMemberMap.containsKey(playerUUID);
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
    public @NotNull List<Player> getNearMembers(@NotNull McMMOPlayer mmoPlayer) {
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
}
