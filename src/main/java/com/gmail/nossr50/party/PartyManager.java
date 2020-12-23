package com.gmail.nossr50.party;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.chat.ChatChannel;
import com.gmail.nossr50.datatypes.database.UpgradeType;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.party.ItemShareType;
import com.neetgames.mcmmo.party.Party;
import com.gmail.nossr50.datatypes.party.PartyMember;
import com.gmail.nossr50.datatypes.party.ShareMode;
import com.neetgames.mcmmo.party.Party;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.events.party.McMMOPartyAllianceChangeEvent;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

/**
 * About mcMMO parties
 * Parties are identified by a {@link String} name
 * Parties always have a party leader, if the party leader is not defined mcMMO will force party leadership onto someone in the party
 */
//TODO: Needs to be optimized, currently all parties are loaded into memory, it should be changed to as needed, but then we need to handle loading asynchronously and accommodate for that
public final class PartyManager {
    private final @NotNull HashMap<String, Party> parties;
    private final @NotNull File partyFile;

    public PartyManager() {
        String partiesFilePath = mcMMO.getFlatFileDirectory() + "parties.yml";
        partyFile = new File(partiesFilePath);
        parties = new HashMap<>();
    }

    /**
     * Attempts to find a party for a player by UUID
     *
     * @param playerUUID target uuid
     * @return the party if it exists otherwise null
     */
    public @Nullable Party queryParty(@NotNull UUID playerUUID) {
        for(Party party : parties.values()) {
            if(party.getPartyMemberManager().hasMember(playerUUID)) {
                return party;
            }
        }

        return null; //No party
    }

    /**
     * Attempts to find a party by party name
     * Party names are not case sensitive
     *
     * @param partyName party name
     * @return the party if it exists otherwise null
     */
    public @Nullable Party queryParty(@NotNull String partyName) {
        return parties.get(partyName.toLowerCase());
    }

    /**
     * Get the level of a party
     *
     * @param party target party
     * @return the level value of the target party
     */
    public int getPartyLevel(@NotNull Party party) {
        return party.getPartyExperienceManager().getLevel();
    }

    /**
     * Check if a party with a given name already exists.
     *
     * @param partyName The name of the party to check
     * @return true if a party with that name exists, false otherwise
     */
    public boolean isParty(@NotNull String partyName) {
        return getParty(partyName) != null;
    }

    /**
     * Checks if the player can join a party, parties can have a size limit, although there is a permission to bypass this
     *
     * @param player player who is attempting to join the party
     * @param targetParty the target party
     * @return true if party is full and cannot be joined
     */
    public boolean isPartyFull(@NotNull Player player, @NotNull Party targetParty)
    {
        return !Permissions.partySizeBypass(player)
                && targetParty.getPartyMembers().size() >= Config.getInstance().getPartyMaxSize();
    }

    /**
     * Attempt to change parties or join a new party.
     *
     * @param mmoPlayer The player changing or joining parties
     * @param newPartyName The name of the party being joined
     * @return true if the party was joined successfully, false otherwise
     */
    public boolean changeOrJoinParty(@NotNull OnlineMMOPlayer mmoPlayer, @NotNull String newPartyName) {
        Player player = Misc.adaptPlayer(mmoPlayer);

        if (inParty(mmoPlayer)) {
            Party oldParty = mmoPlayer.getParty();

            if (!handlePartyChangeEvent(player, oldParty.getPartyName(), newPartyName, EventReason.CHANGED_PARTIES)) {
                return false;
            }

            removeFromParty(mmoPlayer);
        }
        else return handlePartyChangeEvent(player, null, newPartyName, EventReason.JOINED_PARTY);

        return true;
    }

    /**
     * Check if two online players are in the same party.
     *
     * @param firstPlayer The first player
     * @param secondPlayer The second player
     * @return true if they are in the same party, false otherwise
     */
    public boolean inSameParty(Player firstPlayer, Player secondPlayer) {
        //Profile not loaded
        if(mcMMO.getUserManager().queryPlayer(firstPlayer) == null)
        {
            return false;
        }

        //Profile not loaded
        if(mcMMO.getUserManager().queryPlayer(secondPlayer) == null)
        {
            return false;
        }

        Party firstParty = mcMMO.getUserManager().queryPlayer(firstPlayer).getParty();
        Party secondParty = mcMMO.getUserManager().queryPlayer(secondPlayer).getParty();

        if (firstParty == null || secondParty == null) {
            return false;
        }

        return firstParty.equals(secondParty);
    }

