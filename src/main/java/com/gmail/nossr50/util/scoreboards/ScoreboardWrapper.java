package com.gmail.nossr50.util.scoreboards;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.child.FamilyTree;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager.SidebarType;
import com.gmail.nossr50.util.skills.SkillUtils;

public class ScoreboardWrapper {

    // Initialization variables
    public final String playerName;
    private final Scoreboard board;
    private boolean tippedKeep = false;
    private boolean tippedClear = false;

    // Internal usage variables (should exist)
    private SidebarType sidebarType;
    private Objective sidebarObj;
    private Objective powerObj;

    // Parameter variables (May be null / invalid)
    private Scoreboard oldBoard = null;
    public String targetPlayer = null;
    public SkillType targetSkill = null;
    private PlayerProfile targetProfile = null;
    public int leaderboardPage = -1;

    private ScoreboardWrapper(String playerName, Scoreboard s) {
        this.playerName = playerName;
        board = s;
        sidebarType = SidebarType.NONE;
        sidebarObj = board.registerNewObjective(ScoreboardManager.SIDEBAR_OBJECTIVE, "dummy");
        powerObj = board.registerNewObjective(ScoreboardManager.POWER_OBJECTIVE, "dummy");
        if (Config.getInstance().getPowerLevelTagsEnabled()) {
            powerObj.setDisplayName(ScoreboardManager.TAG_POWER_LEVEL);
            powerObj.setDisplaySlot(DisplaySlot.BELOW_NAME);
            for (McMMOPlayer mcpl : UserManager.getPlayers()) {
                powerObj.getScore(mcpl.getPlayer()).setScore(mcpl.getPowerLevel());
            }
        }
    }

    public static ScoreboardWrapper create(Player p) {
        return new ScoreboardWrapper(p.getName(), mcMMO.p.getServer().getScoreboardManager().getNewScoreboard());
    }

    public BukkitTask updateTask = null;
    private class ScoreboardQuickUpdate extends BukkitRunnable {
        @Override
        public void run() {
            ScoreboardWrapper.this.updateSidebar();
            updateTask = null;
        }
    }

    public BukkitTask revertTask = null;
    private class ScoreboardChangeTask extends BukkitRunnable {
        @Override
        public void run() {
            ScoreboardWrapper.this.tryRevertBoard();
            revertTask = null;
        }
    }

    public BukkitTask cooldownTask = null;
    private class ScoreboardCooldownTask extends BukkitRunnable {
        @Override
        public void run() {
            ScoreboardWrapper wrapper = ScoreboardWrapper.this;
            // Stop updating if it's no longer something displaying cooldowns
            if (wrapper.isBoardShown() && (wrapper.isSkillScoreboard() || wrapper.isCooldownScoreboard())) {
                wrapper.doSidebarUpdateSoon();
            }
            else {
                wrapper.stopCooldownUpdating();
            }
        }
    }


    public void doSidebarUpdateSoon() {
        if (updateTask == null) {
            // To avoid spamming the scheduler, store the instance and run 2 ticks later
            updateTask = new ScoreboardQuickUpdate().runTaskLater(mcMMO.p, 2L);
        }
    }

    private void startCooldownUpdating() {
        if (cooldownTask == null) {
            // Repeat every 5 seconds.
            // Cancels once all cooldowns are done, using stopCooldownUpdating().
            cooldownTask = new ScoreboardCooldownTask().runTaskTimer(mcMMO.p, 5 * Misc.TICK_CONVERSION_FACTOR, 5 * Misc.TICK_CONVERSION_FACTOR);
        }
    }

    private void stopCooldownUpdating() {
        if (cooldownTask != null) {
            try {
                cooldownTask.cancel();
            } catch (Throwable ignored) {}
            cooldownTask = null;
        }
    }

    public boolean isSkillScoreboard() {
        return sidebarType == SidebarType.SKILL_BOARD;
    }

    public boolean isCooldownScoreboard() {
        return sidebarType == SidebarType.COOLDOWNS_BOARD;
    }

