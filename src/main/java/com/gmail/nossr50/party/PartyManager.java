package com.gmail.nossr50.party;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.party.ItemShareType;
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

            removeFromParty(mcMMOPlayer);
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
        Party party = getParty(player);

        return party == null ? new LinkedHashSet<String>() : party.getMembers();
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
        return getOnlineMembers(getParty(player));
    }

    private static List<Player> getOnlineMembers(Party party) {
        return party == null ? new ArrayList<Player>() : party.getOnlineMembers();
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
     * Retrieve a party by a members name
     *
     * @param playerName The members name
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
     * Retrieve a party by member
     *
     * @param player The member
     * @return the existing party, null otherwise
     */
    public static Party getParty(Player player) {
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        return mcMMOPlayer.getParty();
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
                setPartyLeader(members.iterator().next(), party);
            }

            informPartyMembersQuit(party, playerName);
        }
    }

    /**
     * Remove a player from a party.
     *
     * @param mcMMOPlayer The player to remove
     */
    public static void removeFromParty(McMMOPlayer mcMMOPlayer) {
        removeFromParty(mcMMOPlayer.getPlayer(), mcMMOPlayer.getParty());
        processPartyLeaving(mcMMOPlayer);
    }

    /**
     * Disband a party. Kicks out all members and removes the party.
     *
     * @param party The party to remove
     */
    public static void disbandParty(Party party) {
        for (Player member : party.getOnlineMembers()) {
            processPartyLeaving(UserManager.getPlayer(member));
        }

        parties.remove(party);
    }

    /**
     * Create a new party
     *
     * @param mcMMOPlayer The player to add to the party
     * @param partyName The party to add the player to
     * @param password The password for this party, null if there was no password
     */
    public static void createParty(McMMOPlayer mcMMOPlayer, String partyName, String password) {
        Player player = mcMMOPlayer.getPlayer();
        String playerName = player.getName();

        Party party = new Party(playerName, partyName.replace(".", ""), password);

        if (password != null) {
            player.sendMessage(LocaleLoader.getString("Party.Password.Set", password));
        }

        parties.add(party);

        player.sendMessage(LocaleLoader.getString("Commands.Party.Create", party.getName()));
        addToParty(mcMMOPlayer, party);
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
        if (party.isLocked()) {
            String partyPassword = party.getPassword();

            if (partyPassword == null) {
                player.sendMessage(LocaleLoader.getString("Party.Locked"));
                return false;
            }

            if (password == null) {
                player.sendMessage(LocaleLoader.getString("Party.Password.None"));
                return false;
            }

            if (!password.equals(partyPassword)) {
                player.sendMessage(LocaleLoader.getString("Party.Password.Incorrect"));
                return false;
            }
        }

        return true;
    }

    /**
     * Accept a party invitation
     *
     * @param mcMMOPlayer The player to add to the party
     */
    public static void joinInvitedParty(McMMOPlayer mcMMOPlayer) {
        Party invite = mcMMOPlayer.getPartyInvite();

        // Check if the party still exists, it might have been disbanded
        if (!parties.contains(invite)) {
            mcMMOPlayer.getPlayer().sendMessage(LocaleLoader.getString("Party.Disband"));
            return;
        }

        mcMMOPlayer.getPlayer().sendMessage(LocaleLoader.getString("Commands.Invite.Accepted", invite.getName()));
        mcMMOPlayer.removePartyInvite();
        addToParty(mcMMOPlayer, invite);
    }

    /**
     * Add a player to a party
     *
     * @param mcMMOPlayer The player to add to the party
     * @param party The party
     */
    public static void addToParty(McMMOPlayer mcMMOPlayer, Party party) {
        String playerName = mcMMOPlayer.getPlayer().getName();

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

        return party == null ? null : party.getLeader();
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
            String memberName = member.getName();

            if (memberName.equalsIgnoreCase(playerName)) {
                member.sendMessage(LocaleLoader.getString("Party.Owner.Player"));
            }
            else if (memberName.equalsIgnoreCase(leaderName)) {
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
     * @return true if the player can invite
     */
    public static boolean canInvite(McMMOPlayer mcMMOPlayer) {
        Party party = mcMMOPlayer.getParty();

        return !party.isLocked() || party.getLeader().equalsIgnoreCase(mcMMOPlayer.getPlayer().getName());
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
     * Remove party data from the mcMMOPlayer.
     *
     * @param mcMMOPlayer The player to remove party data from.
     */
    public static void processPartyLeaving(McMMOPlayer mcMMOPlayer) {
        mcMMOPlayer.removeParty();
        mcMMOPlayer.setPartyChat(false);
        mcMMOPlayer.setItemShareModifier(10);
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
}
