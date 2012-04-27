package com.gmail.nossr50.api;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.util.Users;

public class PartyAPI {

    /**
     * Get the name of the party a player is in.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to check the party name of
     * @return the name of the player's party
     */
    public String getPartyName(Player player) {
        return Users.getProfile(player).getParty();
    }

    /**
     * Checks if a player is in a party.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to check
     * @return true if the player is in a party, false otherwise
     */
    public boolean inParty(Player player) {
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
    public boolean inSameParty(Player playera, Player playerb) {
        return Party.getInstance().inSameParty(playera, playerb);
    }

    /**
     * Get a list of all current party names.
     * </br>
     * This function is designed for API usage.
     *
     * @return the list of parties.
     */
    public ArrayList<String> getParties() {
        return Party.getInstance().getParties();
    }

    /**
     * Add a player to a party.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add to the party
     * @param partyName The party to add the player to
     */
    public void addToParty(Player player, String partyName) {
        Party.getInstance().addToParty(player, Users.getProfile(player), partyName, false, null);
    }

    /**
     * Remove a player from a party.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to remove
     */
    public void removeFromParty(Player player) {
        Party.getInstance().removeFromParty(player, Users.getProfile(player));
    }

    /**
     * Get the leader of a party.
     * </br>
     * This function is designed for API usage.
     *
     * @param partyName The party name
     * @return the leader of the party
     */
    public Player getPartyLeader(String partyName) {
        return Party.getInstance().getPartyLeader(partyName);
    }

    /**
     * Set the leader of a party.
     * </br>
     * This function is designed for API usage.
     *
     * @param partyName The name of the party to set the leader of
     * @param player The player to set as leader
     */
    public void setPartyLeader(String partyName, String player) {
        Party.getInstance().setPartyLeader(partyName, player);
    }

    /**
     * Get a list of all players in this player's party.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to check
     * @return all the players in the player's party
     */
    public ArrayList<Player> getAllMembers(Player player) {
        return Party.getInstance().getAllMembers(player);
    }

    /**
     * Get a list of all online players in this party.
     * </br>
     * This function is designed for API usage.
     *
     * @param partyName The party to check
     * @return all online players in this party
     */
    public ArrayList<Player> getOnlineMembers(String partyName) {
        return Party.getInstance().getOnlineMembers(partyName);
    }

    /**
     * Get a list of all online players in this player's party.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to check
     * @return all online players in the player's party
     */
    public ArrayList<Player> getOnlineMembers(Player player) {
        return Party.getInstance().getOnlineMembers(player);
    }
}
