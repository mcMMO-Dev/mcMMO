package com.gmail.nossr50.party;

import static java.util.Objects.requireNonNull;

import com.gmail.nossr50.datatypes.chat.ChatChannel;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.party.ItemShareType;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.PartyLeader;
import com.gmail.nossr50.datatypes.party.ShareMode;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.events.party.McMMOPartyAllianceChangeEvent;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PartyManager {
    private final @NotNull List<Party> parties;
    private final @NotNull File partyFile;
    private final @NotNull mcMMO pluginRef;

    public PartyManager(@NotNull mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        final String partiesFilePath = mcMMO.getFlatFileDirectory() + "parties.yml";
        this.partyFile = new File(partiesFilePath);
        this.parties = new ArrayList<>();
    }

    /**
     * Checks if the player can join a party, parties can have a size limit, although there is a
     * permission to bypass this
     *
     * @param player player who is attempting to join the party
     * @param targetParty the target party
     * @return true if party is full and cannot be joined
     */
    public boolean isPartyFull(@NotNull Player player, @NotNull Party targetParty) {
        requireNonNull(player, "player cannot be null!");
        requireNonNull(targetParty, "targetParty cannot be null!");
        return !Permissions.partySizeBypass(player) && pluginRef.getGeneralConfig()
                .getPartyMaxSize() >= 1
                && targetParty.getOnlineMembers().size() >= pluginRef.getGeneralConfig()
                .getPartyMaxSize();
    }

    public boolean areAllies(@NotNull Player firstPlayer, @NotNull Player secondPlayer) {
        requireNonNull(firstPlayer, "firstPlayer cannot be null!");
        requireNonNull(secondPlayer, "secondPlayer cannot be null!");

        //Profile not loaded
        if (UserManager.getPlayer(firstPlayer) == null) {
            return false;
        }

        //Profile not loaded
        if (UserManager.getPlayer(secondPlayer) == null) {
            return false;
        }

        Party firstParty = UserManager.getPlayer(firstPlayer).getParty();
        Party secondParty = UserManager.getPlayer(secondPlayer).getParty();

        if (firstParty == null || secondParty == null || firstParty.getAlly() == null
                || secondParty.getAlly() == null) {
            return false;
        }

        return firstParty.equals(secondParty.getAlly()) && secondParty.equals(firstParty.getAlly());
    }

    /**
     * Get the near party members.
     *
     * @param mmoPlayer The player to check
     * @return the near party members
     */
    public @NotNull List<Player> getNearMembers(@NotNull McMMOPlayer mmoPlayer) {
        requireNonNull(mmoPlayer, "mmoPlayer cannot be null!");
        List<Player> nearMembers = new ArrayList<>();
        Party party = mmoPlayer.getParty();

        if (party != null) {
            Player player = mmoPlayer.getPlayer();
            double range = pluginRef.getGeneralConfig().getPartyShareRange();

            for (Player member : party.getOnlineMembers()) {
                if (!player.equals(member) && member.isValid() && Misc.isNear(player.getLocation(),
                        member.getLocation(), range)) {
                    nearMembers.add(member);
                }
            }
        }

        return nearMembers;
    }

    public @NotNull List<Player> getNearVisibleMembers(@NotNull McMMOPlayer mmoPlayer) {
        requireNonNull(mmoPlayer, "mmoPlayer cannot be null!");
        List<Player> nearMembers = new ArrayList<>();
        Party party = mmoPlayer.getParty();

        if (party != null) {
            Player player = mmoPlayer.getPlayer();
            double range = pluginRef.getGeneralConfig().getPartyShareRange();

            for (Player member : party.getVisibleMembers(player)) {
                if (!player.equals(member) && member.isValid() && Misc.isNear(player.getLocation(),
                        member.getLocation(), range)) {
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
    public @NotNull LinkedHashMap<UUID, String> getAllMembers(@NotNull Player player) {
        requireNonNull(player, "player cannot be null!");
        Party party = getParty(player);

        return party == null ? new LinkedHashMap<>() : party.getMembers();
    }

    /**
     * Get a list of all online players in this party.
     *
     * @param partyName The party to check
     * @return all online players in this party
     */
    public @NotNull List<Player> getOnlineMembers(@NotNull String partyName) {
        requireNonNull(partyName, "partyName cannot be null!");
        return getOnlineMembers(getParty(partyName));
    }

    /**
     * Get a list of all online players in this party.
     *
     * @param player The player to check
     * @return all online players in this party
     */
    public @NotNull List<Player> getOnlineMembers(@NotNull Player player) {
        requireNonNull(player, "player cannot be null!");
        return getOnlineMembers(getParty(player));
    }

    private List<Player> getOnlineMembers(@Nullable Party party) {
        return party == null ? new ArrayList<>() : party.getOnlineMembers();
    }

    /**
     * Retrieve a party by its name
     *
     * @param partyName The party name
     * @return the existing party, null otherwise
     */
    public @Nullable Party getParty(@NotNull String partyName) {
        requireNonNull(partyName, "partyName cannot be null!");
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
    public @Nullable Party getPlayerParty(@NotNull String playerName) {
        requireNonNull(playerName, "playerName cannot be null!");
        for (Party party : parties) {
            if (party.getMembers().containsValue(playerName)) {
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
    public @Nullable Party getPlayerParty(@NotNull String playerName, @NotNull UUID uuid) {
        requireNonNull(playerName, "playerName cannot be null!");
        requireNonNull(uuid, "uuid cannot be null!");
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
    public @Nullable Party getParty(@NotNull Player player) {
        requireNonNull(player, "player cannot be null!");
        //Profile not loaded
        if (UserManager.getPlayer(player) == null) {
            return null;
        }

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
        if (mmoPlayer == null) {
            return null;
        }

        return mmoPlayer.getParty();
    }

    /**
     * Get a list of all current parties.
     *
     * @return the list of parties.
     */
    public @NotNull List<Party> getParties() {
        return parties;
    }

    /**
     * Remove a player from a party.
     *
     * @param player The player to remove
     * @param party The party
     */
    public void removeFromParty(@NotNull OfflinePlayer player, @NotNull Party party) {
        requireNonNull(player, "player cannot be null!");
        requireNonNull(party, "party cannot be null!");

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
     * @param mmoPlayer The player to remove
     */
    public void removeFromParty(@NotNull McMMOPlayer mmoPlayer) {
        requireNonNull(mmoPlayer, "mmoPlayer cannot be null!");
        if (mmoPlayer.getParty() == null) {
            return;
        }

        removeFromParty(mmoPlayer.getPlayer(), mmoPlayer.getParty());
        processPartyLeaving(mmoPlayer);
    }

    /**
     * Disband a party. Kicks out all members and removes the party.
     *
     * @param party The party to remove
     * @deprecated Use {@link #disbandParty(McMMOPlayer, Party)}
     */
    @Deprecated
    public void disbandParty(@NotNull Party party) {
        requireNonNull(party, "party cannot be null!");
        disbandParty(null, party);
    }

    /**
     * Disband a party. Kicks out all members and removes the party.
     *
     * @param mmoPlayer The player to remove (can be null? lol)
     * @param party The party to remove
     */
    public void disbandParty(@Nullable McMMOPlayer mmoPlayer, @NotNull Party party) {
        requireNonNull(party, "party cannot be null!");
        //TODO: Potential issues with unloaded profile?
        for (final Player member : party.getOnlineMembers()) {
            //Profile not loaded
            if (UserManager.getPlayer(member) == null) {
                continue;
            }

            processPartyLeaving(UserManager.getPlayer(member));
        }

        // Disband the alliance between the disbanded party and it's ally
        if (party.getAlly() != null) {
            party.getAlly().setAlly(null);
        }

        parties.remove(party);
        if (mmoPlayer != null) {
            handlePartyChangeEvent(mmoPlayer.getPlayer(), party.getName(), null,
                    EventReason.DISBANDED_PARTY);
        }
    }

    /**
     * Create a new party
     *
     * @param mmoPlayer The player to add to the party
     * @param partyName The party to add the player to
     * @param password The password for this party, null if there was no password
     */
    public void createParty(@NotNull McMMOPlayer mmoPlayer, @NotNull String partyName,
            @Nullable String password) {
        Player player = mmoPlayer.getPlayer();

        Party party = new Party(new PartyLeader(player.getUniqueId(), player.getName()),
                partyName.replace(".", ""),
                password);

        if (password != null) {
            player.sendMessage(LocaleLoader.getString("Party.Password.Set", password));
        }

        parties.add(party);

        player.sendMessage(LocaleLoader.getString("Commands.Party.Create", party.getName()));
        addToParty(mmoPlayer, party);
        handlePartyChangeEvent(player, null, partyName, EventReason.CREATED_PARTY);
    }

    /**
     * Check if a player can join a party
     *
     * @param player The player trying to join a party
     * @param party The party
     * @param password The password provided by the player
     * @return true if the player can join the party
     */
    public boolean checkPartyPassword(@NotNull Player player, @NotNull Party party,
            @Nullable String password) {
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
     * @param mmoPlayer The player to add to the party
     */
    public void joinInvitedParty(@NotNull McMMOPlayer mmoPlayer) {
        requireNonNull(mmoPlayer, "mmoPlayer cannot be null!");
        Party invite = mmoPlayer.getPartyInvite();

        // Check if the party still exists, it might have been disbanded
        if (!parties.contains(invite)) {
            NotificationManager.sendPlayerInformation(mmoPlayer.getPlayer(),
                    NotificationType.PARTY_MESSAGE,
                    "Party.Disband");
            return;
        }

        /*
         * Don't let players join a full party
         */
        if (pluginRef.getGeneralConfig().getPartyMaxSize() > 0 && invite.getMembers()
                .size() >= pluginRef.getGeneralConfig().getPartyMaxSize()) {
            NotificationManager.sendPlayerInformation(mmoPlayer.getPlayer(),
                    NotificationType.PARTY_MESSAGE,
                    "Commands.Party.PartyFull.InviteAccept", invite.getName(),
                    String.valueOf(pluginRef.getGeneralConfig().getPartyMaxSize()));
            return;
        }

        NotificationManager.sendPlayerInformation(mmoPlayer.getPlayer(),
                NotificationType.PARTY_MESSAGE,
                "Commands.Party.Invite.Accepted", invite.getName());
        mmoPlayer.removePartyInvite();
        addToParty(mmoPlayer, invite);
    }

    /**
     * Accept a party alliance invitation
     *
     * @param mmoPlayer The player who accepts the alliance invite
     */
    public void acceptAllianceInvite(@NotNull McMMOPlayer mmoPlayer) {
        requireNonNull(mmoPlayer, "mmoPlayer cannot be null!");
        Party invite = mmoPlayer.getPartyAllianceInvite();
        Player player = mmoPlayer.getPlayer();

        // Check if the party still exists, it might have been disbanded
        if (!parties.contains(invite)) {
            player.sendMessage(LocaleLoader.getString("Party.Disband"));
            return;
        }

        if (!handlePartyChangeAllianceEvent(player, mmoPlayer.getParty().getName(),
                invite.getName(),
                McMMOPartyAllianceChangeEvent.EventReason.FORMED_ALLIANCE)) {
            return;
        }

        player.sendMessage(LocaleLoader.getString("Commands.Party.Alliance.Invite.Accepted",
                invite.getName()));
        mmoPlayer.removePartyAllianceInvite();

        createAlliance(mmoPlayer.getParty(), invite);
    }

    public void createAlliance(@NotNull Party firstParty, @NotNull Party secondParty) {
        requireNonNull(firstParty, "firstParty cannot be null!");
        requireNonNull(secondParty, "secondParty cannot be null!");

        firstParty.setAlly(secondParty);
        secondParty.setAlly(firstParty);

        for (Player member : firstParty.getOnlineMembers()) {
            member.sendMessage(
                    LocaleLoader.getString("Party.Alliance.Formed", secondParty.getName()));
        }

        for (Player member : secondParty.getOnlineMembers()) {
            member.sendMessage(
                    LocaleLoader.getString("Party.Alliance.Formed", firstParty.getName()));
        }
    }

    public boolean disbandAlliance(@NotNull Player player, @NotNull Party firstParty,
            @NotNull Party secondParty) {
        requireNonNull(player, "player cannot be null!");
        requireNonNull(firstParty, "firstParty cannot be null!");
        requireNonNull(secondParty, "secondParty cannot be null!");

        if (!handlePartyChangeAllianceEvent(player, firstParty.getName(), secondParty.getName(),
                McMMOPartyAllianceChangeEvent.EventReason.DISBAND_ALLIANCE)) {
            return false;
        }

        disbandAlliance(firstParty, secondParty);
        return true;
    }

    private void disbandAlliance(@NotNull Party firstParty, @NotNull Party secondParty) {
        requireNonNull(firstParty, "firstParty cannot be null!");
        requireNonNull(secondParty, "secondParty cannot be null!");
        firstParty.setAlly(null);
        secondParty.setAlly(null);

        for (Player member : firstParty.getOnlineMembers()) {
            member.sendMessage(
                    LocaleLoader.getString("Party.Alliance.Disband", secondParty.getName()));
        }

        for (Player member : secondParty.getOnlineMembers()) {
            member.sendMessage(
                    LocaleLoader.getString("Party.Alliance.Disband", firstParty.getName()));
        }
    }

    /**
     * Add a player to a party
     *
     * @param mmoPlayer The player to add to the party
     * @param party The party
     */
    public void addToParty(@NotNull McMMOPlayer mmoPlayer, @NotNull Party party) {
        requireNonNull(mmoPlayer, "mmoPlayer cannot be null!");
        requireNonNull(party, "party cannot be null!");

        Player player = mmoPlayer.getPlayer();
        String playerName = player.getName();

        informPartyMembersJoin(party, playerName);
        mmoPlayer.setParty(party);
        party.getMembers().put(player.getUniqueId(), player.getName());
        party.getOnlineMembers().add(player);
    }

    /**
     * Get the leader of a party.
     *
     * @param partyName The party name
     * @return the leader of the party
     */
    public @Nullable String getPartyLeaderName(@NotNull String partyName) {
        requireNonNull(partyName, "partyName cannot be null!");
        Party party = getParty(partyName);

        return party == null ? null : party.getLeader().getPlayerName();
    }

    /**
     * Set the leader of a party.
     *
     * @param uuid The uuid of the player to set as leader
     * @param party The party
     */
    public void setPartyLeader(@NotNull UUID uuid, @NotNull Party party) {
        requireNonNull(uuid, "uuid cannot be null!");
        requireNonNull(party, "party cannot be null!");
        OfflinePlayer player = pluginRef.getServer().getOfflinePlayer(uuid);
        UUID leaderUniqueId = party.getLeader().getUniqueId();

        for (Player member : party.getOnlineMembers()) {
            UUID memberUniqueId = member.getUniqueId();

            if (memberUniqueId.equals(player.getUniqueId())) {
                member.sendMessage(LocaleLoader.getString("Party.Owner.Player"));
            } else if (memberUniqueId.equals(leaderUniqueId)) {
                member.sendMessage(LocaleLoader.getString("Party.Owner.NotLeader"));
            } else {
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
    public boolean canInvite(@NotNull McMMOPlayer mmoPlayer) {
        requireNonNull(mmoPlayer, "mmoPlayer cannot be null!");
        Party party = mmoPlayer.getParty();

        return !party.isLocked() || party.getLeader().getUniqueId()
                .equals(mmoPlayer.getPlayer().getUniqueId());
    }

    /**
     * Check if a party with a given name already exists.
     *
     * @param player The player to notify
     * @param partyName The name of the party to check
     * @return true if a party with that name exists, false otherwise
     */
    public boolean checkPartyExistence(@NotNull Player player, @NotNull String partyName) {
        requireNonNull(player, "player cannot be null!");
        requireNonNull(partyName, "partyName cannot be null!");

        if (getParty(partyName) == null) {
            return false;
        }

        player.sendMessage(LocaleLoader.getString("Commands.Party.AlreadyExists", partyName));
        return true;
    }

    /**
     * Attempt to change parties or join a new party.
     *
     * @param mmoPlayer The player changing or joining parties
     * @param newPartyName The name of the party being joined
     * @return true if the party was joined successfully, false otherwise
     */
    public boolean changeOrJoinParty(@NotNull McMMOPlayer mmoPlayer, @NotNull String newPartyName) {
        requireNonNull(mmoPlayer, "mmoPlayer cannot be null!");
        requireNonNull(newPartyName, "newPartyName cannot be null!");

        final Player player = mmoPlayer.getPlayer();

        if (mmoPlayer.inParty()) {
            final Party oldParty = mmoPlayer.getParty();

            if (!handlePartyChangeEvent(player, oldParty.getName(), newPartyName,
                    EventReason.CHANGED_PARTIES)) {
                return false;
            }

            removeFromParty(mmoPlayer);
        } else {
            return handlePartyChangeEvent(player, null, newPartyName, EventReason.JOINED_PARTY);
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
    public boolean inSameParty(@NotNull Player firstPlayer, @NotNull Player secondPlayer) {
        requireNonNull(firstPlayer, "firstPlayer cannot be null!");
        requireNonNull(secondPlayer, "secondPlayer cannot be null!");

        //Profile not loaded
        if (UserManager.getPlayer(firstPlayer) == null) {
            return false;
        }

        //Profile not loaded
        if (UserManager.getPlayer(secondPlayer) == null) {
            return false;
        }

        Party firstParty = UserManager.getPlayer(firstPlayer).getParty();
        Party secondParty = UserManager.getPlayer(secondPlayer).getParty();

        if (firstParty == null || secondParty == null) {
            return false;
        }

        return firstParty.equals(secondParty);
    }

    /**
     * Load party file.
     */
    public void loadParties() {
        if (!pluginRef.getPartyConfig().isPartyEnabled() || !partyFile.exists()) {
            return;
        }

        try {
            YamlConfiguration partiesFile;
            partiesFile = YamlConfiguration.loadConfiguration(partyFile);

            ArrayList<Party> hasAlly = new ArrayList<>();

            for (String partyName : partiesFile.getConfigurationSection("").getKeys(false)) {
                try {
                    Party party = new Party(partyName);

                    String[] leaderSplit = partiesFile.getString(partyName + ".Leader")
                            .split("[|]");
                    party.setLeader(
                            new PartyLeader(UUID.fromString(leaderSplit[0]), leaderSplit[1]));
                    party.setPassword(partiesFile.getString(partyName + ".Password"));
                    party.setLocked(partiesFile.getBoolean(partyName + ".Locked"));
                    party.setLevel(partiesFile.getInt(partyName + ".Level"));
                    party.setXp(partiesFile.getInt(partyName + ".Xp"));

                    if (partiesFile.getString(partyName + ".Ally") != null) {
                        hasAlly.add(party);
                    }

                    party.setXpShareMode(
                            ShareMode.getShareMode(
                                    partiesFile.getString(partyName + ".ExpShareMode", "NONE")));
                    party.setItemShareMode(
                            ShareMode.getShareMode(
                                    partiesFile.getString(partyName + ".ItemShareMode", "NONE")));

                    for (ItemShareType itemShareType : ItemShareType.values()) {
                        party.setSharingDrops(itemShareType,
                                partiesFile.getBoolean(
                                        partyName + ".ItemShareType." + itemShareType,
                                        true));
                    }

                    LinkedHashMap<UUID, String> members = party.getMembers();

                    for (String memberEntry : partiesFile.getStringList(partyName + ".Members")) {
                        String[] memberSplit = memberEntry.split("[|]");
                        members.put(UUID.fromString(memberSplit[0]), memberSplit[1]);
                    }

                    parties.add(party);
                } catch (Exception e) {
                    pluginRef.getLogger().log(Level.WARNING,
                            "An exception occurred while loading a party with name '" + partyName
                                    + "'. Skipped loading party.",
                            e);
                }
            }

            LogUtils.debug(pluginRef.getLogger(), "Loaded (" + parties.size() + ") Parties...");

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
        LogUtils.debug(pluginRef.getLogger(), "[Party Data] Saving...");

        if (partyFile.exists()) {
            if (!partyFile.delete()) {
                pluginRef.getLogger().warning("Could not delete party file. Party saving failed!");
                return;
            }
        }

        YamlConfiguration partiesFile = new YamlConfiguration();

        for (Party party : parties) {
            String partyName = party.getName();
            PartyLeader leader = party.getLeader();

            partiesFile.set(partyName + ".Leader",
                    leader.getUniqueId().toString() + "|" + leader.getPlayerName());
            partiesFile.set(partyName + ".Password", party.getPassword());
            partiesFile.set(partyName + ".Locked", party.isLocked());
            partiesFile.set(partyName + ".Level", party.getLevel());
            partiesFile.set(partyName + ".Xp", (int) party.getXp());
            partiesFile.set(partyName + ".Ally",
                    (party.getAlly() != null) ? party.getAlly().getName() : "");
            partiesFile.set(partyName + ".ExpShareMode", party.getXpShareMode().toString());
            partiesFile.set(partyName + ".ItemShareMode", party.getItemShareMode().toString());

            for (ItemShareType itemShareType : ItemShareType.values()) {
                partiesFile.set(partyName + ".ItemShareType." + itemShareType.toString(),
                        party.sharingDrops(itemShareType));
            }

            List<String> members = new ArrayList<>();

            for (Entry<UUID, String> memberEntry : party.getMembers().entrySet()) {
                String memberUniqueId =
                        memberEntry.getKey() == null ? "" : memberEntry.getKey().toString();
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

    /**
     * Handle party change event.
     *
     * @param player The player changing parties
     * @param oldPartyName The name of the old party
     * @param newPartyName The name of the new party
     * @param reason The reason for changing parties
     * @return true if the change event was successful, false otherwise
     */
    public boolean handlePartyChangeEvent(Player player, String oldPartyName, String newPartyName,
            EventReason reason) {
        McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, oldPartyName, newPartyName,
                reason);
        pluginRef.getServer().getPluginManager().callEvent(event);

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
    public boolean handlePartyChangeAllianceEvent(Player player, String oldAllyName,
            String newAllyName,
            McMMOPartyAllianceChangeEvent.EventReason reason) {
        McMMOPartyAllianceChangeEvent event = new McMMOPartyAllianceChangeEvent(player, oldAllyName,
                newAllyName,
                reason);
        pluginRef.getServer().getPluginManager().callEvent(event);

        return !event.isCancelled();
    }

    /**
     * Remove party data from the mmoPlayer.
     *
     * @param mmoPlayer The player to remove party data from.
     */
    public void processPartyLeaving(@NotNull McMMOPlayer mmoPlayer) {
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
        boolean levelUpSoundsEnabled = pluginRef.getGeneralConfig().getLevelUpSoundsEnabled();
        for (Player member : party.getOnlineMembers()) {
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
    private void informPartyMembersQuit(Party party, String playerName) {
        for (Player member : party.getOnlineMembers()) {
            member.sendMessage(LocaleLoader.getString("Party.InformedOnQuit", playerName));
        }
    }
}
