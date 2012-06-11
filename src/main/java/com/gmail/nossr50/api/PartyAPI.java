package com.gmail.nossr50.api;

import java.util.List;

import org.bukkit.entity.Player;

import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.Users;

public final class PartyAPI {

    private PartyAPI() {}

    /**
     * Get the name of the party a player is in.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to check the party name of
     * @return the name of the player's party
     */
    public static String getPartyName(Player player) {
        return Users.getProfile(player).getParty().getName();
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
        return Users.getProfile(player).inParty();
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
        return PartyManager.getInstance().inSameParty(playera, playerb);
    }

    /**
     * Get a list of all current parties.
     * </br>
     * This function is designed for API usage.
     *
     * @return the list of parties.
     */
    public static List<Party> getParties() {
        return PartyManager.getInstance().getParties();
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
        PartyManager.getInstance().addToParty(player.getName(), Users.getProfile(player), PartyManager.getInstance().getParty(partyName)); //TODO this will throw a NPE if the party doesn't exist
    }

    /**
     * Remove a player from a party.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to remove
     */
    public static void removeFromParty(Player player) {
        PartyManager.getInstance().removeFromParty(player.getName(), Users.getProfile(player).getParty());
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
        return PartyManager.getInstance().getPartyLeader(partyName);
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
        PartyManager.getInstance().setPartyLeader(player, PartyManager.getInstance().getParty(partyName));
    }

    /**
     * Get a list of all players in this player's party.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to check
     * @return all the players in the player's party
     */
    public static List<String> getAllMembers(Player player) {
        return PartyManager.getInstance().getAllMembers(player);
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
        return PartyManager.getInstance().getOnlineMembers(partyName);
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
        return PartyManager.getInstance().getOnlineMembers(player);
    }
}