    /**
     * Get the near party members.
     *
     * @param mmoPlayer The player to check
     * @return the near party members
     */
    public List<Player> getNearMembers(OnlineMMOPlayer mmoPlayer) {
        List<Player> nearMembers = new ArrayList<>();
        Party party = mmoPlayer.getParty();

        if (party != null) {
            Player player = Misc.adaptPlayer(mmoPlayer);
            double range = Config.getInstance().getPartyShareRange();

            for (PartyMember member : party.getPartyMembers()) {
                if (!player.equals(member) && member.isValid() && Misc.isNear(player.getLocation(), member.getLocation(), range)) {
                    nearMembers.add(member);
                }
            }
        }

        return nearMembers;
    }

    public List<Player> getNearVisibleMembers(@NotNull OnlineMMOPlayer mmoPlayer) {
        List<Player> nearMembers = new ArrayList<>();
        Party party = mmoPlayer.getParty();

        if (party != null) {
            Player player = Misc.adaptPlayer(mmoPlayer);
            double range = Config.getInstance().getPartyShareRange();

            for (Player member : party.getVisibleMembers(player)) {
                if (!player.equals(member)
                        && member.isValid()
                        && Misc.isNear(player.getLocation(), member.getLocation(), range)) {
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
    public LinkedHashMap<UUID, String> getAllMembers(Player player) {
        Party party = getParty(player);

        return party == null ? new LinkedHashMap<>() : party.getMembers();
    }

    /**
     * Get a list of all online players in this party.
     *
     * @param partyName The party to check
     * @return all online players in this party
     */
    public List<Player> getOnlineMembers(String partyName) {
        return getOnlineMembers(getParty(partyName));
    }

    /**
     * Get a list of all online players in this party.
     *
     * @param player The player to check
     * @return all online players in this party
     */
    public List<Player> getOnlineMembers(Player player) {
        return getOnlineMembers(getParty(player));
    }

    private List<Player> getOnlineMembers(Party party) {
        return party == null ? new ArrayList<>() : party.getPartyMembers();
    }

    /**
     * Retrieve a party by its name
     *
     * @param partyName The party name
     * @return the existing party, null otherwise
     */
    public Party getParty(String partyName) {
        for (Party party : parties) {
            if (party.getPartyName().equalsIgnoreCase(partyName)) {
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
    @Deprecated
    public Party getPlayerParty(String playerName) {
        for (Party party : parties) {
            if (party.getMembers().containsKey(playerName)) {
                return party;
            }
        }

        return null;
    }

    /**
     * Retrieve a party by a members uuid
     *
     * @param uuid The members uuid
     * @return the existing party, null otherwise
     */
    public Party getPlayerParty(String playerName, UUID uuid) {
        for (Party party : parties) {
            LinkedHashMap<UUID, String> members = party.getMembers();
            if (members.containsKey(uuid) || members.containsValue(playerName)) {

                // Name changes
                if (members.get(uuid) == null || !members.get(uuid).equals(playerName)) {
                    members.put(uuid, playerName);
                }

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
    public Party getParty(Player player) {
        //Profile not loaded
        if(mcMMO.getUserManager().queryPlayer(player) == null)
        {
            return null;
        }

        OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(player);

        return mmoPlayer.getParty();
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
     * @param player The player to remove
     * @param party The party
     */
    public void removeFromParty(OfflinePlayer player, Party party) {
        LinkedHashMap<UUID, String> members = party.getMembers();
        String playerName = player.getName();

        members.remove(player.getUniqueId());

        if (player.isOnline()) {
            party.getPartyMembers().remove(player.getPlayer());
        }

        if (members.isEmpty()) {
            parties.remove(party);
        }
        else {
            // If the leaving player was the party leader, appoint a new leader from the party members
            if (party.getLeader().getUniqueId().equals(player.getUniqueId())) {
                setPartyLeader(members.keySet().iterator().next(), party);
            }

            informPartyMembersQuit(party, playerName);
        }
    }

    /**
     * Remove a player from a party.
     *
     * @param mmoPlayer The player to remove
     */
    public void removeFromParty(OnlineMMOPlayer mmoPlayer) {
        removeFromParty(Misc.adaptPlayer(mmoPlayer), mmoPlayer.getParty());
        processPartyLeaving(mmoPlayer);
    }

    /**
     * Disband a party. Kicks out all members and removes the party.
     *
     * @param party The party to remove
     */
    public void disbandParty(Party party) {
        //TODO: Potential issues with unloaded profile?
        for (Player member : party.getPartyMembers()) {
            //Profile not loaded
            if(mcMMO.getUserManager().queryPlayer(member) == null)
            {
                continue;
            }

            processPartyLeaving(mcMMO.getUserManager().queryPlayer(member));
        }

        // Disband the alliance between the disbanded party and it's ally
        if (party.getAlly() != null) {
            party.getAlly().setAlly(null);
        }

        parties.remove(party);
    }

    /**
     * Create a new party
     *
     * @param mmoPlayer The player to add to the party
     * @param partyName The party to add the player to
     * @param password The password for this party, null if there was no password
     */
    public void createParty(OnlineMMOPlayer mmoPlayer, String partyName, String password) {
        Player player = Misc.adaptPlayer(mmoPlayer);

        Party party = new Party(new PartyLeader(player.getUniqueId(), player.getName()), partyName.replace(".", ""), password);

        if (password != null) {
            player.sendMessage(LocaleLoader.getString("Party.Password.Set", password));
        }

        parties.add(party);

        player.sendMessage(LocaleLoader.getString("Commands.Party.Create", party.getPartyName()));
        addToParty(mmoPlayer, party);
    }

    /**
     * Check if a player can join a party
     *
     * @param player The player trying to join a party
     * @param party The party
     * @param password The password provided by the player
     * @return true if the player can join the party
     */
    public boolean checkPartyPassword(Player player, Party party, String password) {
        if (party.isLocked()) {
            String partyPassword = party.getPartyPassword();

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
     * @param mmoPlayer The player to add to the party
     */
    public void joinInvitedParty(OnlineMMOPlayer mmoPlayer) {
        Party invite = mmoPlayer.getPartyInvite();

        // Check if the party still exists, it might have been disbanded
        if (!parties.contains(invite)) {
            NotificationManager.sendPlayerInformation(Misc.adaptPlayer(mmoPlayer), NotificationType.PARTY_MESSAGE, "Party.Disband");
            return;
        }

        /*
         * Don't let players join a full party
         */
        if(Config.getInstance().getPartyMaxSize() > 0 && invite.getMembers().size() >= Config.getInstance().getPartyMaxSize())
        {
            NotificationManager.sendPlayerInformation(Misc.adaptPlayer(mmoPlayer), NotificationType.PARTY_MESSAGE, "Commands.Party.PartyFull.InviteAccept", invite.getPartyName(), String.valueOf(Config.getInstance().getPartyMaxSize()));
            return;
        }

        NotificationManager.sendPlayerInformation(Misc.adaptPlayer(mmoPlayer), NotificationType.PARTY_MESSAGE, "Commands.Party.Invite.Accepted", invite.getPartyName());
        mmoPlayer.removePartyInvite();
        addToParty(mmoPlayer, invite);
    }

    /**
     * Accept a party alliance invitation
     *
     * @param mmoPlayer The player who accepts the alliance invite
     */
    public void acceptAllianceInvite(OnlineMMOPlayer mmoPlayer) {
        Party invite = mmoPlayer.getPartyAllianceInvite();
        Player player = Misc.adaptPlayer(mmoPlayer);

        // Check if the party still exists, it might have been disbanded
        if (!parties.contains(invite)) {
            player.sendMessage(LocaleLoader.getString("Party.Disband"));
            return;
        }

        if (!handlePartyChangeAllianceEvent(player, mmoPlayer.getParty().getName(), invite.getPartyName(), McMMOPartyAllianceChangeEvent.EventReason.FORMED_ALLIANCE)) {
            return;
        }

        player.sendMessage(LocaleLoader.getString("Commands.Party.Alliance.Invite.Accepted", invite.getPartyName()));
        mmoPlayer.removePartyAllianceInvite();

        createAlliance(mmoPlayer.getParty(), invite);
    }

    public void createAlliance(Party firstParty, Party secondParty) {
        firstParty.setAlly(secondParty);
        secondParty.setAlly(firstParty);

        for (Player member : firstParty.getPartyMembers()) {
            member.sendMessage(LocaleLoader.getString("Party.Alliance.Formed", secondParty.getPartyName()));
        }

        for (Player member : secondParty.getPartyMembers()) {
            member.sendMessage(LocaleLoader.getString("Party.Alliance.Formed", firstParty.getPartyName()));
        }
    }

    public boolean disbandAlliance(Player player, Party firstParty, Party secondParty){
        if (!handlePartyChangeAllianceEvent(player, firstParty.getPartyName(), secondParty.getPartyName(), McMMOPartyAllianceChangeEvent.EventReason.DISBAND_ALLIANCE)) {
            return false;
        }

        mcMMO.getPartyManager().disbandAlliance(firstParty, secondParty);
        return true;
    }

    private void disbandAlliance(Party firstParty, Party secondParty) {
        firstParty.setAlly(null);
        secondParty.setAlly(null);

        for (Player member : firstParty.getPartyMembers()) {
            member.sendMessage(LocaleLoader.getString("Party.Alliance.Disband", secondParty.getPartyName()));
        }

        for (Player member : secondParty.getPartyMembers()) {
            member.sendMessage(LocaleLoader.getString("Party.Alliance.Disband", firstParty.getPartyName()));
        }
    }

    /**
     * Add a player to a party
     *
     * @param mmoPlayer The player to add to the party
     * @param party The party
     */
    public void addToParty(OnlineMMOPlayer mmoPlayer, Party party) {
        Player player = Misc.adaptPlayer(mmoPlayer);
        String playerName = player.getName();

        informPartyMembersJoin(party, playerName);
        party.getMembers().put(player.getUniqueId(), player.getName());
        party.getPartyMembers().add(player);
    }

    /**
     * Get the leader of a party.
     *
     * @param partyName The party name
     * @return the leader of the party
     */
    public String getPartyLeaderName(String partyName) {
        Party party = getParty(partyName);

        return party == null ? null : party.getLeader().getPlayerName();
    }

    /**
     * Set the leader of a party.
     *
     * @param uuid The uuid of the player to set as leader
     * @param party The party
     */
    public void setPartyLeader(UUID uuid, Party party) {
        OfflinePlayer player = mcMMO.p.getServer().getOfflinePlayer(uuid);
        UUID leaderUniqueId = party.getLeader().getUniqueId();

        for (Player member : party.getPartyMembers()) {
            UUID memberUniqueId = member.getUniqueId();

            if (memberUniqueId.equals(player.getUniqueId())) {
                member.sendMessage(LocaleLoader.getString("Party.Owner.Player"));
            }
            else if (memberUniqueId.equals(leaderUniqueId)) {
                member.sendMessage(LocaleLoader.getString("Party.Owner.NotLeader"));
            }
            else {
                member.sendMessage(LocaleLoader.getString("Party.Owner.New", player.getName()));
            }
        }

        party.setLeader(new PartyLeader(player.getUniqueId(), player.getName()));
    }

    /**
     * Check if a player can invite others to his party.
     *
     * @return true if the player can invite
     */
    public boolean canInvite(OnlineMMOPlayer mmoPlayer) {
        Party party = mmoPlayer.getParty();

        return !party.isLocked() || party.getLeader().getUniqueId().equals(mmoPlayer.getUUID());
    }

    /**
     * Load party file.
     */
    public void loadParties() {
        if (!partyFile.exists()) {
            return;
        }

        if (mcMMO.getUpgradeManager().shouldUpgrade(UpgradeType.ADD_UUIDS_PARTY)) {
            loadAndUpgradeParties();
            return;
        }

        try {
            YamlConfiguration partiesFile;
            partiesFile = YamlConfiguration.loadConfiguration(partyFile);

            ArrayList<Party> hasAlly = new ArrayList<>();

            for (String partyName : partiesFile.getConfigurationSection("").getKeys(false)) {
                Party party = new Party(partyName);

                String[] leaderSplit = partiesFile.getString(partyName + ".Leader").split("[|]");
                party.setLeader(new PartyLeader(UUID.fromString(leaderSplit[0]), leaderSplit[1]));
                party.setPartyPassword(partiesFile.getString(partyName + ".Password"));
                party.setPartyLock(partiesFile.getBoolean(partyName + ".Locked"));
                party.setLevel(partiesFile.getInt(partyName + ".Level"));
                party.setXp(partiesFile.getInt(partyName + ".Xp"));

                if (partiesFile.getString(partyName + ".Ally") != null) {
                    hasAlly.add(party);
                }

                party.setXpShareMode(ShareMode.getShareMode(partiesFile.getString(partyName + ".ExpShareMode", "NONE")));
                party.setItemShareMode(ShareMode.getShareMode(partiesFile.getString(partyName + ".ItemShareMode", "NONE")));

                for (ItemShareType itemShareType : ItemShareType.values()) {
                    party.setSharingDrops(itemShareType, partiesFile.getBoolean(partyName + ".ItemShareType." + itemShareType.toString(), true));
                }

                LinkedHashMap<UUID, String> members = party.getMembers();

                for (String memberEntry : partiesFile.getStringList(partyName + ".Members")) {
                    String[] memberSplit = memberEntry.split("[|]");
                    members.put(UUID.fromString(memberSplit[0]), memberSplit[1]);
                }

                parties.add(party);
            }

            mcMMO.p.getLogger().info("Loaded (" + parties.size() + ") Parties...");

            for (Party party : hasAlly) {
                party.setAlly(mcMMO.getPartyManager().getParty(partiesFile.getString(party.getPartyName() + ".Ally")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Save party file.
     */
    public void saveParties() {
        if (partyFile.exists()) {
            if (!partyFile.delete()) {
                mcMMO.p.getLogger().warning("Could not delete party file. Party saving failed!");
                return;
            }
        }

        YamlConfiguration partiesFile = new YamlConfiguration();

        mcMMO.p.getLogger().info("Saving Parties... (" + parties.size() + ")");
        for (Party party : parties) {
            String partyName = party.getPartyName();
            PartyLeader leader = party.getLeader();

            partiesFile.set(partyName + ".Leader", leader.getUniqueId().toString() + "|" + leader.getPlayerName());
            partiesFile.set(partyName + ".Password", party.getPartyPassword());
            partiesFile.set(partyName + ".Locked", party.isLocked());
            partiesFile.set(partyName + ".Level", party.getLevel());
            partiesFile.set(partyName + ".Xp", (int) party.getXp());
            partiesFile.set(partyName + ".Ally", (party.getAlly() != null) ? party.getAlly().getPartyName() : "");
            partiesFile.set(partyName + ".ExpShareMode", party.getXpShareMode().toString());
            partiesFile.set(partyName + ".ItemShareMode", party.getItemShareMode().toString());

            for (ItemShareType itemShareType : ItemShareType.values()) {
                partiesFile.set(partyName + ".ItemShareType." + itemShareType.toString(), party.sharingDrops(itemShareType));
            }

            List<String> members = new ArrayList<>();

            for (Entry<UUID, String> memberEntry : party.getMembers().entrySet()) {
                String memberUniqueId = memberEntry.getKey() == null ? "" : memberEntry.getKey().toString();
                String memberName = memberEntry.getValue();

                if (!members.contains(memberName)) {
                    members.add(memberUniqueId + "|" + memberName);
                }
            }

            partiesFile.set(partyName + ".Members", members);
        }

        try {
            partiesFile.save(partyFile);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAndUpgradeParties() {
        YamlConfiguration partiesFile = YamlConfiguration.loadConfiguration(partyFile);

        if (!partyFile.renameTo(new File(mcMMO.getFlatFileDirectory() + "parties.yml.converted"))) {
            mcMMO.p.getLogger().severe("Could not rename parties.yml to parties.yml.converted!");
            return;
        }

        ArrayList<Party> hasAlly = new ArrayList<>();

        for (String partyName : partiesFile.getConfigurationSection("").getKeys(false)) {
            Party party = new Party(partyName);

            String leaderName = partiesFile.getString(partyName + ".Leader");
            PlayerProfile profile = mcMMO.getDatabaseManager().queryPlayerDataByUUID(leaderName, false);

            if (!profile.isLoaded()) {
                mcMMO.p.getLogger().warning("Could not find UUID in database for party leader " + leaderName + " in party " + partyName);
                continue;
            }

            UUID leaderUniqueId = profile.getUniqueId();

            party.setLeader(new PartyLeader(leaderUniqueId, leaderName));
            party.setPartyPassword(partiesFile.getString(partyName + ".Password"));
            party.setPartyLock(partiesFile.getBoolean(partyName + ".Locked"));
            party.setLevel(partiesFile.getInt(partyName + ".Level"));
            party.setXp(partiesFile.getInt(partyName + ".Xp"));

            if (partiesFile.getString(partyName + ".Ally") != null) {
                hasAlly.add(party);
            }

            party.setXpShareMode(ShareMode.getShareMode(partiesFile.getString(partyName + ".ExpShareMode", "NONE")));
            party.setItemShareMode(ShareMode.getShareMode(partiesFile.getString(partyName + ".ItemShareMode", "NONE")));

            for (ItemShareType itemShareType : ItemShareType.values()) {
                party.setSharingDrops(itemShareType, partiesFile.getBoolean(partyName + ".ItemShareType." + itemShareType.toString(), true));
            }

            LinkedHashMap<UUID, String> members = party.getMembers();

            for (String memberName : partiesFile.getStringList(partyName + ".Members")) {
                PlayerProfile memberProfile = mcMMO.getDatabaseManager().queryPlayerDataByUUID(memberName, false);

                if (!memberProfile.isLoaded()) {
                    mcMMO.p.getLogger().warning("Could not find UUID in database for party member " + memberName + " in party " + partyName);
                    continue;
                }

                UUID memberUniqueId = memberProfile.getUniqueId();

                members.put(memberUniqueId, memberName);
            }

            parties.add(party);
        }

        mcMMO.p.getLogger().info("Loaded (" + parties.size() + ") Parties...");

        for (Party party : hasAlly) {
            party.setAlly(mcMMO.getPartyManager().getParty(partiesFile.getString(party.getPartyName() + ".Ally")));
        }

        mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.ADD_UUIDS_PARTY);
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
    public boolean handlePartyChangeEvent(Player player, String oldPartyName, String newPartyName, EventReason reason) {
        McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, oldPartyName, newPartyName, reason);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return !event.isCancelled();
    }

    /**
     * Handle party alliance change event.
     *
     * @param player The player changing party alliances
     * @param oldAllyName The name of the old ally
     * @param newAllyName The name of the new ally
     * @param reason The reason for changing allies
     * @return true if the change event was successful, false otherwise
     */
    public boolean handlePartyChangeAllianceEvent(Player player, String oldAllyName, String newAllyName, McMMOPartyAllianceChangeEvent.EventReason reason) {
        McMMOPartyAllianceChangeEvent event = new McMMOPartyAllianceChangeEvent(player, oldAllyName, newAllyName, reason);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return !event.isCancelled();
    }

    /**
     * Remove party data from the mmoPlayer.
     *
     * @param mmoPlayer The player to remove party data from.
     */
    public void processPartyLeaving(@NotNull OnlineMMOPlayer mmoPlayer) {
        mmoPlayer.removeParty();
        mmoPlayer.setChatMode(ChatChannel.NONE);
        mmoPlayer.setItemShareModifier(10);
    }

    /**
     * Notify party members when the party levels up.
     *
     * @param party The concerned party
     * @param levelsGained The amount of levels gained
     * @param level The current party level
     */
    public void informPartyMembersLevelUp(Party party, int levelsGained, int level) {
        boolean levelUpSoundsEnabled = Config.getInstance().getLevelUpSoundsEnabled();
        for (Player member : party.getPartyMembers()) {
            member.sendMessage(LocaleLoader.getString("Party.LevelUp", levelsGained, level));

            if (levelUpSoundsEnabled) {
                SoundManager.sendSound(member, member.getLocation(), SoundType.LEVEL_UP);
            }
        }
    }

    /**
     * Notify party members when a player joins.
     *
     * @param party The concerned party
     * @param playerName The name of the player that joined
     */
    private void informPartyMembersJoin(Party party, String playerName) {
        for (Player member : party.getPartyMembers()) {
            member.sendMessage(LocaleLoader.getString("Party.InformedOnJoin", playerName));
        }
    }

    /**
     * Notify party members when a party member quits.
     *
     * @param party The concerned party
     * @param playerName The name of the player that left
     */
    private void informPartyMembersQuit(Party party, String playerName) {
        for (Player member : party.getPartyMembers()) {
            member.sendMessage(LocaleLoader.getString("Party.InformedOnQuit", playerName));
        }
    }
}
