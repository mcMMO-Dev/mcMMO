package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.chat.author.PlayerAuthor;
import com.gmail.nossr50.config.ChatConfig;
import com.gmail.nossr50.config.WorldBlacklist;
import com.gmail.nossr50.datatypes.chat.ChatChannel;
import com.gmail.nossr50.datatypes.skills.CoreRootSkill;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.party.PartyTeleportRecord;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.acrobatics.AcrobaticsManager;
import com.gmail.nossr50.skills.alchemy.AlchemyManager;
import com.gmail.nossr50.skills.archery.ArcheryManager;
import com.gmail.nossr50.skills.axes.AxesManager;
import com.gmail.nossr50.skills.crossbows.CrossbowsManager;
import com.gmail.nossr50.skills.excavation.ExcavationManager;
import com.gmail.nossr50.skills.fishing.FishingManager;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.repair.RepairManager;
import com.gmail.nossr50.skills.salvage.SalvageManager;
import com.gmail.nossr50.skills.smelting.SmeltingManager;
import com.gmail.nossr50.skills.swords.SwordsManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.tridents.TridentsManager;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;
import com.gmail.nossr50.skills.woodcutting.WoodcuttingManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.experience.MMOExperienceBarManager;
import com.gmail.nossr50.util.input.AbilityActivationProcessor;
import com.gmail.nossr50.util.input.SuperSkillManagerImpl;
import com.neetgames.mcmmo.party.Party;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//TODO: Update javadocs
//TODO: T&C Look over IDE inspection warnings, things have changed
public class McMMOPlayer extends PlayerProfile implements OnlineMMOPlayer, Identified {
    private final @NotNull Player player;
    private final @NotNull Identity identity;
    private @Nullable Party playerPartyRef;

    //Used in our chat systems for chat messages
    private final @NotNull PlayerAuthor playerAuthor;
    private final @NotNull Map<PrimarySkillType, SkillManager> skillManagers = new HashMap<>();
    private final @NotNull MMOExperienceBarManager experienceBarManager;

    private @Nullable PartyTeleportRecord ptpRecord;

    /* Special Flags */
    private boolean debugMode = false;
    private boolean partyChatMode;
    private boolean adminChatMode;
    private boolean godMode;
    private boolean displaySkillNotifications = true;

    private ChatChannel chatChannel;
    private boolean chatSpy = false; //Off by default

    private int recentlyHurt;
    private int respawnATS;
    private int teleportATS;
    private long databaseATS;

    private @Nullable Location teleportCommence;

    private final @NotNull FixedMetadataValue playerMetadata;
    private final @NotNull SuperSkillManagerImpl superSkillManagerImpl;
    private final @NotNull AbilityActivationProcessor abilityActivationProcessor;

    /**
     * Create a new {@link OnlineMMOPlayer} with default values for a {@link Player}
     * @param player target player
     */
    public McMMOPlayer(@NotNull Player player) {
        /*
         * New
         * Player
         */
        super(new PlayerData(player.getUniqueId(), player.getName()));

        UUID uuid = player.getUniqueId();
        identity = Identity.identity(uuid);

        this.player = player;
        playerMetadata = new FixedMetadataValue(mcMMO.p, player.getName());
        experienceBarManager = new MMOExperienceBarManager(this, playerData.getBarStateMap());

        superSkillManagerImpl = new SuperSkillManagerImpl(this, playerData);
        abilityActivationProcessor = new AbilityActivationProcessor(this);

        debugMode = false; //Debug mode helps solve support issues, players can toggle it on or off

        this.playerAuthor = new PlayerAuthor(player);

        this.chatChannel = ChatChannel.NONE;

        if(ChatConfig.getInstance().isSpyingAutomatic() && Permissions.adminChatSpy(getPlayer())) {
            chatSpy = true;
        }

        assignParty();

        //Update last login
        updateLastLogin();
    }

