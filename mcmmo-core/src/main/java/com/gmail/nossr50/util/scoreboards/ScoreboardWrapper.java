package com.gmail.nossr50.util.scoreboards;

import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.events.scoreboard.McMMOScoreboardObjectiveEvent;
import com.gmail.nossr50.events.scoreboard.McMMOScoreboardRevertEvent;
import com.gmail.nossr50.events.scoreboard.ScoreboardEventReason;
import com.gmail.nossr50.events.scoreboard.ScoreboardObjectiveEventReason;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.child.FamilyTree;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;
import java.util.Map;

public class ScoreboardWrapper {
    // Initialization variables
    public final mcMMO pluginRef;
    public final ScoreboardStrings scoreboardStrings;

    public final String playerName;
    public final Player player;
    private final Scoreboard scoreboard;
    public String targetPlayer = null;
    public PrimarySkillType targetSkill = null;
    public int leaderboardPage = -1;
    public BukkitTask updateTask = null;
    public BukkitTask revertTask = null;
    public BukkitTask cooldownTask = null;
    private boolean tippedKeep = false;
    private boolean tippedClear = false;
    // Internal usage variables (should exist)
    private SidebarType sidebarType;
    private Objective sidebarObjective;
    private Objective powerObjective;
    // Parameter variables (May be null / invalid)
    private Scoreboard oldBoard = null;
    private PlayerProfile targetProfile = null;

