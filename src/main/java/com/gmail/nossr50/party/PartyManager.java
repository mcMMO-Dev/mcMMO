package com.gmail.nossr50.party;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
     * @param playerName The name of the player that joins
     * @param party The concerned party
     */
    private void informPartyMembersJoin(String playerName, Party party) {
        for (Player member : party.getOnlineMembers()) {
            if (member.getName().equals(playerName)) {
                member.sendMessage(LocaleLoader.getString("Party.InformedOnJoin", new Object[] {playerName}));
            }
        }
    }

    /**
     * Notify party members when a party member quits.
     *
     * @param playerName The name of the player that quits
     * @param party The concerned party
     */
    private void informPartyMembersQuit(String playerName, Party party) {
        for (Player member : party.getOnlineMembers()) {
            if (member.getName().equals(playerName)) {
                member.sendMessage(LocaleLoader.getString("Party.InformedOnQuit", new Object[] {playerName}));
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
            return null;
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
     * Get a list of all current parties.
     *
     * @return the list of parties.
     */
    public List<Party> getParties() {
        return parties;
    }

    /**
     * Remove a player from a party.
     *
     * @param playerName The name of the player to remove
     * @param party The party
     */
    public void removeFromParty(String playerName, Party party) {
        List<String> members = party.getMembers();

        members.remove(playerName);

        if (members.isEmpty()) {
            parties.remove(party);
        }
        else {
            if (party.getLeader().equals(playerName)) {
                party.setLocked(false);
            }

            informPartyMembersQuit(playerName, party);
        }

        PlayerProfile playerProfile = Users.getProfile(playerName);

        if (playerProfile != null) {
            playerProfile.removeParty();
        }
    }

    /**
     * Add a player to a party.
     *
     * @param player The player to add to the party
     * @param playerProfile The profile of the player to add to the party
     * @param partyName The party to add the player to
     * @param password the password for this party, null if there was no password
     */
    public void joinParty(Player player, PlayerProfile playerProfile, String partyName, String password) {
        partyName = partyName.replace(".", "");
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
        else if (!checkJoinability(player, playerProfile, party, password)) {
            return;
        }

        player.sendMessage(LocaleLoader.getString("Commands.Party.Join", new Object[]{party.getName()}));
        addToParty(player.getName(), playerProfile, party);
    }

    /**
     * Check if a player can join a party
     *
     * @param player The player trying to join a party
     * @param playerProfile The profile of the player
     * @param party The party
     * @param password The password provided by the player
     * @return true if the player can join the party
     */
    private boolean checkJoinability(Player player, PlayerProfile playerProfile, Party party, String password) {
        //Don't care about passwords if it isn't locked
        if (party.isLocked()) {
            String partyPassword = party.getPassword();

            if (partyPassword != null) {
                if (password == null) {
                    player.sendMessage("This party requires a password. Use /party <party> <password> to join it."); //TODO: Needs more locale.
                    return false;
                }
                else if (!password.equals(partyPassword)) {
                    player.sendMessage("Party password incorrect."); //TODO: Needs more locale.
                    return false;
                }
            }
            else {
                player.sendMessage("Party is locked."); //TODO: Needs more locale.
                return false;
            }
        }

        return true;
    }

    /**
     * Accept a party invitation
     * 
     * @param player The player to add to the party
     * @param playerProfile The profile of the player
     */
    public void joinInvitedParty(Player player, PlayerProfile playerProfile) {
        Party invite = playerProfile.getInvite();

        if (!parties.contains(invite)) {
            parties.add(invite);
        }

        player.sendMessage(LocaleLoader.getString("Commands.Invite.Accepted", new Object[]{invite.getName()}));
        playerProfile.removeInvite();
        addToParty(player.getName(), playerProfile, invite);
    }

    /**
     * Add a player to a party
     * 
     * @param playerName The name of the player to add to a party
     * @param playerProfile The profile of the player
     * @param party The party
     */
    public void addToParty(String playerName, PlayerProfile playerProfile, Party party) {
        informPartyMembersJoin(playerName, party);
        playerProfile.setParty(party);
        party.getMembers().add(playerName);
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
     * @param playerName The name of the player to set as leader
     * @param party The party
     */
    public void setPartyLeader(String playerName, Party party) {
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
     * @return true if the player can invite
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
     * Load party file.
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
     * Save party file.
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
