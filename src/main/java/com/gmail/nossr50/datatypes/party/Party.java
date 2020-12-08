package com.gmail.nossr50.datatypes.party;

import com.gmail.nossr50.chat.SamePartyPredicate;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.Misc;
import com.google.common.base.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public class Party {
    private final @NotNull Predicate<CommandSender> samePartyPredicate;
    private final @NotNull PersistentPartyData persistentPartyData;
    private final @NotNull PartyMemberManager partyMemberManager;
    private final @NotNull PartyExperienceManager partyExperienceManager;

    public Party(@NotNull PersistentPartyData persistentPartyData) {
        this.persistentPartyData = persistentPartyData;

        //Initialize Managers
        partyMemberManager = new PartyMemberManager(persistentPartyData);
        partyExperienceManager = new PartyExperienceManager();
        samePartyPredicate = new SamePartyPredicate<>(this);
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
        Party party = (Party) o;
        return Objects.equal(persistentPartyData, party.persistentPartyData);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(persistentPartyData);
    }

    public @NotNull Predicate<CommandSender> getSamePartyPredicate() {
        return samePartyPredicate;
    }
}
