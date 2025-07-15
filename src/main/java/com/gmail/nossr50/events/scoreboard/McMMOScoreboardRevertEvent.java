package com.gmail.nossr50.events.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

/**
 * This event is called when mcMMO is attempting to change a players targetBoard back to their
 * previous board This is used when an mcMMO board is cleared (removed from the screen), changing
 * back from a temporary board (usually from a delayed scheduled task or our mcscoreboard time
 * command)
 */
public class McMMOScoreboardRevertEvent extends McMMOScoreboardEvent {
    public McMMOScoreboardRevertEvent(Scoreboard targetBoard, Scoreboard currentBoard,
            Player targetPlayer, ScoreboardEventReason scoreboardEventReason) {
        super(targetBoard, currentBoard, targetPlayer, scoreboardEventReason);
    }
}
