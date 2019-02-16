package com.gmail.nossr50.core.events.scoreboard;

import com.gmail.nossr50.core.mcmmo.entity.Player;

/**
 * The parent class of all mcMMO scoreboard events
 * All scoreboard events will extend from this
 */
abstract public class McMMOScoreboardEvent extends Event {

    /**
     * Rest of file is required boilerplate for custom events
     **/
    private static final HandlerList handlers = new HandlerList();
    final Scoreboard currentBoard; //Can be null
    private final ScoreboardEventReason scoreboardEventReason;
    protected Scoreboard targetBoard; //Scoreboard involved in this event
    protected Player targetPlayer;

    /** GETTER & SETTER BOILERPLATE **/

    public McMMOScoreboardEvent(Scoreboard targetBoard, Scoreboard currentBoard, Player targetPlayer, ScoreboardEventReason scoreboardEventReason) {
        this.scoreboardEventReason = scoreboardEventReason;
        this.targetBoard = targetBoard;
        this.currentBoard = currentBoard;
        this.targetPlayer = targetPlayer;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * This is the scoreboard the player will be assigned to after this event
     *
     * @return the target board to assign the player after this event fires
     */
    public Scoreboard getTargetBoard() {
        return targetBoard;
    }

    /**
     * Change the scoreboard that the player will be assigned to after this event fires
     *
     * @param targetBoard the new board to assign the player to
     */
    public void setTargetBoard(Scoreboard targetBoard) {
        this.targetBoard = targetBoard;
    }

    /**
     * The player involved in this event (this can be changed)
     *
     * @return the player involved in this event
     */
    public Player getTargetPlayer() {
        return targetPlayer;
    }

    /**
     * Change the target player for this event
     *
     * @param targetPlayer the new target for this event
     */
    public void setTargetPlayer(Player targetPlayer) {
        this.targetPlayer = targetPlayer;
    }

    /**
     * This is the scoreboard the player is currently assigned to at the time the event was fired
     * Grabbed via player.getScoreboard()
     *
     * @return players current scoreboard
     */
    public Scoreboard getCurrentBoard() {
        return currentBoard;
    }

    /**
     * The ENUM defining the reason for this event
     *
     * @return the reason for this event
     */
    public ScoreboardEventReason getScoreboardEventReason() {
        return scoreboardEventReason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
