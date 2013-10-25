package com.gmail.nossr50.util.scoreboards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.player.UserManager;
import com.google.common.collect.ImmutableMap;

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

    static final OfflinePlayer LABEL_POWER_LEVEL = getOfflinePlayer(POWER_LEVEL);
    static final OfflinePlayer LABEL_LEVEL = getOfflinePlayer(LocaleLoader.getString("Scoreboard.Misc.Level"));
    static final OfflinePlayer LABEL_CURRENT_XP = getOfflinePlayer(LocaleLoader.getString("Scoreboard.Misc.CurrentXP"));
    static final OfflinePlayer LABEL_REMAINING_XP = getOfflinePlayer(LocaleLoader.getString("Scoreboard.Misc.RemainingXP"));
    static final OfflinePlayer LABEL_ABILITY_COOLDOWN = getOfflinePlayer(LocaleLoader.getString("Scoreboard.Misc.Cooldown"));
    static final OfflinePlayer LABEL_OVERALL = getOfflinePlayer(LocaleLoader.getString("Scoreboard.Misc.Overall"));

    static final Map<SkillType, OfflinePlayer> skillLabels;
    static final Map<AbilityType, OfflinePlayer> abilityLabelsColored;
    static final Map<AbilityType, OfflinePlayer> abilityLabelsSkill;
    static {
        ImmutableMap.Builder<SkillType, OfflinePlayer> b = ImmutableMap.builder();
        ImmutableMap.Builder<AbilityType, OfflinePlayer> c = ImmutableMap.builder();
        ImmutableMap.Builder<AbilityType, OfflinePlayer> d = ImmutableMap.builder();
        if (Config.getInstance().getScoreboardRainbows()) {
            Random shuffler = new Random(Bukkit.getWorlds().get(0).getSeed());
            List<ChatColor> colors = Arrays.asList(
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
                    ChatColor.DARK_BLUE
            );
            Collections.shuffle(colors, shuffler);
            int i = 0;
            for (SkillType type : SkillType.values()) {
                // Include child skills
                b.put(type, getOfflinePlayer(colors.get(i) + type.getSkillName()));
                if (type.getAbility() != null) {
                    // the toString is the properly formatted verison for abilities
                    c.put(type.getAbility(), getOfflinePlayer(colors.get(i) + type.getAbility().getAbilityName()));
                    if (type == SkillType.MINING) {
                        c.put(AbilityType.BLAST_MINING, getOfflinePlayer(colors.get(i) + AbilityType.BLAST_MINING.getAbilityName()));
                    }
                }
                if (++i == colors.size()) i = 0;
            }
        }
        else {
            for (SkillType type : SkillType.values()) {
                // Include child skills
                b.put(type, getOfflinePlayer(ChatColor.GREEN + type.getSkillName()));
                if (type.getAbility() != null) {
                    // the toString is the properly formatted verison for abilities
                    c.put(type.getAbility(), getOfflinePlayerDots(ChatColor.AQUA + type.getAbility().getAbilityName()));
                    if (type == SkillType.MINING) {
                        c.put(AbilityType.BLAST_MINING, getOfflinePlayerDots(ChatColor.AQUA + AbilityType.BLAST_MINING.getAbilityName()));
                    }
                }
            }
        }

        for (AbilityType type : AbilityType.NORMAL_ABILITIES) {
            if (type == AbilityType.BLAST_MINING) {
                // Special-case: get a different color
                d.put(AbilityType.BLAST_MINING, getOfflinePlayerDots(ChatColor.BLUE + AbilityType.BLAST_MINING.getAbilityName()));
            }
            else {
                d.put(type, getOfflinePlayerDots(ChatColor.AQUA + type.getAbilityName()));
            }
        }

        skillLabels = b.build();
        abilityLabelsColored = c.build();
        abilityLabelsSkill = d.build();
    }

    private static List<String> dirtyPowerLevels = new ArrayList<String>();

    private static OfflinePlayer getOfflinePlayer(String name) {
        if (name.length() > 16) {
            name = name.substring(0, 16);
        }
        return Bukkit.getOfflinePlayer(name);
    }

    private static OfflinePlayer getOfflinePlayerDots(String name) {
        if (name.length() > 16) {
            name = name.substring(0, 16 - 2) + "..";
        }
        return Bukkit.getOfflinePlayer(name);
    }

    public enum SidebarType {
        NONE,
        SKILL_BOARD,
        STATS_BOARD,
        COOLDOWNS_BOARD,
        RANK_BOARD,
        TOP_BOARD;
    }

    // **** Listener call-ins **** //

    // Called by PlayerJoinEvent listener
    public static void setupPlayer(Player p) {
        PLAYER_SCOREBOARDS.put(p.getName(), ScoreboardWrapper.create(p));
        dirtyPowerLevels.add(p.getName());
    }

    // Called by PlayerQuitEvent listener
    public static void teardownPlayer(Player p) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.remove(p.getName());
        if (wrapper.revertTask != null) {
            wrapper.revertTask.cancel();
        }
    }

    // Called in onDisable()
    public static void teardownAll() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            teardownPlayer(p);
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
    public static void handleLevelUp(Player player, SkillType skill) {
        // Selfboards
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());
        if ((wrapper.isSkillScoreboard() && wrapper.targetSkill == skill) || (wrapper.isStatsScoreboard()) && wrapper.isBoardShown()) {
            wrapper.doSidebarUpdateSoon();
        }

        // Otherboards
        String playerName = player.getName();
        for (ScoreboardWrapper w : PLAYER_SCOREBOARDS.values()) {
            if (w.isStatsScoreboard() && playerName.equals(w.targetPlayer) && wrapper.isBoardShown()) {
                wrapper.doSidebarUpdateSoon();
            }
        }

        if (Config.getInstance().getPowerLevelTagsEnabled()) {
            dirtyPowerLevels.add(player.getName());
        }

        if (Config.getInstance().getSkillLevelUpBoard()) {
            enablePlayerSkillLevelUpScoreboard(player, skill);
        }
    }

    // Called by internal xp event listener
    public static void handleXp(Player player, SkillType skill) {
        // Selfboards
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());
        if (wrapper.isSkillScoreboard() && wrapper.targetSkill == skill && wrapper.isBoardShown()) {
            wrapper.doSidebarUpdateSoon();
        }
    }

    // Called by internal ability event listeners
    public static void cooldownUpdate(Player player, SkillType skill, int cooldownSeconds) {
        // Selfboards
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());
        if ((wrapper.isCooldownScoreboard() || wrapper.isSkillScoreboard() && wrapper.targetSkill == skill) && wrapper.isBoardShown()) {
            wrapper.doSidebarUpdateSoon();
        }
    }

    // **** Setup methods **** //

    public static void enablePlayerSkillScoreboard(Player player, SkillType skill) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        wrapper.setOldScoreboard();
        wrapper.setTypeSkill(skill);

        changeScoreboard(wrapper, Config.getInstance().getSkillScoreboardTime());
    }

    public static void enablePlayerSkillLevelUpScoreboard(Player player, SkillType skill) {
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

    public static void showPlayerRankScoreboard(Player bukkitPlayer, Map<SkillType, Integer> rank) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(bukkitPlayer.getName());

        wrapper.setOldScoreboard();
        wrapper.setTypeSelfRank();
        wrapper.acceptRankData(rank);

        changeScoreboard(wrapper, Config.getInstance().getRankScoreboardTime());
    }

    public static void showPlayerRankScoreboardOthers(Player bukkitPlayer, String targetName, Map<SkillType, Integer> rank) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(bukkitPlayer.getName());

        wrapper.setOldScoreboard();
        wrapper.setTypeInspectRank(targetName);
        wrapper.acceptRankData(rank);

        changeScoreboard(wrapper, Config.getInstance().getRankScoreboardTime());
    }

    public static void showTopScoreboard(Player player, SkillType skill, int pageNumber, List<PlayerStat> stats) {
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

        if (!dirtyPowerLevels.isEmpty())
            mcMMO.p.getLogger().info(dirtyPowerLevels.toString());
        for (String playerName : dirtyPowerLevels) {
            McMMOPlayer mcpl = UserManager.getPlayer(playerName);
            Player player = mcpl.getPlayer();
            int power = mcpl.getPowerLevel();

            mainObjective.getScore(player).setScore(power);
            for (ScoreboardWrapper wrapper : PLAYER_SCOREBOARDS.values()) {
                wrapper.updatePowerLevel(player, power);
            }
        }
        dirtyPowerLevels.clear();

        return true;
    }

    /**
     * Gets or creates the power level objective on the main scoreboard.
     * <p>
     * If power levels are disabled, the objective is deleted and null is
     * returned.
     *
     * @return the main scoreboard objective, or null if disabled
     */
    public static Objective getPowerLevelObjective() {
        if (!Config.getInstance().getPowerLevelTagsEnabled()) {
            Objective obj = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(POWER_OBJECTIVE);
            if (obj != null) {
                obj.unregister();
                mcMMO.p.debug("Removed leftover scoreboard objects from Power Level Tags.");
            }
            return null;
        }

        Objective powerObj = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(POWER_OBJECTIVE);
        if (powerObj == null) {
            powerObj = Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective(POWER_OBJECTIVE, "dummy");
            powerObj.setDisplayName(TAG_POWER_LEVEL);
            powerObj.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }
        return powerObj;
    }

    private static void changeScoreboard(ScoreboardWrapper wrapper, int displayTime) {
        if (displayTime == -1) {
            wrapper.showBoardWithNoRevert();
        }
        else {
            wrapper.showBoardAndScheduleRevert(displayTime * Misc.TICK_CONVERSION_FACTOR);
        }
    }

    public static void clearBoard(String playerName) {
        PLAYER_SCOREBOARDS.get(playerName).tryRevertBoard();
    }

    public static void keepBoard(String playerName) {
        if (Config.getInstance().getAllowKeepBoard()) {
            PLAYER_SCOREBOARDS.get(playerName).cancelRevert();
        }
    }

    public static void setRevertTimer(String playerName, int seconds) {
        PLAYER_SCOREBOARDS.get(playerName).showBoardAndScheduleRevert(seconds * Misc.TICK_CONVERSION_FACTOR);;
    }
}
