package com.gmail.nossr50.party;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Users;

public class PartyManager {
    private static String partiesFilePath;
    private static List<Party> parties = new ArrayList<Party>();
    private static mcMMO plugin;
    private static PartyManager instance;

    private PartyManager() {
        plugin = mcMMO.p;
        partiesFilePath = plugin.getDataFolder().getPath() + File.separator + "FlatFileStuff" + File.separator + "parties.yml";

        loadParties();
    }

    public static PartyManager getInstance() {
        if (instance == null) {
            instance = new PartyManager();
        }

        return instance;
    }

    /**
     * Check if two players are in the same party.
     *
     * @param firstPlayer The first player
     * @param secondPlayer The second player
     * @return true if they are in the same party, false otherwise
     */
    public boolean inSameParty(Player firstPlayer, Player secondPlayer) {
        Party firstParty = Users.getProfile(firstPlayer).getParty();
        Party secondParty = Users.getProfile(secondPlayer).getParty();

        if (firstParty == null || secondParty == null || firstParty != secondParty) {
            return false;
        }

        return true;
    }

    /**
     * Check if two players are in the same party.
     *
     * @param firstPlayer The first player
     * @param secondPlayer The second player
     * @return true if they are in the same party, false otherwise
     */
    public boolean inSameParty(OfflinePlayer firstPlayer, OfflinePlayer secondPlayer) {
        PlayerProfile firstProfile = Users.getProfile(firstPlayer);
        PlayerProfile secondProfile = Users.getProfile(secondPlayer);

        if (firstProfile == null || secondProfile == null) {
            return false;
        }

        Party firstParty = firstProfile.getParty();
        Party secondParty = secondProfile.getParty();

        if (firstParty == null || secondParty == null || firstParty != secondParty) {
            return false;
        }

        return true;
    }

    /**
     * Notify party members when a player joins
     * 
     * @param player The player that joins
     * @param party The concerned party
     */
    private void informPartyMembersJoin(Player player, Party party) {
        for (Player member : party.getOnlineMembers()) {
            if (member != player) {
                member.sendMessage(LocaleLoader.getString("Party.InformedOnJoin", new Object[] {player.getName()}));
            }
        }
    }

    /**
     * Notify party members when a party member quits.
     *
     * @param player The player that quits
     * @param party The concerned party
     */
    private void informPartyMembersQuit(Player player, Party party) {
        for (Player member : party.getOnlineMembers()) {
            if (member != player) {
                member.sendMessage(LocaleLoader.getString("Party.InformedOnQuit", new Object[] {player.getName()}));
            }
        }
    }

    /**
     * Get a list of all players in this player's party.
     *
     * @param player The player to check
     * @return all the players in the player's party
     */
    public List<String> getAllMembers(Player player) {
        Party party = Users.getProfile(player).getParty();

        if (party == null) {
            return Collections.emptyList();
        }

        return party.getMembers();
    }

    /**
     * Get a list of all online players in this party.
     *
     * @param partyName The party to check
     * @return all online players in this party
     */
    public List<Player> getOnlineMembers(String partyName) {
        Party party = getParty(partyName);

        if (party == null) {
            return null;
        }

        return party.getOnlineMembers();
    }

    /**
     * Get a list of all online players in this party.
     *
     * @param player The player to check
     * @return all online players in this party
     */
    public List<Player> getOnlineMembers(Player player) {
        return getOnlineMembers(player.getName());
    }

    /**
     * Retrieve a party by its name
     * 
     * @param partyName The party name
     * @return the existing party, null otherwise
     */
    public Party getParty(String partyName) {
        for (Party party : parties) {
            if (party.getName().equals(partyName)) {
                return party;
            }
        }

        return null;
    }

    /**
     * Retrieve a party by a member name
     * 
     * @param playerName The member name
     * @return the existing party, null otherwise
     */
    public Party getPlayerParty(String playerName) {
        for (Party party : parties) {
            if (party.getMembers().contains(playerName)) {
                return party;
            }
        }

        return null;
    }

