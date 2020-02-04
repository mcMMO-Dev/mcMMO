package com.gmail.nossr50.party;

import com.gmail.nossr50.datatypes.chat.ChatMode;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.party.*;
import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.events.party.McMMOPartyAllianceChangeEvent;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.items.TeleportationWarmup;
import com.gmail.nossr50.util.sounds.SoundType;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

public final class PartyManager {
    private final mcMMO pluginRef;
    private List<Party> parties;
    private File partyFile;

    public PartyManager(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        parties = new ArrayList<>();
        partyFile = new File(pluginRef.getFlatFileDirectory() + "parties.yml");
    }

    public boolean canTeleport(CommandSender sender, Player player, String targetName) {
        BukkitMMOPlayer mcMMOTarget = pluginRef.getUserManager().getPlayer(targetName);

        if (!pluginRef.getCommandTools().checkPlayerExistence(sender, targetName, mcMMOTarget)) {
            return false;
        }

        Player target = mcMMOTarget.getNative();

        if (player.equals(target)) {
            player.sendMessage(pluginRef.getLocaleManager().getString("Party.Teleport.Self"));
            return false;
        }

        if (!pluginRef.getPartyManager().inSameParty(player, target)) {
            player.sendMessage(pluginRef.getLocaleManager().getString("Party.NotInYourParty", targetName));
            return false;
        }

        if (!mcMMOTarget.getPartyTeleportRecord().isEnabled()) {
            player.sendMessage(pluginRef.getLocaleManager().getString("Party.Teleport.Disabled", targetName));
            return false;
        }

        if (!target.isValid()) {
            player.sendMessage(pluginRef.getLocaleManager().getString("Party.Teleport.Dead"));
            return false;
        }

        return true;
    }

    public void handleTeleportWarmup(Player teleportingPlayer, Player targetPlayer) {
        if (pluginRef.getUserManager().getPlayer(targetPlayer) == null) {
            targetPlayer.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
            return;
        }

        if (pluginRef.getUserManager().getPlayer(teleportingPlayer) == null) {
            teleportingPlayer.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
            return;
        }

        BukkitMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(teleportingPlayer);
        BukkitMMOPlayer mcMMOTarget = pluginRef.getUserManager().getPlayer(targetPlayer);

        long warmup = pluginRef.getConfigManager().getConfigParty().getPTP().getPtpWarmup();

        mcMMOPlayer.actualizeTeleportCommenceLocation(teleportingPlayer);

        if (warmup > 0) {
            teleportingPlayer.sendMessage(pluginRef.getLocaleManager().getString("Teleport.Commencing", warmup));
            new TeleportationWarmup(pluginRef, mcMMOPlayer, mcMMOTarget).runTaskLater(pluginRef, 20 * warmup);
        } else {
            pluginRef.getEventManager().handlePartyTeleportEvent(teleportingPlayer, targetPlayer);
        }
    }

    /**
     * Grab the appropriate unlock level for a party feature
     *
     * @param partyFeature target party feature
     * @return the unlock level for the feature
     */
    public int getPartyFeatureUnlockLevel(PartyFeature partyFeature) {
        if (pluginRef.getDynamicSettingsManager().getPartyFeatureUnlocks().get(partyFeature) == null)
            return 0;
        else
            return pluginRef.getDynamicSettingsManager().getPartyFeatureUnlocks().get(partyFeature);
    }

    /**
     * Check if a party with a given name already exists.
     *
     * @param player    The player to notify
     * @param partyName The name of the party to check
     * @return true if a party with that name exists, false otherwise
     */
    public boolean checkPartyExistence(Player player, String partyName) {
        if (getParty(partyName) == null) {
            return false;
        }

        player.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.AlreadyExists", partyName));
        return true;
    }

    /**
     * Checks if the player can join a party, parties can have a size limit, although there is a permission to bypass this
     *
     * @param player      player who is attempting to join the party
     * @param targetParty the target party
     * @return true if party is full and cannot be joined
     */
    public boolean isPartyFull(Player player, Party targetParty) {
        return !pluginRef.getPermissionTools().partySizeBypass(player)
                && targetParty.getMembers().size() >= pluginRef.getConfigManager().getConfigParty().getPartySizeLimit();
    }

