package com.gmail.nossr50.api;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.party.Party;

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
        String location = "plugins/mcMMO/mcmmo.users";
        ArrayList<String> parties = new ArrayList<String>();

        try {

            //Open the users file
            FileReader file = new FileReader(location);
            BufferedReader in = new BufferedReader(file);
            String line = "";

            while((line = in.readLine()) != null) {
                String[] character = line.split(":");
                String theparty = null;

                //Party
                if (character.length > 3) {
                    theparty = character[3];
                }

                if (!parties.contains(theparty)) {
                    parties.add(theparty);
                }
            }
            in.close();
        }
        catch (Exception e) {
            Bukkit.getLogger().severe("Exception while reading " + location + " (Are you sure you formatted it correctly?)" + e.toString());
        }
        return parties;
    }

    /**
     * Get a list of all players in this player's party.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to check
     * @return all the players in the player's party
     */
    public ArrayList<Player> getPartyMembers(Player player) {
        return Party.getInstance().getPartyMembers(player);
    }

    
}