    /**
     * Create a new instance based on existing player data
     * @param player target player
     * @param mmoPlayerData existing player data
     */
    public McMMOPlayer(@NotNull Player player, @NotNull PlayerData mmoPlayerData) {
        /*
         * Existing
         * Player
         */
        super(mmoPlayerData);
        this.player = player;
        identity = Identity.identity(player.getUniqueId());
        playerMetadata = new FixedMetadataValue(mcMMO.p, player.getName());

        /*
         * I'm using this method because it makes code shorter and safer (we don't have to add all SkillTypes manually),
         * but I actually have no idea about the performance impact, if there is any.
         * If in the future someone wants to remove this, don't forget to also remove what is in the PrimarySkillType enum. - bm01
         */
        try {
            for (PrimarySkillType primarySkillType : PrimarySkillType.getCoreRootSkills()) {
                skillManagers.put(primarySkillType, coreRootSkill.getSkillManagerClass().getConstructor(McMMOPlayer.class).newInstance(this));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            mcMMO.p.getPluginLoader().disablePlugin(mcMMO.p);
        }

        superSkillManagerImpl = new SuperSkillManagerImpl(this, mmoPlayerData);
        abilityActivationProcessor = new AbilityActivationProcessor(this);
        experienceBarManager = new MMOExperienceBarManager(this, this.playerData.getBarStateMap());

        debugMode = false; //Debug mode helps solve support issues, players can toggle it on or off

        this.playerAuthor = new PlayerAuthor(player);

        this.chatChannel = ChatChannel.NONE;

        if(ChatConfig.getInstance().isSpyingAutomatic() && Permissions.adminChatSpy(getPlayer())) {
            chatSpy = true;
        }

        assignParty();

        //Update last login
        updateLastLogin();
    }

    private void assignParty() {
        if(mcMMO.getPartyManager() != null) {
            Party queryParty = mcMMO.getPartyManager().queryParty(player.getUniqueId());

            if(queryParty != null)
                this.playerPartyRef = queryParty;
        }
    }

    /**
     * Update the last login to the current system time
     */
    private void updateLastLogin() {
        playerData.setLastLogin(System.currentTimeMillis());
    }

    public @NotNull String getPlayerName() {
        return player.getName();
    }

    /**
     * Grab the {@link MMOExperienceBarManager} for this player
     * @return this player's experience bar manager
     */
    public @NotNull MMOExperienceBarManager getExperienceBarManager() {
        return experienceBarManager;
    }

    /**
     * Grab the {@link AcrobaticsManager} for this player
     * @return this player's acrobatics manager
     */
    public @NotNull AcrobaticsManager getAcrobaticsManager() {
        return (AcrobaticsManager) skillManagers.get(PrimarySkillType.ACROBATICS);
    }

    /**
     * Grab the {@link AlchemyManager} for this player
     * @return this player's alchemy manager
     */
    public @NotNull AlchemyManager getAlchemyManager() {
        return (AlchemyManager) skillManagers.get(PrimarySkillType.ALCHEMY);
    }

    /**
     * Grab the {@link ArcheryManager} for this player
     * @return this player's archery manager
     */
    public @NotNull ArcheryManager getArcheryManager() {
        return (ArcheryManager) skillManagers.get(PrimarySkillType.ARCHERY);
    }

    /**
     * Grab the {@link AxesManager} for this player
     * @return this player's axes manager
     */
    public @NotNull AxesManager getAxesManager() {
        return (AxesManager) skillManagers.get(PrimarySkillType.AXES);
    }

    /**
     * Grab the {@link ExcavationManager} for this player
     * @return this player's excavation manager
     */
    public @NotNull ExcavationManager getExcavationManager() {
        return (ExcavationManager) skillManagers.get(PrimarySkillType.EXCAVATION);
    }

    /**
     * Grab the {@link FishingManager} for this player
     * @return this player's fishing manager
     */
    public @NotNull FishingManager getFishingManager() {
        return (FishingManager) skillManagers.get(PrimarySkillType.FISHING);
    }

    /**
     * Grab the {@link HerbalismManager} for this player
     * @return this player's herbalism manager
     */
    public @NotNull HerbalismManager getHerbalismManager() {
        return (HerbalismManager) skillManagers.get(PrimarySkillType.HERBALISM);
    }

    /**
     * Grab the {@link MiningManager} for this player
     * @return this player's mining manager
     */
    public @NotNull MiningManager getMiningManager() {
        return (MiningManager) skillManagers.get(PrimarySkillType.MINING);
    }

    /**
     * Grab the {@link RepairManager} for this player
     * @return this player's repair manager
     */
    public @NotNull RepairManager getRepairManager() {
        return (RepairManager) skillManagers.get(PrimarySkillType.REPAIR);
    }

    /**
     * Grab the {@link SalvageManager} for this player
     * @return this player's salvage manager
     */
    public @NotNull SalvageManager getSalvageManager() {
        return (SalvageManager) skillManagers.get(PrimarySkillType.SALVAGE);
    }

    /**
     * Grab the {@link SmeltingManager} for this player
     * @return this player's smelting manager
     */
    public @NotNull SmeltingManager getSmeltingManager() {
        return (SmeltingManager) skillManagers.get(PrimarySkillType.SMELTING);
    }

    /**
     * Grab the {@link SwordsManager} for this player
     * @return this player's swords manager
     */
    public @NotNull SwordsManager getSwordsManager() {
        return (SwordsManager) skillManagers.get(PrimarySkillType.SWORDS);
    }

    /**
     * Grab the {@link TamingManager} for this player
     * @return this player's taming manager
     */
    public @NotNull TamingManager getTamingManager() {
        return (TamingManager) skillManagers.get(PrimarySkillType.TAMING);
    }

    /**
     * Grab the {@link UnarmedManager} for this player
     * @return this player's unarmed manager
     */
    public @NotNull UnarmedManager getUnarmedManager() {
        return (UnarmedManager) skillManagers.get(PrimarySkillType.UNARMED);
    }

    /**
     * Grab the {@link TridentsManager} for this player
     * @return this player's trident manager
     */
    public @NotNull TridentsManager getTridentManager() {
        return (TridentsManager) skillManagers.get(PrimarySkillType.TRIDENTS);
    }

    /**
     * Grab the {@link CrossbowsManager} for this player
     * @return this player's crossbow manager
     */
    public @NotNull CrossbowsManager getCrossbowManager() {
        return (CrossbowsManager) skillManagers.get(PrimarySkillType.CROSSBOWS);
    }

    /**
     * Grab the {@link WoodcuttingManager} for this player
     * @return this player's woodcutting manager
     */
    public @NotNull WoodcuttingManager getWoodcuttingManager() {
        return (WoodcuttingManager) skillManagers.get(PrimarySkillType.WOODCUTTING);
    }

    /**
     * The timestamp of the last time this player was hurt
     * @return the timestamp of the most recent player damage
     */
    public int getRecentlyHurtTimestamp() {
        return recentlyHurt;
    }

    /**
     * Set when the player was last hurt
     * @param value new timestamp
     */
    public void setRecentlyHurtTimestamp(int value) {
        recentlyHurt = value;
    }

    /**
     * Update recently hurt timestamp to reflect the current system time
     */
    public void actualizeRecentlyHurtTimestamp() {
        recentlyHurt = (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
    }

    /**
     * Get the activation time stamp for this player's last respawn
     * @return the time stamp of this player's last respawn
     */
    public int getRespawnATS() {
        return respawnATS;
    }

    /**
     * Set the respawn timestamp for this player to the current time
     */
    public void actualizeRespawnATS() {
        respawnATS = (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
    }

    /**
     * Get the activation time stamp for this player's last teleport
     * @return the time stamp of this player's last teleport
     */
    public int getTeleportATS() {
        return teleportATS;
    }

    /**
     * Set the timestamp of the most recent teleport to the current system time
     */
    public void actualizeTeleportATS() {
        teleportATS = (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
    }

    /**
     * Get the time stamp of when this player last used an expensive database command
     * @return the time stamp of when this player last used an expensive database command
     */
    public long getDatabaseCommandATS() {
        return databaseATS;
    }

    /**
     * Set the time stamp of when the player last used an expensive database command to the current system time
     */
    public void actualizeDatabaseCommandATS() {
        databaseATS = System.currentTimeMillis();
    }

    /**
     * Whether or not this player is in god mode
     * @return true if this player is in god mode
     */
    public boolean getGodMode() {
        return godMode;
    }

    /**
     * Toggle the god mode setting for this player
     */
    public void toggleGodMode() {
        godMode = !godMode;
    }

    /**
     * Whether or not this player is using debug mode
     * @return true if this player is in debug mode
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Toggle the debug mode for this player
     */
    public void toggleDebugMode() {
        debugMode = !debugMode;
    }

    /**
     * Whether or not this player receives specific skill notifications in chat
     * @return true if the player receives specific chat notifications related to skills
     */
    public boolean hasSkillChatNotifications() {
        return displaySkillNotifications;
    }

    /**
     * Toggles whether or not this player receives specific skill notifications in chat
     */
    public void toggleSkillChatNotifications() {
        displaySkillNotifications = !displaySkillNotifications;
    }

    /*
     * Players & Profiles
     */

    /**
     * Retrieves the associated {@link Player} object for this player
     * @return the {@link Player} object associated with this player
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * Checks whether or not a player can still be in god mode when transitioning between worlds
     */
    public void checkGodMode() {
        if (godMode && !Permissions.mcgod(player)
            || godMode && WorldBlacklist.isWorldBlacklisted(player.getWorld())) {
            toggleGodMode();
            player.sendMessage(LocaleLoader.getString("Commands.GodMode.Forbidden"));
        }
    }

    /**
     * This is sort of a hack, used for thread safety
     * @return this player's {@link FixedMetadataValue}
     */
    public @NotNull FixedMetadataValue getPlayerMetadata() {
        return playerMetadata;
    }

    /**
     * Grab this player's {@link AbilityActivationProcessor}
     * Used to process all things related to manually activated abilities
     * @return this player's ability activation processor
     */
    public @NotNull AbilityActivationProcessor getAbilityActivationProcessor() {
        return abilityActivationProcessor;
    }

    /**
     * Cleanup various things related to this player
     * Such as temporary summons..
     * Turning off abilities...
     * Etc...
     */
    public void cleanup() {
        superSkillManagerImpl.disableSuperAbilities();
        getTamingManager().cleanupAllSummons();
    }

    /**
     * Whether or not this player is currently spying on all party chat
     * @return true if this player is currently spying
     */
    @Override
    public boolean isChatSpying() {
        return playerData.isPartyChatSpying();
    }

    /**
     * Toggle this player's party chat spying flag
     */
    public void togglePartyChatSpying() {
        playerData.togglePartyChatSpying();
    }

    /**
     * For use with Adventure API (Kyori lib)
     * @return this players identity
     */
    @Override
    public @NotNull Identity identity() {
        return identity;
    }

    /**
     * The {@link com.gmail.nossr50.chat.author.Author} for this player, used by mcMMO chat
     * @return the {@link com.gmail.nossr50.chat.author.Author} for this player
     */
    public @NotNull PlayerAuthor getPlayerAuthor() {
        return playerAuthor;
    }

    public @NotNull ChatChannel getChatChannel() {
        return chatChannel;
    }

    /**
     * Change the chat channel for a player
     * This does not inform the player
     * @param chatChannel new chat channel
     */
    public void setChatMode(@NotNull ChatChannel chatChannel) {
        this.chatChannel = chatChannel;
    }

    /**
     * Gets the {@link Party} for the player if it exists
     * @return the player's party or null if one doesn't exist
     */
    public @Nullable Party getPlayerPartyRef() {
        return playerPartyRef;
    }

    @Override
    public boolean inParty() {
        return playerPartyRef == null;
    }

    @Override
    public boolean isGodMode() {
        return godMode;
    }

    @Nullable
    public Location getTeleportCommenceLocation() {
        return teleportCommence;
    }

    public void setTeleportCommenceLocation(@Nullable Location location) {
        teleportCommence = location;
    }

    public void actualizeTeleportCommenceLocation() {
        teleportCommence = getPlayer().getLocation();
    }

    @Override
    public void sendMessage(@NotNull String s) {
        player.sendMessage(s);
    }

    @Override
    public @NotNull Object getServerAPIPlayerImpl() {
        return player;
    }

    @Override
    public void updateXPBar(@NotNull PrimarySkillType primarySkillType) {
        experienceBarManager.updateExperienceBar(primarySkillType, mcMMO.p);
    }

    @Override
    public void validateGodMode() {
        if(!Permissions.mcgod(player)) {
            godMode = false;
        }
    }

    @Override
    public @Nullable Party getParty() {
        return playerPartyRef;
    }

    public @NotNull SuperSkillManagerImpl getSuperSkillManager() {
        return superSkillManagerImpl;
    }

}
