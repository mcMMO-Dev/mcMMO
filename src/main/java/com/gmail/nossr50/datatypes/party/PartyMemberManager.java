package com.gmail.nossr50.datatypes.party;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class PartyMemberManager {

    private final @NotNull PersistentPartyData persistentPartyData;
    
    public PartyMemberManager(@NotNull PersistentPartyData persistentPartyData, @NotNull HashSet<PartyMember> partyMembers) {
        this.persistentPartyData = persistentPartyData;
    }

    public @NotNull Set<PartyMember> getPartyMembers() {
        return persistentPartyData.getPartyMembers();
    }

    public void addPartyMember(OfflinePlayer player, PartyMemberRank partyMemberRank) {
        //TODO: Prevent adding multiple leaders
        //TODO: Call event
        partyMembers.add(new PartyMember(player, partyMemberRank));
    }

    public HashSet<PartyMember> getVisibleMembers(Player player)
    {
        HashSet<PartyMember> visibleMembers = new HashSet<>();

        for(PartyMember partyMember : partyMembers)
        {
            if(partyMember.getOfflinePlayer().getPlayer() == null)
                continue;

            if(player.canSee(partyMember.getOfflinePlayer().getPlayer()))
                visibleMembers.add(partyMember);
        }

        return visibleMembers;
    }
}
