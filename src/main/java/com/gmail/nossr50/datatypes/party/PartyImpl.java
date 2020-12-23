package com.gmail.nossr50.datatypes.party;

import com.gmail.nossr50.chat.SamePartyPredicate;
import com.neetgames.mcmmo.party.Party;
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
    private final @NotNull PartyMemberManager partyMemberManager;
    private final @NotNull PartyExperienceManager partyExperienceManager;

    public PartyImpl(@NotNull PersistentPartyData persistentPartyData) {
        this.persistentPartyData = persistentPartyData;

        //Initialize Managers
        partyMemberManager = new PartyMemberManager(persistentPartyData);
        partyExperienceManager = new PartyExperienceManager(partyMemberManager, this);
        samePartyPredicate = new SamePartyPredicate<>(this);
    }

    public @NotNull PartyMemberManager getPartyMemberManager() {
        return partyMemberManager;
    }

    public @NotNull PartyExperienceManager getPartyExperienceManager() {
        return partyExperienceManager;
    }

    public @NotNull Set<PartyMember> getPartyMembers() {
        return partyMemberManager.getPartyMembers();
    }

    public @NotNull String getPartyName() {
        return persistentPartyData.getPartyName();
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
                && partyMemberManager.equals(party.partyMemberManager)
                && partyExperienceManager.equals(party.partyExperienceManager);
    }

    @Override
    public int hashCode() {
        return Objects.hash(samePartyPredicate, persistentPartyData, partyMemberManager, partyExperienceManager);
    }

    public @Nullable PartyMember getPartyMember(@NotNull Player player) {
        return getPartyMember(player.getUniqueId());
    }

    public @Nullable PartyMember getPartyMember(@NotNull UUID playerUUID) {
        return partyMemberManager.getPartyMember(playerUUID);
    }

    public @NotNull Predicate<CommandSender> getSamePartyPredicate() {
        return samePartyPredicate;
    }
}