    /**
     * Get a list of all current party names.
     *
     * @return the list of parties.
     */
    public List<String> getParties() {
        String location = mcMMO.usersFile;
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
     * Remove a player from a party.
     *
     * @param player The player to remove
     * @param playerProfile The profile of the player to remove
     */
    public void removeFromParty(Player player, PlayerProfile playerProfile) {
        String playerName = player.getName();
        Party party = playerProfile.getParty();
        List<String> members = party.getMembers();

        if (members.contains(playerName)) {
            members.remove(playerName);

            if (members.isEmpty()) {
                parties.remove(party);
            }
            else {
                if (party.getLeader().equals(playerName) && party.isLocked()) {
                    party.setLocked(false);
                }

                informPartyMembersQuit(player, party);
            }
        }

        playerProfile.removeParty();
    }

    /**
     * Add a player to a party.
     *
     * @param player The player to add to the party
     * @param playerProfile The profile of the player to add to the party
     * @param partyName The party to add the player to
     * @param password the password for this party, null if there was no password
     */
    public void addToParty(Player player, PlayerProfile playerProfile, String partyName, String password) {
        //Fix for FFS
        partyName = partyName.replace(":", ".");
        Party party = getParty(partyName);
        String playerName = player.getName();

        if (party == null) {
            party = new Party();

            party.setName(partyName);
            party.setLeader(playerName);
            
            if (password != null) {
                party.setPassword(password);
                party.setLocked(true);
            }

            parties.add(party);
        }
        else {
            //Don't care about passwords if it isn't locked
            if (party.isLocked()) {
                String partyPassword = party.getPassword();

                if (partyPassword != null) {
                    if (password == null) {
                        player.sendMessage("This party requires a password. Use /party <party> <password> to join it."); //TODO: Needs more locale.
                        return;
                    }
                    else if (!password.equalsIgnoreCase(partyPassword)) {
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

        player.sendMessage(LocaleLoader.getString("Commands.Party.Join", new Object[]{partyName}));
        informPartyMembersJoin(player, party);
        playerProfile.setParty(party);
        party.getMembers().add(player.getName());
    }

    /**
     * Accept a party invitation
     * 
     * @param player The player to add to the party
     * @param playerProfile The profile of the player
     * @param party The party
     */
    public void addToInvitedParty(Player player, PlayerProfile playerProfile, Party party) {
        if (!parties.contains(party)) {
            parties.add(party);
        }

        player.sendMessage(LocaleLoader.getString("Commands.Invite.Accepted", new Object[]{party.getName()}));
        informPartyMembersJoin(player, party);
        playerProfile.removeInvite();
        playerProfile.setParty(party);
        party.getMembers().add(player.getName());
    }

    /**
     * Get the leader of a party.
     *
     * @param partyName The party name
     * @return the leader of the party
     */
    public String getPartyLeader(String partyName) {
        Party party = getParty(partyName);

        if (party == null) {
            return null;
        }

        return party.getLeader();
    }

    /**
     * Set the leader of a party.
     *
     * @param partyName The party name
     * @param playerName The name of the player to set as leader
     */
    public void setPartyLeader(String partyName, String playerName) {
        Party party = getParty(partyName);

        if (party == null) {
            return;
        }

        String leaderName = party.getLeader();

        for (Player member : party.getOnlineMembers()) {
            if (member.getName().equals(playerName)) {
                member.sendMessage("You are now the party owner."); //TODO: Needs more locale.
            }
            else if (member.equals(leaderName)) {
                member.sendMessage("You are no longer party owner."); //TODO: Needs more locale.
            }
            else {
                member.sendMessage(playerName + " is the new party owner."); //TODO: Needs more Locale.
            }
        }

        party.setLeader(playerName);
    }

    /**
     * Check if a player can invite others to their party.
     *
     * @param player The player to check
     * @param playerProfile The profile of the given player
     * @return true if the player can invite, false otherwise
     */
    public boolean canInvite(Player player, PlayerProfile playerProfile) {
        Party party = playerProfile.getParty();

        if (party == null || (party.isLocked() && !party.getLeader().equals(player.getName()))) {
            return false;
        }

        return true;
    }

    /**
     * Check if a string is a valid party name.
     *
     * @param partyName The party name to check
     * @return true if this is a valid party, false otherwise
     */
    public boolean isParty(String partyName) {
        for (Party party : parties) {
            if (party.getName().equals(partyName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Load all party related files.
     */
    private void loadParties() {
        File file = new File(partiesFilePath);
        
        if (!file.exists()) {
            return;
        }

        YamlConfiguration partiesFile = new YamlConfiguration();

        try {
            partiesFile.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String partyName : partiesFile.getConfigurationSection("").getKeys(false)) {
            Party party = new Party();

            party.setName(partyName);
            party.setLeader(partiesFile.getString(partyName + ".Leader"));
            party.setPassword(partiesFile.getString(partyName + ".Password"));
            party.setLocked(partiesFile.getBoolean(partyName + ".Locked"));
            party.getMembers().addAll(partiesFile.getStringList(partyName + ".Members"));

            parties.add(party);
        }
    }

    /**
     * Save all party-related files.
     * 
     * @throws Exception 
     */
    public void saveParties() {
        File file = new File(partiesFilePath);

        if (file.exists()) {
            file.delete();
        }

        YamlConfiguration partiesFile = new YamlConfiguration();

        for (Party party : parties) {
            String partyName = party.getName();

            partiesFile.set(partyName + ".Leader", party.getLeader());
            partiesFile.set(partyName + ".Password", party.getPassword());
            partiesFile.set(partyName + ".Locked", party.isLocked());
            partiesFile.set(partyName + ".Members", party.getMembers());

            try {
                partiesFile.save(new File(partiesFilePath));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
