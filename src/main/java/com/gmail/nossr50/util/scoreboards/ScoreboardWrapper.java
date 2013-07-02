package com.gmail.nossr50.util.scoreboards;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager.SidebarType;
import com.gmail.nossr50.util.skills.SkillUtils;

public class ScoreboardWrapper {
    public static final String SIDEBAR_OBJECTIVE = "mcmmo_sidebar";
    public static final String POWER_OBJECTIVE = "mcmmo_pwrlvl";

    public static final OfflinePlayer labelLevel = Bukkit.getOfflinePlayer("Level");
    public static final OfflinePlayer labelCurrentXp = Bukkit.getOfflinePlayer("Current XP");
    public static final OfflinePlayer labelRemainingXp = Bukkit.getOfflinePlayer("Remaining XP");
    public static final OfflinePlayer labelPowerLevel = Bukkit.getOfflinePlayer(ChatColor.GOLD + "Power Level");
    public static final Map<SkillType, OfflinePlayer> skillLabels;

    static {
        skillLabels = new HashMap<SkillType, OfflinePlayer>();
        for (SkillType type : SkillType.values()) {
            // Include child skills
            skillLabels.put(type, Bukkit.getOfflinePlayer(SkillUtils.getSkillName(type)));
        }
    }

    // Initialization variables
    public final String playerName;
    public final Scoreboard board;

    // Internal usage variables (should exist)
    public SidebarType sidebarType;
    public Objective sidebarObj;
    public Objective powerObj;

    // Parameter variables (May be null / invalid)
    public Scoreboard oldBoard = null;
    public String targetPlayer = null;
    public SkillType targetSkill = null;
    public int leaderboardPage = -1;

    private ScoreboardWrapper(String playerName, Scoreboard s) {
        this.playerName = playerName;
        board = s;
        sidebarType = SidebarType.NONE;
        sidebarObj = board.registerNewObjective(SIDEBAR_OBJECTIVE, "dummy");
        powerObj = board.registerNewObjective(POWER_OBJECTIVE, "dummy");
    }

    public static ScoreboardWrapper create(Player p) {
        return new ScoreboardWrapper(p.getName(), mcMMO.p.getServer().getScoreboardManager().getNewScoreboard());
    }

    // Board Type Changing 'API' methods
    public void setTypeNone() {
        this.sidebarType = SidebarType.NONE;

        targetPlayer = null;
        targetSkill = null;
        leaderboardPage = -1;

        loadObjective("");
    }

    public void setTypeSkill(SkillType skill) {
        this.sidebarType = SidebarType.SKILL_BOARD;
        targetSkill = skill;

        targetPlayer = null;
        leaderboardPage = -1;

        loadObjective(SkillUtils.getSkillName(skill));
    }

    public void setTypeSelfStats() {
        this.sidebarType = SidebarType.STATS_BOARD;

        targetPlayer = null;
        targetSkill = null;
        leaderboardPage = -1;

        loadObjective("mcMMO Stats");
    }

    public void setTypeInspectStats(String otherPlayer) {
        this.sidebarType = SidebarType.STATS_BOARD;
        targetPlayer = otherPlayer;

        targetSkill = null;
        leaderboardPage = -1;

        loadObjective("mcMMO Stats");
    }

    public void setTypeSelfRank() {
        this.sidebarType = SidebarType.RANK_BOARD;
        targetPlayer = null;

        targetSkill = null;
        leaderboardPage = -1;

        loadObjective("mcMMO Rankings");
    }

    public void setTypeInspectRank(String otherPlayer) {
        this.sidebarType = SidebarType.RANK_BOARD;
        targetPlayer = otherPlayer;

        targetSkill = null;
        leaderboardPage = -1;

        loadObjective("mcMMO Rankings");
    }

    public void setTypeTopPower(int page) {
        this.sidebarType = SidebarType.TOP_BOARD;
        leaderboardPage = page;
        targetSkill = null;

        targetPlayer = null;

        int endPosition = page * 15;
        int startPosition = endPosition - 14;
        loadObjective(String.format("Power Level (%2d - %2d)", startPosition, endPosition));
    }

    public void setTypeTop(SkillType skill, int page) {
        this.sidebarType = SidebarType.TOP_BOARD;
        leaderboardPage = page;
        targetSkill = skill;

        targetPlayer = null;

        int endPosition = page * 15;
        int startPosition = endPosition - 14;
        loadObjective(String.format("%s (%2d - %2d)", SkillUtils.getSkillName(skill), startPosition, endPosition));
    }

    // Setup for after a board type change
    protected void loadObjective(String displayName) {
        sidebarObj.unregister();
        sidebarObj = board.registerNewObjective(SIDEBAR_OBJECTIVE, "");

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
        if (sidebarType == SidebarType.NONE) {
            return;
        }

        McMMOPlayer mcPlayer = UserManager.getPlayer(playerName);
        PlayerProfile profile = mcPlayer.getProfile();
        Player bukkitPlayer = mcPlayer.getPlayer();
        Server server = Bukkit.getServer();

        switch (sidebarType) {
        case NONE:
            break;

        case SKILL_BOARD:
            Validate.notNull(targetSkill);
            int currentXP = profile.getSkillXpLevel(targetSkill);
            sidebarObj.getScore(labelLevel).setScore(profile.getSkillLevel(targetSkill));
            sidebarObj.getScore(labelCurrentXp).setScore(currentXP);
            sidebarObj.getScore(labelRemainingXp).setScore(profile.getXpToLevel(targetSkill) - currentXP);
            break;

        case STATS_BOARD:
            // Select the target profile, which is self if target==null
            PlayerProfile prof;
            if (targetPlayer == null) {
                prof = profile;
            }
            else {
                prof = UserManager.getPlayer(targetPlayer).getProfile();
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
                sidebarObj.getScore(skillLabels.get(skill)).setScore(level);
            }
            sidebarObj.getScore(labelPowerLevel).setScore(powerLevel);
            break;

        case RANK_BOARD:
            Map<String, Integer> rankData = mcMMO.getDatabaseManager().readRank(targetPlayer == null ? playerName : targetPlayer);
            Integer rank;

            for (SkillType skill : SkillType.nonChildSkills()) {
                if (!Permissions.skillEnabled(bukkitPlayer, skill)) {
                    continue;
                }

                rank = rankData.get(skill);
                if (rank != null) {
                    sidebarObj.getScore(skillLabels.get(skill)).setScore(rank);
                }
            }
            rank = rankData.get("ALL");
            if (rank != null) {
                sidebarObj.getScore(labelPowerLevel).setScore(rank);
            }
            break;

        case TOP_BOARD:
            String skillQuery = (targetSkill == null) ? "all" : targetSkill.name();

            List<PlayerStat> leaderboardData = mcMMO.getDatabaseManager().readLeaderboard(skillQuery, leaderboardPage, 15);

            for (PlayerStat stat : leaderboardData) {
                String statname = stat.name;
                if (statname.equals(playerName)) {
                    statname = ChatColor.GOLD + "--You--";
                }
                sidebarObj.getScore(Bukkit.getOfflinePlayer(statname)).setScore(stat.statVal);
            }
        }
    }
}
