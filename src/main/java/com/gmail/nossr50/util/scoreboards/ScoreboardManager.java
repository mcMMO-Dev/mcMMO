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
import com.gmail.nossr50.util.player.UserManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveManager;
import net.megavex.scoreboardlibrary.api.objective.ScoreboardObjective;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Manages the Scoreboards used to display a variety of mcMMO related information to the player
 */
public class ScoreboardManager {
    static final Map<String, ScoreboardWrapper> PLAYER_SCOREBOARDS = new HashMap<>();

    // do not localize; these are internal identifiers
    static final String SIDEBAR_OBJECTIVE = "mcmmo_sidebar";
    // Randomized per server start so it can never collide with a leftover objective of the same
    // name on a client - e.g. the old Bukkit implementation's 'mcmmo_pwrlvl' lingering after an
    // in-place upgrade, or another plugin's objective. Minecraft caps objective names at 16 chars,
    // so this stays well under: "mcmmo_" (6) + 8 hex = 14.
    static final String POWER_OBJECTIVE = "mcmmo_" + java.util.UUID.randomUUID()
            .toString().replace("-", "").substring(0, 8);

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

    public static final String DISPLAY_NAME = "powerLevel";
    public static ScoreboardLibrary scoreboardLibrary;

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    // Packet-based below-name power level tag (replaces the old Bukkit main-scoreboard objective)
    private static ObjectiveManager powerLevelObjectiveManager;
    private static ScoreboardObjective powerLevelObjective;

