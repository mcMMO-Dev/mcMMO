package com.gmail.nossr50.util.scoreboards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.google.common.collect.ImmutableMap;

public class ScoreboardManager {
    static final Map<String, ScoreboardWrapper> PLAYER_SCOREBOARDS = new HashMap<String, ScoreboardWrapper>();
    static final List<String> SCOREBOARD_TASKS = new ArrayList<String>();

    // Package-private constants galore!

    static final String SIDEBAR_OBJECTIVE = "mcmmo_sidebar"; // not localized
    static final String POWER_OBJECTIVE   = "mcmmo_pwrlvl";

    static final String HEADER_STATS    = LocaleLoader.getString("Scoreboard.Header.PlayerStats");
    static final String HEADER_RANK     = LocaleLoader.getString("Scoreboard.Header.PlayerRank");
    static final String TAG_POWER_LEVEL = LocaleLoader.getString("Scoreboard.Header.PowerLevel");

    static final String POWER_LEVEL  = LocaleLoader.getString("Scoreboard.Misc.PowerLevel");
    static final String LEVEL        = LocaleLoader.getString("Scoreboard.Misc.Level");
    static final String CURRENT_XP   = LocaleLoader.getString("Scoreboard.Misc.CurrentXP");
    static final String REMAINING_XP = LocaleLoader.getString("Scoreboard.Misc.RemainingXP");
    static final String OVERALL      = LocaleLoader.getString("Scoreboard.Misc.Overall");

    static final OfflinePlayer labelLevel       = Bukkit.getOfflinePlayer(LEVEL);
    static final OfflinePlayer labelCurrentXp   = Bukkit.getOfflinePlayer(CURRENT_XP);
    static final OfflinePlayer labelRemainingXp = Bukkit.getOfflinePlayer(REMAINING_XP);
    static final OfflinePlayer labelPowerLevel  = Bukkit.getOfflinePlayer(POWER_LEVEL);
    static final OfflinePlayer labelOverall     = Bukkit.getOfflinePlayer(OVERALL);

    static final Map<SkillType, OfflinePlayer> skillLabels;

    static {
        ImmutableMap.Builder<SkillType, OfflinePlayer> b = ImmutableMap.builder();
        for (SkillType type : SkillType.values()) {
            // Include child skills
            b.put(type, Bukkit.getOfflinePlayer(SkillUtils.getSkillName(type)));
        }
        skillLabels = b.build();
    }

    // Listener call-ins

    public static void setupPlayer(Player p) {
        PLAYER_SCOREBOARDS.put(p.getName(), ScoreboardWrapper.create(p));
    }

    public static void teardownPlayer(Player p) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.remove(p.getName());
        if (wrapper.revertTask != null) {
            wrapper.revertTask.cancel();
        }
    }

    public static void cleanup(ScoreboardWrapper wrapper) {
        PLAYER_SCOREBOARDS.remove(wrapper.playerName);
        if (wrapper.revertTask != null) {
            wrapper.revertTask.cancel();
        }
    }

    public enum SidebarType {
        NONE        (false, false, false),
        SKILL_BOARD (true,  true,  false),
        STATS_BOARD (true,  false, false),
        RANK_BOARD  (false, false, true ),
        TOP_BOARD   (false, false, true ),
        ;

        private final boolean listenLevelUp;
        private final boolean listenXpUp;
        private final boolean listenTimer;

        private SidebarType(boolean a, boolean b, boolean c) {
            listenLevelUp = a;
            listenXpUp = b;
            listenTimer = c;
        }
    }

    public static void handleLevelUp(Player player, SkillType skill) {
        // Selfboards
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());
        if (wrapper.sidebarType == SidebarType.SKILL_BOARD && wrapper.targetSkill == skill && wrapper.isBoardShown()) {
            wrapper.doSidebarUpdateSoon();
        }
        else if (wrapper.sidebarType == SidebarType.STATS_BOARD && wrapper.isBoardShown()) {
            wrapper.doSidebarUpdateSoon();
        }

        // Otherboards
        String playerName = player.getName();
        for (ScoreboardWrapper w : PLAYER_SCOREBOARDS.values()) {
            if (w.sidebarType == SidebarType.STATS_BOARD && w.targetPlayer == playerName && wrapper.isBoardShown()) {
                wrapper.doSidebarUpdateSoon();
            }
        }
    }

    public static void handleXp(Player player, SkillType skill) {
        // Selfboards
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());
        if (wrapper.sidebarType == SidebarType.SKILL_BOARD && wrapper.targetSkill == skill && wrapper.isBoardShown()) {
            wrapper.doSidebarUpdateSoon();
        }
    }

    public static void enablePowerLevelDisplay(Player player) {
        if (!Config.getInstance().getPowerLevelsEnabled()) {
            return;
        }

        Scoreboard scoreboard = player.getScoreboard();
        Objective objective;

        if (scoreboard.getObjective(DisplaySlot.BELOW_NAME) == null) {
            objective = scoreboard.registerNewObjective(TAG_POWER_LEVEL, "dummy");

            objective.getScore(player).setScore(UserManager.getPlayer(player).getPowerLevel());
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }
        else {
            objective = scoreboard.getObjective(TAG_POWER_LEVEL);

            if (scoreboard.getObjective(TAG_POWER_LEVEL) != null) {
                objective.getScore(player).setScore(UserManager.getPlayer(player).getPowerLevel());
            }
            else {
                mcMMO.p.debug("Another plugin is using this scoreboard slot, so power levels cannot be enabled."); //TODO: Locale
            }
        }
    }

    public static void enablePlayerSkillScoreboard(McMMOPlayer mcMMOPlayer, SkillType skill) {
        Player bukkitPlayer = mcMMOPlayer.getPlayer();
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(bukkitPlayer.getName());

        wrapper.setOldScoreboard();
        wrapper.setTypeSkill(skill);

        changeScoreboard(wrapper, Config.getInstance().getSkillScoreboardTime());
    }

    public static void enablePlayerStatsScoreboard(McMMOPlayer mcMMOPlayer) {
        Player bukkitPlayer = mcMMOPlayer.getPlayer();
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(bukkitPlayer.getName());

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

    private static void changeScoreboard(ScoreboardWrapper wrapper, int displayTime) {
        wrapper.showBoardAndScheduleRevert(displayTime * Misc.TICK_CONVERSION_FACTOR);
    }

    public static void clearBoard(String playerName) {
        PLAYER_SCOREBOARDS.get(playerName).tryRevertBoard();
    }

    public static void keepBoard(String playerName) {
        if (Config.getInstance().getAllowKeepBoard()) {
            PLAYER_SCOREBOARDS.get(playerName).cancelRevert();
        }
    }
}
