package com.gmail.nossr50.util.scoreboards;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.player.UserManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.util.*;

/**
 * Manages the Scoreboards used to display a variety of mcMMO related information to the player
 */
public class ScoreboardManager {
    static final Map<String, ScoreboardWrapper> PLAYER_SCOREBOARDS = new HashMap<String, ScoreboardWrapper>();

    // do not localize; these are internal identifiers
    static final String SIDEBAR_OBJECTIVE = "mcmmo_sidebar";
    static final String POWER_OBJECTIVE = "mcmmo_pwrlvl";

    static final String HEADER_STATS = LocaleLoader.getString("Scoreboard.Header.PlayerStats");
    static final String HEADER_COOLDOWNS = LocaleLoader.getString("Scoreboard.Header.PlayerCooldowns");
    static final String HEADER_RANK = LocaleLoader.getString("Scoreboard.Header.PlayerRank");
    static final String TAG_POWER_LEVEL = LocaleLoader.getString("Scoreboard.Header.PowerLevel");

    static final String POWER_LEVEL = LocaleLoader.getString("Scoreboard.Misc.PowerLevel");

    static final String LABEL_POWER_LEVEL = POWER_LEVEL;
    static final String LABEL_LEVEL = LocaleLoader.getString("Scoreboard.Misc.Level");
    static final String LABEL_CURRENT_XP = LocaleLoader.getString("Scoreboard.Misc.CurrentXP");
    static final String LABEL_REMAINING_XP = LocaleLoader.getString("Scoreboard.Misc.RemainingXP");
    static final String LABEL_ABILITY_COOLDOWN = LocaleLoader.getString("Scoreboard.Misc.Cooldown");
    static final String LABEL_OVERALL = LocaleLoader.getString("Scoreboard.Misc.Overall");

    static final Map<PrimarySkillType, String>   skillLabels;
    static final Map<SuperAbilityType, String> abilityLabelsColored;
    static final Map<SuperAbilityType, String> abilityLabelsSkill;

