package com.gmail.nossr50.datatypes.player;

import static com.gmail.nossr50.util.EventUtils.callPlayerAbilityActivateEvent;
import static java.util.Objects.requireNonNull;

import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.chat.author.PlayerAuthor;
import com.gmail.nossr50.config.ChatConfig;
import com.gmail.nossr50.config.WorldBlacklist;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.chat.ChatChannel;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.meta.RuptureTaskMeta;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.PartyTeleportRecord;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.events.experience.McMMOPlayerPreXpGainEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.party.ShareHandler;
import com.gmail.nossr50.runnables.skills.AbilityDisableTask;
import com.gmail.nossr50.runnables.skills.RuptureTask;
import com.gmail.nossr50.runnables.skills.ToolLowerTask;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.acrobatics.AcrobaticsManager;
import com.gmail.nossr50.skills.alchemy.AlchemyManager;
import com.gmail.nossr50.skills.archery.ArcheryManager;
import com.gmail.nossr50.skills.axes.AxesManager;
import com.gmail.nossr50.skills.crossbows.CrossbowsManager;
import com.gmail.nossr50.skills.excavation.ExcavationManager;
import com.gmail.nossr50.skills.fishing.FishingManager;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.maces.MacesManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.repair.RepairManager;
import com.gmail.nossr50.skills.salvage.SalvageManager;
import com.gmail.nossr50.skills.smelting.SmeltingManager;
import com.gmail.nossr50.skills.spears.SpearsManager;
import com.gmail.nossr50.skills.swords.SwordsManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.tridents.TridentsManager;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;
import com.gmail.nossr50.skills.woodcutting.WoodcuttingManager;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.experience.ExperienceBarManager;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.gmail.nossr50.util.skills.PerksUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillTools;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

public class McMMOPlayer implements Identified {
    private static final long NO_SWING = 0L;
    private final @NotNull Identity identity;

    //Hacky fix for now, redesign later
    private final @NotNull PlayerAuthor playerAuthor;

    private final Player player;
    private final PlayerProfile profile;

    private final Map<PrimarySkillType, SkillManager> skillManagers = new EnumMap<>(
            PrimarySkillType.class);
    private final ExperienceBarManager experienceBarManager;

    private Party party;
    private Party invite;
    private Party allianceInvite;
    private int itemShareModifier;
    private long lastSwingTimestamp = NO_SWING;

    private PartyTeleportRecord ptpRecord;

    private boolean displaySkillNotifications = true;
    private boolean debugMode;

    private boolean abilityUse = true;
    private boolean godMode;
    private boolean chatSpy = false; //Off by default

    private ChatChannel chatChannel;

    private final Map<SuperAbilityType, Boolean> abilityMode = new EnumMap<>(
            SuperAbilityType.class);
    private final Map<SuperAbilityType, Boolean> abilityInformed = new EnumMap<>(
            SuperAbilityType.class);

    private final Map<ToolType, Boolean> toolMode = new EnumMap<>(ToolType.class);

    private int recentlyHurt;
    private int respawnATS;
    private int teleportATS;
    private long databaseATS;
    //private int chimeraWingLastUse;
    private Location teleportCommence;

    private boolean isUsingUnarmed;
    private final FixedMetadataValue playerMetadata;
    private final String playerName;

    private PrimarySkillType lastSkillShownScoreboard = PrimarySkillType.values()[0];

    public McMMOPlayer(Player player, PlayerProfile profile) {
        requireNonNull(player, "player cannot be null");
        requireNonNull(profile, "profile cannot be null");
        this.playerName = player.getName();
        UUID uuid = player.getUniqueId();
        identity = Identity.identity(uuid);

        this.player = player;
        playerMetadata = new FixedMetadataValue(mcMMO.p, playerName);
        this.profile = profile;

        if (profile.getUniqueId() == null) {
            profile.setUniqueId(uuid);
        }

        initSkillManagers();

        for (SuperAbilityType superAbilityType : SuperAbilityType.values()) {
            abilityMode.put(superAbilityType, false);
            abilityInformed.put(superAbilityType, true); // This is intended
        }

        for (ToolType toolType : ToolType.values()) {
            toolMode.put(toolType, false);
        }

        experienceBarManager = new ExperienceBarManager(this);

        debugMode = false; //Debug mode helps solve support issues, players can toggle it on or off

        this.playerAuthor = new PlayerAuthor(player);

        this.chatChannel = ChatChannel.NONE;

        if (ChatConfig.getInstance().isSpyingAutomatic() && Permissions.adminChatSpy(getPlayer())) {
            chatSpy = true;
        }
    }

