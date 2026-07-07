package com.gmail.nossr50.util.scoreboards;

import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.events.scoreboard.McMMOScoreboardMakeboardEvent;
import com.gmail.nossr50.events.scoreboard.ScoreboardEventReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.PaperUtil;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.backend.BukkitScoreboardBackend;
import com.gmail.nossr50.util.scoreboards.backend.NoopScoreboardBackend;
import com.gmail.nossr50.util.scoreboards.backend.PlayerBoard;
import com.gmail.nossr50.util.scoreboards.backend.PacketScoreboardBackend;
import com.gmail.nossr50.util.scoreboards.backend.ScoreboardBackend;
import com.gmail.nossr50.util.scoreboards.backend.ScoreboardBackendSelector;
import com.gmail.nossr50.util.scoreboards.backend.ScoreboardBackendType;
import com.gmail.nossr50.util.skills.SkillTools;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.megavex.scoreboardlibrary.api.objective.ScoreboardObjective;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the Scoreboards used to display a variety of mcMMO related information to the player
 */
public class ScoreboardManager {
    static final Map<String, ScoreboardWrapper> PLAYER_SCOREBOARDS = new ConcurrentHashMap<>();

    static final String HEADER_STATS = LocaleLoader.getString("Scoreboard.Header.PlayerStats");
    static final String HEADER_COOLDOWNS = LocaleLoader.getString(
            "Scoreboard.Header.PlayerCooldowns");
    static final String HEADER_RANK = LocaleLoader.getString("Scoreboard.Header.PlayerRank");
    static final String TAG_POWER_LEVEL = LocaleLoader.getString("Scoreboard.Header.PowerLevel");

    static final String POWER_LEVEL = LocaleLoader.getString("Scoreboard.Misc.PowerLevel");

    static final String LABEL_POWER_LEVEL = POWER_LEVEL;
    static final String LABEL_LEVEL = LocaleLoader.getString("Scoreboard.Misc.Level");
    static final String LABEL_CURRENT_XP = LocaleLoader.getString("Scoreboard.Misc.CurrentXP");
    static final String LABEL_REMAINING_XP = LocaleLoader.getString("Scoreboard.Misc.RemainingXP");
//    static final String LABEL_ABILITY_COOLDOWN = LocaleLoader.getString("Scoreboard.Misc.Cooldown");
//    static final String LABEL_OVERALL = LocaleLoader.getString("Scoreboard.Misc.Overall");

    static final Map<PrimarySkillType, String> skillLabels;
    static final Map<SuperAbilityType, String> abilityLabelsColored;
    static final Map<SuperAbilityType, String> abilityLabelsSkill;

    private static ScoreboardBackend backend;
    private static ScoreboardBackendType backendType = ScoreboardBackendType.NOOP;