    /*
     * Initializes the static properties of this class
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
        if (Config.getInstance().getScoreboardRainbows()) {
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
            for (PrimarySkillType type : PrimarySkillType.values()) {
                // Include child skills
                skillLabelBuilder.put(type, getShortenedName(colors.get(i) + type.getName(), false));

                if (type.getAbility() != null) {
                    abilityLabelBuilder.put(type.getAbility(), getShortenedName(colors.get(i) + type.getAbility().getName()));

                    if (type == PrimarySkillType.MINING) {
                        abilityLabelBuilder.put(SuperAbilityType.BLAST_MINING, getShortenedName(colors.get(i) + SuperAbilityType.BLAST_MINING.getName()));
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
            for (PrimarySkillType type : PrimarySkillType.values()) {
                // Include child skills
                skillLabelBuilder.put(type, getShortenedName(ChatColor.GREEN + type.getName()));

                if (type.getAbility() != null) {
                    abilityLabelBuilder.put(type.getAbility(), formatAbility(type.getAbility().getName()));

                    if (type == PrimarySkillType.MINING) {
                        abilityLabelBuilder.put(SuperAbilityType.BLAST_MINING, formatAbility(SuperAbilityType.BLAST_MINING.getName()));
                    }
                }
            }
        }

        for (SuperAbilityType type : SuperAbilityType.values()) {
            abilityLabelSkillBuilder.put(type, formatAbility((type == SuperAbilityType.BLAST_MINING ? ChatColor.BLUE : ChatColor.AQUA), type.getName()));
        }

        skillLabels = skillLabelBuilder.build();
        abilityLabelsColored = abilityLabelBuilder.build();
        abilityLabelsSkill = abilityLabelSkillBuilder.build();
    }

    private static List<String> dirtyPowerLevels = new ArrayList<String>();

    public enum SidebarType {
        NONE,
        SKILL_BOARD,
        STATS_BOARD,
        COOLDOWNS_BOARD,
        RANK_BOARD,
        TOP_BOARD;
    }

    private static String formatAbility(String abilityName) {
        return formatAbility(ChatColor.AQUA, abilityName);
    }

    private static String formatAbility(ChatColor color, String abilityName) {
        if (Config.getInstance().getShowAbilityNames()) {
            return getShortenedName(color + abilityName);
        }
        else {
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
        PLAYER_SCOREBOARDS.put(player.getName(), ScoreboardWrapper.create(player));
        dirtyPowerLevels.add(player.getName());
    }

    // Called by PlayerQuitEvent listener and OnPlayerTeleport under certain circumstances
    public static void teardownPlayer(Player player) {
        //Hacky world blacklist fix
        if(player.isOnline() && player.isValid())
            if(Bukkit.getServer().getScoreboardManager() != null)
                player.setScoreboard(Bukkit.getServer().getScoreboardManager().getMainScoreboard());

        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.remove(player.getName());

        if (wrapper != null && wrapper.revertTask != null) {
            wrapper.revertTask.cancel();
        }
    }

    // Called in onDisable()
    public static void teardownAll() {
        ImmutableList<Player> onlinePlayers = ImmutableList.copyOf(mcMMO.p.getServer().getOnlinePlayers());
        mcMMO.p.debug("Tearing down scoreboards... (" + onlinePlayers.size() + ")");
        for (Player player : onlinePlayers) {
            teardownPlayer(player);
        }
    }

    // Called by ScoreboardWrapper when its Player logs off and an action tries to be performed
    public static void cleanup(ScoreboardWrapper wrapper) {
        PLAYER_SCOREBOARDS.remove(wrapper.playerName);

        if (wrapper.revertTask != null) {
            wrapper.revertTask.cancel();
        }
    }

    // Called by internal level-up event listener
    public static void handleLevelUp(Player player, PrimarySkillType skill) {
        // Selfboards
        ScoreboardWrapper selfboardWrapper = PLAYER_SCOREBOARDS.get(player.getName());

        if ((selfboardWrapper.isSkillScoreboard() && selfboardWrapper.targetSkill == skill) || (selfboardWrapper.isStatsScoreboard()) && selfboardWrapper.isBoardShown()) {
            selfboardWrapper.doSidebarUpdateSoon();
        }

        // Otherboards
        String playerName = player.getName();

        for (ScoreboardWrapper wrapper : PLAYER_SCOREBOARDS.values()) {
            if (wrapper.isStatsScoreboard() && playerName.equals(wrapper.targetPlayer) && selfboardWrapper.isBoardShown()) {
                selfboardWrapper.doSidebarUpdateSoon();
            }
        }

        if (Config.getInstance().getPowerLevelTagsEnabled() && !dirtyPowerLevels.contains(playerName)) {
            dirtyPowerLevels.add(playerName);
        }

        if (Config.getInstance().getSkillLevelUpBoard()) {
            enablePlayerSkillLevelUpScoreboard(player, skill);
        }
    }

    // Called by internal xp event listener
    public static void handleXp(Player player, PrimarySkillType skill) {
        // Selfboards
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        if (wrapper != null && wrapper.isSkillScoreboard() && wrapper.targetSkill == skill && wrapper.isBoardShown()) {
            wrapper.doSidebarUpdateSoon();
        }
    }

    // Called by internal ability event listeners
    public static void cooldownUpdate(Player player, PrimarySkillType skill) {
        // Selfboards
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        if (wrapper != null && (wrapper.isCooldownScoreboard() || wrapper.isSkillScoreboard() && wrapper.targetSkill == skill) && wrapper.isBoardShown()) {
            wrapper.doSidebarUpdateSoon();
        }
    }

    // **** Setup methods **** //

    public static void enablePlayerSkillScoreboard(Player player, PrimarySkillType skill) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        wrapper.setOldScoreboard();
        wrapper.setTypeSkill(skill);

        changeScoreboard(wrapper, Config.getInstance().getSkillScoreboardTime());
    }

    public static void enablePlayerSkillLevelUpScoreboard(Player player, PrimarySkillType skill) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        // Do NOT run if already shown
        if (wrapper.isBoardShown()) {
            return;
        }

        wrapper.setOldScoreboard();
        wrapper.setTypeSkill(skill);

        changeScoreboard(wrapper, Config.getInstance().getSkillLevelUpTime());
    }

    public static void enablePlayerStatsScoreboard(Player player) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        wrapper.setOldScoreboard();
        wrapper.setTypeSelfStats();

        changeScoreboard(wrapper, Config.getInstance().getStatsScoreboardTime());
    }

    public static void enablePlayerInspectScoreboard(Player player, PlayerProfile targetProfile) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        wrapper.setOldScoreboard();
        wrapper.setTypeInspectStats(targetProfile);

        changeScoreboard(wrapper, Config.getInstance().getInspectScoreboardTime());
    }

    public static void enablePlayerCooldownScoreboard(Player player) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        wrapper.setOldScoreboard();
        wrapper.setTypeCooldowns();

        changeScoreboard(wrapper, Config.getInstance().getCooldownScoreboardTime());
    }

    public static void showPlayerRankScoreboard(Player player, Map<PrimarySkillType, Integer> rank) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        wrapper.setOldScoreboard();
        wrapper.setTypeSelfRank();
        wrapper.acceptRankData(rank);

        changeScoreboard(wrapper, Config.getInstance().getRankScoreboardTime());
    }

    public static void showPlayerRankScoreboardOthers(Player player, String targetName, Map<PrimarySkillType, Integer> rank) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        wrapper.setOldScoreboard();
        wrapper.setTypeInspectRank(targetName);
        wrapper.acceptRankData(rank);

        changeScoreboard(wrapper, Config.getInstance().getRankScoreboardTime());
    }

    public static void showTopScoreboard(Player player, PrimarySkillType skill, int pageNumber, List<PlayerStat> stats) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        wrapper.setOldScoreboard();
        wrapper.setTypeTop(skill, pageNumber);
        wrapper.acceptLeaderboardData(stats);

        changeScoreboard(wrapper, Config.getInstance().getTopScoreboardTime());
    }

    public static void showTopPowerScoreboard(Player player, int pageNumber, List<PlayerStat> stats) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        wrapper.setOldScoreboard();
        wrapper.setTypeTopPower(pageNumber);
        wrapper.acceptLeaderboardData(stats);

        changeScoreboard(wrapper, Config.getInstance().getTopScoreboardTime());
    }

    // **** Helper methods **** //

    /**
     * @return false if power levels are disabled
     */
    public static boolean powerLevelHeartbeat() {
        Objective mainObjective = getPowerLevelObjective();

        if (mainObjective == null) {
            return false; // indicates
        }

        for (String playerName : dirtyPowerLevels) {
            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(playerName);

            if (mcMMOPlayer == null) {
                continue;
            }

            Player player = mcMMOPlayer.getPlayer();
            int power = mcMMOPlayer.getPowerLevel();

            mainObjective.getScore(playerName).setScore(power);

            for (ScoreboardWrapper wrapper : PLAYER_SCOREBOARDS.values()) {
                wrapper.updatePowerLevel(player, power);
            }
        }

        dirtyPowerLevels.clear();
        return true;
    }

