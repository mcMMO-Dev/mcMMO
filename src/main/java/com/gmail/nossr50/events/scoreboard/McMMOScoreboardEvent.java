package com.gmail.nossr50.events.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

/**
 * The parent class of all mcMMO scoreboard events
 * All scoreboard events will extend from this
 */
abstract public class McMMOScoreboardEvent extends Event {

    protected Scoreboard targetBoard; //Scoreboard involved in this event
    final Scoreboard currentBoard; //Can be null
    protected Player targetPlayer;

    private final ScoreboardEventReason scoreboardEventReason;

    public McMMOScoreboardEvent(Scoreboard targetBoard, Scoreboard currentBoard, Player targetPlayer, ScoreboardEventReason scoreboardEventReason) {
        this.scoreboardEventReason = scoreboardEventReason;
        this.targetBoard = targetBoard;
        this.currentBoard = currentBoard;
        this.targetPlayer = targetPlayer;
    }

    /* GETTER & SETTER BOILERPLATE **/

    /**
     * This is the scoreboard the player will be assigned to after this event
     * @return the target board to assign the player after this event fires
     */
    public Scoreboard getTargetBoard() {
        return targetBoard;
    }

    /**
     * Change the scoreboard that the player will be assigned to after this event fires
     * @param targetBoard the new board to assign the player to
     */
    public void setTargetBoard(Scoreboard targetBoard) {
        this.targetBoard = targetBoard;
    }

    /**
     * The player involved in this event (this can be changed)
     * @return the player involved in this event
     */
    public Player getTargetPlayer() {
        return targetPlayer;
    }

    /**
     * This is the scoreboard the player is currently assigned to at the time the event was fired
     * Grabbed via player.getScoreboard()
     * @return players current scoreboard
     */
    public Scoreboard getCurrentBoard() {
        return currentBoard;
    }

    /**
     * The ENUM defining the reason for this event
     * @return the reason for this event
     */
    public ScoreboardEventReason getScoreboardEventReason() {
        return scoreboardEventReason;
    }

    /**
     * Change the target player for this event
     * @param targetPlayer the new target for this event
     */
    public void setTargetPlayer(Player targetPlayer) {
        this.targetPlayer = targetPlayer;
    }

    /** Rest of file is required boilerplate for custom events **/
    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
