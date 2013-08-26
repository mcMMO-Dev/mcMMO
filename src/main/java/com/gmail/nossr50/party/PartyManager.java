package com.gmail.nossr50.party;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.party.ItemShareType;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.ShareMode;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.player.UserManager;

public final class PartyManager {
    private static String partiesFilePath = mcMMO.getFlatFileDirectory() + "parties.yml";
    private static List<Party> parties = new ArrayList<Party>();
    private static File partyFile = new File(partiesFilePath);

    private PartyManager() {}

    /**
     * Check if a party with a given name already exists.
     *
     * @param player The player to notify
     * @param partyName The name of the party to check
     * @return true if a party with that name exists, false otherwise
     */
    public static boolean checkPartyExistence(Player player, String partyName) {
        if (getParty(partyName) == null) {
            return false;
        }

        player.sendMessage(LocaleLoader.getString("Commands.Party.AlreadyExists", partyName));
        return true;
    }

    /**
     * Attempt to change parties or join a new party.
     *
     * @param mcMMOPlayer The player changing or joining parties
     * @param newPartyName The name of the party being joined
     * @return true if the party was joined successfully, false otherwise
     */
    public static boolean changeOrJoinParty(McMMOPlayer mcMMOPlayer, String newPartyName) {
        Player player = mcMMOPlayer.getPlayer();

        if (mcMMOPlayer.inParty()) {
            Party oldParty = mcMMOPlayer.getParty();

            if (!handlePartyChangeEvent(player, oldParty.getName(), newPartyName, EventReason.CHANGED_PARTIES)) {
                return false;
            }

            removeFromParty(player, oldParty);
        }
        else if (!handlePartyChangeEvent(player, null, newPartyName, EventReason.JOINED_PARTY)) {
            return false;
        }

        return true;
    }

    /**
     * Check if two online players are in the same party.
     *
     * @param firstPlayer The first player
     * @param secondPlayer The second player
     * @return true if they are in the same party, false otherwise
     */
    public static boolean inSameParty(Player firstPlayer, Player secondPlayer) {
        Party firstParty = UserManager.getPlayer(firstPlayer).getParty();
        Party secondParty = UserManager.getPlayer(secondPlayer).getParty();

        if (firstParty == null || secondParty == null) {
            return false;
        }

        return firstParty.equals(secondParty);
    }

    /**
     * Get the near party members.
     *
     * @param mcMMOPlayer The player to check
     * @param range The distance
     * @return the near party members
     */
    public static List<Player> getNearMembers(McMMOPlayer mcMMOPlayer) {
        List<Player> nearMembers = new ArrayList<Player>();
        Party party = mcMMOPlayer.getParty();

        if (party != null) {
            Player player = mcMMOPlayer.getPlayer();
            double range = Config.getInstance().getPartyShareRange();

            for (Player member : party.getOnlineMembers()) {
                if (!player.equals(member) && member.isValid() && Misc.isNear(player.getLocation(), member.getLocation(), range)) {
                    nearMembers.add(member);
                }
            }
        }

        return nearMembers;
    }

    /**
     * Get a list of all players in this player's party.
     *
     * @param player The player to check
     * @return all the players in the player's party
     */
    public static LinkedHashSet<String> getAllMembers(Player player) {
        Party party = getPlayerParty(player.getName());

        return party == null ? null : party.getMembers();
    }

    /**
     * Get a list of all online players in this party.
     *
     * @param partyName The party to check
     * @return all online players in this party
     */
    public static List<Player> getOnlineMembers(String partyName) {
        return getOnlineMembers(getParty(partyName));
    }

    /**
     * Get a list of all online players in this party.
     *
     * @param player The player to check
     * @return all online players in this party
     */
    public static List<Player> getOnlineMembers(Player player) {
        return getOnlineMembers(getPlayerParty(player.getName()));
    }

