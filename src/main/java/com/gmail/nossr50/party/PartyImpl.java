package com.gmail.nossr50.party;

import com.gmail.nossr50.chat.SamePartyPredicate;
import com.neetgames.mcmmo.party.Party;
import com.neetgames.mcmmo.party.PartyExperience;
import com.neetgames.mcmmo.party.PartyMember;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public class PartyImpl implements Party {
    private final @NotNull Predicate<CommandSender> samePartyPredicate;
    private final @NotNull PersistentPartyData persistentPartyData;
    private final @NotNull PartyMemberManagerImpl partyMemberManagerImpl;
    private final @NotNull PartyExperience partyExperienceManager;

    public PartyImpl(@NotNull PersistentPartyData persistentPartyData) {
        this.persistentPartyData = persistentPartyData;

        //Initialize Managers
        partyMemberManagerImpl = new PartyMemberManagerImpl(persistentPartyData);
        partyExperienceManager = new PartyExperienceManagerImpl(partyMemberManagerImpl, this);
        samePartyPredicate = new SamePartyPredicate<>(this);
    }

    public @NotNull PartyMemberManagerImpl getPartyMemberManager() {
        return partyMemberManagerImpl;
    }

    public @NotNull PartyExperience getPartyExperienceManager() {
        return partyExperienceManager;
    }

    public @NotNull Set<PartyMember> getPartyMembers() {
        return partyMemberManagerImpl.getPartyMembers();
    }

    public @NotNull String getPartyName() {
        return persistentPartyData.getPartyName();
    }

    @Override
    public @Nullable PartyMember getPartyMember(@NotNull OnlineMMOPlayer onlineMMOPlayer) {
        return getPartyMemberManager().getPartyMember(onlineMMOPlayer.getUUID());
    }

    private void buildChatMessage(@NotNull StringBuilder stringBuilder, String @NotNull [] names) {
        for(int i = 0; i < names.length; i++) {
            if(i + 1 >= names.length) {
                stringBuilder
                        .append(names[i]);
            } else {
                stringBuilder
                        .append(names[i])
                        .append(" ");
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartyImpl party = (PartyImpl) o;
        return samePartyPredicate.equals(party.samePartyPredicate)
                && persistentPartyData.equals(party.persistentPartyData)
                && partyMemberManagerImpl.equals(party.partyMemberManagerImpl)
                && partyExperienceManager.equals(party.partyExperienceManager);
    }

    @Override
    public int hashCode() {
        return Objects.hash(samePartyPredicate, persistentPartyData, partyMemberManagerImpl, partyExperienceManager);
    }

    public @Nullable PartyMember getPartyMember(@NotNull Player player) {
        return getPartyMember(player.getUniqueId());
    }

    public @Nullable PartyMember getPartyMember(@NotNull UUID playerUUID) {
        return partyMemberManagerImpl.getPartyMember(playerUUID);
    }

    public @NotNull Predicate<CommandSender> getSamePartyPredicate() {
        return samePartyPredicate;
    }
}
