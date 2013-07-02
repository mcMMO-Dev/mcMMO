package com.gmail.nossr50.util.scoreboards;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import com.gmail.nossr50.mcMMO;

public class ScoreboardWrapper {
    public final String playerName;
    public final Scoreboard board;
    public Scoreboard oldBoard;

    private ScoreboardWrapper(String playerName, Scoreboard s) {
        this.playerName = playerName;
        board = s;
    }

    public static ScoreboardWrapper create(Player p) {
        return new ScoreboardWrapper(p.getName(), mcMMO.p.getServer().getScoreboardManager().getNewScoreboard());
    }
}
