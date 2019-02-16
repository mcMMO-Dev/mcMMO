package com.gmail.nossr50.core.events.scoreboard;

import com.gmail.nossr50.core.mcmmo.entity.Player;

/**
 * This event is called when mcMMO creates its custom boards
 * You should not interfere with this event unless you understand our board code thoroughly
 * mcMMO relies on using new scoreboards to show players individually catered boards with stats specific to them
 */
public class McMMOScoreboardMakeboardEvent extends McMMOScoreboardEvent {
    public McMMOScoreboardMakeboardEvent(Scoreboard targetBoard, Scoreboard currentBoard, Player targetPlayer, ScoreboardEventReason scoreboardEventReason) {
        super(targetBoard, currentBoard, targetPlayer, scoreboardEventReason);
    }
}
