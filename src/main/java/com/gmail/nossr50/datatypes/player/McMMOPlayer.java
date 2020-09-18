package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.config.WorldBlacklist;
import com.gmail.nossr50.datatypes.chat.ChatMode;
import com.gmail.nossr50.datatypes.party.PartyTeleportRecord;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.acrobatics.AcrobaticsManager;
import com.gmail.nossr50.skills.alchemy.AlchemyManager;
import com.gmail.nossr50.skills.archery.ArcheryManager;
import com.gmail.nossr50.skills.axes.AxesManager;
import com.gmail.nossr50.skills.crossbows.CrossbowManager;
import com.gmail.nossr50.skills.excavation.ExcavationManager;
import com.gmail.nossr50.skills.fishing.FishingManager;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.repair.RepairManager;
import com.gmail.nossr50.skills.salvage.SalvageManager;
import com.gmail.nossr50.skills.smelting.SmeltingManager;
import com.gmail.nossr50.skills.swords.SwordsManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.tridents.TridentManager;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;
import com.gmail.nossr50.skills.woodcutting.WoodcuttingManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.experience.MMOExperienceBarManager;
import com.gmail.nossr50.util.input.AbilityActivationProcessor;
import com.gmail.nossr50.util.input.SuperAbilityManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class McMMOPlayer extends PlayerProfile {
    private final @NotNull Player player;

    private final @NotNull Map<PrimarySkillType, SkillManager> skillManagers = new HashMap<>();
    private final @NotNull MMOExperienceBarManager experienceBarManager;

    private @Nullable PartyTeleportRecord ptpRecord;

    /* Special Flags */
    private boolean debugMode = false;
    private boolean partyChatMode;
    private boolean adminChatMode;
    private boolean godMode;
    private boolean displaySkillNotifications = true;

    private int recentlyHurt;
    private int respawnATS;
    private int teleportATS;
    private long databaseATS;

    private @Nullable Location teleportCommence;

    private final @NotNull FixedMetadataValue playerMetadata;
    private final @NotNull SuperAbilityManager superAbilityManager;
    private final @NotNull AbilityActivationProcessor abilityActivationProcessor;

    /**
     * Create a new {@link McMMOPlayer} with default values for a {@link Player}
     * @param player target player
     */
    public McMMOPlayer(@NotNull Player player) {
        /*
         * New
         * Player
         */
        super(player);

        this.player = player;
        playerMetadata = new FixedMetadataValue(mcMMO.p, player.getName());
        experienceBarManager = new MMOExperienceBarManager(this, getPersistentPlayerData().getDirtyBarStateMap());

        superAbilityManager = new SuperAbilityManager(this);
        abilityActivationProcessor = new AbilityActivationProcessor(this);

        //Update last login
        updateLastLogin();
    }

    /**
     * Create a new instance based on existing player data
     * @param player target player
     * @param persistentPlayerData existing player data
     */
    public McMMOPlayer(@NotNull Player player, @NotNull PersistentPlayerData persistentPlayerData) {
        /*
         * Existing
         * Player
         */
        super(persistentPlayerData);

        this.player = player;
        playerMetadata = new FixedMetadataValue(mcMMO.p, player.getName());

        /*
         * I'm using this method because it makes code shorter and safer (we don't have to add all SkillTypes manually),
         * but I actually have no idea about the performance impact, if there is any.
         * If in the future someone wants to remove this, don't forget to also remove what is in the PrimarySkillType enum. - bm01
         */
        try {
            for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
                skillManagers.put(primarySkillType, primarySkillType.getManagerClass().getConstructor(McMMOPlayer.class).newInstance(this));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            mcMMO.p.getPluginLoader().disablePlugin(mcMMO.p);
        }

        superAbilityManager = new SuperAbilityManager(this);
        abilityActivationProcessor = new AbilityActivationProcessor(this);
        experienceBarManager = new MMOExperienceBarManager(this, persistentPlayerData.getBarStateMap());

        //Update last login
        updateLastLogin();
    }

    /**
     * Update the last login to the current system time
     */
    private void updateLastLogin() {
        getPersistentPlayerData().setLastLogin(System.currentTimeMillis());
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
     * Grab the {@link TridentManager} for this player
     * @return this player's trident manager
     */
    public @NotNull TridentManager getTridentManager() {
        return (TridentManager) skillManagers.get(PrimarySkillType.TRIDENTS);
    }

    /**
     * Grab the {@link CrossbowManager} for this player
     * @return this player's crossbow manager
     */
    public @NotNull CrossbowManager getCrossbowManager() {
        return (CrossbowManager) skillManagers.get(PrimarySkillType.CROSSBOWS);
    }

    /**
     * Grab the {@link WoodcuttingManager} for this player
     * @return this player's woodcutting manager
     */
    public @NotNull WoodcuttingManager getWoodcuttingManager() {
        return (WoodcuttingManager) skillManagers.get(PrimarySkillType.WOODCUTTING);
    }

    /*
     * Recently Hurt
     */

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

    /*
     * Exploit Prevention
     */

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

    /*
     * God Mode
     */

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

    /*
     * Debug Mode Flags
     */

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

    /*
     * Skill notifications
     */

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

    /*
     * Chat modes
     */

    public boolean isChatEnabled(ChatMode mode) {
        switch (mode) {
            case ADMIN:
                return adminChatMode;

            case PARTY:
                return partyChatMode;

            default:
                return false;
        }
    }

    public void disableChat(ChatMode mode) {
        switch (mode) {
            case ADMIN:
                adminChatMode = false;
                return;

            case PARTY:
                partyChatMode = false;
                return;

            default:
        }
    }

    public void enableChat(ChatMode mode) {
        switch (mode) {
            case ADMIN:
                adminChatMode = true;
                partyChatMode = false;
                return;

            case PARTY:
                partyChatMode = true;
                adminChatMode = false;
                return;

            default:
        }

    }

    public void toggleChat(ChatMode mode) {
        switch (mode) {
            case ADMIN:
                adminChatMode = !adminChatMode;
                partyChatMode = !adminChatMode && partyChatMode;
                return;

            case PARTY:
                partyChatMode = !partyChatMode;
                adminChatMode = !partyChatMode && adminChatMode;
                return;

            default:
        }
    }

    /**
     * Update the experience bars for this player
     * @param primarySkillType target skill
     * @param plugin your {@link Plugin}
     */
    public void updateXPBar(PrimarySkillType primarySkillType, Plugin plugin)
    {
        //XP BAR UPDATES
        experienceBarManager.updateExperienceBar(primarySkillType, plugin);
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
     * Calculate the time remaining until the superAbilityType's cooldown expires.
     *
     * @param superAbilityType the super ability cooldown to check
     *
     * @return the number of seconds remaining before the cooldown expires
     */
    public int calculateTimeRemaining(SuperAbilityType superAbilityType) {
        return superAbilityManager.calculateTimeRemaining(superAbilityType);
    }

    /**
     * This is sort of a hack, used for thread safety
     * @return this player's {@link FixedMetadataValue}
     */
    public @NotNull FixedMetadataValue getPlayerMetadata() {
        return playerMetadata;
    }

    /**
     * Grab this players {@link SuperAbilityManager}
     * @return this player's super ability manager
     */
    public @NotNull SuperAbilityManager getSuperAbilityManager() {
        return superAbilityManager;
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
        superAbilityManager.resetSuperAbilities();
        getTamingManager().cleanupAllSummons();
    }

    /**
     * Whether or not this player is currently spying on all party chat
     * @return true if this player is currently spying
     */
    public boolean isPartyChatSpying() {
        return getPersistentPlayerData().isPartyChatSpying();
    }

    /**
     * Toggle this player's party chat spying flag
     */
    public void togglePartyChatSpying() {
        getPersistentPlayerData().togglePartyChatSpying();
    }

    //TODO: Rewrite this
    public double getAttackStrength() {
        return 1.0F;
    }
}
