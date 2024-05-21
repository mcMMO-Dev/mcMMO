package com.gmail.nossr50.api;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.PartyLeader;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class PartyAPI {
    private PartyAPI() {}

    /**
     * Get the name of the party a player is in.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to check the party name of
     * @return the name of the player's party, or null if not in a party
     */
    public static String getPartyName(Player player) {
        if (!mcMMO.p.getPartyConfig().isPartyEnabled() || !inParty(player)) {
            return null;
        }

        return UserManager.getPlayer(player).getParty().getName();
    }

    /**
     * Check if the party system is enabled.
     *
     * @return true if the party system is enabled, false otherwise
     */
    public static boolean isPartySystemEnabled() {
        return mcMMO.p.getPartyConfig().isPartyEnabled();
    }

    /**
     * Checks if a player is in a party.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to check
     * @return true if the player is in a party, false otherwise
     */
    public static boolean inParty(Player player) {
        if (!mcMMO.p.getPartyConfig().isPartyEnabled() || UserManager.getPlayer(player) == null)
            return false;

        return UserManager.getPlayer(player).inParty();
    }

    /**
     * Check if two players are in the same party.
     * </br>
     * This function is designed for API usage.
     *
     * @param playerA The first player to check
     * @param playerB The second player to check
     * @return true if the two players are in the same party, false otherwise
     */
    public static boolean inSameParty(Player playerA, Player playerB) {
        if (!mcMMO.p.getPartyConfig().isPartyEnabled())
            return false;

        return mcMMO.p.getPartyManager().inSameParty(playerA, playerB);
    }

    /**
     * Get a list of all current parties.
     * </br>
     * This function is designed for API usage.
     *
     * @return the list of parties.
     */
    public static List<Party> getParties() {
        return mcMMO.p.getPartyManager().getParties();
    }

    /**
     * Add a player to a party.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add to the party
     * @param partyName The party to add the player to
     * @deprecated parties can have limits, use the other method
     */
    @Deprecated
    public static void addToParty(Player player, String partyName) {
        if (!mcMMO.p.getPartyConfig().isPartyEnabled()) {
            return;
        }

        //Check if player profile is loaded
        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        if (mmoPlayer == null)
            return;

        Party party = mcMMO.p.getPartyManager().getParty(partyName);

        if (party == null) {
            party = new Party(new PartyLeader(player.getUniqueId(), player.getName()), partyName);
        } else {
            if (mcMMO.p.getPartyManager().isPartyFull(player, party)) {
                NotificationManager.sendPlayerInformation(player, NotificationType.PARTY_MESSAGE, "Commands.Party.PartyFull", party.toString());
                return;
            }
        }

        mcMMO.p.getPartyManager().addToParty(mmoPlayer, party);
    }

    /**
     * The max party size of the server
     * 0 or less for no size limit
     * @return the max party size on this server
     */
    public static int getMaxPartySize() {
        return mcMMO.p.getGeneralConfig().getPartyMaxSize();
    }

    /**
     * Add a player to a party.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add to the party
     * @param partyName The party to add the player to
     * @param bypassLimit if true bypasses party size limits
     */
    public static void addToParty(Player player, String partyName, boolean bypassLimit) {
        //Check if player profile is loaded
        if (!mcMMO.p.getPartyConfig().isPartyEnabled() || UserManager.getPlayer(player) == null)
            return;

        Party party = mcMMO.p.getPartyManager().getParty(partyName);

        if (party == null) {
            party = new Party(new PartyLeader(player.getUniqueId(), player.getName()), partyName);
        }

        mcMMO.p.getPartyManager().addToParty(UserManager.getPlayer(player), party);
    }

    /**
     * Remove a player from a party.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to remove
     */
    public static void removeFromParty(Player player) {
        //Check if player profile is loaded
        if (!mcMMO.p.getPartyConfig().isPartyEnabled() || UserManager.getPlayer(player) == null)
            return;

        mcMMO.p.getPartyManager().removeFromParty(UserManager.getPlayer(player));
    }

    /**
     * Get the leader of a party.
     * </br>
     * This function is designed for API usage.
     *
     * @param partyName The party name
     * @return the leader of the party
     */
    public static @Nullable String getPartyLeader(String partyName) {
        if (!mcMMO.p.getPartyConfig().isPartyEnabled())
            return null;

        return mcMMO.p.getPartyManager().getPartyLeaderName(partyName);
    }

    /**
     * Set the leader of a party.
     * </br>
     * This function is designed for API usage.
     *
     * @param partyName The name of the party to set the leader of
     * @param playerName The playerName to set as leader
     */
    @Deprecated
    public static void setPartyLeader(String partyName, String playerName) {
        if (!mcMMO.p.getPartyConfig().isPartyEnabled())
            return;

        mcMMO.p.getPartyManager().setPartyLeader(mcMMO.p.getServer().getOfflinePlayer(playerName).getUniqueId(), mcMMO.p.getPartyManager().getParty(partyName));
    }

    /**
     * Get a list of all players in this player's party.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to check
     * @return all the players in the player's party
     */
    @Deprecated
    public static List<OfflinePlayer> getOnlineAndOfflineMembers(Player player) {
        if (!mcMMO.p.getPartyConfig().isPartyEnabled()) {
            return null;
        }

        List<OfflinePlayer> members = new ArrayList<>();
        for (UUID memberUniqueId : mcMMO.p.getPartyManager().getAllMembers(player).keySet()) {
            OfflinePlayer member = mcMMO.p.getServer().getOfflinePlayer(memberUniqueId);
            members.add(member);
        }
        return members;
    }

    /**
     * Get a list of all player names in this player's party.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to check
     * @return all the player names in the player's party
     */
    @Deprecated
    public static LinkedHashSet<String> getMembers(Player player) {
        if (!mcMMO.p.getPartyConfig().isPartyEnabled())
            return null;

        return (LinkedHashSet<String>) mcMMO.p.getPartyManager().getAllMembers(player).values();
    }

    /**
     * Get a list of all player names and uuids in this player's party.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to check
     * @return all the player names and uuids in the player's party
     */
    public static LinkedHashMap<UUID, String> getMembersMap(Player player) {
        if (!mcMMO.p.getPartyConfig().isPartyEnabled())
            return null;

        return mcMMO.p.getPartyManager().getAllMembers(player);
    }

    /**
     * Get a list of all online players in this party.
     * </br>
     * This function is designed for API usage.
     *
     * @param partyName The party to check
     * @return all online players in this party
     */
    public static List<Player> getOnlineMembers(String partyName) {
        if (!mcMMO.p.getPartyConfig().isPartyEnabled())
            return null;

        return mcMMO.p.getPartyManager().getOnlineMembers(partyName);
    }

    /**
     * Get a list of all online players in this player's party.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to check
     * @return all online players in the player's party
     */
    public static List<Player> getOnlineMembers(Player player) {
        return mcMMO.p.getPartyManager().getOnlineMembers(player);
    }

    public static boolean hasAlly(String partyName) {
        return getAllyName(partyName) != null;
    }

    public static String getAllyName(String partyName) {
        Party ally = mcMMO.p.getPartyManager().getParty(partyName).getAlly();
        if (ally != null) {
            return ally.getName();
        }

        return null;
    }
}
