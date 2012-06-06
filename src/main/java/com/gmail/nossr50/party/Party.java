package com.gmail.nossr50.party;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.gmail.nossr50.McMMO;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Users;

public class Party {
    public static String pluginPath;
    public static String partyPlayersFile;
    public static String partyLocksFile;
    public static String partyPasswordsFile;

    HashMap<String, HashMap<String, Boolean>> partyPlayers = new HashMap<String, HashMap<String, Boolean>>();
    HashMap<String, Boolean> partyLocks = new HashMap<String, Boolean>();
    HashMap<String, String> partyPasswords = new HashMap<String, String>();

    private static McMMO plugin;
    private static volatile Party instance;

    private Party() {
        plugin = McMMO.p;
        pluginPath = plugin.getDataFolder().getPath();
        partyPlayersFile = pluginPath + File.separator + "FlatFileStuff" + File.separator + "partyPlayers";
        partyLocksFile = pluginPath + File.separator + "FlatFileStuff" + File.separator + "partyLocks";
        partyPasswordsFile = pluginPath + File.separator + "FlatFileStuff" + File.separator + "partyPasswords";
        new File(pluginPath + File.separator + "FlatFileStuff").mkdir();
        loadParties();
    }

    public static Party getInstance() {
        if (instance == null) {
            instance = new Party();
        }
        return instance;
    }

    /**
     * Check if two players are in the same party.
     *
     * @param playera The first player
     * @param playerb The second player
     * @return true if they are in the same party, false otherwise
     */
    public boolean inSameParty(Player playera, Player playerb){
        PlayerProfile PPa = Users.getProfile(playera);
        PlayerProfile PPb = Users.getProfile(playerb);

        if ((PPa.inParty() && PPb.inParty()) && (PPa.getParty().equals(PPb.getParty()))) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Get the number of players in this player's party.
     *
     * @param player The player to check
     * @param players A list of players to
     * @return the number of players in this player's party
     */
    public int partyCount(Player player) {
        if (player != null) {
            return getAllMembers(player).size();
        }
        else {
            return 0;
        }
    }

    private void informPartyMembers(Player player) {
        String playerName = player.getName();

        if (player != null) {
            for (Player p : getOnlineMembers(player)) {
                if (p.getName() != playerName) {
                    p.sendMessage(LocaleLoader.getString("Party.InformedOnJoin", new Object[] {playerName}));
                }
            }
        }
    }

    /**
     * Get a list of all online players in this player's party.
     *
     * @param player The player to check
     * @return all online players in the player's party
     */
    public ArrayList<Player> getOnlineMembers(Player player) {
        ArrayList<Player> players = new ArrayList<Player>();

        if (player != null) {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (inSameParty(player, p)) {
                    players.add(p);
                }
            }
        }

        return players;
    }

    /**
     * Get a list of all online players in this party.
     *
     * @param partyName The party to check
     * @return all online players in this party
     */
    public ArrayList<Player> getOnlineMembers(String partyName) {
        ArrayList<Player> players = new ArrayList<Player>();

        for (Player p : plugin.getServer().getOnlinePlayers()) {
            PlayerProfile PP = Users.getProfile(p);

            if (PP.inParty()) {
                if (PP.getParty().equalsIgnoreCase(partyName)) {
                    players.add(p);
                }
            }
        }

        return players;
    }

    /**
     * Get a list of all players in this player's party.
     *
     * @param player The player to check
     * @return all the players in the player's party
     */
    public ArrayList<Player> getAllMembers(Player player) {
        ArrayList<Player> players = new ArrayList<Player>();
        HashMap<Player, PlayerProfile> profiles = Users.getProfiles();

        if (player != null) {
            for (Player otherPlayer : profiles.keySet()) {
                if (otherPlayer != null && inSameParty(otherPlayer, player)) {
                    players.add(otherPlayer);
                }
            }
        }

        return players;
    }


