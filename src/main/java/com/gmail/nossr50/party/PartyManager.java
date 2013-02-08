package com.gmail.nossr50.party;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Users;

public final class PartyManager {
    private static String partiesFilePath = mcMMO.p.getDataFolder().getPath() + File.separator + "FlatFileStuff" + File.separator + "parties.yml";
    private static List<Party> parties = new ArrayList<Party>();

    private PartyManager() {}

    public static boolean changeOrJoinParty(McMMOPlayer mcMMOPlayer, Player player, Party oldParty, Party newParty) {
        if (mcMMOPlayer.inParty()) {
            if (!handlePartyChangeEvent(player, oldParty.getName(), newParty.getName(), EventReason.CHANGED_PARTIES)) {
                return false;
            }

            removeFromParty(player.getName(), oldParty);
        }
        else if (!handlePartyChangeEvent(player, null, newParty.getName(), EventReason.JOINED_PARTY)) {
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
    public static boolean inSameParty(Player firstPlayer, Player secondPlayer) {
        McMMOPlayer firstMcMMOPlayer = Users.getPlayer(firstPlayer);
        McMMOPlayer secondMcMMOPlayer = Users.getPlayer(secondPlayer);

        if (firstMcMMOPlayer == null || secondMcMMOPlayer == null) {
            return false;
        }

        Party firstParty = firstMcMMOPlayer.getParty();
        Party secondParty = secondMcMMOPlayer.getParty();

        if (firstParty == null || secondParty == null || firstParty != secondParty) {
            return false;
        }

        return true;
    }

    /**
     * Get the near party members.
     *
     * @param player The player to check
     * @param range The distance
     * @return the near party members
     */
    public static List<Player> getNearMembers(Player player, Party party, double range) {
        List<Player> nearMembers = new ArrayList<Player>();
        if (party != null) {
            for (Player member : party.getOnlineMembers()) {
                if (player != member && Misc.isNear(player.getLocation(), member.getLocation(), range)) {
                    nearMembers.add(member);
                }
            }
        }
        return nearMembers;
    }

    /**
     * Notify party members when a player joins
     * 
     * @param playerName The name of the player that joins
     * @param party The concerned party
     */
    private static void informPartyMembersJoin(String playerName, Party party) {
        for (Player member : party.getOnlineMembers()) {
            if (!member.getName().equals(playerName)) {
                member.sendMessage(LocaleLoader.getString("Party.InformedOnJoin", playerName));
            }
        }
    }

    /**
     * Notify party members when a party member quits.
     *
     * @param playerName The name of the player that quits
     * @param party The concerned party
     */
    private static void informPartyMembersQuit(String playerName, Party party) {
        for (Player member : party.getOnlineMembers()) {
            if (!member.getName().equals(playerName)) {
                member.sendMessage(LocaleLoader.getString("Party.InformedOnQuit", playerName));
            }
        }
    }

    /**
     * Get a list of all players in this player's party.
     *
     * @param player The player to check
     * @return all the players in the player's party
     */
    public static List<String> getAllMembers(Player player) {
        Party party = Users.getPlayer(player).getParty();

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
    public static List<Player> getOnlineMembers(String partyName) {
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
    public static List<Player> getOnlineMembers(Player player) {
        return getOnlineMembers(player.getName());
    }

    /**
     * Retrieve a party by its name
     * 
     * @param partyName The party name
     * @return the existing party, null otherwise
     */
    public static Party getParty(String partyName) {
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
    public static Party getPlayerParty(String playerName) {
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
    public static List<Party> getParties() {
        return parties;
    }

    /**
     * Remove a player from a party.
     *
     * @param playerName The name of the player to remove
     * @param party The party
     */
    public static void removeFromParty(String playerName, Party party) {
        List<String> members = party.getMembers();

        members.remove(playerName);

        if (members.isEmpty()) {
            parties.remove(party);
        }
        else {
            //If the leaving player was the party leader, appoint a new leader from the party members
            if (party.getLeader().equals(playerName)) {
                    String newLeader = members.get(0);
                    party.setLeader(newLeader);
            }

            informPartyMembersQuit(playerName, party);
        }

        McMMOPlayer mcMMOPlayer = Users.getPlayer(playerName);
 
        if (mcMMOPlayer != null) {
            mcMMOPlayer.removeParty();
        }
    }

    /**
     * Disband a party. Kicks out all members and removes the party.
     *
     * @param party The party to remove
     */
    public static void disbandParty(Party party) {
        List<String> members = party.getMembers();

        for (String member : party.getMembers()) {

            McMMOPlayer mcMMOPlayer = Users.getPlayer(member);

            if (mcMMOPlayer != null) {
                mcMMOPlayer.removeParty();
            }
        }

        members.clear();
        if (members.isEmpty()) {
            parties.remove(party);
        }
    }

    /**
     * Create a new party
     *
     * @param player The player to add to the party
     * @param mcMMOPlayer The player to add to the party
     * @param partyName The party to add the player to
     * @param password The password for this party, null if there was no password
     */
    public static void createParty(Player player, McMMOPlayer mcMMOPlayer, String partyName, String password) {
        partyName = partyName.replace(".", "");
        Party party = getParty(partyName);
        String playerName = player.getName();

        if (party == null) {
            party = new Party();

            party.setName(partyName);
            party.setLeader(playerName);
            party.setXpShareMode(ShareHandler.ShareMode.NONE);
            party.setLocked(true);//Parties are now invite-only by default, can be set to open with /party unlock

            if (password != null) {
                party.setPassword(password);
                party.setLocked(true);
                player.sendMessage(LocaleLoader.getString("Party.Password.Set", password));
            }
            parties.add(party);
        }
        else {
            player.sendMessage(LocaleLoader.getString("Commands.Party.AlreadyExists"));
            return;
        }

        player.sendMessage(LocaleLoader.getString("Commands.Party.Create", party.getName()));
        addToParty(player.getName(), mcMMOPlayer, party);
    }

    /**
     * Add a player to a party.
     *
     * @param player The player to add to the party
     * @param mcMMOPlayer The player to add to the party
     * @param partyName The party to add the player to
     * @param password the password for this party, null if there was no password
     */
    public static void joinParty(Player player, McMMOPlayer mcMMOPlayer, String partyName, String password) {
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
        else if (!checkPartyPassword(player, party, password)) {
            return;
        }

        player.sendMessage(LocaleLoader.getString("Commands.Party.Join", party.getName()));
        addToParty(player.getName(), mcMMOPlayer, party);
    }

    /**
     * Check if a player can join a party
     *
     * @param player The player trying to join a party
     * @param party The party
     * @param password The password provided by the player
     * @return true if the player can join the party
     */
    public static boolean checkPartyPassword(Player player, Party party, String password) {
        //Don't care about passwords if it isn't locked
        if (party.isLocked()) {
            String partyPassword = party.getPassword();

            if (partyPassword != null) {
                if (password == null) {
                    player.sendMessage(LocaleLoader.getString("Party.Password.None"));
                    return false;
                }
                else if (!password.equals(partyPassword)) {
                    player.sendMessage(LocaleLoader.getString("Party.Password.Incorrect"));
                    return false;
                }
            }
            else {
                player.sendMessage(LocaleLoader.getString("Party.Locked"));
                return false;
            }
        }

        return true;
    }

    /**
     * Accept a party invitation
     * 
     * @param Player The plaer to add to the party
     * @param mcMMOPlayer The player to add to the party
     */
    public static void joinInvitedParty(Player player, McMMOPlayer mcMMOPlayer) {
        Party invite = mcMMOPlayer.getPartyInvite();

        if (!parties.contains(invite)) {
            parties.add(invite);
        }

        player.sendMessage(LocaleLoader.getString("Commands.Invite.Accepted", invite.getName()));
        mcMMOPlayer.removePartyInvite();
        addToParty(player.getName(), mcMMOPlayer, invite);
    }

    /**
     * Add a player to a party
     * 
     * @param playerName The name of the player to add to a party
     * @param mcMMOPlayer The player to add to the party
     * @param party The party
     */
    public static void addToParty(String playerName, McMMOPlayer mcMMOPlayer, Party party) {
        informPartyMembersJoin(playerName, party);
        mcMMOPlayer.setParty(party);
        party.getMembers().add(playerName);
    }

    /**
     * Get the leader of a party.
     *
     * @param partyName The party name
     * @return the leader of the party
     */
    public static String getPartyLeader(String partyName) {
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
    public static void setPartyLeader(String playerName, Party party) {
        String leaderName = party.getLeader();

        for (Player member : party.getOnlineMembers()) {
            if (member.getName().equals(playerName)) {
                member.sendMessage(LocaleLoader.getString("Party.Owner.Player"));
            }
            else if (member.getName().equals(leaderName)) {
                member.sendMessage(LocaleLoader.getString("Party.Owner.NotLeader"));
            }
            else {
                member.sendMessage(LocaleLoader.getString("Party.Owner.New", playerName));
            }
        }

        party.setLeader(playerName);
    }

    /**
     * Check if a player can invite others to his party.
     *
     * @param player The player to check
     * @param mcMMOPlayer The player to check
     * @return true if the player can invite
     */
    public static boolean canInvite(Player player, McMMOPlayer mcMMOPlayer) {
        Party party = mcMMOPlayer.getParty();

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
    public static boolean isParty(String partyName) {
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
    public static void loadParties() {
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
            party.setXpShareMode(ShareHandler.ShareMode.getFromString(partiesFile.getString(partyName + ".ExpShareMode")));
            party.getMembers().addAll(partiesFile.getStringList(partyName + ".Members"));

            parties.add(party);
        }
    }

    /**
     * Save party file.
     */
    public static void saveParties() {
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
            partiesFile.set(partyName + ".ExpShareMode", party.getXpShareMode().toString());
            partiesFile.set(partyName + ".Members", party.getMembers());

            try {
                partiesFile.save(new File(partiesFilePath));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handle party change event.
     *
     * @param player The player changing parties
     * @param oldPartyName The name of the old party
     * @param newPartyName The name of the new party
     * @param reason The reason for changing parties
     * @return true if the change event was successful, false otherwise
     */
    public static boolean handlePartyChangeEvent(Player player, String oldPartyName, String newPartyName, EventReason reason) {
        McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, oldPartyName, newPartyName, reason);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return !event.isCancelled();
    }
}
