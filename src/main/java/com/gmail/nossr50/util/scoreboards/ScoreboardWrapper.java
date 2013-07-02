package com.gmail.nossr50.util.scoreboards;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager.SidebarType;

public class ScoreboardWrapper {
    public static final String SIDEBAR_OBJECTIVE = "mcmmo_sidebar";
    public static final String POWER_OBJECTIVE = "mcmmo_pwrlvl";

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

    // Board Type Changes
    public void setTypeNone() {
        this.sidebarType = SidebarType.NONE;

        targetPlayer = null;
        targetSkill = null;
        leaderboardPage = -1;

        update();
    }

    public void setTypeSkill(SkillType skill) {
        this.sidebarType = SidebarType.SKILL_BOARD;
        targetSkill = skill;

        targetPlayer = null;
        leaderboardPage = -1;

        update();
    }

    public void setTypeSelfStats() {
        this.sidebarType = SidebarType.STATS_BOARD;

        targetPlayer = null;
        targetSkill = null;
        leaderboardPage = -1;

        update();
    }

    public void setTypeInspectStats(String otherPlayer) {
        this.sidebarType = SidebarType.STATS_BOARD;
        targetPlayer = otherPlayer;

        targetSkill = null;
        leaderboardPage = -1;

        update();
    }

    public void setTypeSelfRank() {
        this.sidebarType = SidebarType.RANK_BOARD;
        targetPlayer = null;

        targetSkill = null;
        leaderboardPage = -1;

        update();
    }

    public void setTypeInspectRank(String otherPlayer) {
        this.sidebarType = SidebarType.RANK_BOARD;
        targetPlayer = otherPlayer;

        targetSkill = null;
        leaderboardPage = -1;

        update();
    }

    public void setTypeTopPower(int page) {
        this.sidebarType = SidebarType.TOP_BOARD;
        leaderboardPage = page;
        targetSkill = null;

        targetPlayer = null;

        update();
    }

    public void setTypeTop(SkillType skill, int page) {
        this.sidebarType = SidebarType.TOP_BOARD;
        leaderboardPage = page;
        targetSkill = skill;

        targetPlayer = null;

        update();
    }

    public void update() {

    }
}