    private static List<Player> getOnlineMembers(Party party) {
        return party == null ? null : party.getOnlineMembers();
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
            for (String memberName : party.getMembers()) {
                if (memberName.equalsIgnoreCase(playerName)) {
                    return party;
                }
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
     * @param player The player to remove
     * @param party The party
     */
    public static void removeFromParty(OfflinePlayer player, Party party) {
        LinkedHashSet<String> members = party.getMembers();
        String playerName = player.getName();

        members.remove(playerName);

        if (members.isEmpty()) {
            parties.remove(party);
        }
        else {
            // If the leaving player was the party leader, appoint a new leader from the party members
            if (party.getLeader().equalsIgnoreCase(playerName)) {
                String newLeader = members.iterator().next();
                party.setLeader(newLeader);
            }

            informPartyMembersQuit(party, playerName);
        }

        processPartyLeaving(UserManager.getPlayer(player));
    }

    /**
     * Disband a party. Kicks out all members and removes the party.
     *
     * @param party The party to remove
     */
    public static void disbandParty(Party party) {
        for (String memberName : party.getMembers()) {
            processPartyLeaving(UserManager.getPlayer(memberName));
        }

        parties.remove(party);
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

        if (party != null) {
            player.sendMessage(LocaleLoader.getString("Commands.Party.AlreadyExists"));
            return;
        }

        party = new Party(playerName, partyName, password);

        if (password != null) {
            player.sendMessage(LocaleLoader.getString("Party.Password.Set", password));
        }

        parties.add(party);

        player.sendMessage(LocaleLoader.getString("Commands.Party.Create", party.getName()));
        addToParty(playerName, mcMMOPlayer, party);
    }

    /**
     * Add a player to a party.
     *
     * @param player The player to add to the party
     * @param mcMMOPlayer The player to add to the party
     * @param party The party to add the player to
     * @param password the password for this party, null if there was no password
     */
    public static void joinParty(Player player, McMMOPlayer mcMMOPlayer, Party party, String password) {
        if (!checkPartyPassword(player, party, password) || mcMMOPlayer.getParty() == party) {
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
        if (!party.isLocked()) {
            return true;
        }

        String partyPassword = party.getPassword();

        if (partyPassword == null) {
            player.sendMessage(LocaleLoader.getString("Party.Locked"));
            return false;
        }

        if (password == null) {
            player.sendMessage(LocaleLoader.getString("Party.Password.None"));
            return false;
        }
        else if (!password.equals(partyPassword)) {
            player.sendMessage(LocaleLoader.getString("Party.Password.Incorrect"));
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Accept a party invitation
     *
     * @param player The player to add to the party
     * @param mcMMOPlayer The player to add to the party
     */
    public static void joinInvitedParty(Player player, McMMOPlayer mcMMOPlayer) {
        Party invite = mcMMOPlayer.getPartyInvite();

        if (mcMMOPlayer.getParty() == invite) {
            return;
        }

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
        if (mcMMOPlayer.getParty() == party) {
            return;
        }

        informPartyMembersJoin(party, playerName);
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
            if (member.getName().equalsIgnoreCase(playerName)) {
                member.sendMessage(LocaleLoader.getString("Party.Owner.Player"));
            }
            else if (member.getName().equalsIgnoreCase(leaderName)) {
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
     * @param party The party to check
     * @return true if the player can invite
     */
    public static boolean canInvite(Player player, Party party) {
        return !(party.isLocked() && !party.getLeader().equalsIgnoreCase(player.getName()));
    }

    /**
     * Check if a string is a valid party name.
     *
     * @param partyName The party name to check
     * @return true if this is a valid party, false otherwise
     */
    public static boolean isParty(String partyName) {
        return getParty(partyName) != null;
    }

    /**
     * Load party file.
     */
    public static void loadParties() {
        if (!partyFile.exists()) {
            return;
        }

        YamlConfiguration partiesFile = YamlConfiguration.loadConfiguration(partyFile);

        for (String partyName : partiesFile.getConfigurationSection("").getKeys(false)) {
            Party party = new Party(partyName);

            party.setLeader(partiesFile.getString(partyName + ".Leader"));
            party.setPassword(partiesFile.getString(partyName + ".Password"));
            party.setLocked(partiesFile.getBoolean(partyName + ".Locked"));
            party.setXpShareMode(ShareMode.getShareMode(partiesFile.getString(partyName + ".ExpShareMode", "NONE")));
            party.setItemShareMode(ShareMode.getShareMode(partiesFile.getString(partyName + ".ItemShareMode", "NONE")));

            for (ItemShareType itemShareType : ItemShareType.values()) {
                party.setSharingDrops(itemShareType, partiesFile.getBoolean(partyName + ".ItemShareType." + itemShareType.toString(), true));
            }

            List<String> memberNames = partiesFile.getStringList(partyName + ".Members");
            LinkedHashSet<String> members = party.getMembers();

            for (String memberName : memberNames) {
                members.add(memberName);
            }

            parties.add(party);
        }
    }

    /**
     * Save party file.
     */
    public static void saveParties() {
        if (partyFile.exists()) {
            partyFile.delete();
        }

        YamlConfiguration partiesFile = new YamlConfiguration();

        for (Party party : parties) {
            String partyName = party.getName();

            partiesFile.set(partyName + ".Leader", party.getLeader());
            partiesFile.set(partyName + ".Password", party.getPassword());
            partiesFile.set(partyName + ".Locked", party.isLocked());
            partiesFile.set(partyName + ".ExpShareMode", party.getXpShareMode().toString());
            partiesFile.set(partyName + ".ItemShareMode", party.getItemShareMode().toString());

            for (ItemShareType itemShareType : ItemShareType.values()) {
                partiesFile.set(partyName + ".ItemShareType." + itemShareType.toString(), party.sharingDrops(itemShareType));
            }

            List<String> memberNames = new ArrayList<String>();

            for (String member : party.getMembers()) {
                if (!memberNames.contains(member)) {
                    memberNames.add(member);
                }
            }

            partiesFile.set(partyName + ".Members", memberNames);
        }

        try {
            partiesFile.save(partyFile);
        }
        catch (Exception e) {
            e.printStackTrace();
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

    /**
     * Notify party members when a player joins.
     *
     * @param party The concerned party
     * @param playerName The name of the player that joined
     */
    private static void informPartyMembersJoin(Party party, String playerName) {
        for (Player member : party.getOnlineMembers()) {
            member.sendMessage(LocaleLoader.getString("Party.InformedOnJoin", playerName));
        }
    }

    /**
     * Notify party members when a party member quits.
     *
     * @param party The concerned party
     * @param playerName The name of the player that left
     */
    private static void informPartyMembersQuit(Party party, String playerName) {
        for (Player member : party.getOnlineMembers()) {
            member.sendMessage(LocaleLoader.getString("Party.InformedOnQuit", playerName));
        }
    }

    private static void processPartyLeaving(McMMOPlayer mcMMOPlayer) {
        mcMMOPlayer.removeParty();
        mcMMOPlayer.setPartyChat(false);
        mcMMOPlayer.setItemShareModifier(10);
    }
}
