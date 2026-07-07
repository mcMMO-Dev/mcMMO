package com.gmail.nossr50.util.scoreboards;

import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.events.scoreboard.McMMOScoreboardRevertEvent;
import com.gmail.nossr50.events.scoreboard.ScoreboardEventReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager.SidebarType;
import com.gmail.nossr50.util.scoreboards.backend.PlayerBoard;
import com.gmail.nossr50.util.scoreboards.backend.SidebarLine;
import com.gmail.nossr50.util.skills.SkillTools;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Wraps a backend-specific player board for a single player.
 */
public class ScoreboardWrapper {
    private static final int MAX_LINES = 15;

    // Initialization variables
    public final String playerName;
    public final Player player;
    private final PlayerBoard playerBoard;
    private boolean tippedKeep = false;
    private boolean tippedClear = false;
    private Scoreboard oldBoard = null;

    // Internal usage variables (should exist)
    private SidebarType sidebarType;

    // Parameter variables (May be null / invalid)
    public String targetPlayer = null;
    public PrimarySkillType targetSkill = null;
    private PlayerProfile targetProfile = null;
    public int leaderboardPage = -1;

    // Data supplied by the manager for RANK/TOP boards, consumed by render()
    private Map<PrimarySkillType, Integer> rankData = null;
    private List<PlayerStat> leaderboardData = null;

    public ScoreboardWrapper(Player player, PlayerBoard playerBoard) {
        this.player = player;
        this.playerName = player.getName();
        this.sidebarType = SidebarType.NONE;
        this.playerBoard = playerBoard;
    }

    public WrappedTask updateTask = null;

    private class ScoreboardQuickUpdate implements Runnable {
        @Override
        public void run() {
            render();
            updateTask = null;
        }
    }

    public WrappedTask revertTask = null;

    private class ScoreboardChangeTask implements Runnable {
        @Override
        public void run() {
            tryRevertBoard();
            revertTask = null;
        }
    }

    public WrappedTask cooldownTask = null;

    private class ScoreboardCooldownTask implements Runnable {
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

    public void doSidebarUpdateSoon() {
        if (updateTask == null) {
            // To avoid spamming the scheduler, store the instance and run 2 ticks later
            updateTask = mcMMO.p.getFoliaLib().getScheduler()
                    .runAtEntityLater(player, new ScoreboardQuickUpdate(), 2L);
        }
    }

    private void startCooldownUpdating() {
        if (cooldownTask == null) {
            // Repeat every 5 seconds.
            // Cancels once all cooldowns are done, using stopCooldownUpdating().
            cooldownTask = mcMMO.p.getFoliaLib().getScheduler()
                    .runAtEntityTimer(player, new ScoreboardCooldownTask(),
                            5 * Misc.TICK_CONVERSION_FACTOR, 5 * Misc.TICK_CONVERSION_FACTOR);
        }
    }

