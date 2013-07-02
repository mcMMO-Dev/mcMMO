package com.gmail.nossr50.util.scoreboards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Server;
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
import com.gmail.nossr50.runnables.scoreboards.ScoreboardChangeTask;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;

public class ScoreboardManager {
    private static final Map<String, Scoreboard> PLAYER_SCOREBOARDS = new HashMap<String, Scoreboard>();
    private static final Scoreboard GLOBAL_STATS_SCOREBOARD = mcMMO.p.getServer().getScoreboardManager().getNewScoreboard();

    private final static String PLAYER_STATS_HEADER   = LocaleLoader.getString("Scoreboard.Header.PlayerStats");
    private final static String PLAYER_RANK_HEADER    = LocaleLoader.getString("Scoreboard.Header.PlayerRank");
    private final static String PLAYER_INSPECT_HEADER = LocaleLoader.getString("Scoreboard.Header.PlayerInspect");
    private final static String POWER_LEVEL_HEADER    = LocaleLoader.getString("Scoreboard.Header.PowerLevel");

    private final static String POWER_LEVEL  = LocaleLoader.getString("Scoreboard.Misc.PowerLevel");
    private final static String LEVEL        = LocaleLoader.getString("Scoreboard.Misc.Level");
    private final static String CURRENT_XP   = LocaleLoader.getString("Scoreboard.Misc.CurrentXP");
    private final static String REMAINING_XP = LocaleLoader.getString("Scoreboard.Misc.RemainingXP");
    private final static String OVERALL      = LocaleLoader.getString("Scoreboard.Misc.Overall");

    private final static List<String> SCOREBOARD_TASKS = new ArrayList<String>();

    public static void setupPlayer(Player p) {
        PLAYER_SCOREBOARDS.put(p.getName(), mcMMO.p.getServer().getScoreboardManager().getNewScoreboard());
    }

    public static void teardownPlayer(Player p) {
        PLAYER_SCOREBOARDS.put(p.getName(), mcMMO.p.getServer().getScoreboardManager().getNewScoreboard());
    }

    public static void enablePowerLevelDisplay(Player player) {
        if (!Config.getInstance().getPowerLevelsEnabled()) {
            return;
        }

        Scoreboard scoreboard = player.getScoreboard();
        Objective objective;

        if (scoreboard.getObjective(DisplaySlot.BELOW_NAME) == null) {
            objective = scoreboard.registerNewObjective(POWER_LEVEL_HEADER, "dummy");

            objective.getScore(player).setScore(UserManager.getPlayer(player).getPowerLevel());
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }
        else {
            objective = scoreboard.getObjective(POWER_LEVEL_HEADER);

            if (scoreboard.getObjective(POWER_LEVEL_HEADER) != null) {
                objective.getScore(player).setScore(UserManager.getPlayer(player).getPowerLevel());
            }
            else {
                mcMMO.p.debug("Another plugin is using this scoreboard slot, so power levels cannot be enabled."); //TODO: Locale
            }
        }
    }

    public static void enablePlayerSkillScoreboard(McMMOPlayer mcMMOPlayer, SkillType skill) {
        Player player = mcMMOPlayer.getPlayer();
        Scoreboard oldScoreboard = player.getScoreboard();
        Scoreboard newScoreboard = PLAYER_SCOREBOARDS.get(player.getName());
        Objective objective = newScoreboard.getObjective(SkillUtils.getSkillName(skill));

        if (objective == null) {
            objective = newScoreboard.registerNewObjective(SkillUtils.getSkillName(skill), "dummy");
        }

        updatePlayerSkillScores(mcMMOPlayer.getProfile(), skill, objective);
        changeScoreboard(player, oldScoreboard, newScoreboard, Config.getInstance().getSkillScoreboardTime());
    }

    public static void enablePlayerStatsScoreboard(McMMOPlayer mcMMOPlayer) {
        Player player = mcMMOPlayer.getPlayer();
        Scoreboard oldScoreboard = player.getScoreboard();
        Scoreboard newScoreboard = PLAYER_SCOREBOARDS.get(player.getName());
        Objective objective = newScoreboard.getObjective(PLAYER_STATS_HEADER);

        if (objective == null) {
            objective = newScoreboard.registerNewObjective(PLAYER_STATS_HEADER, "dummy");
        }

        updatePlayerStatsScores(mcMMOPlayer, objective);
        changeScoreboard(player, oldScoreboard, newScoreboard, Config.getInstance().getMcstatsScoreboardTime());
    }

