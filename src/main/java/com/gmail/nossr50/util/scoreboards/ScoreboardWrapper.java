package com.gmail.nossr50.util.scoreboards;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.runnables.scoreboards.ScoreboardChangeTask;
import com.gmail.nossr50.runnables.scoreboards.ScoreboardQuickUpdate;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager.SidebarType;
import com.gmail.nossr50.util.skills.SkillUtils;

public class ScoreboardWrapper {
    // Initialization variables
    public final String playerName;
    public final Scoreboard board;
    public final Object oldBoardLock = new Object();
    public boolean tippedKeep = false;
    public boolean tippedClear = false;

    // Internal usage variables (should exist)
    public SidebarType sidebarType;
    public Objective sidebarObj;
    public Objective powerObj;

    // Parameter variables (May be null / invalid)
    public Scoreboard oldBoard = null;
    public String targetPlayer = null;
    public SkillType targetSkill = null;
    public PlayerProfile targetProfile = null;
    public int leaderboardPage = -1;

    /**
     * Is set to null when reverting has been disabled or is complete.
     */
    public BukkitTask revertTask;
    /**
     * Set to null when appropriate to schedule again.
     */
    public BukkitTask updateTask;

    private ScoreboardWrapper(String playerName, Scoreboard s) {
        this.playerName = playerName;
        board = s;
        sidebarType = SidebarType.NONE;
        sidebarObj = board.registerNewObjective(ScoreboardManager.SIDEBAR_OBJECTIVE, "dummy");
        powerObj = board.registerNewObjective(ScoreboardManager.POWER_OBJECTIVE, "dummy");
    }

    public static ScoreboardWrapper create(Player p) {
        return new ScoreboardWrapper(p.getName(), mcMMO.p.getServer().getScoreboardManager().getNewScoreboard());
    }

    public void doSidebarUpdateSoon() {
        if (updateTask == null) {
            updateTask = new ScoreboardQuickUpdate(this).runTaskLater(mcMMO.p, 2L); // Wait two tick, to avoid task floods
        }
    }

    /**
     * Set the old scoreboard, for use in reverting.
     */
    public void setOldScoreboard() {
        Player player = Bukkit.getPlayerExact(playerName);
        if (player == null) {
            ScoreboardManager.cleanup(this);
            return;
        }

        Scoreboard old = player.getScoreboard();
        if (old == board) { // Already displaying it
            if (oldBoard == null) {
                // Failsafe value - we're already displaying our board, but we don't have the one we should revert to
                oldBoard = Bukkit.getScoreboardManager().getMainScoreboard();
            }
            else {
                // Do nothing, we already have a prev board
            }
        }
        else {
            oldBoard = old;
        }
    }

    public void showBoardWithNoRevert() {
        Player player = Bukkit.getPlayerExact(playerName);
        if (player == null) {
            ScoreboardManager.cleanup(this);
            return;
        }

        if (revertTask != null) {
            revertTask.cancel();
        }
        player.setScoreboard(board);
        revertTask = null;
    }

    public void showBoardAndScheduleRevert(int ticks) {
        Player player = Bukkit.getPlayerExact(playerName);
        if (player == null) {
            ScoreboardManager.cleanup(this);
            return;
        }

        if (revertTask != null) {
            revertTask.cancel();
        }
        player.setScoreboard(board);
        revertTask = new ScoreboardChangeTask(this).runTaskLater(mcMMO.p, ticks);

        if (!tippedKeep) {
            tippedKeep = true;
            player.sendMessage(LocaleLoader.getString("Scoreboard.Tip.Mcboard.Keep"));
        }
        else if (!tippedClear) {
            tippedClear = true;
            player.sendMessage(LocaleLoader.getString("Scoreboard.Tip.Mcboard.Clear"));
        }
    }

    public void tryRevertBoard() {
        Player player = Bukkit.getPlayerExact(playerName);
        if (player == null) {
            ScoreboardManager.cleanup(this);
            return;
        }

        if (oldBoard != null) {
            if (player.getScoreboard() == board) {
                player.setScoreboard(oldBoard);
                oldBoard = null;
            }
            else {
                mcMMO.p.debug("Not reverting scoreboard for " + playerName + " - scoreboard was changed by another plugin (Consider disabling the mcMMO scoreboards if you don't want them!)");
            }
        }
        else {
            // Was already reverted
        }

        if (revertTask != null) {
            revertTask.cancel();
            revertTask = null;
        }
    }

    public boolean isBoardShown() {
        Player player = Bukkit.getPlayerExact(playerName);
        if (player == null) {
            ScoreboardManager.cleanup(this);
            return false;
        }

        return player.getScoreboard() == board;
    }

    public void cancelRevert() {
        if (revertTask != null) {
            revertTask.cancel();
        }
        revertTask = null;
    }

    // Board Type Changing 'API' methods

    public void setTypeNone() {
        this.sidebarType = SidebarType.NONE;

        targetPlayer = null;
        targetSkill = null;
        targetProfile = null;
        leaderboardPage = -1;

        loadObjective("");
    }

    public void setTypeSkill(SkillType skill) {
        this.sidebarType = SidebarType.SKILL_BOARD;
        targetSkill = skill;

        targetPlayer = null;
        targetProfile = null;
        leaderboardPage = -1;

        loadObjective(SkillUtils.getSkillName(skill));
    }

    public void setTypeSelfStats() {
        this.sidebarType = SidebarType.STATS_BOARD;

        targetPlayer = null;
        targetSkill = null;
        targetProfile = null;
        leaderboardPage = -1;

        loadObjective(ScoreboardManager.HEADER_STATS);
    }

