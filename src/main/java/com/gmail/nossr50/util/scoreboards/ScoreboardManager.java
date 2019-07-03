package com.gmail.nossr50.util.scoreboards;

import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.scoreboard.McMMOScoreboardMakeboardEvent;
import com.gmail.nossr50.events.scoreboard.ScoreboardEventReason;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Misc;
import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the Scoreboards used to display a variety of mcMMO related information to the player
 */
public class ScoreboardManager {
    private final mcMMO pluginRef;
    private final Map<String, ScoreboardWrapper> PLAYER_SCOREBOARDS = new HashMap<>();

    private final ScoreboardStrings scoreboardStrings;

    private List<String> dirtyPowerLevels = new ArrayList<>();

    public ScoreboardManager(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        this.scoreboardStrings = new ScoreboardStrings(pluginRef);
    }

    // Called by PlayerJoinEvent listener
    public void setupPlayer(Player player) {
        PLAYER_SCOREBOARDS.put(player.getName(), create(player));
        dirtyPowerLevels.add(player.getName());
    }

    public ScoreboardWrapper create(Player player) {
        //TODO: Christ...

        //Call our custom event
        if(pluginRef.getServer().getScoreboardManager() != null) {
            McMMOScoreboardMakeboardEvent event = new McMMOScoreboardMakeboardEvent(pluginRef.getServer().getScoreboardManager().getNewScoreboard(), player.getScoreboard(), player, ScoreboardEventReason.CREATING_NEW_SCOREBOARD);
            player.getServer().getPluginManager().callEvent(event);
            return new ScoreboardWrapper(event.getTargetPlayer(), event.getTargetBoard(), scoreboardStrings, pluginRef);

        }

        //Use the values from the event
        return null;
    }

    // **** Listener call-ins **** //

    // Called by PlayerQuitEvent listener
    public void teardownPlayer(Player player) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.remove(player.getName());