    /**
     * Get a list of all current party names.
     *
     * @return the list of parties.
     */
    public ArrayList<String> getParties() {
        String location = McMMO.usersFile;
        ArrayList<String> parties = new ArrayList<String>();

        try {
            FileReader file = new FileReader(location);
            BufferedReader in = new BufferedReader(file);
            String line = "";

            while ((line = in.readLine()) != null) {
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
            plugin.getLogger().severe("Exception while reading " + location + " (Are you sure you formatted it correctly?)" + e.toString());
        }
        return parties;
    }

    /**
     * Notify party members when the party owner changes.
     *
     * @param newOwnerName The name of the new party owner
     */
    private void informPartyMembersOwnerChange(String newOwnerName) {
        Player newOwner = plugin.getServer().getPlayer(newOwnerName);

        if (newOwner != null) {
            for (Player p : getOnlineMembers(newOwner)) {
                if (p.getName() != newOwnerName) {
                    p.sendMessage(newOwnerName + " is the new party owner."); //TODO: Needs more locale
                }
            }
        }
    }

    /**
     * Notify party members when the a party member quits.
     *
     * @param player The player that quit
     */
    private void informPartyMembersQuit(Player player) {
        String playerName = player.getName();

        if (player != null) {
            for (Player p : getOnlineMembers(player)) {
                if (p.getName() != playerName) {
                    p.sendMessage(LocaleLoader.getString("Party.InformedOnQuit", new Object[] {playerName}));
                }
            }
        }
    }

    /**
     * Remove a player from a party.
     *
     * @param player The player to remove
     * @param PP The profile of the player to remove
     */
    public void removeFromParty(Player player, PlayerProfile PP) {
        String party = PP.getParty();
        String playerName = player.getName();

        //Stop NPE... hopefully
        if (!isParty(party) || !isInParty(player, PP)) {
            addToParty(player, PP, party, false, null);
        }

        informPartyMembersQuit(player);

        if (isPartyLeader(playerName, party)) {
            if (isPartyLocked(party)) {
                unlockParty(party);
            }
        }

        partyPlayers.get(party).remove(playerName);

        if (isPartyEmpty(party)) {
            deleteParty(party);
        }

        PP.removeParty();
        savePartyFile(partyPlayersFile, partyPlayers);
    }

    /**
     * Add a player to a party.
     *
     * @param player The player to add to the party
     * @param PP The profile of the player to add to the party
     * @param newParty The party to add the player to
     * @param invite true if the player was invited to this party, false otherwise
     * @param password the password for this party, null if there was no password
     */
    public void addToParty(Player player, PlayerProfile PP, String newParty, Boolean invite, String password) {
        String playerName = player.getName();

        //Fix for FFS
        newParty = newParty.replace(":", ".");

        //Don't care about passwords on invites
        if (!invite) {

            //Don't care about passwords if it isn't locked
            if (isPartyLocked(newParty)) {
                if (isPartyPasswordProtected(newParty)) {
                    if (password == null) {
                        player.sendMessage("This party requires a password. Use /party <party> <password> to join it."); //TODO: Needs more locale.
                        return;
                    }
                    else if(!password.equalsIgnoreCase(getPartyPassword(newParty))) {
                        player.sendMessage("Party password incorrect."); //TODO: Needs more locale.
                        return;
                    }
                }
                else {
                    player.sendMessage("Party is locked."); //TODO: Needs more locale.
                    return;
                }
            }
        }
        else {
            PP.acceptInvite();
        }

        //New party?
        if (!isParty(newParty)) {
            putNestedEntry(partyPlayers, newParty, playerName, true);

            //Get default locking behavior from config?
            partyLocks.put(newParty, false);
            partyPasswords.put(newParty, null);
            saveParties();
        }
        else {
            putNestedEntry(partyPlayers, newParty, playerName, false);
            savePartyFile(partyPlayersFile, partyPlayers);
        }

        PP.setParty(newParty);
        informPartyMembers(player);

        if (!invite) {
            player.sendMessage(LocaleLoader.getString("Commands.Party.Join", new Object[]{ newParty }));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Commands.Invite.Accepted", new Object[]{ PP.getParty() }));
        }
    }

    private static <U,V,W> W putNestedEntry(HashMap<U, HashMap<V, W>> nest, U nestKey, V nestedKey, W nestedValue) {
        HashMap<V,W> nested = nest.get(nestKey);

        if (nested == null) {
            nested = new HashMap<V,W>();
            nest.put(nestKey, nested);
        }

        return nested.put(nestedKey, nestedValue);
    }

    /**
     * Lock a party.
     *
     * @param partyName The party to lock
     */
    public void lockParty(String partyName) {
        partyLocks.put(partyName, true);
        savePartyFile(partyLocksFile, partyLocks);
    }

    /**
     * Unlock a party.
     *
     * @param partyName The party to unlock
     */
    public void unlockParty(String partyName) {
        partyLocks.put(partyName, false);
        savePartyFile(partyLocksFile, partyLocks);
    }

    /**
     * Delete a party.
     *
     * @param partyName The party to delete
     */
    private void deleteParty(String partyName) {
        partyPlayers.remove(partyName);
        partyLocks.remove(partyName);
        partyPasswords.remove(partyName);
        saveParties();
    }

    /**
     * Set the password for a party.
     *
     * @param partyName The party name
     * @param password The new party password
     */
    public void setPartyPassword(String partyName, String password) {
        if (password.equalsIgnoreCase("\"\"")) { //What's with that password string?
            password = null;
        }

        partyPasswords.put(partyName, password);
        savePartyFile(partyPasswordsFile, partyPasswords);
    }

    /**
     * Get the leader of a party.
     *
     * @param partyName The party name
     * @return the leader of the party
     */
    public Player getPartyLeader(String partyName) {
        Player leader = null;

        for (String name : partyPlayers.get(partyName).keySet()) {
            if (partyPlayers.get(partyName).get(name)) {
                leader = plugin.getServer().getPlayer(name);
                break;
            }
        }

        return leader;
    }

    /**
     * Set the leader of a party.
     *
     * @param partyName The party name
     * @param playerName The name of the player to set as leader
     */
    public void setPartyLeader(String partyName, String playerName) {
        for (String name : partyPlayers.get(partyName).keySet()) {
            if (name.equalsIgnoreCase(playerName)) {
                partyPlayers.get(partyName).put(playerName, true);
                informPartyMembersOwnerChange(playerName);
                plugin.getServer().getPlayer(playerName).sendMessage("You are now the party owner."); //TODO: Needs more locale.
                continue;
            }

            if (partyPlayers.get(partyName).get(name)) {
                plugin.getServer().getPlayer(name).sendMessage("You are no longer party owner."); //TODO: Needs more locale.
                partyPlayers.get(partyName).put(name, false);
            }
        }
    }

    /**
     * Get the password of a party.
     *
     * @param partyName The party name
     * @return The password of this party
     */
    public String getPartyPassword(String partyName) {
        return partyPasswords.get(partyName);
    }

    /**
     * Check if a player can invite others to their party.
     *
     * @param player The player to check
     * @param PP The profile of the given player
     * @return true if the player can invite, false otherwise
     */
    public boolean canInvite(Player player, PlayerProfile PP) {
        String party = PP.getParty();

        if (isPartyLocked(party) && !isPartyLeader(player.getName(), party)) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Check if a string is a valid party name.
     *
     * @param partyName The party name to check
     * @return true if this is a valid party, false otherwise
     */
    public boolean isParty(String partyName) {
        return partyPlayers.containsKey(partyName);
    }

    /**
     * Check if a party is empty.
     *
     * @param partyName The party to check
     * @return true if this party is empty, false otherwise
     */
    public boolean isPartyEmpty(String partyName) {
        return partyPlayers.get(partyName).isEmpty();
    }

    /**
     * Check if a player is the party leader.
     *
     * @param playerName The player name to check
     * @param partyName The party name to check
     * @return true if the player is the party leader, false otherwise
     */
    public boolean isPartyLeader(String playerName, String partyName) {
        HashMap<String, Boolean> partyMembers = partyPlayers.get(partyName);

        if (partyMembers != null) {
            Boolean isLeader = partyMembers.get(playerName);

            if (isLeader == null) {
                return false;
            }
            else {
                return isLeader;
            }
        }
        else {
            return false;
        }
    }

    /**
     * Check if this party is locked.
     *
     * @param partyName The party to check
     * @return true if this party is locked, false otherwise
     */
    public boolean isPartyLocked(String partyName) {
        Boolean isLocked = partyLocks.get(partyName);

        if (isLocked ==  null) {
            return false;
        }
        else {
            return isLocked;
        }
    }

    /**
     * Check if this party is password protected.
     *
     * @param partyName The party to check
     * @return true if this party is password protected, false otherwise
     */
    public boolean isPartyPasswordProtected(String partyName) {
        String password = partyPasswords.get(partyName);

        if (password == null) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Check if a player is in the party reflected by their profile.
     *
     * @param player The player to check
     * @param PP The profile of the player
     * @return true if this player is in the right party, false otherwise
     */
    public boolean isInParty(Player player, PlayerProfile PP) {
        Map<String, Boolean> party = partyPlayers.get(PP.getParty());

        if (party != null && party.containsKey(player.getName())) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Load all party related files.
     */
    @SuppressWarnings("unchecked")
    public void loadParties() {
        if (new File(partyPlayersFile).exists()) {
            try {
                ObjectInputStream obj = new ObjectInputStream(new FileInputStream(partyPlayersFile));
                partyPlayers = (HashMap<String, HashMap<String, Boolean>>) obj.readObject();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (EOFException e) {
                plugin.getLogger().info("partyPlayersFile empty.");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (new File(partyLocksFile).exists()) {
            try {
                ObjectInputStream obj = new ObjectInputStream(new FileInputStream(partyLocksFile));
                partyLocks = (HashMap<String, Boolean>) obj.readObject();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (EOFException e) {
                plugin.getLogger().info("partyLocksFile empty.");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (new File(partyPasswordsFile).exists()) {
            try {
                ObjectInputStream obj = new ObjectInputStream(new FileInputStream(partyPasswordsFile));
                this.partyPasswords = (HashMap<String, String>) obj.readObject();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (EOFException e) {
                plugin.getLogger().info("partyPasswordsFile empty.");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Save all party-related files.
     */
    private void saveParties() {
        savePartyFile(partyPlayersFile, partyPlayers);
        savePartyFile(partyLocksFile, partyLocks);
        savePartyFile(partyPasswordsFile, partyPasswords);
    }

    /**
     * Save a party-related file.
     *
     * @param fileName The filename to save as
     * @param partyData The Hashmap with the party data
     */
    private void savePartyFile(String fileName, Object partyData) {
        try {
            new File(fileName).createNewFile();
            ObjectOutputStream obj = new ObjectOutputStream(new FileOutputStream(fileName));
            obj.writeObject(partyData);
            obj.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