    public static void enablePlayerRankScoreboard(Player player) {
        Scoreboard oldScoreboard = player.getScoreboard();
        Scoreboard newScoreboard = PLAYER_SCOREBOARDS.get(player.getName());
        Objective objective = newScoreboard.getObjective(PLAYER_RANK_HEADER);

        if (objective == null) {
            objective = newScoreboard.registerNewObjective(PLAYER_RANK_HEADER, "dummy");
        }

        updatePlayerRankScores(player, objective);
        changeScoreboard(player, oldScoreboard, newScoreboard, Config.getInstance().getMcrankScoreboardTime());
    }

    public static void enablePlayerRankScoreboardOthers(Player player, String targetName) {
        Scoreboard oldScoreboard = player.getScoreboard();
        Scoreboard newScoreboard = PLAYER_SCOREBOARDS.get(player.getName());
        Objective objective = newScoreboard.getObjective(PLAYER_RANK_HEADER);

        if (objective == null) {
            objective = newScoreboard.registerNewObjective(PLAYER_RANK_HEADER, "dummy");
        }

        updatePlayerRankOthersScores(targetName, objective);
        changeScoreboard(player, oldScoreboard, newScoreboard, Config.getInstance().getMcrankScoreboardTime());
    }

    public static void enablePlayerInspectScoreboardOnline(Player player, McMMOPlayer mcMMOTarget) {
        Scoreboard oldScoreboard = player.getScoreboard();
        Scoreboard newScoreboard = PLAYER_SCOREBOARDS.get(player.getName());
        Objective objective = newScoreboard.getObjective(PLAYER_INSPECT_HEADER);

        if (objective == null) {
            objective = newScoreboard.registerNewObjective(PLAYER_INSPECT_HEADER, "dummy");
        }

        updatePlayerInspectOnlineScores(mcMMOTarget, objective);
        changeScoreboard(player, oldScoreboard, newScoreboard, Config.getInstance().getInspectScoreboardTime());
    }

    public static void enablePlayerInspectScoreboardOffline(Player player, PlayerProfile targetProfile) {
        Scoreboard oldScoreboard = player.getScoreboard();
        Scoreboard newScoreboard = PLAYER_SCOREBOARDS.get(player.getName());
        Objective objective = newScoreboard.getObjective(PLAYER_INSPECT_HEADER);

        if (objective == null) {
            objective = newScoreboard.registerNewObjective(PLAYER_INSPECT_HEADER, "dummy");
        }

        updatePlayerInspectOfflineScores(targetProfile, objective);
        changeScoreboard(player, oldScoreboard, newScoreboard, Config.getInstance().getInspectScoreboardTime());
    }

    public static void enableGlobalStatsScoreboard(Player player, String skillName, int pageNumber) {
        Objective oldObjective = GLOBAL_STATS_SCOREBOARD.getObjective(skillName);
        Scoreboard oldScoreboard = player.getScoreboard();

        if (oldObjective != null) {
            oldObjective.unregister();
        }

        Objective newObjective = GLOBAL_STATS_SCOREBOARD.registerNewObjective(skillName, "dummy");
        newObjective.setDisplayName(ChatColor.GOLD + (skillName.equalsIgnoreCase("all") ? POWER_LEVEL : SkillUtils.getSkillName(SkillType.getSkill(skillName))));

        updateGlobalStatsScores(player, newObjective, skillName, pageNumber);
        changeScoreboard(player, oldScoreboard, GLOBAL_STATS_SCOREBOARD, Config.getInstance().getMctopScoreboardTime());
    }