    /**
     * Attempt to change parties or join a new party.
     *
     * @param mcMMOPlayer  The player changing or joining parties
     * @param newPartyName The name of the party being joined
     * @return true if the party was joined successfully, false otherwise
     */
    public boolean changeOrJoinParty(BukkitMMOPlayer mcMMOPlayer, String newPartyName) {
        Player player = mcMMOPlayer.getNative();

        if (mcMMOPlayer.inParty()) {
            Party oldParty = mcMMOPlayer.getParty();

            if (!handlePartyChangeEvent(player, oldParty.getName(), newPartyName, EventReason.CHANGED_PARTIES)) {
                return false;
            }

            removeFromParty(mcMMOPlayer);
        } else return handlePartyChangeEvent(player, null, newPartyName, EventReason.JOINED_PARTY);

        return true;
    }

    /**
     * Check if two online players are in the same party.
     *
     * @param firstPlayer  The first player
     * @param secondPlayer The second player
     * @return true if they are in the same party, false otherwise
     */
    public boolean inSameParty(Player firstPlayer, Player secondPlayer) {
        //If the party system is disabled, return false
        if (!pluginRef.getConfigManager().getConfigParty().isPartySystemEnabled())
            return false;

        //Profile not loaded
        if (pluginRef.getUserManager().getPlayer(firstPlayer) == null) {
            return false;
        }

        //Profile not loaded
        if (pluginRef.getUserManager().getPlayer(secondPlayer) == null) {
            return false;
        }

        Party firstParty = pluginRef.getUserManager().getPlayer(firstPlayer).getParty();
        Party secondParty = pluginRef.getUserManager().getPlayer(secondPlayer).getParty();

        if (firstParty == null || secondParty == null) {
            return false;
        }

        return firstParty.equals(secondParty);
    }

    public boolean areAllies(Player firstPlayer, Player secondPlayer) {
        //If the party system is disabled, return false
        if (!pluginRef.getConfigManager().getConfigParty().isPartySystemEnabled())
            return false;

        //Profile not loaded
        if (pluginRef.getUserManager().getPlayer(firstPlayer) == null) {
            return false;
        }

        //Profile not loaded
        if (pluginRef.getUserManager().getPlayer(secondPlayer) == null) {
            return false;
        }

        Party firstParty = pluginRef.getUserManager().getPlayer(firstPlayer).getParty();
        Party secondParty = pluginRef.getUserManager().getPlayer(secondPlayer).getParty();

        if (firstParty == null || secondParty == null || firstParty.getAlly() == null || secondParty.getAlly() == null) {
            return false;
        }

        return firstParty.equals(secondParty.getAlly()) && secondParty.equals(firstParty.getAlly());
    }

    /**
     * Get the near party members.
     *
     * @param mcMMOPlayer The player to check
     * @return the near party members
     */
    public List<Player> getNearMembers(BukkitMMOPlayer mcMMOPlayer) {
        List<Player> nearMembers = new ArrayList<>();
        Party party = mcMMOPlayer.getParty();

        if (party != null) {
            Player player = mcMMOPlayer.getNative();
            double range = pluginRef.getPartyXPShareSettings().getPartyShareRange();

            for (Player member : party.getOnlineMembers()) {
                if (!player.equals(member) && member.isValid() && pluginRef.getMiscTools().isNear(player.getLocation(), member.getLocation(), range)) {
                    nearMembers.add(member);
                }
            }
        }

        return nearMembers;
    }