    protected ScoreboardWrapper(Player player, Scoreboard scoreboard, ScoreboardStrings scoreboardStrings, mcMMO pluginRef) {
        this.scoreboardStrings = scoreboardStrings;
        this.pluginRef = pluginRef;


        this.player = player;
        this.playerName = player.getName();
        this.scoreboard = scoreboard;
        sidebarType = SidebarType.NONE;
        sidebarObjective = this.scoreboard.registerNewObjective(scoreboardStrings.SIDEBAR_OBJECTIVE, "dummy");
        powerObjective = this.scoreboard.registerNewObjective(scoreboardStrings.POWER_OBJECTIVE, "dummy");

        if (pluginRef.getScoreboardSettings().getPowerLevelTagsEnabled()) {
            powerObjective.setDisplayName(scoreboardStrings.TAG_POWER_LEVEL);
            powerObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);

            for (McMMOPlayer mcMMOPlayer : pluginRef.getUserManager().getPlayers()) {
                powerObjective.getScore(mcMMOPlayer.getProfile().getPlayerName()).setScore(mcMMOPlayer.getPowerLevel());
            }
        }
    }

    public void doSidebarUpdateSoon() {
        if (updateTask == null) {
            // To avoid spamming the scheduler, store the instance and run 2 ticks later
            updateTask = new ScoreboardQuickUpdate().runTaskLater(pluginRef, 2L);
        }
    }

    private void startCooldownUpdating() {
        if (cooldownTask == null) {
            // Repeat every 5 seconds.
            // Cancels once all cooldowns are done, using stopCooldownUpdating().
            cooldownTask = new ScoreboardCooldownTask().runTaskTimer(pluginRef, 5 * pluginRef.getMiscTools().TICK_CONVERSION_FACTOR, 5 * pluginRef.getMiscTools().TICK_CONVERSION_FACTOR);
        }
    }

    private void stopCooldownUpdating() {
        if (cooldownTask != null) {
            try {
                cooldownTask.cancel();
            } catch (Throwable ignored) {
            }

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
     * Set the old targetBoard, for use in reverting.
     */
    public void setOldScoreboard() {
        Player player = pluginRef.getServer().getPlayerExact(playerName);

        if (player == null) {
            pluginRef.getScoreboardManager().cleanup(this);
            return;
        }

        Scoreboard oldBoard = player.getScoreboard();

        if (oldBoard == scoreboard) { // Already displaying it
            if (this.oldBoard == null) {
                // (Shouldn't happen) Use failsafe value - we're already displaying our board, but we don't have the one we should revert to
                this.oldBoard = pluginRef.getServer().getScoreboardManager().getMainScoreboard();
            }
        } else {
            this.oldBoard = oldBoard;
        }
    }

    public void showBoardWithNoRevert() {
        Player player = pluginRef.getServer().getPlayerExact(playerName);

        if (player == null) {
            pluginRef.getScoreboardManager().cleanup(this);
            return;
        }

        if (revertTask != null) {
            revertTask.cancel();
        }

        player.setScoreboard(scoreboard);
        revertTask = null;
    }

    public void showBoardAndScheduleRevert(int ticks) {
        Player player = pluginRef.getServer().getPlayerExact(playerName);

        if (player == null) {
            pluginRef.getScoreboardManager().cleanup(this);
            return;
        }

        if (revertTask != null) {
            revertTask.cancel();
        }

        player.setScoreboard(scoreboard);
        revertTask = new ScoreboardChangeTask().runTaskLater(pluginRef, ticks);

        // TODO is there any way to do the time that looks acceptable?
        // player.sendMessage(LocaleLoader.getString("Commands.Scoreboard.Timer", StringUtils.capitalize(sidebarType.toString().toLowerCase(Locale.ENGLISH)), ticks / 20F));

        if (pluginRef.getUserManager().getPlayer(playerName) == null)
            return;

        PlayerProfile profile = pluginRef.getUserManager().getPlayer(player).getProfile();

        if (profile.getScoreboardTipsShown() >= pluginRef.getScoreboardSettings().getTipsAmount()) {
            return;
        }

        if (!tippedKeep) {
            tippedKeep = true;
            player.sendMessage(pluginRef.getLocaleManager().getString("Commands.Scoreboard.Tip.Keep"));
        } else if (!tippedClear) {
            tippedClear = true;
            player.sendMessage(pluginRef.getLocaleManager().getString("Commands.Scoreboard.Tip.Clear"));
            profile.increaseTipsShown();
        }
    }

    public void tryRevertBoard() {
        Player player = pluginRef.getServer().getPlayerExact(playerName);

        if (player == null) {
            pluginRef.getScoreboardManager().cleanup(this);
            return;
        }

        if (oldBoard != null) {
            if (player.getScoreboard() == scoreboard) {
                /**
                 * Call the revert scoreboard custom event
                 */
                McMMOScoreboardRevertEvent event = new McMMOScoreboardRevertEvent(oldBoard, player.getScoreboard(), player, ScoreboardEventReason.REVERTING_BOARD);
                player.getServer().getPluginManager().callEvent(event);
                //Modify the player based on the event
                event.getTargetPlayer().setScoreboard(event.getTargetBoard());
                oldBoard = null;
            } else {
                pluginRef.debug("Not reverting targetBoard for " + playerName + " - targetBoard was changed by another plugin (Consider disabling the mcMMO scoreboards if you don't want them!)");
            }
        }

        cancelRevert();

        sidebarType = SidebarType.NONE;
        targetPlayer = null;
        targetSkill = null;
        targetProfile = null;
        leaderboardPage = -1;
    }

    public boolean isBoardShown() {
        Player player = pluginRef.getServer().getPlayerExact(playerName);

        if (player == null) {
            pluginRef.getScoreboardManager().cleanup(this);
            return false;
        }

        return player.getScoreboard() == scoreboard;
    }

    public void cancelRevert() {
        if (revertTask == null) {
            return;
        }

        revertTask.cancel();
        revertTask = null;
    }

    public void setTypeNone() {
        this.sidebarType = SidebarType.NONE;

        targetPlayer = null;
        targetSkill = null;
        targetProfile = null;
        leaderboardPage = -1;

        loadObjective("");
    }

    public void setTypeSkill(PrimarySkillType skill) {
        this.sidebarType = SidebarType.SKILL_BOARD;
        targetSkill = skill;

        targetPlayer = null;
        targetProfile = null;
        leaderboardPage = -1;

        loadObjective(scoreboardStrings.skillLabels.get(skill));
    }

    public void setTypeSelfStats() {
        this.sidebarType = SidebarType.STATS_BOARD;

        targetPlayer = null;
        targetSkill = null;
        targetProfile = null;
        leaderboardPage = -1;

        loadObjective(scoreboardStrings.HEADER_STATS);
    }

    // Board Type Changing 'API' methods

    public void setTypeInspectStats(PlayerProfile profile) {
        this.sidebarType = SidebarType.STATS_BOARD;
        targetPlayer = profile.getPlayerName();
        targetProfile = profile;

        targetSkill = null;
        leaderboardPage = -1;

        loadObjective(pluginRef.getLocaleManager().getString("Scoreboard.Header.PlayerInspect", targetPlayer));
    }

    public void setTypeCooldowns() {
        this.sidebarType = SidebarType.COOLDOWNS_BOARD;

        targetPlayer = null;
        targetSkill = null;
        targetProfile = null;
        leaderboardPage = -1;

        loadObjective(scoreboardStrings.HEADER_COOLDOWNS);
    }

    public void setTypeSelfRank() {
        this.sidebarType = SidebarType.RANK_BOARD;
        targetPlayer = null;

        targetSkill = null;
        targetProfile = null;
        leaderboardPage = -1;

        loadObjective(scoreboardStrings.HEADER_RANK);
    }

    public void setTypeInspectRank(String otherPlayer) {
        this.sidebarType = SidebarType.RANK_BOARD;
        targetPlayer = otherPlayer;

        targetSkill = null;
        targetProfile = null;
        leaderboardPage = -1;

        loadObjective(scoreboardStrings.HEADER_RANK);
    }

    public void setTypeTopPower(int page) {
        this.sidebarType = SidebarType.TOP_BOARD;
        leaderboardPage = page;
        targetSkill = null;

        targetPlayer = null;
        targetProfile = null;

        int endPosition = page * 10;
        int startPosition = endPosition - 9;
        loadObjective(String.format("%s (%2d - %2d)", scoreboardStrings.POWER_LEVEL, startPosition, endPosition));
    }

    public void setTypeTop(PrimarySkillType skill, int page) {
        this.sidebarType = SidebarType.TOP_BOARD;
        leaderboardPage = page;
        targetSkill = skill;

        targetPlayer = null;
        targetProfile = null;

        int endPosition = page * 10;
        int startPosition = endPosition - 9;
        loadObjective(String.format("%s (%2d - %2d)", scoreboardStrings.skillLabels.get(skill), startPosition, endPosition));
    }

    // Setup for after a board type change
    protected void loadObjective(String displayName) {
        //Unregister objective
        McMMOScoreboardObjectiveEvent unregisterEvent = callObjectiveEvent(ScoreboardObjectiveEventReason.UNREGISTER_THIS_OBJECTIVE);
        if (!unregisterEvent.isCancelled()) {
            sidebarObjective.unregister();
        }

        //Register objective
        McMMOScoreboardObjectiveEvent registerEvent = callObjectiveEvent(ScoreboardObjectiveEventReason.REGISTER_NEW_OBJECTIVE);
        if (!registerEvent.isCancelled())
            sidebarObjective = registerEvent.getTargetBoard().registerNewObjective(scoreboardStrings.SIDEBAR_OBJECTIVE, "dummy");

        if (displayName.length() > 32) {
            displayName = displayName.substring(0, 32);
        }

        sidebarObjective.setDisplayName(displayName);

        updateSidebar();
        // Do last! Minimize packets!
        sidebarObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    private McMMOScoreboardObjectiveEvent callObjectiveEvent(ScoreboardObjectiveEventReason reason) {
        McMMOScoreboardObjectiveEvent event = new McMMOScoreboardObjectiveEvent(sidebarObjective, reason, scoreboard, scoreboard, player, ScoreboardEventReason.OBJECTIVE);
        player.getServer().getPluginManager().callEvent(event);
        return event;
    }

    /**
     * Load new values into the sidebar.
     */
    private void updateSidebar() {
        try {
            updateTask.cancel();
        } catch (Throwable ignored) {
        } // catch NullPointerException and IllegalStateException and any Error; don't care

        updateTask = null;

        if (sidebarType == SidebarType.NONE) {
            return;
        }

        Player player = pluginRef.getServer().getPlayerExact(playerName);

        if (player == null) {
            pluginRef.getScoreboardManager().cleanup(this);
            return;
        }

        McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);

        if (mcMMOPlayer == null)
            return;

        switch (sidebarType) {

            case SKILL_BOARD:
                Validate.notNull(targetSkill);

                if (!pluginRef.getSkillTools().isChildSkill(targetSkill)) {
                    int currentXP = mcMMOPlayer.getSkillXpLevel(targetSkill);

                    sidebarObjective.getScore(scoreboardStrings.LABEL_CURRENT_XP).setScore(currentXP);
                    sidebarObjective.getScore(scoreboardStrings.LABEL_REMAINING_XP).setScore(mcMMOPlayer.getXpToLevel(targetSkill) - currentXP);
                } else {
                    for (PrimarySkillType parentSkill : FamilyTree.getParents(targetSkill)) {
                        sidebarObjective.getScore(scoreboardStrings.skillLabels.get(parentSkill)).setScore(mcMMOPlayer.getSkillLevel(parentSkill));
                    }
                }

                sidebarObjective.getScore(scoreboardStrings.LABEL_LEVEL).setScore(mcMMOPlayer.getSkillLevel(targetSkill));

                if (pluginRef.getSkillTools().getSuperAbility(targetSkill) != null) {
                    boolean stopUpdating;

                    if (targetSkill == PrimarySkillType.MINING) {
                        // Special-Case: Mining has two abilities, both with cooldowns
                        Score cooldownSB = sidebarObjective.getScore(scoreboardStrings.abilityLabelsSkill.get(SuperAbilityType.SUPER_BREAKER));
                        Score cooldownBM = sidebarObjective.getScore(scoreboardStrings.abilityLabelsSkill.get(SuperAbilityType.BLAST_MINING));
                        int secondsSB = Math.max(mcMMOPlayer.calculateTimeRemaining(SuperAbilityType.SUPER_BREAKER), 0);
                        int secondsBM = Math.max(mcMMOPlayer.calculateTimeRemaining(SuperAbilityType.BLAST_MINING), 0);

                        cooldownSB.setScore(secondsSB);
                        cooldownBM.setScore(secondsBM);

                        stopUpdating = (secondsSB == 0 && secondsBM == 0);
                    } else {
                        SuperAbilityType ability = pluginRef.getSkillTools().getSuperAbility(targetSkill);
                        Score cooldown = sidebarObjective.getScore(scoreboardStrings.abilityLabelsSkill.get(ability));
                        int seconds = Math.max(mcMMOPlayer.calculateTimeRemaining(ability), 0);

                        cooldown.setScore(seconds);

                        stopUpdating = seconds == 0;
                    }

                    if (stopUpdating) {
                        stopCooldownUpdating();
                    } else {
                        startCooldownUpdating();
                    }
                }
                break;

            case COOLDOWNS_BOARD:
                boolean anyCooldownsActive = false;

                for (SuperAbilityType ability : SuperAbilityType.values()) {
                    int seconds = Math.max(mcMMOPlayer.calculateTimeRemaining(ability), 0);

                    if (seconds != 0) {
                        anyCooldownsActive = true;
                    }

                    sidebarObjective.getScore(scoreboardStrings.abilityLabelsColored.get(ability)).setScore(seconds);
                }

                if (anyCooldownsActive) {
                    startCooldownUpdating();
                } else {
                    stopCooldownUpdating();
                }
                break;

            case STATS_BOARD:
                // Select the profile to read from
                PlayerProfile newProfile;

                if (targetProfile != null) {
                    newProfile = targetProfile; // offline
                } else if (targetPlayer == null) {
                    newProfile = mcMMOPlayer.getProfile(); // self
                } else {
                    newProfile = pluginRef.getUserManager().getPlayer(targetPlayer).getProfile(); // online
                }

                // Calculate power level here
                int powerLevel = 0;
                for (PrimarySkillType primarySkillType : pluginRef.getSkillTools().NON_CHILD_SKILLS) { // Don't include child skills, makes the list too long
                    int level = newProfile.getSkillLevel(primarySkillType);

                    powerLevel += level;

                    // TODO: Verify that this is what we want - calculated in power level but not displayed
                    if (!pluginRef.getPermissionTools().skillEnabled(player, primarySkillType)) {
                        continue;
                    }

                    sidebarObjective.getScore(scoreboardStrings.skillLabels.get(primarySkillType)).setScore(level);
                }

                sidebarObjective.getScore(scoreboardStrings.LABEL_POWER_LEVEL).setScore(powerLevel);
                break;

            case RANK_BOARD:
            case TOP_BOARD:
                /*
                 * @see #acceptRankData(Map<PrimarySkillType, Integer> rank)
                 * @see #acceptLeaderboardData(List<PlayerStat> stats)
                 */
                break;

            default:
                break;
        }
    }

    public void acceptRankData(Map<PrimarySkillType, Integer> rankData) {
        Integer rank;
        Player player = pluginRef.getServer().getPlayerExact(playerName);

        for (PrimarySkillType primarySkillType : pluginRef.getSkillTools().NON_CHILD_SKILLS) {
            if (!pluginRef.getPermissionTools().skillEnabled(player, primarySkillType)) {
                continue;
            }

            rank = rankData.get(primarySkillType);

            if (rank != null) {
                sidebarObjective.getScore(scoreboardStrings.skillLabels.get(primarySkillType)).setScore(rank);
            }
        }

        rank = rankData.get(null);

        if (rank != null) {
            sidebarObjective.getScore(scoreboardStrings.LABEL_POWER_LEVEL).setScore(rank);
        }
    }

    public void acceptLeaderboardData(List<PlayerStat> leaderboardData) {
        for (PlayerStat stat : leaderboardData) {
            String name = stat.name;

            if (name.equals(playerName)) {
                name = ChatColor.GOLD + "--You--";
            }

            sidebarObjective.getScore(name).setScore(stat.statVal);
        }
    }

    public void updatePowerLevel(Player player, int newPowerLevel) {
        powerObjective.getScore(player.getName()).setScore(newPowerLevel);
    }

    private class ScoreboardQuickUpdate extends BukkitRunnable {
        @Override
        public void run() {
            updateSidebar();
            updateTask = null;
        }
    }

    private class ScoreboardChangeTask extends BukkitRunnable {
        @Override
        public void run() {
            tryRevertBoard();
            revertTask = null;
        }
    }

    private class ScoreboardCooldownTask extends BukkitRunnable {
        @Override
        public void run() {
            // Stop updating if it's no longer something displaying cooldowns
            if (isBoardShown() && (isSkillScoreboard() || isCooldownScoreboard())) {
                doSidebarUpdateSoon();
            } else {
                stopCooldownUpdating();
            }
        }
    }
}