    /*
     * Initializes the static label maps for our scoreboards.
     *
     * Note: the packet ScoreboardLibrary is NOT loaded here. Loading it can fail on unsupported
     * server versions, and doing that inside a static initializer would turn the failure into an
     * unrecoverable ExceptionInInitializerError on first access to this class. Instead the library
     * is loaded explicitly from the main class via {@link #init()} during onEnable, guarded by the
     * scoreboards-enabled config, so it can fail gracefully.
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

    private static final List<String> dirtyPowerLevels = new ArrayList<>();

    /**
     * Loads the packet-based {@link ScoreboardLibrary}. Call this once from the main class during
     * onEnable (guarded by the scoreboards-enabled config).
     * <p>
     * If the running server version has no packet adapter, we fall back to a
     * {@link NoopScoreboardLibrary} as recommended by the library author: scoreboards simply won't
     * render, but nothing throws and the rest of the plugin keeps working.
     */
    public static void init() {
        if (scoreboardLibrary != null && !scoreboardLibrary.closed()) {
            return; // already initialized
        }

        try {
            scoreboardLibrary = ScoreboardLibrary.loadScoreboardLibrary(mcMMO.p);
        } catch (NoPacketAdapterAvailableException e) {
            scoreboardLibrary = new NoopScoreboardLibrary();
            mcMMO.p.getLogger().warning(
                    "Server version unsupported, scoreboard functionality will not be visible!");
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
        teardownPlayer(player);
        PLAYER_SCOREBOARDS.put(player.getName(), makeNewScoreboard(player));
        dirtyPowerLevels.add(player.getName());

        // Make the player see the below-name power level tag, if enabled
        if (mcMMO.p.getGeneralConfig().getPowerLevelTagsEnabled() && getPowerLevelObjective() != null) {
            powerLevelObjectiveManager.addPlayer(player);
        }
    }

    // Called by PlayerQuitEvent listener and OnPlayerTeleport under certain circumstances
    public static void teardownPlayer(Player player) {
        if (player == null) {
            return;
        }

        // Only remove the player from the shared power-level ObjectiveManager while they are still
        // online. scoreboard-library tracks players in a weak-key map and processes removals on an
        // async tick; if we enqueue a removal for an already-offline player, their Player object can
        // be garbage-collected before that tick runs, which makes the library's
        // ObjectiveManagerImpl.tick() throw an NPE (requireNonNull on the now-evicted map entry).
        // For offline players we let the library's weak-key map evict them on its own instead.
        if (powerLevelObjectiveManager != null && !powerLevelObjectiveManager.closed()
                && player.isOnline()) {
            powerLevelObjectiveManager.removePlayer(player);
            // Remove the player's score entry so it doesn't accumulate for offline players.
            // Without this, every player who ever joined adds a persistent entry to the scores map;
            // when a new player connects, the library sends the full map to them (potentially
            // thousands of entries) causing a large initial packet burst.
            if (powerLevelObjective != null) {
                powerLevelObjective.removeScore(player.getName());
            }
        }

        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.remove(player.getName());
        if (wrapper != null) {
            wrapper.close(); // cancels revertTask, cooldownTask, and updateTask internally
        }
    }

    // Called in onDisable()
    public static void teardownAll() {
        ImmutableList<Player> onlinePlayers = ImmutableList.copyOf(
                mcMMO.p.getServer().getOnlinePlayers());
        LogUtils.debug(mcMMO.p.getLogger(),
                "Tearing down scoreboards... (" + onlinePlayers.size() + ")");
        for (Player player : onlinePlayers) {
            teardownPlayer(player);
        }

        // The external plugin never closes the library; do it here so reload/disable doesn't
        // leak the packet listeners or registered players.
        if (powerLevelObjectiveManager != null && !powerLevelObjectiveManager.closed()) {
            powerLevelObjectiveManager.close();
        }
        powerLevelObjectiveManager = null;
        powerLevelObjective = null;

        if (scoreboardLibrary != null && !scoreboardLibrary.closed()) {
            scoreboardLibrary.close();
        }
        scoreboardLibrary = null;
    }

    // Called by ScoreboardWrapper when its Player logs off and an action tries to be performed
    public static void cleanup(ScoreboardWrapper wrapper) {
        PLAYER_SCOREBOARDS.remove(wrapper.playerName);
        wrapper.close(); // cancels revertTask, cooldownTask, and updateTask internally
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
            if ((wrapper.isSkillScoreboard() && wrapper.targetSkill == skill)
                    || (wrapper.isStatsScoreboard()) && wrapper.isBoardShown()) {
                wrapper.doSidebarUpdateSoon();
            }

            // Otherboards
            String playerName = player.getName();

            for (ScoreboardWrapper iWrapper : PLAYER_SCOREBOARDS.values()) {
                if (iWrapper.isStatsScoreboard() && playerName.equals(iWrapper.targetPlayer)
                        && wrapper.isBoardShown()) {
                    wrapper.doSidebarUpdateSoon();
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

        if (wrapper != null && wrapper.isSkillScoreboard() && wrapper.targetSkill == skill
                && wrapper.isBoardShown()) {
            wrapper.doSidebarUpdateSoon();
        }
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
        ScoreboardObjective mainObjective = getPowerLevelObjective();

        if (mainObjective == null) {
            return false; // indicates
        }

        for (String playerName : dirtyPowerLevels) {
            final McMMOPlayer mmoPlayer = UserManager.getPlayer(playerName);

            if (mmoPlayer == null) {
                continue;
            }

            int power = mmoPlayer.getPowerLevel();

            mainObjective.score(playerName, power);
        }

        dirtyPowerLevels.clear();
        return true;
    }

    /**
     * Gets or creates the packet-based below-name power level objective.
     * <p/>
     * If power levels are disabled, the objective manager is closed and null is returned.
     *
     * @return the power level objective, or null if disabled
     */
    public static @Nullable ScoreboardObjective getPowerLevelObjective() {
        if (!mcMMO.p.getGeneralConfig().getPowerLevelTagsEnabled()) {
            if (powerLevelObjectiveManager != null && !powerLevelObjectiveManager.closed()) {
                powerLevelObjectiveManager.close();
                LogUtils.debug(mcMMO.p.getLogger(),
                        "Removed leftover objects from Power Level Tags.");
            }
            powerLevelObjectiveManager = null;
            powerLevelObjective = null;
            return null;
        }

        if (scoreboardLibrary == null || scoreboardLibrary.closed()) {
            return null;
        }

        if (powerLevelObjectiveManager == null || powerLevelObjectiveManager.closed()) {
            powerLevelObjectiveManager = scoreboardLibrary.createObjectiveManager();
            powerLevelObjective = powerLevelObjectiveManager.create(POWER_OBJECTIVE);
            powerLevelObjective.value(LEGACY.deserialize(TAG_POWER_LEVEL));
            powerLevelObjectiveManager.display(ObjectiveDisplaySlot.belowName(), powerLevelObjective);

            // Show the tag to everyone currently online
            powerLevelObjectiveManager.addPlayers(
                    new ArrayList<>(mcMMO.p.getServer().getOnlinePlayers()));
        }

        return powerLevelObjective;
    }

    /**
     * @deprecated The packet-based sidebar no longer uses Bukkit's {@link
     * org.bukkit.scoreboard.ScoreboardManager}; this method has no internal callers and is kept
     * only as a thin pass-through for backwards compatibility.
     */
    @Deprecated
    public @Nullable
    static org.bukkit.scoreboard.ScoreboardManager getScoreboardManager() {
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
        return PLAYER_SCOREBOARDS.get(playerName).isBoardShown();
    }

    public static void clearBoard(String playerName) {
        PLAYER_SCOREBOARDS.get(playerName).tryRevertBoard();
    }

    public static void keepBoard(String playerName) {
        PLAYER_SCOREBOARDS.get(playerName).cancelRevert();
    }

    public static void setRevertTimer(String playerName, int seconds) {
        PLAYER_SCOREBOARDS.get(playerName)
                .showBoardAndScheduleRevert(seconds * Misc.TICK_CONVERSION_FACTOR);
    }

    public static boolean isPlayerBoardSetup(@NotNull String playerName) {
        return PLAYER_SCOREBOARDS.get(playerName) != null;
    }

    public static @NotNull ScoreboardWrapper makeNewScoreboard(Player player) {
        // The board is now a packet-based sidebar overlay, so there is no Bukkit Scoreboard to
        // create or swap. We still fire McMMOScoreboardMakeboardEvent so external plugins that
        // listened for board creation keep getting notified.
        //
        // Compatibility note: this event was designed around Bukkit Scoreboards (target/current
        // board). Since none exist here, we pass the player's current Bukkit scoreboard for both
        // arguments purely as a non-null placeholder. The event's board fields are NOT consumed by
        // the packet implementation - it is informational only, and setTargetBoard()/setTargetPlayer()
        // no longer influence which board is shown.
        McMMOScoreboardMakeboardEvent event = new McMMOScoreboardMakeboardEvent(
                player.getScoreboard(), player.getScoreboard(), player,
                ScoreboardEventReason.CREATING_NEW_SCOREBOARD);
        player.getServer().getPluginManager().callEvent(event);

        // Honour any player reassignment the event performed; the sidebar follows that player.
        return new ScoreboardWrapper(event.getTargetPlayer());
    }
}