    private void stopCooldownUpdating() {
        if (cooldownTask != null) {
            try {
                cooldownTask.cancel();
            } catch (Exception e) {
                LogUtils.debug(mcMMO.p.getLogger(),
                        "Unable to cancel cooldown scoreboard task for " + playerName + ": "
                                + e.getMessage());
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

    public void showBoardWithNoRevert() {
        final Player onlinePlayer = mcMMO.p.getServer().getPlayerExact(playerName);

        if (onlinePlayer == null) {
            ScoreboardManager.cleanup(this);
            return;
        }

        if (revertTask != null) {
            revertTask.cancel();
        }

        final boolean alreadyShown = playerBoard.isShown();
        final Scoreboard previousBoard = playerBoard.show();

        if (ScoreboardManager.isBukkitBackendActive()) {
            if (alreadyShown && oldBoard == null && Bukkit.getScoreboardManager() != null) {
                oldBoard = Bukkit.getScoreboardManager().getMainScoreboard();
            } else if (!alreadyShown) {
                oldBoard = previousBoard;
            }
        }

        revertTask = null;
    }

    public void showBoardAndScheduleRevert(int ticks) {
        final Player onlinePlayer = mcMMO.p.getServer().getPlayerExact(playerName);

        if (onlinePlayer == null) {
            ScoreboardManager.cleanup(this);
            return;
        }

        if (revertTask != null) {
            revertTask.cancel();
        }

        final boolean alreadyShown = playerBoard.isShown();
        final Scoreboard previousBoard = playerBoard.show();
        if (ScoreboardManager.isBukkitBackendActive()) {
            if (alreadyShown && oldBoard == null && Bukkit.getScoreboardManager() != null) {
                oldBoard = Bukkit.getScoreboardManager().getMainScoreboard();
            } else if (!alreadyShown) {
                oldBoard = previousBoard;
            }
        }

        revertTask = mcMMO.p.getFoliaLib().getScheduler()
                .runAtEntityLater(onlinePlayer, new ScoreboardChangeTask(), ticks);

        if (UserManager.getPlayer(playerName) == null) {
            return;
        }

        PlayerProfile profile = UserManager.getPlayer(onlinePlayer).getProfile();

        if (profile.getScoreboardTipsShown() >= mcMMO.p.getGeneralConfig().getTipsAmount()) {
            return;
        }

        if (!tippedKeep) {
            tippedKeep = true;
            onlinePlayer.sendMessage(LocaleLoader.getString("Commands.Scoreboard.Tip.Keep"));
        } else if (!tippedClear) {
            tippedClear = true;
            onlinePlayer.sendMessage(LocaleLoader.getString("Commands.Scoreboard.Tip.Clear"));
            profile.increaseTipsShown();
        }
    }

    public void tryRevertBoard() {
        Player onlinePlayer = mcMMO.p.getServer().getPlayerExact(playerName);

        if (onlinePlayer == null) {
            ScoreboardManager.cleanup(this);
            return;
        }

        if (ScoreboardManager.isBukkitBackendActive()) {
            if (oldBoard != null && isBoardShown()) {
                McMMOScoreboardRevertEvent event = new McMMOScoreboardRevertEvent(
                        oldBoard,
                        onlinePlayer.getScoreboard(),
                        onlinePlayer,
                        ScoreboardEventReason.REVERTING_BOARD);
                onlinePlayer.getServer().getPluginManager().callEvent(event);
                onlinePlayer = event.getTargetPlayer();
                playerBoard.hide(onlinePlayer, event.getTargetBoard());
                oldBoard = null;
            } else if (oldBoard != null) {
                LogUtils.debug(mcMMO.p.getLogger(),
                        "Not reverting scoreboard for "
                                + playerName
                                + " - scoreboard was changed by another plugin.");
            }
        } else if (isBoardShown()) {
            McMMOScoreboardRevertEvent event = new McMMOScoreboardRevertEvent(
                    onlinePlayer.getScoreboard(),
                    onlinePlayer.getScoreboard(),
                    onlinePlayer,
                    ScoreboardEventReason.REVERTING_BOARD);
            onlinePlayer.getServer().getPluginManager().callEvent(event);
            onlinePlayer = event.getTargetPlayer();
            playerBoard.hide(onlinePlayer, event.getTargetBoard());
        } else {
            playerBoard.hide(onlinePlayer, null);
        }

        cancelRevert();

        sidebarType = SidebarType.NONE;
        targetPlayer = null;
        targetSkill = null;
        targetProfile = null;
        leaderboardPage = -1;
        rankData = null;
        leaderboardData = null;
    }

    public boolean isBoardShown() {
        Player onlinePlayer = mcMMO.p.getServer().getPlayerExact(playerName);

        if (onlinePlayer == null) {
            ScoreboardManager.cleanup(this);
            return false;
        }

        return playerBoard.isShown();
    }

    public void cancelRevert() {
        if (revertTask == null) {
            return;
        }

        revertTask.cancel();
        revertTask = null;
    }

    /**
     * Releases backend resources and scheduled tasks.
     */
    public void close() {
        try {
            stopCooldownUpdating();
            cancelRevert();
            if (updateTask != null) {
                updateTask.cancel();
                updateTask = null;
            }
        } catch (Exception ignored) {
            // best-effort task cleanup
        }

        playerBoard.close();
        ScoreboardManager.onPlayerBoardClosed(playerName);
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

    public void setTypeInspectStats(@NotNull McMMOPlayer mmoPlayer) {
        this.sidebarType = SidebarType.STATS_BOARD;
        targetPlayer = mmoPlayer.getPlayer().getName();
        targetProfile = mmoPlayer.getProfile();

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
        loadObjective(String.format("%s (%2d - %2d)", ScoreboardManager.POWER_LEVEL, startPosition,
                endPosition));
    }

    public void setTypeTop(PrimarySkillType skill, int page) {
        this.sidebarType = SidebarType.TOP_BOARD;
        leaderboardPage = page;
        targetSkill = skill;

        targetPlayer = null;
        targetProfile = null;

        int endPosition = page * 10;
        int startPosition = endPosition - 9;
        loadObjective(String.format("%s (%2d - %2d)", ScoreboardManager.skillLabels.get(skill),
                startPosition, endPosition));
    }

    /**
     * Sets the sidebar title and re-renders. Replaces the old register/unregister objective flow.
     */
    protected void loadObjective(String displayName) {
        playerBoard.setTitle(displayName);
        render();
    }

    /**
     * Pushes an ordered list of rows to the active backend board.
     */
    private void drawLines(List<SidebarLine> lines) {
        int count = Math.min(lines.size(), MAX_LINES);
        List<SidebarLine> toRender = lines;

        if (count != lines.size()) {
            toRender = new ArrayList<>(lines.subList(0, count));
        }

        playerBoard.draw(toRender);
    }

    /**
     * Recomputes and redraws every row based on the current {@link SidebarType}. Replaces the old
     * {@code updateSidebar()}; the data sources and per-type logic mirror the original board.
     */
    private void render() {
        if (updateTask != null) {
            try {
                updateTask.cancel();
            } catch (Exception e) {
                LogUtils.debug(mcMMO.p.getLogger(),
                        "Unable to cancel sidebar update task for " + playerName + ": "
                                + e.getMessage());
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

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        if (mmoPlayer == null) {
            return;
        }

        final List<SidebarLine> lines = new ArrayList<>();

        switch (sidebarType) {
            case NONE:
                break;

            case SKILL_BOARD:
                renderSkill(player, mmoPlayer, lines);
                break;

            case COOLDOWNS_BOARD:
                renderCooldowns(mmoPlayer, lines);
                break;

            case STATS_BOARD:
                renderStats(player, mmoPlayer, lines);
                break;

            case RANK_BOARD:
                renderRank(player, lines);
                break;

            case TOP_BOARD:
                renderLeaderboard(lines);
                break;

            default:
                break;
        }

        drawLines(lines);
    }

    private void renderSkill(Player player, McMMOPlayer mmoPlayer, List<SidebarLine> lines) {
        if (!SkillTools.isChildSkill(targetSkill)) {
            int currentXP = mmoPlayer.getSkillXpLevel(targetSkill);
            lines.add(new SidebarLine(ScoreboardManager.LABEL_LEVEL,
                    mmoPlayer.getSkillLevel(targetSkill)));
            lines.add(new SidebarLine(ScoreboardManager.LABEL_CURRENT_XP, currentXP));
            lines.add(new SidebarLine(ScoreboardManager.LABEL_REMAINING_XP,
                    mmoPlayer.getXpToLevel(targetSkill) - currentXP));
        } else {
            for (PrimarySkillType parentSkill : mcMMO.p.getSkillTools()
                    .getChildSkillParents(targetSkill)) {
                lines.add(new SidebarLine(ScoreboardManager.skillLabels.get(parentSkill),
                        mmoPlayer.getSkillLevel(parentSkill)));
            }
            lines.add(new SidebarLine(ScoreboardManager.LABEL_LEVEL,
                    mmoPlayer.getSkillLevel(targetSkill)));
        }

        if (mcMMO.p.getSkillTools().getSuperAbility(targetSkill) != null) {
            boolean stopUpdating;

            if (targetSkill == PrimarySkillType.MINING) {
                // Special-Case: Mining has two abilities, both with cooldowns
                int secondsSB = Math.max(
                        mmoPlayer.calculateTimeRemaining(SuperAbilityType.SUPER_BREAKER), 0);
                int secondsBM = Math.max(
                        mmoPlayer.calculateTimeRemaining(SuperAbilityType.BLAST_MINING), 0);

                lines.add(new SidebarLine(
                        ScoreboardManager.abilityLabelsSkill.get(SuperAbilityType.SUPER_BREAKER),
                        secondsSB));
                lines.add(new SidebarLine(
                        ScoreboardManager.abilityLabelsSkill.get(SuperAbilityType.BLAST_MINING),
                        secondsBM));

                stopUpdating = (secondsSB == 0 && secondsBM == 0);
            } else {
                SuperAbilityType ability = mcMMO.p.getSkillTools().getSuperAbility(targetSkill);
                int seconds = Math.max(mmoPlayer.calculateTimeRemaining(ability), 0);

                lines.add(new SidebarLine(ScoreboardManager.abilityLabelsSkill.get(ability),
                        seconds));

                stopUpdating = seconds == 0;
            }

            if (stopUpdating) {
                stopCooldownUpdating();
            } else {
                startCooldownUpdating();
            }
        }
    }

    private void renderCooldowns(McMMOPlayer mmoPlayer, List<SidebarLine> lines) {
        boolean anyCooldownsActive = false;

        for (SuperAbilityType ability : SuperAbilityType.values()) {
            int seconds = Math.max(mmoPlayer.calculateTimeRemaining(ability), 0);

            if (seconds != 0) {
                anyCooldownsActive = true;
            }

            lines.add(new SidebarLine(ScoreboardManager.abilityLabelsColored.get(ability), seconds));
        }

        if (anyCooldownsActive) {
            startCooldownUpdating();
        } else {
            stopCooldownUpdating();
        }
    }

    private void renderStats(Player player, McMMOPlayer mmoPlayer, List<SidebarLine> lines) {
        // Select the profile to read from
        PlayerProfile newProfile;

        if (targetProfile != null) {
            newProfile = targetProfile; // offline
        } else if (targetPlayer == null) {
            newProfile = mmoPlayer.getProfile(); // self
        } else {
            newProfile = UserManager.getPlayer(targetPlayer).getProfile(); // online
        }

        int powerLevel = 0;
        // Don't include child skills, makes the list too long
        for (PrimarySkillType skill : SkillTools.NON_CHILD_SKILLS) {
            // Skills hidden by permission stay out of the power level total so the
            // total always matches the rows shown and the permission-aware power level
            if (!mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, skill)) {
                continue;
            }

            int level = newProfile.getSkillLevel(skill);
            powerLevel += level;

            lines.add(new SidebarLine(ScoreboardManager.skillLabels.get(skill), level));
        }

        // Sort skills by level descending to mirror the old score-sorted Bukkit board
        lines.sort((a, b) -> Integer.compare(b.value(), a.value()));
        lines.add(new SidebarLine(ScoreboardManager.LABEL_POWER_LEVEL, powerLevel));
    }

    private void renderRank(Player player, List<SidebarLine> lines) {
        if (rankData == null) {
            return;
        }

        for (PrimarySkillType skill : SkillTools.NON_CHILD_SKILLS) {
            if (!mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, skill)) {
                continue;
            }

            Integer rank = rankData.get(skill);
            if (rank != null) {
                lines.add(new SidebarLine(ScoreboardManager.skillLabels.get(skill), rank));
            }
        }

        Integer powerRank = rankData.get(null);
        if (powerRank != null) {
            lines.add(new SidebarLine(ScoreboardManager.LABEL_POWER_LEVEL, powerRank));
        }
    }

    private void renderLeaderboard(List<SidebarLine> lines) {
        if (leaderboardData == null) {
            return;
        }

        for (PlayerStat stat : leaderboardData) {
            String name = stat.playerName();

            if (name.equals(playerName)) {
                name = ChatColor.GOLD + "--You--";
            }

            lines.add(new SidebarLine(name, stat.value()));
        }

        // Highest score at the top
        lines.sort((a, b) -> Integer.compare(b.value(), a.value()));
    }

    public void acceptRankData(Map<PrimarySkillType, Integer> rankData) {
        this.rankData = rankData;
        render();
    }

    public void acceptLeaderboardData(@NotNull List<PlayerStat> leaderboardData) {
        this.leaderboardData = leaderboardData;
        render();
    }
}