    /*
     * Initializes the static label maps for our scoreboards.
     *
     * Note: backend resources are not loaded here. Loading packet adapters can fail on unsupported
     * server versions, and doing that inside a static initializer would turn failures into
     * ExceptionInInitializerError on first access. Backends are initialized explicitly via
     * {@link #init()} during onEnable, guarded by the scoreboards-enabled config.
     */
    static {
        /*
         * We need immutable objects for our Scoreboard's labels
         */
        ImmutableMap.Builder<PrimarySkillType, String> skillLabelBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<SuperAbilityType, String> abilityLabelBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<SuperAbilityType, String> abilityLabelSkillBuilder = ImmutableMap.builder();

        /*
         * Builds the labels for our ScoreBoards
         * Stylizes the targetBoard in a Rainbow Pattern
         * This is off by default
         */
        if (mcMMO.p.getGeneralConfig().getScoreboardRainbows()) {
            // Everything but black, gray, gold
            List<ChatColor> colors = Lists.newArrayList(
                    ChatColor.WHITE,
                    ChatColor.YELLOW,
                    ChatColor.LIGHT_PURPLE,
                    ChatColor.RED,
                    ChatColor.AQUA,
                    ChatColor.GREEN,
                    ChatColor.DARK_GRAY,
                    ChatColor.BLUE,
                    ChatColor.DARK_PURPLE,
                    ChatColor.DARK_RED,
                    ChatColor.DARK_AQUA,
                    ChatColor.DARK_GREEN,
                    ChatColor.DARK_BLUE);

            Collections.shuffle(colors, Misc.getRandom());

            int i = 0;
            for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
                // Include child skills
                skillLabelBuilder.put(primarySkillType, getShortenedName(
                        colors.get(i) + mcMMO.p.getSkillTools()
                                .getLocalizedSkillName(primarySkillType), false));

                if (mcMMO.p.getSkillTools().getSuperAbility(primarySkillType) != null) {
                    abilityLabelBuilder.put(
                            mcMMO.p.getSkillTools().getSuperAbility(primarySkillType),
                            getShortenedName(colors.get(i) + mcMMO.p.getSkillTools()
                                    .getSuperAbility(primarySkillType).getLocalizedName()));

                    if (primarySkillType == PrimarySkillType.MINING) {
                        abilityLabelBuilder.put(SuperAbilityType.BLAST_MINING, getShortenedName(
                                colors.get(i) + SuperAbilityType.BLAST_MINING.getLocalizedName()));
                    }
                }

                if (++i == colors.size()) {
                    i = 0;
                }
            }
        }
        /*
         * Builds the labels for our ScoreBoards
         * Stylizes the targetBoard using our normal color scheme
         */
        else {
            for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
                // Include child skills
                skillLabelBuilder.put(primarySkillType, getShortenedName(
                        ChatColor.GREEN + mcMMO.p.getSkillTools()
                                .getLocalizedSkillName(primarySkillType)));

                if (mcMMO.p.getSkillTools().getSuperAbility(primarySkillType) != null) {
                    abilityLabelBuilder.put(
                            mcMMO.p.getSkillTools().getSuperAbility(primarySkillType),
                            formatAbility(mcMMO.p.getSkillTools().getSuperAbility(primarySkillType)
                                    .getLocalizedName()));

                    if (primarySkillType == PrimarySkillType.MINING) {
                        abilityLabelBuilder.put(SuperAbilityType.BLAST_MINING,
                                formatAbility(SuperAbilityType.BLAST_MINING.getLocalizedName()));
                    }
                }
            }
        }

        for (SuperAbilityType type : SuperAbilityType.values()) {
            abilityLabelSkillBuilder.put(type, formatAbility(
                    (type == SuperAbilityType.BLAST_MINING ? ChatColor.BLUE : ChatColor.AQUA),
                    type.getLocalizedName()));
        }

        skillLabels = skillLabelBuilder.build();
        abilityLabelsColored = abilityLabelBuilder.build();
        abilityLabelsSkill = abilityLabelSkillBuilder.build();
    }

    private static final Set<String> dirtyPowerLevels = ConcurrentHashMap.newKeySet();

    public static void init() {
        if (backend != null) {
            return;
        }

        if (!mcMMO.p.getGeneralConfig().getScoreboardsEnabled()) {
            backendType = ScoreboardBackendType.NOOP;
            backend = new NoopScoreboardBackend();
            backend.init();
            cleanUpLeftoverBukkitPowerObjective();
            return;
        }

        final boolean isFolia = PaperUtil.isFolia();
        final ScoreboardBackendType selectedType = ScoreboardBackendSelector.select(
                isFolia,
                mcMMO.getMinecraftGameVersion());
        backendType = selectedType;

        if (selectedType != ScoreboardBackendType.BUKKIT) {
            // The Bukkit backend owns the persisted main-scoreboard objective; when any other
            // backend is selected, a leftover persisted objective would keep rendering below
            // nametags unmanaged.
            cleanUpLeftoverBukkitPowerObjective();
        }

        switch (selectedType) {
            case BUKKIT -> backend = new BukkitScoreboardBackend();
            case PACKET -> backend = new PacketScoreboardBackend();
            case NOOP -> {
                backend = new NoopScoreboardBackend();
                mcMMO.p.getLogger().warning(
                        "Folia detected on unsupported Minecraft version "
                                + mcMMO.getMinecraftGameVersion().getVersionStr()
                                + " (supported packet scoreboard max is "
                                + ScoreboardBackendSelector.MAX_PACKET_VERSION_STRING
                                + "). Scoreboards are disabled to avoid Folia async scoreboard issues. "
                                + "Please update mcMMO.");
            }
        }

        try {
            backend.init();
        } catch (RuntimeException e) {
            if (selectedType == ScoreboardBackendType.PACKET) {
                mcMMO.p.getLogger().warning(
                        "Packet scoreboard backend failed to initialize ("
                                + e.getMessage()
                                + "). Falling back to a Folia-safe no-op scoreboard backend.");
                backendType = ScoreboardBackendType.NOOP;
                backend = new NoopScoreboardBackend();
                backend.init();
            } else {
                throw e;
            }
        }
    }

    public enum SidebarType {
        NONE,
        SKILL_BOARD,
        STATS_BOARD,
        COOLDOWNS_BOARD,
        RANK_BOARD,
        TOP_BOARD
    }

    private static String formatAbility(String abilityName) {
        return formatAbility(ChatColor.AQUA, abilityName);
    }

    private static String formatAbility(ChatColor color, String abilityName) {
        if (mcMMO.p.getGeneralConfig().getShowAbilityNames()) {
            return getShortenedName(color + abilityName);
        } else {
            return color + LocaleLoader.getString("Scoreboard.Misc.Ability");
        }
    }

    private static String getShortenedName(String name) {
        return getShortenedName(name, true);
    }

    private static String getShortenedName(String name, boolean useDots) {
        if (name.length() > 16) {
            name = useDots ? name.substring(0, 14) + ".." : name.substring(0, 16);
        }

        return name;
    }

    // **** Listener call-ins **** //

    // Called by PlayerJoinEvent listener
    public static void setupPlayer(Player player) {
        ensureBackendReady();
        teardownPlayer(player);
        PLAYER_SCOREBOARDS.put(player.getName(), makeNewScoreboard(player));
        dirtyPowerLevels.add(player.getName());

        if (mcMMO.p.getGeneralConfig().getPowerLevelTagsEnabled() && backend.isPowerLevelTagActive()) {
            backend.setupPowerLevelTag(player);
        }
    }

    // Called by PlayerQuitEvent listener and OnPlayerTeleport under certain circumstances
    public static void teardownPlayer(Player player) {
        ensureBackendReady();

        if (player == null) {
            return;
        }

        if (isBukkitBackendActive() && player.isOnline() && player.isValid()
                && Bukkit.getScoreboardManager() != null) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }

        backend.removePowerLevelTag(player);
        dirtyPowerLevels.remove(player.getName());

        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.remove(player.getName());
        if (wrapper != null) {
            wrapper.close();
        }
    }

    // Called in onDisable()
    public static void teardownAll() {
        ensureBackendReady();
        ImmutableList<Player> onlinePlayers = ImmutableList.copyOf(
                mcMMO.p.getServer().getOnlinePlayers());
        LogUtils.debug(mcMMO.p.getLogger(),
                "Tearing down scoreboards... (" + onlinePlayers.size() + ")");
        for (Player player : onlinePlayers) {
            teardownPlayer(player);
        }

        if (backend != null) {
            backend.shutdown();
        }
        backend = null;
        backendType = ScoreboardBackendType.NOOP;
    }

    // Called by ScoreboardWrapper when its Player logs off and an action tries to be performed
    public static void cleanup(ScoreboardWrapper wrapper) {
        ensureBackendReady();
        PLAYER_SCOREBOARDS.remove(wrapper.playerName);
        wrapper.close();
    }

    // Called by internal level-up event listener
    public static void handleLevelUp(Player player, PrimarySkillType skill) {
        // Selfboards
        ScoreboardWrapper wrapper = getWrapper(player);

        if (wrapper == null) {
            setupPlayer(player);
            wrapper = getWrapper(player);
        }

        if (wrapper != null) {
            if (isSkillBoardTracking(wrapper, skill)
                    || (wrapper.isStatsScoreboard()) && wrapper.isBoardShown()) {
                wrapper.doSidebarUpdateSoon();
            }

            // Otherboards
            String playerName = player.getName();

            for (ScoreboardWrapper iWrapper : PLAYER_SCOREBOARDS.values()) {
                if (iWrapper.isStatsScoreboard() && playerName.equals(iWrapper.targetPlayer)
                        && iWrapper.isBoardShown()) {
                    iWrapper.doSidebarUpdateSoon();
                }
            }

            if (mcMMO.p.getGeneralConfig().getPowerLevelTagsEnabled() && !dirtyPowerLevels.contains(
                    playerName)) {
                dirtyPowerLevels.add(playerName);
            }

            if (mcMMO.p.getGeneralConfig().getSkillLevelUpBoard()) {
                enablePlayerSkillLevelUpScoreboard(player, skill);
            }

        }
    }

    // Called by internal xp event listener
    public static void handleXp(Player player, PrimarySkillType skill) {
        // Selfboards
        ScoreboardWrapper wrapper = getWrapper(player);

        if (wrapper != null && isSkillBoardTracking(wrapper, skill) && wrapper.isBoardShown()) {
            wrapper.doSidebarUpdateSoon();
        }
    }

    /**
     * Whether a wrapper's skill sidebar displays data affected by a change to the given skill.
     * <p>
     * Child skills (Salvage, Smelting) never gain XP or levels directly; their sidebar shows
     * levels derived from parent skills, so a child-skill board must also refresh when one of
     * its parent skills changes.
     */
    private static boolean isSkillBoardTracking(ScoreboardWrapper wrapper,
            PrimarySkillType skill) {
        if (!wrapper.isSkillScoreboard() || wrapper.targetSkill == null) {
            return false;
        }

        if (wrapper.targetSkill == skill) {
            return true;
        }

        return SkillTools.isChildSkill(wrapper.targetSkill)
                && mcMMO.p.getSkillTools().getChildSkillParents(wrapper.targetSkill)
                        .contains(skill);
    }

    // Called by internal ability event listeners
    public static void cooldownUpdate(Player player, PrimarySkillType skill) {
        // Selfboards
        ScoreboardWrapper wrapper = getWrapper(player);

        if (wrapper == null) {
            setupPlayer(player);
            wrapper = getWrapper(player);
        }

        if (wrapper != null) {
            if ((wrapper.isCooldownScoreboard()
                    || wrapper.isSkillScoreboard() && wrapper.targetSkill == skill)
                    && wrapper.isBoardShown()) {
                wrapper.doSidebarUpdateSoon();
            }
        }
    }

    // **** Setup methods **** //

    public static void enablePlayerSkillScoreboard(Player player, PrimarySkillType skill) {
        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
        mmoPlayer.setLastSkillShownScoreboard(skill);

        ScoreboardWrapper wrapper = getWrapper(player);

        if (wrapper == null) {
            setupPlayer(player);
            wrapper = getWrapper(player);
        }

        if (wrapper != null) {
            wrapper.setTypeSkill(skill);

            changeScoreboard(wrapper, mcMMO.p.getGeneralConfig().getSkillScoreboardTime());
        }
    }

    public static void enablePlayerSkillLevelUpScoreboard(Player player, PrimarySkillType skill) {
        ScoreboardWrapper wrapper = getWrapper(player);

        // Do NOT run if already shown
        if (wrapper != null && wrapper.isBoardShown()) {

            if (wrapper.isBoardShown()) {
                return;
            }

            wrapper.setTypeSkill(skill);
            changeScoreboard(wrapper, mcMMO.p.getGeneralConfig().getSkillLevelUpTime());
        }
    }

    public static void enablePlayerStatsScoreboard(Player player) {
        ScoreboardWrapper wrapper = getWrapper(player);

        if (wrapper == null) {
            return;
        }

        wrapper.setTypeSelfStats();

        changeScoreboard(wrapper, mcMMO.p.getGeneralConfig().getStatsScoreboardTime());
    }

    public static void enablePlayerInspectScoreboard(@NotNull Player player,
                                                     @NotNull PlayerProfile targetProfile) {
        ScoreboardWrapper wrapper = getWrapper(player);

        if (wrapper == null) {
            setupPlayer(player);
            wrapper = getWrapper(player);
        }

        if (wrapper != null) {
            wrapper.setTypeInspectStats(targetProfile);

            changeScoreboard(wrapper, mcMMO.p.getGeneralConfig().getInspectScoreboardTime());
        }
    }

    public static void enablePlayerInspectScoreboard(@NotNull Player player,
                                                     @NotNull McMMOPlayer targetMcMMOPlayer) {
        ScoreboardWrapper wrapper = getWrapper(player);

        if (wrapper == null) {
            setupPlayer(player);
            wrapper = getWrapper(player);
        }

        if (wrapper != null) {
            wrapper.setTypeInspectStats(targetMcMMOPlayer);

            changeScoreboard(wrapper, mcMMO.p.getGeneralConfig().getInspectScoreboardTime());
        }
    }

    public static void enablePlayerCooldownScoreboard(Player player) {
        ScoreboardWrapper wrapper = getWrapper(player);

        if (wrapper == null) {
            setupPlayer(player);
            wrapper = getWrapper(player);
        }

        if (wrapper != null) {
            wrapper.setTypeCooldowns();

            changeScoreboard(wrapper, mcMMO.p.getGeneralConfig().getCooldownScoreboardTime());
        }
    }

    public static void showPlayerRankScoreboard(Player player,
                                                Map<PrimarySkillType, Integer> rank) {
        ScoreboardWrapper wrapper = getWrapper(player);

        if (wrapper == null) {
            setupPlayer(player);
            wrapper = getWrapper(player);
        }

        if (wrapper != null) {
            wrapper.setTypeSelfRank();
            wrapper.acceptRankData(rank);

            changeScoreboard(wrapper, mcMMO.p.getGeneralConfig().getRankScoreboardTime());
        }
    }

    public static void showPlayerRankScoreboardOthers(Player player, String targetName,
                                                      Map<PrimarySkillType, Integer> rank) {
        ScoreboardWrapper wrapper = getWrapper(player);

        if (wrapper == null) {
            setupPlayer(player);
            wrapper = getWrapper(player);
        }

        if (wrapper != null) {
            wrapper.setTypeInspectRank(targetName);
            wrapper.acceptRankData(rank);

            changeScoreboard(wrapper, mcMMO.p.getGeneralConfig().getRankScoreboardTime());
        }
    }

    public static void showTopScoreboard(Player player, PrimarySkillType skill, int pageNumber,
                                         List<PlayerStat> stats) {

        ScoreboardWrapper wrapper = getWrapper(player);

        if (wrapper == null) {
            setupPlayer(player);
            wrapper = getWrapper(player);
        }

        if (wrapper != null) {
            wrapper.setTypeTop(skill, pageNumber);
            wrapper.acceptLeaderboardData(stats);

            changeScoreboard(wrapper, mcMMO.p.getGeneralConfig().getTopScoreboardTime());
        }
    }

    public static void showTopPowerScoreboard(Player player, int pageNumber,
                                              List<PlayerStat> stats) {
        ScoreboardWrapper wrapper = getWrapper(player);

        if (wrapper == null) {
            setupPlayer(player);
            wrapper = getWrapper(player);
        }

        if (wrapper != null) {
            wrapper.setTypeTopPower(pageNumber);
            wrapper.acceptLeaderboardData(stats);

            changeScoreboard(wrapper, mcMMO.p.getGeneralConfig().getTopScoreboardTime());
        }
    }

    public static @Nullable ScoreboardWrapper getWrapper(Player player) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        if (wrapper == null) {
            // Lazily set the player up. We must go through setupPlayer (not a bare
            // makeNewScoreboard) so the created wrapper is actually stored in the map - otherwise
            // its packet Sidebar would be created and immediately leaked, since the packet sidebar
            // holds a native resource that must be close()d (unlike the old Bukkit board).
            setupPlayer(player);
            wrapper = PLAYER_SCOREBOARDS.get(player.getName());
        }

        return wrapper;
    }

    // **** Helper methods **** //

    /**
     * @return false if power levels are disabled
     */
    public static boolean powerLevelHeartbeat() {
        ensureBackendReady();

        if (!backend.isPowerLevelTagActive()) {
            return false;
        }

        PowerLevelTagUpdater.applyPending(dirtyPowerLevels, ScoreboardManager::resolvePowerLevel,
                playerName -> Bukkit.getPlayerExact(playerName) != null, backend::setPowerLevel);
        return true;
    }

    /**
     * @return the player's current power level, or null while their profile has not loaded
     */
    private static @Nullable Integer resolvePowerLevel(final String playerName) {
        final McMMOPlayer mmoPlayer = UserManager.getPlayer(playerName);
        return mmoPlayer == null ? null : mmoPlayer.getPowerLevel();
    }

    /**
     * Gets or creates the packet-based below-name power level objective.
     * <p/>
     * If power levels are disabled, the objective manager is closed and null is returned.
     *
     * @return the power level objective, or null if disabled
     */
    public static @Nullable ScoreboardObjective getPowerLevelObjective() {
        ensureBackendReady();
        return backend.getPacketPowerLevelObjective();
    }

    /**
     * @deprecated The packet-based sidebar no longer uses Bukkit's {@link
     * org.bukkit.scoreboard.ScoreboardManager}; this method has no internal callers and is kept
     * only as a thin pass-through for backwards compatibility.
     */
    @Deprecated
    public static @Nullable org.bukkit.scoreboard.ScoreboardManager getScoreboardManager() {
        return mcMMO.p.getServer().getScoreboardManager();
    }

    public static void changeScoreboard(ScoreboardWrapper wrapper, int displayTime) {
        if (displayTime == -1) {
            wrapper.showBoardWithNoRevert();
        } else {
            wrapper.showBoardAndScheduleRevert(displayTime * Misc.TICK_CONVERSION_FACTOR);
        }
    }

    public static boolean isBoardShown(String playerName) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(playerName);
        return wrapper != null && wrapper.isBoardShown();
    }

    public static void clearBoard(String playerName) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(playerName);
        if (wrapper != null) {
            wrapper.tryRevertBoard();
        }
    }

    public static void keepBoard(String playerName) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(playerName);
        if (wrapper != null) {
            wrapper.cancelRevert();
        }
    }

    public static void setRevertTimer(String playerName, int seconds) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(playerName);
        if (wrapper != null) {
            wrapper.showBoardAndScheduleRevert(seconds * Misc.TICK_CONVERSION_FACTOR);
        }
    }

    public static boolean isPlayerBoardSetup(@NotNull String playerName) {
        return PLAYER_SCOREBOARDS.get(playerName) != null;
    }

    public static @NotNull ScoreboardWrapper makeNewScoreboard(Player player) {
        ensureBackendReady();

        final Scoreboard eventTargetBoard = backend.createEventTargetBoard(player);
        final Scoreboard currentBoard = player.getScoreboard();
        McMMOScoreboardMakeboardEvent event = new McMMOScoreboardMakeboardEvent(
                eventTargetBoard, currentBoard, player,
                ScoreboardEventReason.CREATING_NEW_SCOREBOARD);
        player.getServer().getPluginManager().callEvent(event);

        final Player targetPlayer = event.getTargetPlayer();
        final Scoreboard targetBoard = event.getTargetBoard() == null
                ? currentBoard
                : event.getTargetBoard();
        final PlayerBoard playerBoard = backend.createPlayerBoard(targetPlayer, targetBoard);
        return new ScoreboardWrapper(targetPlayer, playerBoard);
    }

    public static @NotNull ScoreboardBackend getBackend() {
        ensureBackendReady();
        return backend;
    }

    public static @NotNull ScoreboardBackendType getBackendType() {
        ensureBackendReady();
        return backendType;
    }

    public static boolean isBukkitBackendActive() {
        return getBackendType() == ScoreboardBackendType.BUKKIT;
    }

    public static void onPlayerBoardClosed(@NotNull String playerName) {
        ensureBackendReady();
        backend.onPlayerBoardClosed(playerName);
    }

    /**
     * Removes a leftover persisted below-name power level objective from the main scoreboard.
     * Guarded so scheduler thread restrictions (e.g. Folia off-thread scoreboard access when a
     * backend is initialized lazily) can never break backend initialization.
     */
    private static void cleanUpLeftoverBukkitPowerObjective() {
        try {
            BukkitScoreboardBackend.removeLeftoverPowerObjective();
        } catch (RuntimeException e) {
            LogUtils.debug(mcMMO.p.getLogger(),
                    "Skipped leftover power level objective cleanup: " + e.getMessage());
        }
    }

    private static void ensureBackendReady() {
        if (backend == null) {
            init();
        }

        if (backend == null) {
            backend = new NoopScoreboardBackend();
            backendType = ScoreboardBackendType.NOOP;
            backend.init();
        }
    }
}