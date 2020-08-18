package com.gmail.nossr50.datatypes.party;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class PartyMemberManager {
    private final @NotNull Map<String, PartyMember> partyMembers;
    private @NotNull PartyMember partyLeaderRef;
    private final @NotNull HashSet<PartyMember> partyOfficers;

    public PartyMemberManager(@NotNull HashSet<PartyMember> partyMembers) {
        this.partyMembers = partyMembers;
    }

    public PartyMemberManager(@NotNull Player partyLeader) {
        addPartyMember(partyLeader, PartyMemberRank.LEADER);
    }

    public @NotNull Collection<PartyMember> getPartyMembers() {
        return partyMembers.values();
    }

    private void registerSpecialPartyMembers() {
        clearOfficers();

        for(PartyMember partyMember : partyMembers) {
            switch (partyMember.getPartyMemberRank()) {

                case MEMBER:
                    break;
                case OFFICER:
                    partyOfficers.add(partyMember);
                    break;
                case LEADER:
                    partyLeaderRef = partyMember;
                    break;
            }
        }
    }

    private void clearOfficers() {
        partyOfficers.clear();
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