    private void initSkillManagers() {
        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            try {
                initManager(primarySkillType);
            } catch (InvalidSkillException e) {
                mcMMO.p.getLogger().log(Level.SEVERE,
                        "Invalid skill while initializing skill managers for player "
                        + player.getName()
                        + ". Contact the plugin developers.", e);
            }
        }
    }

    private void initManager(PrimarySkillType primarySkillType) throws InvalidSkillException {
        final var version = mcMMO.getCompatibilityManager().getMinecraftGameVersion();

        final SkillManager manager = switch (primarySkillType) {
            case ACROBATICS -> new AcrobaticsManager(this);
            case ALCHEMY -> new AlchemyManager(this);
            case ARCHERY -> new ArcheryManager(this);
            case AXES -> new AxesManager(this);
            case CROSSBOWS -> new CrossbowsManager(this);
            case EXCAVATION -> new ExcavationManager(this);
            case FISHING -> new FishingManager(this);
            case HERBALISM -> new HerbalismManager(this);
            case MINING -> new MiningManager(this);
            case REPAIR -> new RepairManager(this);
            case SALVAGE -> new SalvageManager(this);
            case SMELTING -> new SmeltingManager(this);
            case SWORDS -> new SwordsManager(this);
            case TAMING -> new TamingManager(this);
            case TRIDENTS -> new TridentsManager(this);
            case UNARMED -> new UnarmedManager(this);
            case WOODCUTTING -> new WoodcuttingManager(this);
            case MACES -> version.isAtLeast(1, 21, 0) ? new MacesManager(this) : null;
            case SPEARS -> version.isAtLeast(1, 21, 11) ? new SpearsManager(this) : null;
        };

        if (manager != null) {
            skillManagers.put(primarySkillType, manager);
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public double getAttackStrength() {
        if (mcMMO.p.getAdvancedConfig().useAttackCooldown()) {
            return player.getAttackCooldown();
        } else {
            return 1.0D;
        }
    }

    public @NotNull PrimarySkillType getLastSkillShownScoreboard() {
        return lastSkillShownScoreboard;
    }

    public void setLastSkillShownScoreboard(PrimarySkillType primarySkillType) {
        this.lastSkillShownScoreboard = primarySkillType;
    }

    public void processPostXpEvent(PrimarySkillType primarySkillType, Plugin plugin,
            XPGainSource xpGainSource) {
        //Check if they've reached the power level cap just now
        if (hasReachedPowerLevelCap()) {
            NotificationManager.sendPlayerInformationChatOnly(player, "LevelCap.PowerLevel",
                    String.valueOf(mcMMO.p.getGeneralConfig().getPowerLevelCap()));
        } else if (hasReachedLevelCap(primarySkillType)) {
            NotificationManager.sendPlayerInformationChatOnly(player, "LevelCap.Skill",
                    String.valueOf(mcMMO.p.getSkillTools().getLevelCap(primarySkillType)),
                    mcMMO.p.getSkillTools().getLocalizedSkillName(primarySkillType));
        }

        //Updates from Party sources
        if (xpGainSource == XPGainSource.PARTY_MEMBERS && !ExperienceConfig.getInstance()
                .isPartyExperienceBarsEnabled()) {
            return;
        }

        //Updates from passive sources (Alchemy, Smelting, etc...)
        if (xpGainSource == XPGainSource.PASSIVE && !ExperienceConfig.getInstance()
                .isPassiveGainsExperienceBarsEnabled()) {
            return;
        }

        updateXPBar(primarySkillType, plugin);
    }

    public void processUnlockNotifications(mcMMO plugin, PrimarySkillType primarySkillType,
            int skillLevel) {
        RankUtils.executeSkillUnlockNotifications(plugin, this, primarySkillType, skillLevel);
    }

    public void updateXPBar(PrimarySkillType primarySkillType, Plugin plugin) {
        //XP BAR UPDATES
        experienceBarManager.updateExperienceBar(primarySkillType, plugin);
    }

    public double getProgressInCurrentSkillLevel(PrimarySkillType primarySkillType) {
        if (SkillTools.isChildSkill(primarySkillType)) {
            return 1.0D;
        }

        double currentXP = profile.getSkillXpLevel(primarySkillType);
        double maxXP = profile.getXpToLevel(primarySkillType);

        return (currentXP / maxXP);
    }

    public ExperienceBarManager getExperienceBarManager() {
        return experienceBarManager;
    }

    public AcrobaticsManager getAcrobaticsManager() {
        return (AcrobaticsManager) skillManagers.get(PrimarySkillType.ACROBATICS);
    }

    public AlchemyManager getAlchemyManager() {
        return (AlchemyManager) skillManagers.get(PrimarySkillType.ALCHEMY);
    }

    public ArcheryManager getArcheryManager() {
        return (ArcheryManager) skillManagers.get(PrimarySkillType.ARCHERY);
    }

    public AxesManager getAxesManager() {
        return (AxesManager) skillManagers.get(PrimarySkillType.AXES);
    }

    public CrossbowsManager getCrossbowsManager() {
        return (CrossbowsManager) skillManagers.get(PrimarySkillType.CROSSBOWS);
    }

    public TridentsManager getTridentsManager() {
        return (TridentsManager) skillManagers.get(PrimarySkillType.TRIDENTS);
    }

    public MacesManager getMacesManager() {
        return (MacesManager) skillManagers.get(PrimarySkillType.MACES);
    }

    public ExcavationManager getExcavationManager() {
        return (ExcavationManager) skillManagers.get(PrimarySkillType.EXCAVATION);
    }

    public FishingManager getFishingManager() {
        return (FishingManager) skillManagers.get(PrimarySkillType.FISHING);
    }

    public HerbalismManager getHerbalismManager() {
        return (HerbalismManager) skillManagers.get(PrimarySkillType.HERBALISM);
    }

    public MiningManager getMiningManager() {
        return (MiningManager) skillManagers.get(PrimarySkillType.MINING);
    }

    public RepairManager getRepairManager() {
        return (RepairManager) skillManagers.get(PrimarySkillType.REPAIR);
    }

    public SalvageManager getSalvageManager() {
        return (SalvageManager) skillManagers.get(PrimarySkillType.SALVAGE);
    }

    public SmeltingManager getSmeltingManager() {
        return (SmeltingManager) skillManagers.get(PrimarySkillType.SMELTING);
    }

    public SpearsManager getSpearsManager() {
        return (SpearsManager) skillManagers.get(PrimarySkillType.SPEARS);
    }

    public SwordsManager getSwordsManager() {
        return (SwordsManager) skillManagers.get(PrimarySkillType.SWORDS);
    }

    public TamingManager getTamingManager() {
        return (TamingManager) skillManagers.get(PrimarySkillType.TAMING);
    }

    public UnarmedManager getUnarmedManager() {
        return (UnarmedManager) skillManagers.get(PrimarySkillType.UNARMED);
    }

    public WoodcuttingManager getWoodcuttingManager() {
        return (WoodcuttingManager) skillManagers.get(PrimarySkillType.WOODCUTTING);
    }

    /*
     * Abilities
     */

    /**
     * Reset the mode of all abilities.
     */
    public void resetAbilityMode() {
        for (SuperAbilityType ability : SuperAbilityType.values()) {
            // Correctly disable and handle any special deactivate code
            new AbilityDisableTask(this, ability).run();
        }
    }

    /**
     * Get the mode of a superAbilityType.
     *
     * @param superAbilityType The superAbilityType to check
     * @return true if the superAbilityType is enabled, false otherwise
     */
    public boolean getAbilityMode(@NotNull SuperAbilityType superAbilityType) {
        requireNonNull(superAbilityType, "superAbilityType cannot be null");
        return abilityMode.get(superAbilityType);
    }

    /**
     * Set the mode of an ability.
     *
     * @param ability The ability to check
     * @param isActive True if the ability is active, false otherwise
     */
    public void setAbilityMode(SuperAbilityType ability, boolean isActive) {
        // TODO: This should reject "one and done" type abilities
        abilityMode.put(ability, isActive);
    }

    /**
     * Get the informed state of an ability
     *
     * @param ability The ability to check
     * @return true if the ability is informed, false otherwise
     */
    public boolean getAbilityInformed(SuperAbilityType ability) {
        return abilityInformed.get(ability);
    }

    /**
     * Set the informed state of an ability.
     *
     * @param ability The ability to check
     * @param isInformed True if the ability is informed, false otherwise
     */
    public void setAbilityInformed(SuperAbilityType ability, boolean isInformed) {
        abilityInformed.put(ability, isInformed);
    }

    /**
     * Get the current prep mode of a tool.
     *
     * @param tool Tool to get the mode for
     * @return true if the tool is prepped, false otherwise
     */
    public boolean getToolPreparationMode(ToolType tool) {
        return toolMode.get(tool);
    }

    public boolean getAbilityUse() {
        return abilityUse;
    }

    public void toggleAbilityUse() {
        abilityUse = !abilityUse;
    }

    /*
     * Tools
     */

    /**
     * Reset the prep modes of all tools.
     */
    public void resetToolPrepMode() {
        for (ToolType tool : ToolType.values()) {
            setToolPreparationMode(tool, false);
        }
    }

    /**
     * Set the current prep mode of a tool.
     *
     * @param tool Tool to set the mode for
     * @param isPrepared true if the tool should be prepped, false otherwise
     */
    public void setToolPreparationMode(ToolType tool, boolean isPrepared) {
        toolMode.put(tool, isPrepared);
    }

    /*
     * Recently Hurt
     */

    public int getRecentlyHurt() {
        return recentlyHurt;
    }

    public void setRecentlyHurt(int value) {
        recentlyHurt = value;
    }

    public void actualizeRecentlyHurt() {
        recentlyHurt = (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
    }

    /*
     * Teleportation cooldown & warmup
     */

    public int getChimeraWingLastUse() {
        return profile.getChimaerWingDATS();
    }

    public void actualizeChimeraWingLastUse() {
        profile.setChimaeraWingDATS(
                (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR));
    }

    public Location getTeleportCommenceLocation() {
        return teleportCommence;
    }

    public void setTeleportCommenceLocation(Location location) {
        teleportCommence = location;
    }

    public void actualizeTeleportCommenceLocation(Player player) {
        teleportCommence = player.getLocation();
    }

    /*
     * Exploit Prevention
     */

    public int getRespawnATS() {
        return respawnATS;
    }

    public void actualizeRespawnATS() {
        respawnATS = (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
    }

    public int getTeleportATS() {
        return teleportATS;
    }

    public void actualizeTeleportATS() {
        teleportATS = (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
    }

    public long getDatabaseATS() {
        return databaseATS;
    }

    public void actualizeDatabaseATS() {
        databaseATS = System.currentTimeMillis();
    }

    /*
     * God Mode
     */

    public boolean getGodMode() {
        return godMode;
    }

    public void toggleGodMode() {
        godMode = !godMode;
    }

    /*
     * Party Chat Spy
     */

    public boolean isPartyChatSpying() {
        return chatSpy;
    }

    public void togglePartyChatSpying() {
        chatSpy = !chatSpy;
    }

    /*
     * Debug Mode Flags
     */

    public boolean isDebugMode() {
        return debugMode;
    }

    public void toggleDebugMode() {
        debugMode = !debugMode;
    }

    /*
     * Skill notifications
     */

    public boolean useChatNotifications() {
        return displaySkillNotifications;
    }

    public void toggleChatNotifications() {
        displaySkillNotifications = !displaySkillNotifications;
    }

    /**
     * Gets the power level of this player.
     *
     * @return the power level of the player
     */
    public int getPowerLevel() {
        int powerLevel = 0;

        for (PrimarySkillType primarySkillType : SkillTools.NON_CHILD_SKILLS) {
            if (mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, primarySkillType)) {
                powerLevel += getSkillLevel(primarySkillType);
            }
        }

        return powerLevel;
    }

    /**
     * Whether a player is level capped If they are at the power level cap, this will return true,
     * otherwise it checks their skill level
     *
     * @param primarySkillType
     * @return
     */
    public boolean hasReachedLevelCap(PrimarySkillType primarySkillType) {
        if (hasReachedPowerLevelCap()) {
            return true;
        }

        return getSkillLevel(primarySkillType) >= mcMMO.p.getSkillTools()
                .getLevelCap(primarySkillType);
    }

    /**
     * Whether a player is power level capped Compares their power level total to the current set
     * limit
     *
     * @return true if they have reached the power level cap
     */
    public boolean hasReachedPowerLevelCap() {
        return this.getPowerLevel() >= mcMMO.p.getGeneralConfig().getPowerLevelCap();
    }

    /**
     * Begins an experience gain. The amount will be affected by skill modifiers, global rate,
     * perks, and may be shared with the party
     *
     * @param skill Skill being used
     * @param xp Experience amount to process
     */
    public void beginXpGain(PrimarySkillType skill, float xp, XPGainReason xpGainReason,
            XPGainSource xpGainSource) {
        if (xp <= 0) {
            return;
        }

        if (SkillTools.isChildSkill(skill)) {
            var parentSkills = mcMMO.p.getSkillTools().getChildSkillParents(skill);
            float splitXp = xp / parentSkills.size();

            for (PrimarySkillType parentSkill : parentSkills) {
                if (mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, parentSkill)) {
                    beginXpGain(parentSkill, splitXp, xpGainReason, xpGainSource);
                }
            }

            return;
        }

        // Return if the experience has been shared
        if (party != null && ShareHandler.handleXpShare(xp, this, skill,
                ShareHandler.getSharedXpGainReason(xpGainReason))) {
            return;
        }

        beginUnsharedXpGain(skill, xp, xpGainReason, xpGainSource);
    }

    /**
     * Begins an experience gain. The amount will be affected by skill modifiers, global rate and
     * perks
     *
     * @param skill Skill being used
     * @param xp Experience amount to process
     */
    public void beginUnsharedXpGain(PrimarySkillType skill, float xp, XPGainReason xpGainReason,
            XPGainSource xpGainSource) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        applyXpGain(skill, modifyXpGain(skill, xp), xpGainReason, xpGainSource);

        if (!mcMMO.p.getPartyConfig().isPartyEnabled() || party == null
                || party.hasReachedLevelCap()) {
            return;
        }

        if (!mcMMO.p.getGeneralConfig().getPartyXpNearMembersNeeded() || !mcMMO.p.getPartyManager()
                .getNearMembers(this).isEmpty()) {
            party.applyXpGain(modifyXpGain(skill, xp));
        }
    }

    /**
     * Applies an experience gain
     *
     * @param primarySkillType Skill being used
     * @param xp Experience amount to add
     */
    public void applyXpGain(PrimarySkillType primarySkillType, float xp, XPGainReason xpGainReason,
            XPGainSource xpGainSource) {
        if (!mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, primarySkillType)) {
            return;
        }

        final McMMOPlayerPreXpGainEvent mmoPlayerPreXpGainEvent = new McMMOPlayerPreXpGainEvent(
                player, primarySkillType, xp, xpGainReason);
        Bukkit.getPluginManager().callEvent(mmoPlayerPreXpGainEvent);
        xp = mmoPlayerPreXpGainEvent.getXpGained();

        if (SkillTools.isChildSkill(primarySkillType)) {
            var parentSkills = mcMMO.p.getSkillTools().getChildSkillParents(primarySkillType);

            for (PrimarySkillType parentSkill : parentSkills) {
                applyXpGain(parentSkill, xp / parentSkills.size(), xpGainReason, xpGainSource);
            }

            return;
        }

        if (!EventUtils.handleXpGainEvent(player, primarySkillType, xp, xpGainReason)) {
            return;
        }

        isUsingUnarmed = (primarySkillType == PrimarySkillType.UNARMED);
        checkXp(primarySkillType, xpGainReason, xpGainSource);
    }

    /**
     * Check the XP of a skill.
     *
     * @param primarySkillType The skill to check
     */
    private void checkXp(PrimarySkillType primarySkillType, XPGainReason xpGainReason,
            XPGainSource xpGainSource) {
        if (hasReachedLevelCap(primarySkillType)) {
            return;
        }

        if (getSkillXpLevelRaw(primarySkillType) < getXpToLevel(primarySkillType)) {
            processPostXpEvent(primarySkillType, mcMMO.p, xpGainSource);
            return;
        }

        int levelsGained = 0;
        float xpRemoved = 0;

        while (getSkillXpLevelRaw(primarySkillType) >= getXpToLevel(primarySkillType)) {
            if (hasReachedLevelCap(primarySkillType)) {
                setSkillXpLevel(primarySkillType, 0);
                break;
            }

            xpRemoved += profile.levelUp(primarySkillType);
            levelsGained++;
        }

        if (EventUtils.tryLevelChangeEvent(this, primarySkillType, levelsGained, xpRemoved, true,
                xpGainReason)) {
            return;
        }

        if (mcMMO.p.getGeneralConfig().getLevelUpSoundsEnabled()) {
            SoundManager.sendSound(player, player.getLocation(), SoundType.LEVEL_UP);
        }

        /*
         * Check to see if the player unlocked any new skills
         */

        NotificationManager.sendPlayerLevelUpNotification(this, primarySkillType, levelsGained,
                profile.getSkillLevel(primarySkillType));

        //UPDATE XP BARS
        processPostXpEvent(primarySkillType, mcMMO.p, xpGainSource);
    }

    /*
     * Players & Profiles
     */

    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull PlayerProfile getProfile() {
        return profile;
    }

    /*
     * Party Stuff
     */

    public void setupPartyData() {
        if (mcMMO.p.isPartySystemEnabled()) {
            party = mcMMO.p.getPartyManager()
                    .getPlayerParty(player.getName(), player.getUniqueId());
            ptpRecord = new PartyTeleportRecord();

            if (inParty()) {
                loginParty();
            }
        }
    }

    public void setPartyInvite(Party invite) {
        this.invite = invite;
    }

    public Party getPartyInvite() {
        return invite;
    }

    public boolean hasPartyInvite() {
        return (invite != null);
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public @Nullable Party getParty() {
        return party;
    }

    public boolean inParty() {
        return (party != null);
    }

    public void removeParty() {
        party = null;
    }

    public void removePartyInvite() {
        invite = null;
    }

    public @Nullable PartyTeleportRecord getPartyTeleportRecord() {
        return ptpRecord;
    }

    public void setPartyAllianceInvite(Party allianceInvite) {
        this.allianceInvite = allianceInvite;
    }

    public Party getPartyAllianceInvite() {
        return allianceInvite;
    }

    public boolean hasPartyAllianceInvite() {
        return (allianceInvite != null);
    }

    public void removePartyAllianceInvite() {
        allianceInvite = null;
    }

    public void loginParty() {
        party.addOnlineMember(this.getPlayer());
    }

    public int getItemShareModifier() {
        if (itemShareModifier < 10) {
            setItemShareModifier(10);
        }

        return itemShareModifier;
    }

    public void setItemShareModifier(int modifier) {
        itemShareModifier = Math.max(10, modifier);
    }

    public boolean isUsingUnarmed() {
        return isUsingUnarmed;
    }

    /**
     * Modifies an experience gain using skill modifiers, global rate and perks
     *
     * @param primarySkillType Skill being used
     * @param xp Experience amount to process
     * @return Modified experience
     */
    @VisibleForTesting
    float modifyXpGain(PrimarySkillType primarySkillType, float xp) {
        //TODO: A rare situation can occur where the default Power Level cap can prevent a player with one skill edited to something silly like Integer.MAX_VALUE from gaining XP in any skill, we may need to represent power level with another data type
        if ((mcMMO.p.getSkillTools().getLevelCap(primarySkillType) <= getSkillLevel(
                primarySkillType))
                || (mcMMO.p.getGeneralConfig().getPowerLevelCap() <= getPowerLevel())) {
            return 0;
        }

        xp = (float) (
                (xp * ExperienceConfig.getInstance().getFormulaSkillModifier(primarySkillType))
                        * ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier());

        return PerksUtils.handleXpPerks(player, xp, primarySkillType);
    }

    public void checkGodMode() {
        if (godMode && !Permissions.mcgod(player)
                || godMode && WorldBlacklist.isWorldBlacklisted(player.getWorld())) {
            toggleGodMode();
            player.sendMessage(LocaleLoader.getString("Commands.GodMode.Forbidden"));
        }
    }

    public void checkParty() {
        if (inParty() && !Permissions.party(player)) {
            removeParty();
            player.sendMessage(LocaleLoader.getString("Party.Forbidden"));
        }
    }

    /**
     * Check to see if an ability can be activated.
     *
     * @param primarySkillType The skill the ability is based on
     */
    public void checkAbilityActivation(PrimarySkillType primarySkillType) {
        ToolType tool = mcMMO.p.getSkillTools().getPrimarySkillToolType(primarySkillType);
        SuperAbilityType superAbilityType = mcMMO.p.getSkillTools()
                .getSuperAbility(primarySkillType);
        SubSkillType subSkillType = superAbilityType.getSubSkillTypeDefinition();

        if (getAbilityMode(superAbilityType) || !superAbilityType.getPermissions(player)) {
            return;
        }

        //TODO: This is hacky and temporary solution until skills are move to the new system
        //Potential problems with this include skills with two super abilities (ie mining)
        if (!RankUtils.hasUnlockedSubskill(player, subSkillType)) {
            int diff = RankUtils.getSuperAbilityUnlockRequirement(superAbilityType) - getSkillLevel(
                    primarySkillType);

            //Inform the player they are not yet skilled enough
            NotificationManager.sendPlayerInformation(player, NotificationType.ABILITY_COOLDOWN,
                    "Skills.AbilityGateRequirementFail", String.valueOf(diff),
                    mcMMO.p.getSkillTools().getLocalizedSkillName(primarySkillType));
            return;
        }

        int timeRemaining = calculateTimeRemaining(superAbilityType);

        if (timeRemaining > 0) {
            /*
             * Axes and Woodcutting are odd because they share the same tool.
             * We show them the too tired message when they take action.
             */
            if (primarySkillType == PrimarySkillType.WOODCUTTING
                    || primarySkillType == PrimarySkillType.AXES) {
                NotificationManager.sendPlayerInformation(player, NotificationType.ABILITY_COOLDOWN,
                        "Skills.TooTired", String.valueOf(timeRemaining));
                //SoundManager.sendSound(player, player.getLocation(), SoundType.TIRED);
            }

            return;
        }

        if (callPlayerAbilityActivateEvent(this, primarySkillType).isCancelled()) {
            return;
        }

        //These values change depending on whether the server is in retro mode
        int abilityLengthVar = mcMMO.p.getAdvancedConfig().getAbilityLength();
        int abilityLengthCap = mcMMO.p.getAdvancedConfig().getAbilityLengthCap();

        int ticks;

        //Ability cap of 0 or below means no cap
        if (abilityLengthCap > 0) {
            ticks = PerksUtils.handleActivationPerks(player,
                    2 + (Math.min(abilityLengthCap, getSkillLevel(primarySkillType))
                            / abilityLengthVar), superAbilityType.getMaxLength());
        } else {
            ticks = PerksUtils.handleActivationPerks(player,
                    2 + (getSkillLevel(primarySkillType) / abilityLengthVar),
                    superAbilityType.getMaxLength());
        }

        if (useChatNotifications()) {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUPER_ABILITY,
                    superAbilityType.getAbilityOn());
            //player.sendMessage(ability.getAbilityOn());
        }

        if (mcMMO.p.getAdvancedConfig().sendAbilityNotificationToOtherPlayers()) {
            SkillUtils.sendSkillMessage(player, NotificationType.SUPER_ABILITY_ALERT_OTHERS,
                    superAbilityType.getAbilityPlayer());
        }

        //Sounds
        SoundManager.worldSendSound(player.getWorld(), player.getLocation(),
                SoundType.ABILITY_ACTIVATED_GENERIC);

        //If the current item is still buffed somehow, remove it to prevent enchantments from stacking.
        if (superAbilityType == SuperAbilityType.SUPER_BREAKER
                || superAbilityType == SuperAbilityType.GIGA_DRILL_BREAKER) {
            SkillUtils.removeAbilityBuff(player.getInventory().getItemInMainHand());
        }

        // Enable the ability
        profile.setAbilityDATS(superAbilityType,
                System.currentTimeMillis() + ((long) ticks * Misc.TIME_CONVERSION_FACTOR));
        setAbilityMode(superAbilityType, true);

        if (superAbilityType == SuperAbilityType.SUPER_BREAKER
                || superAbilityType == SuperAbilityType.GIGA_DRILL_BREAKER) {
            SkillUtils.handleAbilitySpeedIncrease(player);
        }

        setToolPreparationMode(tool, false);
        mcMMO.p.getFoliaLib().getScheduler()
                .runAtEntityLater(player, new AbilityDisableTask(this, superAbilityType),
                        (long) ticks * Misc.TICK_CONVERSION_FACTOR);
    }

    public void processAbilityActivation(@NotNull PrimarySkillType primarySkillType) {
        if (!mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(getPlayer(), primarySkillType)) {
            return;
        }

        if (mcMMO.p.getGeneralConfig().getAbilitiesOnlyActivateWhenSneaking()
                && !player.isSneaking()) {
            return;
        }

        ItemStack inHand = player.getInventory().getItemInMainHand();

        if (!getAbilityUse()) {
            return;
        }

        for (SuperAbilityType superAbilityType : SuperAbilityType.values()) {
            if (getAbilityMode(superAbilityType)) {
                return;
            }
        }

        SuperAbilityType ability = mcMMO.p.getSkillTools().getSuperAbility(primarySkillType);
        ToolType tool = mcMMO.p.getSkillTools().getPrimarySkillToolType(primarySkillType);

        /*
         * Woodcutting & Axes need to be treated differently.
         * Basically the tool always needs to ready and we check to see if the cooldown is over when the user takes action
         */
        if (tool.inHand(inHand) && !getToolPreparationMode(tool)) {
            if (primarySkillType != PrimarySkillType.WOODCUTTING
                    && primarySkillType != PrimarySkillType.AXES) {
                int timeRemaining = calculateTimeRemaining(ability);

                if (isAbilityOnCooldown(ability)) {
                    NotificationManager.sendPlayerInformation(player,
                            NotificationType.ABILITY_COOLDOWN, "Skills.TooTired",
                            String.valueOf(timeRemaining));
                    return;
                }
            }

            if (mcMMO.p.getGeneralConfig().getAbilityMessagesEnabled()) {
                /*
                 *
                 * IF THE TOOL IS AN AXE
                 *
                 */
                if (tool == ToolType.AXE) {
                    processAxeToolMessages();
                } else {
                    NotificationManager.sendPlayerInformation(player, NotificationType.TOOL,
                            tool.getRaiseTool());
                }

                //Send Sound
                SoundManager.sendSound(player, player.getLocation(), SoundType.TOOL_READY);
            }

            setToolPreparationMode(tool, true);
            mcMMO.p.getFoliaLib().getScheduler()
                    .runAtEntityLater(player, new ToolLowerTask(this, tool),
                            4 * Misc.TICK_CONVERSION_FACTOR);
        }
    }

    public void processAxeToolMessages() {
        Block rayCast = player.getTargetBlock(null, 100);

        /*
         * IF BOTH TREE FELLER & SKULL SPLITTER ARE ON CD
         */
        if (isAbilityOnCooldown(SuperAbilityType.TREE_FELLER) && isAbilityOnCooldown(
                SuperAbilityType.SKULL_SPLITTER)) {
            tooTiredMultiple(PrimarySkillType.WOODCUTTING, SubSkillType.WOODCUTTING_TREE_FELLER,
                    SuperAbilityType.TREE_FELLER, SubSkillType.AXES_SKULL_SPLITTER,
                    SuperAbilityType.SKULL_SPLITTER);
            /*
             * IF TREE FELLER IS ON CD
             * AND PLAYER IS LOOKING AT TREE
             */
        } else if (isAbilityOnCooldown(SuperAbilityType.TREE_FELLER)
                && BlockUtils.isPartOfTree(rayCast)) {
            raiseToolWithCooldowns(SubSkillType.WOODCUTTING_TREE_FELLER,
                    SuperAbilityType.TREE_FELLER);

            /*
             * IF SKULL SPLITTER IS ON CD
             */
        } else if (isAbilityOnCooldown(SuperAbilityType.SKULL_SPLITTER)) {
            raiseToolWithCooldowns(SubSkillType.AXES_SKULL_SPLITTER,
                    SuperAbilityType.SKULL_SPLITTER);
        } else {
            NotificationManager.sendPlayerInformation(player, NotificationType.TOOL,
                    ToolType.AXE.getRaiseTool());
        }
    }

    private void tooTiredMultiple(PrimarySkillType primarySkillType, SubSkillType aSubSkill,
            SuperAbilityType aSuperAbility, SubSkillType bSubSkill,
            SuperAbilityType bSuperAbility) {
        String aSuperAbilityCD = LocaleLoader.getString("Skills.TooTired.Named",
                aSubSkill.getLocaleName(),
                String.valueOf(calculateTimeRemaining(aSuperAbility)));
        String bSuperAbilityCD = LocaleLoader.getString("Skills.TooTired.Named",
                bSubSkill.getLocaleName(),
                String.valueOf(calculateTimeRemaining(bSuperAbility)));
        String allCDStr = aSuperAbilityCD + ", " + bSuperAbilityCD;

        NotificationManager.sendPlayerInformation(player, NotificationType.TOOL,
                "Skills.TooTired.Extra",
                mcMMO.p.getSkillTools().getLocalizedSkillName(primarySkillType),
                allCDStr);
    }

    private void raiseToolWithCooldowns(SubSkillType subSkillType,
            SuperAbilityType superAbilityType) {
        NotificationManager.sendPlayerInformation(player, NotificationType.TOOL,
                "Axes.Ability.Ready.Extra",
                subSkillType.getLocaleName(),
                String.valueOf(calculateTimeRemaining(superAbilityType)));
    }

    public boolean isAbilityOnCooldown(SuperAbilityType ability) {
        return !getAbilityMode(ability) && calculateTimeRemaining(ability) > 0;
    }

    /**
     * Calculate the time remaining until the ability's cooldown expires.
     *
     * @param ability SuperAbilityType whose cooldown to check
     * @return the number of seconds remaining before the cooldown expires
     */
    public int calculateTimeRemaining(SuperAbilityType ability) {
        long deactivatedTimestamp = profile.getAbilityDATS(ability) * Misc.TIME_CONVERSION_FACTOR;
        return (int) (((deactivatedTimestamp + (
                PerksUtils.handleCooldownPerks(player, ability.getCooldown())
                        * Misc.TIME_CONVERSION_FACTOR)) - System.currentTimeMillis())
                / Misc.TIME_CONVERSION_FACTOR);
    }

    /*
     * These functions are wrapped from PlayerProfile so that we don't always have to store it alongside the McMMOPlayer object.
     */
    public int getSkillLevel(PrimarySkillType skill) {
        return profile.getSkillLevel(skill);
    }

    public float getSkillXpLevelRaw(PrimarySkillType skill) {
        return profile.getSkillXpLevelRaw(skill);
    }

    public int getSkillXpLevel(PrimarySkillType skill) {
        return profile.getSkillXpLevel(skill);
    }

    public void setSkillXpLevel(PrimarySkillType skill, float xpLevel) {
        profile.setSkillXpLevel(skill, xpLevel);
    }

    public int getXpToLevel(PrimarySkillType skill) {
        return profile.getXpToLevel(skill);
    }

    public void removeXp(PrimarySkillType skill, int xp) {
        profile.removeXp(skill, xp);
    }

    public void modifySkill(PrimarySkillType skill, int level) {
        profile.modifySkill(skill, level);
    }

    public void addLevels(PrimarySkillType skill, int levels) {
        profile.addLevels(skill, levels);
    }

    public void addXp(PrimarySkillType skill, float xp) {
        profile.addXp(skill, xp);
    }

    public void setAbilityDATS(SuperAbilityType ability, long DATS) {
        profile.setAbilityDATS(ability, DATS);
    }

    public void resetCooldowns() {
        profile.resetCooldowns();
    }

    public FixedMetadataValue getPlayerMetadata() {
        return playerMetadata;
    }

    /**
     * This method is called by PlayerQuitEvent to tear down the mmoPlayer.
     *
     * @param syncSave if true, data is saved synchronously
     */
    public void logout(boolean syncSave) {
        final Player thisPlayer = getPlayer();
        if (getPlayer() != null && getPlayer().hasMetadata(
                MetadataConstants.METADATA_KEY_RUPTURE)) {
            final RuptureTaskMeta ruptureTaskMeta
                    = (RuptureTaskMeta) getPlayer().getMetadata(
                    MetadataConstants.METADATA_KEY_RUPTURE).get(0);
            if (ruptureTaskMeta != null) {
                final RuptureTask ruptureTimerTask = ruptureTaskMeta.getRuptureTimerTask();
                if (ruptureTimerTask != null) {
                    ruptureTimerTask.cancel();
                }
                getPlayer().removeMetadata(MetadataConstants.METADATA_KEY_RUPTURE, mcMMO.p);
            }
        }

        cleanup();

        if (syncSave) {
            getProfile().save(true);
        } else {
            getProfile().scheduleAsyncSave();
        }

        UserManager.remove(thisPlayer);

        if (mcMMO.p.getGeneralConfig().getScoreboardsEnabled()) {
            ScoreboardManager.teardownPlayer(thisPlayer);
        }

        if (inParty()) {
            party.removeOnlineMember(thisPlayer);
        }

        //Remove user from cache
        mcMMO.getDatabaseManager().cleanupUser(thisPlayer.getUniqueId());
    }

    /**
     * Cleanup various things related to this player Such as temporary summons.. Turning off
     * abilities... Etc...
     */
    public void cleanup() {
        resetAbilityMode();
        getTamingManager().cleanupAllSummons();
    }

    /**
     * For use with Adventure API (Kyori lib)
     *
     * @return this players identity
     */
    @Override
    public @NonNull Identity identity() {
        return identity;
    }


    /**
     * The {@link com.gmail.nossr50.chat.author.Author} for this player, used by mcMMO chat
     *
     * @return the {@link com.gmail.nossr50.chat.author.Author} for this player
     */
    public @NotNull PlayerAuthor getPlayerAuthor() {
        return playerAuthor;
    }

    public @NotNull ChatChannel getChatChannel() {
        return chatChannel;
    }

    /**
     * Change the chat channel for a player This does not inform the player
     *
     * @param chatChannel new chat channel
     */
    public void setChatMode(@NotNull ChatChannel chatChannel) {
        this.chatChannel = chatChannel;
    }

    public long getLastSwingTimestamp() {
        return lastSwingTimestamp;
    }

    public void setLastSwingTimestamp(long lastSwingTimestamp) {
        this.lastSwingTimestamp = lastSwingTimestamp;
    }
}