    public boolean isStatsScoreboard() {
        return sidebarType == SidebarType.STATS_BOARD;
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
                // (Shouldn't happen) Use failsafe value - we're already displaying our board, but we don't have the one we should revert to
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
        revertTask = new ScoreboardChangeTask().runTaskLater(mcMMO.p, ticks);

        // TODO is there any way to do the time that looks acceptable?
        // player.sendMessage(LocaleLoader.getString("Commands.Scoreboard.Timer", StringUtils.capitalize(sidebarType.toString().toLowerCase()), ticks / 20F));
        if (!tippedKeep) {
            tippedKeep = true;
            player.sendMessage(LocaleLoader.getString("Commands.Scoreboard.Tip.Keep"));
        }
        else if (!tippedClear) {
            tippedClear = true;
            player.sendMessage(LocaleLoader.getString("Commands.Scoreboard.Tip.Clear"));
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
        sidebarType = SidebarType.NONE;
        targetPlayer = null;
        targetSkill = null;
        targetProfile = null;
        leaderboardPage = -1;
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

        loadObjective(ScoreboardManager.skillLabels.get(skill).getName());
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

    public void setTypeCooldowns() {
        this.sidebarType = SidebarType.COOLDOWNS_BOARD;

        targetPlayer = null;
        targetSkill = null;
        targetProfile = null;
        leaderboardPage = -1;

        loadObjective(ScoreboardManager.HEADER_COOLDOWNS);
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
        loadObjective(String.format("%s (%2d - %2d)", ScoreboardManager.skillLabels.get(skill).getName(), startPosition, endPosition));
    }

    // Setup for after a board type change
    protected void loadObjective(String displayName) {
        sidebarObj.unregister();
        sidebarObj = board.registerNewObjective(ScoreboardManager.SIDEBAR_OBJECTIVE, "dummy");

        if (displayName.length() > 32) {
            displayName = displayName.substring(0, 32);
        }
        sidebarObj.setDisplayName(displayName);

        updateSidebar();
        // Do last! Minimize packets!
        sidebarObj.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    /**
     * Load new values into the sidebar.
     */
    private void updateSidebar() {
        try {
            updateTask.cancel();
        } catch (Throwable ignored) {} // catch NullPointerException and IllegalStateException and any Error; don't care
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
            if (!targetSkill.isChildSkill()) {
                int currentXP = profile.getSkillXpLevel(targetSkill);
                sidebarObj.getScore(ScoreboardManager.LABEL_CURRENT_XP).setScore(currentXP);
                sidebarObj.getScore(ScoreboardManager.LABEL_REMAINING_XP).setScore(profile.getXpToLevel(targetSkill) - currentXP);
            }
            else {
                Set<SkillType> parents = FamilyTree.getParents(targetSkill);
                for (SkillType parentSkill : parents) {
                    sidebarObj.getScore(ScoreboardManager.skillLabels.get(parentSkill)).setScore(profile.getSkillLevel(parentSkill));
                }
            }
            sidebarObj.getScore(ScoreboardManager.LABEL_LEVEL).setScore(profile.getSkillLevel(targetSkill));
            if (targetSkill.getAbility() != null) {
                if (targetSkill != SkillType.MINING) {
                    AbilityType ab = targetSkill.getAbility();
                    Score cooldown = sidebarObj.getScore(ScoreboardManager.abilityLabelsSkill.get(ab));
                    int seconds = SkillUtils.calculateTimeLeft(ab, profile, bukkitPlayer);
                    seconds = (seconds <= 0) ? 0 : seconds;
                    if (seconds == 0) {
                        cooldown.setScore(0);
                        stopCooldownUpdating();
                    }
                    else {
                        cooldown.setScore(seconds);
                        startCooldownUpdating();
                    }
                } else {
                    // Special-Case: Mining has two abilities, both with cooldowns
                    AbilityType sb = AbilityType.SUPER_BREAKER;
                    AbilityType bm = AbilityType.BLAST_MINING;
                    Score cooldownSB = sidebarObj.getScore(ScoreboardManager.abilityLabelsSkill.get(sb));
                    Score cooldownBM = sidebarObj.getScore(ScoreboardManager.abilityLabelsSkill.get(bm));
                    int secondsSB = SkillUtils.calculateTimeLeft(sb, profile, bukkitPlayer);
                    int secondsBM = SkillUtils.calculateTimeLeft(bm, profile, bukkitPlayer);
                    secondsSB = (secondsSB <= 0) ? 0 : secondsSB;
                    secondsBM = (secondsBM <= 0) ? 0 : secondsBM;
                    if (secondsSB == 0 && secondsBM == 0) {
                        cooldownSB.setScore(0);
                        cooldownBM.setScore(0);
                        stopCooldownUpdating();
                    }
                    else {
                        cooldownSB.setScore(secondsSB);
                        cooldownBM.setScore(secondsBM);
                        startCooldownUpdating();
                    }
                }
            }
            break;

        case COOLDOWNS_BOARD:
            boolean anyCooldownsActive = false;
            for (AbilityType ability : AbilityType.NORMAL_ABILITIES) {
                int seconds = SkillUtils.calculateTimeLeft(ability, profile, bukkitPlayer);
                seconds = (seconds <= 0) ? 0 : seconds;
                if (seconds != 0) {
                    anyCooldownsActive = true;
                }
                sidebarObj.getScore(ScoreboardManager.abilityLabelsColored.get(ability)).setScore(seconds);
            }

            if (anyCooldownsActive) {
                startCooldownUpdating();
            }
            else {
                stopCooldownUpdating();
            }
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
            sidebarObj.getScore(ScoreboardManager.LABEL_POWER_LEVEL).setScore(powerLevel);
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

        for (SkillType skill : SkillType.NON_CHILD_SKILLS) {
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
            sidebarObj.getScore(ScoreboardManager.LABEL_POWER_LEVEL).setScore(rank);
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

    public void updatePowerLevel(Player leveledPlayer, int newPowerLevel) {
        powerObj.getScore(leveledPlayer).setScore(newPowerLevel);
    }
}
