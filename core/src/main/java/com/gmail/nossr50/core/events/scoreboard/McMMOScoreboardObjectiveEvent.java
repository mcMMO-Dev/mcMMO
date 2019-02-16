package com.gmail.nossr50.core.events.scoreboard;

import com.gmail.nossr50.core.mcmmo.entity.Player;

public class McMMOScoreboardObjectiveEvent extends McMMOScoreboardEvent implements Cancellable {
    protected final ScoreboardObjectiveEventReason objectiveEventReason;
    protected boolean cancelled;
    protected Objective targetObjective;

    public McMMOScoreboardObjectiveEvent(Objective targetObjective, ScoreboardObjectiveEventReason objectiveEventReason, Scoreboard scoreboard, Scoreboard oldboard, Player targetPlayer, ScoreboardEventReason scoreboardEventReason) {
        super(scoreboard, oldboard, targetPlayer, scoreboardEventReason);
        this.objectiveEventReason = objectiveEventReason;
        this.targetObjective = targetObjective;
        cancelled = false;
    }

    /**
     * The objective that will be modified by this event
     *
     * @return
     */
    public Objective getTargetObjective() {
        return targetObjective;
    }

    /**
     * Change the target objective for this event
     *
     * @param newObjective new target objective
     */
    public void setTargetObjective(Objective newObjective) {
        this.targetObjective = newObjective;
    }

    public ScoreboardObjectiveEventReason getObjectiveEventReason() {
        return objectiveEventReason;
    }

    /* BOILERPLATE FROM INTERFACES */

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
