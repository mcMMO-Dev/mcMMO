package com.gmail.nossr50.api;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.player.UserManager;

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
        if (!inParty(player)) {
            return null;
        }

        return UserManager.getPlayer(player).getParty().getName();
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
        return UserManager.getPlayer(player).inParty();
    }

    /**
     * Check if two players are in the same party.
     * </br>
     * This function is designed for API usage.
     *
     * @param playera The first player to check
     * @param playerb The second player to check
     * @return true if the two players are in the same party, false otherwise
     */
    public static boolean inSameParty(Player playera, Player playerb) {
        return PartyManager.inSameParty(playera, playerb);
    }

    /**
     * Get a list of all current parties.
     * </br>
     * This function is designed for API usage.
     *
     * @return the list of parties.
     */
    public static List<Party> getParties() {
        return PartyManager.getParties();
    }

    /**
     * Add a player to a party.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add to the party
     * @param partyName The party to add the player to
     */
    public static void addToParty(Player player, String partyName) {
        Party party = PartyManager.getParty(partyName);

        if (party == null) {
            party = new Party();
            party.setName(partyName);
            party.setLeader(player.getName());
        }

        PartyManager.addToParty(player, UserManager.getPlayer(player), party);
    }

    /**
     * Remove a player from a party.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to remove
     */
    public static void removeFromParty(Player player) {
        PartyManager.removeFromParty(player, UserManager.getPlayer(player).getParty());
    }

    /**
     * Get the leader of a party.
     * </br>
     * This function is designed for API usage.
     *
     * @param partyName The party name
     * @return the leader of the party
     */
    public static String getPartyLeader(String partyName) {
        return PartyManager.getPartyLeader(partyName);
    }

    /**
     * Set the leader of a party.
     * </br>
     * This function is designed for API usage.
     *
     * @param partyName The name of the party to set the leader of
     * @param player The player to set as leader
     */
    public static void setPartyLeader(String partyName, String player) {
        PartyManager.setPartyLeader(player, PartyManager.getParty(partyName));
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
        List<OfflinePlayer> members = new ArrayList<OfflinePlayer>();

        for (String memberName : PartyManager.getAllMembers(player)) {
            OfflinePlayer member = mcMMO.p.getServer().getOfflinePlayer(memberName);
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
    public static LinkedHashSet<String> getMembers(Player player) {
        return PartyManager.getAllMembers(player);
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
        return PartyManager.getOnlineMembers(partyName);
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
        return PartyManager.getOnlineMembers(player);
    }
}