    public void setTypeInspectStats(PlayerProfile profile) {
        this.sidebarType = SidebarType.STATS_BOARD;
        targetPlayer = profile.getPlayerName();
        targetProfile = profile;

        targetSkill = null;
        leaderboardPage = -1;

        loadObjective(LocaleLoader.getString("Scoreboard.Header.PlayerInspect", targetPlayer));
    }

    public void setTypeSelfRank() {
        this.sidebarType = SidebarType.RANK_BOARD;
        targetPlayer = null;

        targetSkill = null;
        targetProfile = null;
        leaderboardPage = -1;

        loadObjective(ScoreboardManager.HEADER_RANK);
    }

    public void setTypeInspectRank(String otherPlayer) {
        this.sidebarType = SidebarType.RANK_BOARD;
        targetPlayer = otherPlayer;

        targetSkill = null;
        targetProfile = null;
        leaderboardPage = -1;

        loadObjective(ScoreboardManager.HEADER_RANK);
    }

    public void setTypeTopPower(int page) {
        this.sidebarType = SidebarType.TOP_BOARD;
        leaderboardPage = page;
        targetSkill = null;

        targetPlayer = null;
        targetProfile = null;

        int endPosition = page * 15;
        int startPosition = endPosition - 14;
        loadObjective(String.format("%s (%2d - %2d)", ScoreboardManager.POWER_LEVEL, startPosition, endPosition));
    }

    public void setTypeTop(SkillType skill, int page) {
        this.sidebarType = SidebarType.TOP_BOARD;
        leaderboardPage = page;
        targetSkill = skill;

        targetPlayer = null;
        targetProfile = null;

        int endPosition = page * 15;
        int startPosition = endPosition - 14;
        loadObjective(String.format("%s (%2d - %2d)", SkillUtils.getSkillName(skill), startPosition, endPosition));
    }

    // Setup for after a board type change
    protected void loadObjective(String displayName) {
        sidebarObj.unregister();
        sidebarObj = board.registerNewObjective(ScoreboardManager.SIDEBAR_OBJECTIVE, "");

        if (displayName.length() > 32) {
            displayName = displayName.substring(0, 32);
        }
        sidebarObj.setDisplayName(displayName);

        updateSidebar();
        // Do last! Minimize packets!
        sidebarObj.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    /**
     * Load all new values into the sidebar. The calling of this method should
     * depend on the update preferences of the SidebarType.
     */
    public void updateSidebar() {
        try {
            updateTask.cancel();
        } catch (Throwable e) {} // catch NullPointerException + IllegalStateException + Error, Don't Care
        updateTask = null;

        if (sidebarType == SidebarType.NONE) {
            return;
        }

        Player bukkitPlayer = Bukkit.getPlayerExact(playerName);
        if (bukkitPlayer == null) {
            ScoreboardManager.cleanup(this);
            return;
        }

        McMMOPlayer mcPlayer = UserManager.getPlayer(bukkitPlayer);
        PlayerProfile profile = mcPlayer.getProfile();

        switch (sidebarType) {
        case NONE:
            break;

        case SKILL_BOARD:
            Validate.notNull(targetSkill);
            int currentXP = profile.getSkillXpLevel(targetSkill);
            sidebarObj.getScore(ScoreboardManager.labelLevel).setScore(profile.getSkillLevel(targetSkill));
            sidebarObj.getScore(ScoreboardManager.labelCurrentXp).setScore(currentXP);
            sidebarObj.getScore(ScoreboardManager.labelRemainingXp).setScore(profile.getXpToLevel(targetSkill) - currentXP);
            break;

        case STATS_BOARD:
            // Select the profile to read from
            PlayerProfile prof;
            if (targetProfile != null) {
                prof = targetProfile; // offline
            }
            else if (targetPlayer == null) {
                prof = profile; // self
            }
            else {
                prof = UserManager.getPlayer(targetPlayer).getProfile(); // online
            }
            // Calculate power level here
            int powerLevel = 0;
            for (SkillType skill : SkillType.values()) { // Include child skills, but not in power level
                int level = prof.getSkillLevel(skill);
                if (!skill.isChildSkill())
                    powerLevel += level;

                // TODO: Verify that this is what we want - calculated in power level but not displayed
                if (!Permissions.skillEnabled(bukkitPlayer, skill)) {
                    continue;
                }
                sidebarObj.getScore(ScoreboardManager.skillLabels.get(skill)).setScore(level);
            }
            sidebarObj.getScore(ScoreboardManager.labelPowerLevel).setScore(powerLevel);
            break;

        case RANK_BOARD:
        case TOP_BOARD:
            /*
             * @see #acceptRankData(Map<SkillType, Integer> rank)
             * @see #acceptLeaderboardData(List<PlayerStat> stats)
             */
            break;

        }
    }

    public void acceptRankData(Map<SkillType, Integer> rankData) {
        Integer rank;
        Player bukkitPlayer = Bukkit.getPlayerExact(playerName);

        for (SkillType skill : SkillType.nonChildSkills()) {
            if (!Permissions.skillEnabled(bukkitPlayer, skill)) {
                continue;
            }

            rank = rankData.get(skill);
            if (rank != null) {
                sidebarObj.getScore(ScoreboardManager.skillLabels.get(skill)).setScore(rank);
            }
        }
        rank = rankData.get(null);
        if (rank != null) {
            sidebarObj.getScore(ScoreboardManager.labelPowerLevel).setScore(rank);
        }
    }

    public void acceptLeaderboardData(List<PlayerStat> leaderboardData) {
        for (PlayerStat stat : leaderboardData) {
            String statname = stat.name;
            if (statname.equals(playerName)) {
                statname = ChatColor.GOLD + "--You--";
            }
            sidebarObj.getScore(Bukkit.getOfflinePlayer(statname)).setScore(stat.statVal);
        }
    }
}
