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
        StringBuilder memberList = new StringBuilder();
        List<String> coloredNames = new ArrayList<>();

        for(UUID playerUUID : members.keySet()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);

            if(offlinePlayer.isOnline() && player.canSee((Player) offlinePlayer)) {
                ChatColor onlineColor = leader.getUniqueId().equals(playerUUID) ? ChatColor.GOLD : ChatColor.GREEN;
                coloredNames.add(onlineColor + offlinePlayer.getName());
            } else {
                coloredNames.add(ChatColor.DARK_GRAY + members.get(playerUUID));
            }
        }

        buildChatMessage(memberList, coloredNames.toArray(new String[0]));
        return memberList.toString();
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

    public @NotNull Predicate<CommandSender> getSamePartyPredicate() {
        return samePartyPredicate;
    }
}