        if (wrapper != null && wrapper.revertTask != null) {
            wrapper.revertTask.cancel();
        }
    }

    // Called in onDisable()
    public void teardownAll() {
        ImmutableList<Player> onlinePlayers = ImmutableList.copyOf(pluginRef.getServer().getOnlinePlayers());
        pluginRef.debug("Tearing down scoreboards... (" + onlinePlayers.size() + ")");
        for (Player player : onlinePlayers) {
            teardownPlayer(player);
        }
    }

    // Called by ScoreboardWrapper when its Player logs off and an action tries to be performed
    public void cleanup(ScoreboardWrapper wrapper) {
        PLAYER_SCOREBOARDS.remove(wrapper.playerName);

        if (wrapper.revertTask != null) {
            wrapper.revertTask.cancel();
        }
    }

    // Called by internal level-up event listener
    public void handleLevelUp(Player player, PrimarySkillType skill) {
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

        if (pluginRef.getScoreboardSettings().getPowerLevelTagsEnabled() && !dirtyPowerLevels.contains(playerName)) {
            dirtyPowerLevels.add(playerName);
        }

        if (pluginRef.getScoreboardSettings().getConfigSectionScoreboardTypes().getConfigSectionSkillBoard().isUseThisBoard()) {
            enablePlayerSkillLevelUpScoreboard(player, skill);
        }
    }

    // Called by internal xp event listener
    public void handleXp(Player player, PrimarySkillType skill) {
        // Selfboards
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        if (wrapper != null && wrapper.isSkillScoreboard() && wrapper.targetSkill == skill && wrapper.isBoardShown()) {
            wrapper.doSidebarUpdateSoon();
        }
    }

    // Called by internal ability event listeners
    public void cooldownUpdate(Player player, PrimarySkillType skill) {
        // Selfboards
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        if (wrapper != null && (wrapper.isCooldownScoreboard() || wrapper.isSkillScoreboard() && wrapper.targetSkill == skill) && wrapper.isBoardShown()) {
            wrapper.doSidebarUpdateSoon();
        }
    }

    public void enablePlayerSkillScoreboard(Player player, PrimarySkillType skill) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        wrapper.setOldScoreboard();
        wrapper.setTypeSkill(skill);

        changeScoreboard(wrapper, pluginRef.getScoreboardSettings().getScoreboardDisplayTime(SidebarType.SKILL_BOARD));
    }

    // **** Setup methods **** //

    public void enablePlayerSkillLevelUpScoreboard(Player player, PrimarySkillType skill) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        // Do NOT run if already shown
        if (wrapper.isBoardShown()) {
            return;
        }

        wrapper.setOldScoreboard();
        wrapper.setTypeSkill(skill);

        changeScoreboard(wrapper, pluginRef.getScoreboardSettings().getConfigSectionScoreboardTypes().getConfigSectionSkillBoard().getShowBoardOnPlayerLevelUpTime());
    }

    public void enablePlayerStatsScoreboard(Player player) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        wrapper.setOldScoreboard();
        wrapper.setTypeSelfStats();

        changeScoreboard(wrapper, pluginRef.getScoreboardSettings().getScoreboardDisplayTime(SidebarType.STATS_BOARD));
    }

    public void enablePlayerInspectScoreboard(Player player, PlayerProfile targetProfile) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        wrapper.setOldScoreboard();
        wrapper.setTypeInspectStats(targetProfile);

        changeScoreboard(wrapper, pluginRef.getScoreboardSettings().getConfigSectionScoreboardTypes().getConfigSectionInspectBoard().getDisplayTimeInSeconds());
    }

    public void enablePlayerCooldownScoreboard(Player player) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        wrapper.setOldScoreboard();
        wrapper.setTypeCooldowns();

        changeScoreboard(wrapper, pluginRef.getScoreboardSettings().getScoreboardDisplayTime(SidebarType.COOLDOWNS_BOARD));
    }

    public void showPlayerRankScoreboard(Player player, Map<PrimarySkillType, Integer> rank) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        wrapper.setOldScoreboard();
        wrapper.setTypeSelfRank();
        wrapper.acceptRankData(rank);

        changeScoreboard(wrapper, pluginRef.getScoreboardSettings().getScoreboardDisplayTime(SidebarType.RANK_BOARD));
    }

    public void showPlayerRankScoreboardOthers(Player player, String targetName, Map<PrimarySkillType, Integer> rank) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        wrapper.setOldScoreboard();
        wrapper.setTypeInspectRank(targetName);
        wrapper.acceptRankData(rank);

        changeScoreboard(wrapper, pluginRef.getScoreboardSettings().getScoreboardDisplayTime(SidebarType.RANK_BOARD));
    }

    public void showTopScoreboard(Player player, PrimarySkillType skill, int pageNumber, List<PlayerStat> stats) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        wrapper.setOldScoreboard();
        wrapper.setTypeTop(skill, pageNumber);
        wrapper.acceptLeaderboardData(stats);

        changeScoreboard(wrapper, pluginRef.getScoreboardSettings().getScoreboardDisplayTime(SidebarType.TOP_BOARD));
    }

    public void showTopPowerScoreboard(Player player, int pageNumber, List<PlayerStat> stats) {
        ScoreboardWrapper wrapper = PLAYER_SCOREBOARDS.get(player.getName());

        wrapper.setOldScoreboard();
        wrapper.setTypeTopPower(pageNumber);
        wrapper.acceptLeaderboardData(stats);

        changeScoreboard(wrapper, pluginRef.getScoreboardSettings().getScoreboardDisplayTime(SidebarType.TOP_BOARD));
    }

    /**
     * @return false if power levels are disabled
     */
    public boolean powerLevelHeartbeat() {
        Objective mainObjective = getPowerLevelObjective();

        if (mainObjective == null) {
            return false; // indicates
        }

        for (String playerName : dirtyPowerLevels) {
            McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(playerName);

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

    // **** Helper methods **** //

    /**
     * Gets or creates the power level objective on the main targetBoard.
     * <p/>
     * If power levels are disabled, the objective is deleted and null is
     * returned.
     *
     * @return the main targetBoard objective, or null if disabled
     */
    public Objective getPowerLevelObjective() {
        if (!pluginRef.getScoreboardSettings().getPowerLevelTagsEnabled()) {
            Objective objective = pluginRef.getServer().getScoreboardManager().getMainScoreboard().getObjective(scoreboardStrings.POWER_OBJECTIVE);

            if (objective != null) {
                objective.unregister();
                pluginRef.debug("Removed leftover targetBoard objects from Power Level Tags.");
            }

            return null;
        }

        Objective powerObjective = pluginRef.getServer().getScoreboardManager().getMainScoreboard().getObjective(scoreboardStrings.POWER_OBJECTIVE);

        if (powerObjective == null) {
            powerObjective = pluginRef.getServer().getScoreboardManager().getMainScoreboard().registerNewObjective(scoreboardStrings.POWER_OBJECTIVE, "dummy");
            powerObjective.setDisplayName(scoreboardStrings.TAG_POWER_LEVEL);
            powerObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }

        return powerObjective;
    }

    private void changeScoreboard(ScoreboardWrapper wrapper, int displayTime) {
        if (displayTime == -1) {
            wrapper.showBoardWithNoRevert();
        } else {
            wrapper.showBoardAndScheduleRevert(displayTime * Misc.TICK_CONVERSION_FACTOR);
        }
    }

    public boolean isBoardShown(String playerName) {
        return PLAYER_SCOREBOARDS.get(playerName).isBoardShown();
    }

    public void clearBoard(String playerName) {
        PLAYER_SCOREBOARDS.get(playerName).tryRevertBoard();
    }

    public void keepBoard(String playerName) {
        PLAYER_SCOREBOARDS.get(playerName).cancelRevert();
    }

    public void setRevertTimer(String playerName, int seconds) {
        PLAYER_SCOREBOARDS.get(playerName).showBoardAndScheduleRevert(seconds * Misc.TICK_CONVERSION_FACTOR);
    }

    public List<String> getDirtyPowerLevels() {
        return dirtyPowerLevels;
    }
}