    public List<Player> getNearVisibleMembers(BukkitMMOPlayer mcMMOPlayer) {
        List<Player> nearMembers = new ArrayList<>();
        Party party = mcMMOPlayer.getParty();

        if (party != null) {
            Player player = mcMMOPlayer.getNative();
            double range = pluginRef.getPartyXPShareSettings().getPartyShareRange();

            for (Player member : party.getVisibleMembers(player)) {
                if (!player.equals(member)
                        && member.isValid()
                        && pluginRef.getMiscTools().isNear(player.getLocation(), member.getLocation(), range)) {
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
        return party == null ? new ArrayList<>() : party.getOnlineMembers();
    }

    /**
     * Retrieve a party by its name
     *
     * @param partyName The party name
     * @return the existing party, null otherwise
     */
    public Party getParty(String partyName) {
        //If the party system is disabled, return null
        if (!pluginRef.getConfigManager().getConfigParty().isPartySystemEnabled())
            return null;

        for (Party party : parties) {
            if (party.getName().equalsIgnoreCase(partyName)) {
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
        //If the party system is disabled, return null
        if (!pluginRef.getConfigManager().getConfigParty().isPartySystemEnabled())
            return null;

        for (Party party : parties) {
            if (party.getMembers().keySet().contains(playerName)) {
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
        //If the party system is disabled, return null
        if (!pluginRef.getConfigManager().getConfigParty().isPartySystemEnabled())
            return null;

        for (Party party : parties) {
            LinkedHashMap<UUID, String> members = party.getMembers();
            if (members.keySet().contains(uuid) || members.values().contains(playerName)) {

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
        if (pluginRef.getUserManager().getPlayer(player) == null) {
            return null;
        }

        BukkitMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);

        return mcMMOPlayer.getParty();
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
     * @param party  The party
     */
    public void removeFromParty(OfflinePlayer player, Party party) {
        LinkedHashMap<UUID, String> members = party.getMembers();
        String playerName = player.getName();

        members.remove(player.getUniqueId());

        if (player.isOnline()) {
            party.getOnlineMembers().remove(player.getPlayer());
        }

        if (members.isEmpty()) {
            parties.remove(party);
        } else {
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
     * @param mcMMOPlayer The player to remove
     */
    public void removeFromParty(BukkitMMOPlayer mcMMOPlayer) {
        removeFromParty(mcMMOPlayer.getNative(), mcMMOPlayer.getParty());
        processPartyLeaving(mcMMOPlayer);
    }

    /**
     * Disband a party. Kicks out all members and removes the party.
     *
     * @param party The party to remove
     */
    public void disbandParty(Party party) {
        //TODO: Potential issues with unloaded profile?
        for (Player member : party.getOnlineMembers()) {
            //Profile not loaded
            if (pluginRef.getUserManager().getPlayer(member) == null) {
                continue;
            }

            processPartyLeaving(pluginRef.getUserManager().getPlayer(member));
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
     * @param mcMMOPlayer The player to add to the party
     * @param partyName   The party to add the player to
     * @param password    The password for this party, null if there was no password
     */
    public void createParty(BukkitMMOPlayer mcMMOPlayer, String partyName, String password) {
        Player player = mcMMOPlayer.getNative();

        Party party = new Party(new PartyLeader(player.getUniqueId(), player.getName()), partyName.replace(".", ""), password, pluginRef);

        if (password != null) {
            player.sendMessage(pluginRef.getLocaleManager().getString("Party.Password.Set", password));
        }

        parties.add(party);

        player.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.Create", party.getName()));
        addToParty(mcMMOPlayer, party);
    }

    /**
     * Check if a player can join a party
     *
     * @param player   The player trying to join a party
     * @param party    The party
     * @param password The password provided by the player
     * @return true if the player can join the party
     */
    public boolean checkPartyPassword(Player player, Party party, String password) {
        if (party.isLocked()) {
            String partyPassword = party.getPassword();

            if (partyPassword == null) {
                player.sendMessage(pluginRef.getLocaleManager().getString("Party.Locked"));
                return false;
            }

            if (password == null) {
                player.sendMessage(pluginRef.getLocaleManager().getString("Party.Password.None"));
                return false;
            }

            if (!password.equals(partyPassword)) {
                player.sendMessage(pluginRef.getLocaleManager().getString("Party.Password.Incorrect"));
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
    public void joinInvitedParty(BukkitMMOPlayer mcMMOPlayer) {
        Party invite = mcMMOPlayer.getPartyInvite();

        // Check if the party still exists, it might have been disbanded
        if (!parties.contains(invite)) {
            pluginRef.getNotificationManager().sendPlayerInformation(mcMMOPlayer.getNative(), NotificationType.PARTY_MESSAGE, "Party.Disband");
            return;
        }

        /*
         * Don't let players join a full party
         */
        if (pluginRef.getConfigManager().getConfigParty().isPartySizeCapped() && invite.getMembers().size() >= pluginRef.getConfigManager().getConfigParty().getPartySizeLimit()) {
            pluginRef.getNotificationManager().sendPlayerInformation(mcMMOPlayer.getNative(),
                    NotificationType.PARTY_MESSAGE, "Commands.Party.PartyFull.InviteAccept",
                    invite.getName(), String.valueOf(pluginRef.getConfigManager().getConfigParty().getPartySizeLimit()));
            return;
        }

        pluginRef.getNotificationManager().sendPlayerInformation(mcMMOPlayer.getNative(), NotificationType.PARTY_MESSAGE, "Commands.Party.Invite.Accepted", invite.getName());
        mcMMOPlayer.removePartyInvite();
        addToParty(mcMMOPlayer, invite);
    }

    /**
     * Accept a party alliance invitation
     *
     * @param mcMMOPlayer The player who accepts the alliance invite
     */
    public void acceptAllianceInvite(BukkitMMOPlayer mcMMOPlayer) {
        Party invite = mcMMOPlayer.getPartyAllianceInvite();
        Player player = mcMMOPlayer.getNative();

        // Check if the party still exists, it might have been disbanded
        if (!parties.contains(invite)) {
            player.sendMessage(pluginRef.getLocaleManager().getString("Party.Disband"));
            return;
        }

        if (!handlePartyChangeAllianceEvent(player, mcMMOPlayer.getParty().getName(), invite.getName(), McMMOPartyAllianceChangeEvent.EventReason.FORMED_ALLIANCE)) {
            return;
        }

        player.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.Alliance.Invite.Accepted", invite.getName()));
        mcMMOPlayer.removePartyAllianceInvite();

        createAlliance(mcMMOPlayer.getParty(), invite);
    }

    private void createAlliance(Party firstParty, Party secondParty) {
        firstParty.setAlly(secondParty);
        secondParty.setAlly(firstParty);

        for (Player member : firstParty.getOnlineMembers()) {
            member.sendMessage(pluginRef.getLocaleManager().getString("Party.Alliance.Formed", secondParty.getName()));
        }

        for (Player member : secondParty.getOnlineMembers()) {
            member.sendMessage(pluginRef.getLocaleManager().getString("Party.Alliance.Formed", firstParty.getName()));
        }
    }

    public void disbandAlliance(Player player, Party firstParty, Party secondParty) {
        if (!handlePartyChangeAllianceEvent(player, firstParty.getName(), secondParty.getName(), McMMOPartyAllianceChangeEvent.EventReason.DISBAND_ALLIANCE)) {
            return;
        }

        firstParty.setAlly(null);
        secondParty.setAlly(null);

        for (Player member : firstParty.getOnlineMembers()) {
            member.sendMessage(pluginRef.getLocaleManager().getString("Party.Alliance.Disband", secondParty.getName()));
        }

        for (Player member : secondParty.getOnlineMembers()) {
            member.sendMessage(pluginRef.getLocaleManager().getString("Party.Alliance.Disband", firstParty.getName()));
        }
    }

    /**
     * Add a player to a party
     *
     * @param mcMMOPlayer The player to add to the party
     * @param party       The party
     */
    public void addToParty(BukkitMMOPlayer mcMMOPlayer, Party party) {
        Player player = mcMMOPlayer.getNative();
        String playerName = player.getName();

        informPartyMembersJoin(party, playerName);
        mcMMOPlayer.setParty(party);
        party.getMembers().put(player.getUniqueId(), player.getName());
        party.getOnlineMembers().add(player);
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
     * @param uuid  The uuid of the player to set as leader
     * @param party The party
     */
    public void setPartyLeader(UUID uuid, Party party) {
        OfflinePlayer player = pluginRef.getServer().getOfflinePlayer(uuid);
        UUID leaderUniqueId = party.getLeader().getUniqueId();

        for (Player member : party.getOnlineMembers()) {
            UUID memberUniqueId = member.getUniqueId();

            if (memberUniqueId.equals(player.getUniqueId())) {
                member.sendMessage(pluginRef.getLocaleManager().getString("Party.Owner.Player"));
            } else if (memberUniqueId.equals(leaderUniqueId)) {
                member.sendMessage(pluginRef.getLocaleManager().getString("Party.Owner.NotLeader"));
            } else {
                member.sendMessage(pluginRef.getLocaleManager().getString("Party.Owner.New", player.getName()));
            }
        }

        party.setLeader(new PartyLeader(player.getUniqueId(), player.getName()));
    }

    /**
     * Check if a player can invite others to his party.
     *
     * @return true if the player can invite
     */
    public boolean canInvite(BukkitMMOPlayer mcMMOPlayer) {
        Party party = mcMMOPlayer.getParty();

        return !party.isLocked() || party.getLeader().getUniqueId().equals(mcMMOPlayer.getNative().getUniqueId());
    }

    /**
     * Load party file.
     */
    public void loadParties() {
        if (!partyFile.exists()) {
            return;
        }

        /*if (mcMMO.getUpgradeManager().shouldUpgrade(UpgradeType.ADD_UUIDS_PARTY)) {
            loadAndUpgradeParties();
            return;
        }*/

        try {
            YamlConfiguration partiesFile;
            partiesFile = YamlConfiguration.loadConfiguration(partyFile);

            ArrayList<Party> hasAlly = new ArrayList<Party>();

            for (String partyName : partiesFile.getConfigurationSection("").getKeys(false)) {
                Party party = new Party(partyName, pluginRef);

                String[] leaderSplit = partiesFile.getString(partyName + ".Leader").split("[|]");
                party.setLeader(new PartyLeader(UUID.fromString(leaderSplit[0]), leaderSplit[1]));
                party.setPassword(partiesFile.getString(partyName + ".Password"));
                party.setLocked(partiesFile.getBoolean(partyName + ".Locked"));
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

            pluginRef.debug("Loaded (" + parties.size() + ") Parties...");

            for (Party party : hasAlly) {
                party.setAlly(getParty(partiesFile.getString(party.getName() + ".Ally")));
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
                pluginRef.getLogger().warning("Could not delete party file. Party saving failed!");
                return;
            }
        }

        YamlConfiguration partiesFile = new YamlConfiguration();

        pluginRef.debug("Saving Parties... (" + parties.size() + ")");
        for (Party party : parties) {
            String partyName = party.getName();
            PartyLeader leader = party.getLeader();

            partiesFile.set(partyName + ".Leader", leader.getUniqueId().toString() + "|" + leader.getPlayerName());
            partiesFile.set(partyName + ".Password", party.getPassword());
            partiesFile.set(partyName + ".Locked", party.isLocked());
            partiesFile.set(partyName + ".Level", party.getLevel());
            partiesFile.set(partyName + ".Xp", (int) party.getXp());
            partiesFile.set(partyName + ".Ally", (party.getAlly() != null) ? party.getAlly().getName() : "");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private static void loadAndUpgradeParties() {
        YamlConfiguration partiesFile = YamlConfiguration.loadConfiguration(partyFile);

        if (!partyFile.renameTo(new File(mcMMO.getFlatFileDirectory() + "parties.yml.converted"))) {
            mcMMO.p.getLogger().severe("Could not rename parties.yml to parties.yml.converted!");
            return;
        }

        ArrayList<Party> hasAlly = new ArrayList<Party>();

        for (String partyName : partiesFile.getConfigurationSection("").getKeys(false)) {
            Party party = new Party(partyName);

            String leaderName = partiesFile.getString(partyName + ".Leader");
            PlayerProfile profile = mcMMO.getDatabaseManager().loadPlayerProfile(leaderName, false);

            if (!profile.isLoaded()) {
                mcMMO.p.getLogger().warning("Could not find UUID in database for party leader " + leaderName + " in party " + partyName);
                continue;
            }

            UUID leaderUniqueId = profile.getUniqueId();

            party.setLeader(new PartyLeader(leaderUniqueId, leaderName));
            party.setPassword(partiesFile.getString(partyName + ".Password"));
            party.setLocked(partiesFile.getBoolean(partyName + ".Locked"));
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
                PlayerProfile memberProfile = mcMMO.getDatabaseManager().loadPlayerProfile(memberName, false);

                if (!memberProfile.isLoaded()) {
                    mcMMO.p.getLogger().warning("Could not find UUID in database for party member " + memberName + " in party " + partyName);
                    continue;
                }

                UUID memberUniqueId = memberProfile.getUniqueId();

                members.put(memberUniqueId, memberName);
            }

            parties.add(party);
        }

        mcMMO.p.debug("Loaded (" + parties.size() + ") Parties...");

        for (Party party : hasAlly) {
            party.setAlly(pluginRef.getPartyManager().getParty(partiesFile.getString(party.getName() + ".Ally")));
        }

        //mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.ADD_UUIDS_PARTY);
    }*/

    /**
     * Handle party change event.
     *
     * @param player       The player changing parties
     * @param oldPartyName The name of the old party
     * @param newPartyName The name of the new party
     * @param reason       The reason for changing parties
     * @return true if the change event was successful, false otherwise
     */
    public boolean handlePartyChangeEvent(Player player, String oldPartyName, String newPartyName, EventReason reason) {
        McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, oldPartyName, newPartyName, reason);
        pluginRef.getServer().getPluginManager().callEvent(event);

        return !event.isCancelled();
    }

    /**
     * Handle party alliance change event.
     *
     * @param player      The player changing party alliances
     * @param oldAllyName The name of the old ally
     * @param newAllyName The name of the new ally
     * @param reason      The reason for changing allies
     * @return true if the change event was successful, false otherwise
     */
    public boolean handlePartyChangeAllianceEvent(Player player, String oldAllyName, String newAllyName, McMMOPartyAllianceChangeEvent.EventReason reason) {
        McMMOPartyAllianceChangeEvent event = new McMMOPartyAllianceChangeEvent(player, oldAllyName, newAllyName, reason);
        pluginRef.getServer().getPluginManager().callEvent(event);

        return !event.isCancelled();
    }

    /**
     * Remove party data from the mcMMOPlayer.
     *
     * @param mcMMOPlayer The player to remove party data from.
     */
    public void processPartyLeaving(BukkitMMOPlayer mcMMOPlayer) {
        mcMMOPlayer.removeParty();
        mcMMOPlayer.disableChat(ChatMode.PARTY);
        mcMMOPlayer.setItemShareModifier(10);
    }

    /**
     * Notify party members when the party levels up.
     *
     * @param party        The concerned party
     * @param levelsGained The amount of levels gained
     * @param level        The current party level
     */
    public void informPartyMembersLevelUp(Party party, int levelsGained, int level) {
        for (Player member : party.getOnlineMembers()) {
            member.sendMessage(pluginRef.getLocaleManager().getString("Party.LevelUp", levelsGained, level));

            pluginRef.getSoundManager().sendSound(member, member.getLocation(), SoundType.LEVEL_UP);
        }
    }

    /**
     * Notify party members when a player joins.
     *
     * @param party      The concerned party
     * @param playerName The name of the player that joined
     */
    private void informPartyMembersJoin(Party party, String playerName) {
        for (Player member : party.getOnlineMembers()) {
            member.sendMessage(pluginRef.getLocaleManager().getString("Party.InformedOnJoin", playerName));
        }
    }

    /**
     * Notify party members when a party member quits.
     *
     * @param party      The concerned party
     * @param playerName The name of the player that left
     */
    private void informPartyMembersQuit(Party party, String playerName) {
        for (Player member : party.getOnlineMembers()) {
            member.sendMessage(pluginRef.getLocaleManager().getString("Party.InformedOnQuit", playerName));
        }
    }
}