    private static void updatePlayerSkillScores(PlayerProfile profile, SkillType skill, Objective objective) {
        Server server = mcMMO.p.getServer();
        int currentXP = profile.getSkillXpLevel(skill);

        objective.getScore(server.getOfflinePlayer(LEVEL)).setScore(profile.getSkillLevel(skill));
        objective.getScore(server.getOfflinePlayer(CURRENT_XP)).setScore(currentXP);
        objective.getScore(server.getOfflinePlayer(REMAINING_XP)).setScore(profile.getXpToLevel(skill) - currentXP);

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    private static void updatePlayerStatsScores(McMMOPlayer mcMMOPlayer, Objective objective) {
        Player player = mcMMOPlayer.getPlayer();
        PlayerProfile profile = mcMMOPlayer.getProfile();
        Server server = mcMMO.p.getServer();

        for (SkillType skill : SkillType.nonChildSkills()) {
            if (!Permissions.skillEnabled(player, skill)) {
                continue;
            }

            objective.getScore(server.getOfflinePlayer(SkillUtils.getSkillName(skill))).setScore(profile.getSkillLevel(skill));
        }

        objective.getScore(server.getOfflinePlayer(ChatColor.GOLD + POWER_LEVEL)).setScore(mcMMOPlayer.getPowerLevel());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    private static void updatePlayerRankScores(Player player, Objective objective) {
        String playerName = player.getName();
        Server server = mcMMO.p.getServer();
        Integer rank;

        Map<String, Integer> skills = mcMMO.getDatabaseManager().readRank(playerName);

        for (SkillType skill : SkillType.nonChildSkills()) {
            if (!Permissions.skillEnabled(player, skill)) {
                continue;
            }

            rank = skills.get(skill.name());

            if (rank != null) {
                objective.getScore(server.getOfflinePlayer(SkillUtils.getSkillName(skill))).setScore(rank);
            }
        }

        rank = skills.get("ALL");

        if (rank != null) {
            objective.getScore(server.getOfflinePlayer(ChatColor.GOLD + OVERALL)).setScore(rank);
        }

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    private static void updatePlayerRankOthersScores(String targetName, Objective objective) {
        Server server = mcMMO.p.getServer();
        Integer rank;

        Map<String, Integer> skills = mcMMO.getDatabaseManager().readRank(targetName);

        for (SkillType skill : SkillType.nonChildSkills()) {
            rank = skills.get(skill.name());

            if (rank != null) {
                objective.getScore(server.getOfflinePlayer(SkillUtils.getSkillName(skill))).setScore(rank);
            }
        }

        rank = skills.get("ALL");

        if (rank != null) {
            objective.getScore(server.getOfflinePlayer(ChatColor.GOLD + OVERALL)).setScore(rank);
        }

        objective.setDisplayName(PLAYER_RANK_HEADER + ": " + targetName);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    private static void updatePlayerInspectOnlineScores(McMMOPlayer mcMMOTarget, Objective objective) {
        Player target = mcMMOTarget.getPlayer();
        PlayerProfile profile = mcMMOTarget.getProfile();
        Server server = mcMMO.p.getServer();
        int powerLevel = 0;
        int skillLevel;

        for (SkillType skill : SkillType.nonChildSkills()) {
            if (!Permissions.skillEnabled(target, skill)) {
                continue;
            }

            skillLevel = profile.getSkillLevel(skill);
            objective.getScore(server.getOfflinePlayer(SkillUtils.getSkillName(skill))).setScore(skillLevel);
            powerLevel += skillLevel;
        }

        objective.getScore(server.getOfflinePlayer(ChatColor.GOLD + POWER_LEVEL)).setScore(powerLevel);
        objective.setDisplayName(PLAYER_INSPECT_HEADER + target.getName());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    private static void updatePlayerInspectOfflineScores(PlayerProfile targetProfile, Objective objective) {
        Server server = mcMMO.p.getServer();
        int powerLevel = 0;
        int skillLevel;

        for (SkillType skill : SkillType.nonChildSkills()) {
            skillLevel = targetProfile.getSkillLevel(skill);
            objective.getScore(server.getOfflinePlayer(SkillUtils.getSkillName(skill))).setScore(skillLevel);
            powerLevel += skillLevel;
        }

        objective.getScore(server.getOfflinePlayer(ChatColor.GOLD + POWER_LEVEL)).setScore(powerLevel);
        objective.setDisplayName(PLAYER_INSPECT_HEADER + targetProfile.getPlayerName());
    }

    private static void updateGlobalStatsScores(Player player, Objective objective, String skillName, int pageNumber) {
        int position = (pageNumber * 15) - 14;
        String startPosition = ((position < 10) ? "0" : "") + String.valueOf(position);
        String endPosition = String.valueOf(position + 14);
        Server server = mcMMO.p.getServer();

        for (PlayerStat stat : mcMMO.getDatabaseManager().readLeaderboard(skillName, pageNumber, 15)) {
            String playerName = stat.name;
            playerName = (playerName.equals(player.getName()) ? ChatColor.GOLD : "") + playerName;

            if (playerName.length() > 16) {
                playerName = playerName.substring(0, 16);
            }

            objective.getScore(server.getOfflinePlayer(playerName)).setScore(stat.statVal);
        }

        objective.setDisplayName(objective.getDisplayName() + " (" + startPosition + " - " + endPosition + ")");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    private static void changeScoreboard(Player player, Scoreboard oldScoreboard, Scoreboard newScoreboard, int displayTime) {
        if (oldScoreboard != newScoreboard) {
            String playerName = player.getName();

            player.setScoreboard(newScoreboard);
            enablePowerLevelDisplay(player);

            if (displayTime != -1 && !SCOREBOARD_TASKS.contains(playerName)) {
                new ScoreboardChangeTask(player, oldScoreboard).runTaskLater(mcMMO.p, displayTime * Misc.TICK_CONVERSION_FACTOR);
                SCOREBOARD_TASKS.add(playerName);
            }
        }
    }

    public static void clearPendingTask(String playerName) {
        SCOREBOARD_TASKS.remove(playerName);
    }
}
