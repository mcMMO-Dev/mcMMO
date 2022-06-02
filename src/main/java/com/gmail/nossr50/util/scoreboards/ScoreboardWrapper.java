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
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.child.FamilyTree;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager.SidebarType;
import com.gmail.nossr50.util.skills.SkillTools;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class ScoreboardWrapper {
    public static final String SIDE_OBJECTIVE = "mcMMO_sideObjective";
    public static final String POWER_OBJECTIVE = "mcMMO_powerObjective";
    // Initialization variables
    public final String playerName;
    public final Player player;
    private final Scoreboard scoreboard;
    private boolean tippedKeep = false;
    private boolean tippedClear = false;

    // Internal usage variables (should exist)
    private SidebarType sidebarType;
    private Objective sidebarObjective;
    private Objective powerObjective;

    // Parameter variables (May be null / invalid)
    private Scoreboard oldBoard = null;
    public String targetPlayer = null;
    public PrimarySkillType targetSkill = null;
    private PlayerProfile targetProfile = null;
    public int leaderboardPage = -1;
    private boolean registered = false;

    public ScoreboardWrapper(Player player, Scoreboard scoreboard) {
        this.player = player;
        this.playerName = player.getName();
        this.scoreboard = scoreboard;
        initBoard();
    }

    private void initBoard() {
        sidebarType = SidebarType.NONE;
        if(registered) {
            //Make sure our references are pointed at the right things
            sidebarObjective = scoreboard.getObjective(ScoreboardManager.SIDEBAR_OBJECTIVE);
            powerObjective = scoreboard.getObjective(ScoreboardManager.POWER_OBJECTIVE);
        } else {
            //Register Objectives
            sidebarObjective = this.scoreboard.registerNewObjective(ScoreboardManager.SIDEBAR_OBJECTIVE, "dummy", SIDE_OBJECTIVE);
            powerObjective = this.scoreboard.registerNewObjective(ScoreboardManager.POWER_OBJECTIVE, "dummy", POWER_OBJECTIVE);
            registered = true;
        }

        if (mcMMO.p.getGeneralConfig().getPowerLevelTagsEnabled()) {
            powerObjective.setDisplayName(ScoreboardManager.TAG_POWER_LEVEL);
            powerObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);

            for (McMMOPlayer mcMMOPlayer : UserManager.getPlayers()) {
                powerObjective.getScore(mcMMOPlayer.getProfile().getPlayerName()).setScore(mcMMOPlayer.getPowerLevel());
            }
        }
    }

    public BukkitTask updateTask = null;

    private class ScoreboardQuickUpdate extends BukkitRunnable {
        @Override
        public void run() {
            updateSidebar();
            updateTask = null;
        }
    }

    public BukkitTask revertTask = null;

    private class ScoreboardChangeTask extends BukkitRunnable {
        @Override
        public void run() {
            tryRevertBoard();
            revertTask = null;
        }
    }

    public BukkitTask cooldownTask = null;

    private class ScoreboardCooldownTask extends BukkitRunnable {
        @Override
        public void run() {
            // Stop updating if it's no longer something displaying cooldowns
            if (isBoardShown() && (isSkillScoreboard() || isCooldownScoreboard())) {
                doSidebarUpdateSoon();
            }
            else {
                stopCooldownUpdating();
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
            }
            catch (Exception e) {
                e.printStackTrace();
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
        Player player = mcMMO.p.getServer().getPlayerExact(playerName);

        if (player == null) {
            ScoreboardManager.cleanup(this);
            return;
        }

        Scoreboard previousBoard = player.getScoreboard();

        if (previousBoard == scoreboard) { // Already displaying it
            if (this.oldBoard == null) {
                // (Shouldn't happen) Use failsafe value - we're already displaying our board, but we don't have the one we should revert to
                if(mcMMO.p.getServer().getScoreboardManager() != null)
                    this.oldBoard = mcMMO.p.getServer().getScoreboardManager().getMainScoreboard();
            }
        }
        else {
            this.oldBoard = previousBoard;
        }
    }

    public void showBoardWithNoRevert() {
        Player player = mcMMO.p.getServer().getPlayerExact(playerName);

        if (player == null) {
            ScoreboardManager.cleanup(this);
            return;
        }

        if (revertTask != null) {
            revertTask.cancel();
        }

        player.setScoreboard(scoreboard);
        revertTask = null;
    }

    public void showBoardAndScheduleRevert(int ticks) {
        Player player = mcMMO.p.getServer().getPlayerExact(playerName);

        if (player == null) {
            ScoreboardManager.cleanup(this);
            return;
        }

        if (revertTask != null) {
            revertTask.cancel();
        }

        player.setScoreboard(scoreboard);
        revertTask = new ScoreboardChangeTask().runTaskLater(mcMMO.p, ticks);

        // TODO is there any way to do the time that looks acceptable?
        // player.sendMessage(LocaleLoader.getString("Commands.Scoreboard.Timer", StringUtils.capitalize(sidebarType.toString().toLowerCase(Locale.ENGLISH)), ticks / 20F));

        if(UserManager.getPlayer(playerName) == null)
            return;

        PlayerProfile profile = UserManager.getPlayer(player).getProfile();

        if (profile.getScoreboardTipsShown() >= mcMMO.p.getGeneralConfig().getTipsAmount()) {
            return;
        }

        if (!tippedKeep) {
            tippedKeep = true;
            player.sendMessage(LocaleLoader.getString("Commands.Scoreboard.Tip.Keep"));
        }
        else if (!tippedClear) {
            tippedClear = true;
            player.sendMessage(LocaleLoader.getString("Commands.Scoreboard.Tip.Clear"));
            profile.increaseTipsShown();
        }
    }

    public void tryRevertBoard() {
        Player player = mcMMO.p.getServer().getPlayerExact(playerName);

        if (player == null) {
            ScoreboardManager.cleanup(this);
            return;
        }

        if (oldBoard != null) {
            if (player.getScoreboard() == scoreboard) {
                /*
                  Call the revert scoreboard custom event
                 */
                McMMOScoreboardRevertEvent event = new McMMOScoreboardRevertEvent(oldBoard, player.getScoreboard(), player, ScoreboardEventReason.REVERTING_BOARD);
                player.getServer().getPluginManager().callEvent(event);
                //Modify the player based on the event
                event.getTargetPlayer().setScoreboard(event.getTargetBoard());
                oldBoard = null;
            }
            else {
                mcMMO.p.debug("Not reverting targetBoard for " + playerName + " - targetBoard was changed by another plugin (Consider disabling the mcMMO scoreboards if you don't want them!)");
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
        Player player = mcMMO.p.getServer().getPlayerExact(playerName);

        if (player == null) {
            ScoreboardManager.cleanup(this);
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

    // Board Type Changing 'API' methods

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

        loadObjective(ScoreboardManager.skillLabels.get(skill));
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

    public void setTypeInspectStats(@NotNull McMMOPlayer mcMMOPlayer) {
        this.sidebarType = SidebarType.STATS_BOARD;
        targetPlayer = mcMMOPlayer.getPlayer().getName();
        targetProfile = mcMMOPlayer.getProfile();

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

        int endPosition = page * 10;
        int startPosition = endPosition - 9;
        loadObjective(String.format("%s (%2d - %2d)", ScoreboardManager.POWER_LEVEL, startPosition, endPosition));
    }

    public void setTypeTop(PrimarySkillType skill, int page) {
        this.sidebarType = SidebarType.TOP_BOARD;
        leaderboardPage = page;
        targetSkill = skill;

        targetPlayer = null;
        targetProfile = null;

        int endPosition = page * 10;
        int startPosition = endPosition - 9;
        loadObjective(String.format("%s (%2d - %2d)", ScoreboardManager.skillLabels.get(skill), startPosition, endPosition));
    }

    // Setup for after a board type change
    protected void loadObjective(String displayName) {
        //Unregister objective
        McMMOScoreboardObjectiveEvent unregisterEvent = callObjectiveEvent(ScoreboardObjectiveEventReason.UNREGISTER_THIS_OBJECTIVE);
        if(!unregisterEvent.isCancelled()) {
            try {
                sidebarObjective.unregister();
            } catch (IllegalStateException e) {
                McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

                mcMMO.p.debug("Recovering scoreboard for player: " + player.getName());

                if(mmoPlayer.isDebugMode())
                    NotificationManager.sendPlayerInformationChatOnlyPrefixed(player, "Scoreboard.Recovery");

                initBoard(); //Start over
                Bukkit.getScheduler().runTaskLater(mcMMO.p, () -> ScoreboardManager.retryLastSkillBoard(player), 0);
            }
        }

        //Register objective
        McMMOScoreboardObjectiveEvent registerEvent = callObjectiveEvent(ScoreboardObjectiveEventReason.REGISTER_NEW_OBJECTIVE);
        if(!registerEvent.isCancelled())
            sidebarObjective = registerEvent.getTargetBoard().registerNewObjective(ScoreboardManager.SIDEBAR_OBJECTIVE, "dummy", SIDE_OBJECTIVE);

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
        if(updateTask != null) {
            try {
                updateTask.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }

            updateTask = null;
        }


        if (sidebarType == SidebarType.NONE) {
            return;
        }

        Player player = mcMMO.p.getServer().getPlayerExact(playerName);

        if (player == null) {
            ScoreboardManager.cleanup(this);
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        if(mcMMOPlayer == null)
            return;

        switch (sidebarType) {
            case NONE:
                break;

            case SKILL_BOARD:
                Validate.notNull(targetSkill);

                if (!SkillTools.isChildSkill(targetSkill)) {
                    int currentXP = mcMMOPlayer.getSkillXpLevel(targetSkill);

                    sidebarObjective.getScore(ScoreboardManager.LABEL_CURRENT_XP).setScore(currentXP);
                    sidebarObjective.getScore(ScoreboardManager.LABEL_REMAINING_XP).setScore(mcMMOPlayer.getXpToLevel(targetSkill) - currentXP);
                }
                else {
                    for (PrimarySkillType parentSkill : FamilyTree.getParents(targetSkill)) {
                        sidebarObjective.getScore(ScoreboardManager.skillLabels.get(parentSkill)).setScore(mcMMOPlayer.getSkillLevel(parentSkill));
                    }
                }

                sidebarObjective.getScore(ScoreboardManager.LABEL_LEVEL).setScore(mcMMOPlayer.getSkillLevel(targetSkill));

                if (mcMMO.p.getSkillTools().getSuperAbility(targetSkill) != null) {
                    boolean stopUpdating;

                    if (targetSkill == PrimarySkillType.MINING) {
                        // Special-Case: Mining has two abilities, both with cooldowns
                        Score cooldownSB = sidebarObjective.getScore(ScoreboardManager.abilityLabelsSkill.get(SuperAbilityType.SUPER_BREAKER));
                        Score cooldownBM = sidebarObjective.getScore(ScoreboardManager.abilityLabelsSkill.get(SuperAbilityType.BLAST_MINING));
                        int secondsSB = Math.max(mcMMOPlayer.calculateTimeRemaining(SuperAbilityType.SUPER_BREAKER), 0);
                        int secondsBM = Math.max(mcMMOPlayer.calculateTimeRemaining(SuperAbilityType.BLAST_MINING), 0);

                        cooldownSB.setScore(secondsSB);
                        cooldownBM.setScore(secondsBM);

                        stopUpdating = (secondsSB == 0 && secondsBM == 0);
                    }
                    else {
                        SuperAbilityType ability = mcMMO.p.getSkillTools().getSuperAbility(targetSkill);
                        Score cooldown = sidebarObjective.getScore(ScoreboardManager.abilityLabelsSkill.get(ability));
                        int seconds = Math.max(mcMMOPlayer.calculateTimeRemaining(ability), 0);

                        cooldown.setScore(seconds);

                        stopUpdating = seconds == 0;
                    }

                    if (stopUpdating) {
                        stopCooldownUpdating();
                    }
                    else {
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

                    sidebarObjective.getScore(ScoreboardManager.abilityLabelsColored.get(ability)).setScore(seconds);
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
                PlayerProfile newProfile;

                if (targetProfile != null) {
                    newProfile = targetProfile; // offline
                }
                else if (targetPlayer == null) {
                    newProfile = mcMMOPlayer.getProfile(); // self
                }
                else {
                    newProfile = UserManager.getPlayer(targetPlayer).getProfile(); // online
                }

                // Calculate power level here
                int powerLevel = 0;
                for (PrimarySkillType skill : SkillTools.NON_CHILD_SKILLS) { // Don't include child skills, makes the list too long
                    int level = newProfile.getSkillLevel(skill);

                    powerLevel += level;

                    // TODO: Verify that this is what we want - calculated in power level but not displayed
                    if (!mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, skill)) {
                        continue;
                    }

                    sidebarObjective.getScore(ScoreboardManager.skillLabels.get(skill)).setScore(level);
                }

                sidebarObjective.getScore(ScoreboardManager.LABEL_POWER_LEVEL).setScore(powerLevel);
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
        Player player = mcMMO.p.getServer().getPlayerExact(playerName);

        for (PrimarySkillType skill : SkillTools.NON_CHILD_SKILLS) {
            if (!mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, skill)) {
                continue;
            }

            rank = rankData.get(skill);

            if (rank != null) {
                sidebarObjective.getScore(ScoreboardManager.skillLabels.get(skill)).setScore(rank);
            }
        }

        rank = rankData.get(null);

        if (rank != null) {
            sidebarObjective.getScore(ScoreboardManager.LABEL_POWER_LEVEL).setScore(rank);
        }
    }

    public void acceptLeaderboardData(List<PlayerStat> leaderboardData) {
        for (PlayerStat stat : leaderboardData) {
            String name = stat.name;

            if (name.equals(playerName)) {
                name = ChatColor.GOLD + "--You--";
            }
            else {
                name = " " + ChatColor.WHITE + name + " ";
            }

            sidebarObjective.getScore(name).setScore(stat.statVal);
        }
    }

    public void updatePowerLevel(Player player, int newPowerLevel) {
        powerObjective.getScore(player.getName()).setScore(newPowerLevel);
    }
}