    /**
     * Gets or creates the power level objective on the main targetBoard.
     * <p/>
     * If power levels are disabled, the objective is deleted and null is
     * returned.
     *
     * @return the main targetBoard objective, or null if disabled
     */
    public static Objective getPowerLevelObjective() {
        if (!Config.getInstance().getPowerLevelTagsEnabled()) {
            Objective objective = mcMMO.p.getServer().getScoreboardManager().getMainScoreboard().getObjective(POWER_OBJECTIVE);

            if (objective != null) {
                objective.unregister();
                mcMMO.p.debug("Removed leftover targetBoard objects from Power Level Tags.");
            }

            return null;
        }

        Objective powerObjective = mcMMO.p.getServer().getScoreboardManager().getMainScoreboard().getObjective(POWER_OBJECTIVE);

        if (powerObjective == null) {
            powerObjective = mcMMO.p.getServer().getScoreboardManager().getMainScoreboard().registerNewObjective(POWER_OBJECTIVE, "dummy");
            powerObjective.setDisplayName(TAG_POWER_LEVEL);
            powerObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }

        return powerObjective;
    }

    private static void changeScoreboard(ScoreboardWrapper wrapper, int displayTime) {
        if (displayTime == -1) {
            wrapper.showBoardWithNoRevert();
        }
        else {
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
        PLAYER_SCOREBOARDS.get(playerName).showBoardAndScheduleRevert(seconds * Misc.TICK_CONVERSION_FACTOR);
    }
}
