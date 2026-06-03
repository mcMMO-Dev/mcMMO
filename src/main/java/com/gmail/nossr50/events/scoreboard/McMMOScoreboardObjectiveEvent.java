package com.gmail.nossr50.events.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 * @deprecated No longer fired since mcMMO moved to a packet-based sidebar (scoreboard-library) for
 * Folia/Canvas compatibility. This event exposes a Bukkit {@link Objective}, which the packet
 * implementation does not have - sidebar lines are sent directly as packets and there is no Bukkit
 * objective to register/unregister. The event class is kept only for binary compatibility with
 * external plugins; it will never be called by mcMMO.
 * <p>
 * Changing the field type from {@link Objective} to {@code String} to "revive" this event was
 * considered but rejected: it would be a binary-incompatible API break (different method
 * signature on {@link #getTargetObjective()}) that would crash any plugin compiled against the
 * old API with {@code NoSuchMethodError}, and the cancellation semantics
 * ({@link ScoreboardObjectiveEventReason#UNREGISTER_THIS_OBJECTIVE} /
 * {@link ScoreboardObjectiveEventReason#REGISTER_NEW_OBJECTIVE}) no longer map to anything in the
 * packet implementation.
 * <p>
 * <b>Suggestion for maintainers:</b> if a hook for sidebar title/content changes is needed in
 * the packet era, please add a new dedicated event (e.g. {@code McMMOSidebarUpdateEvent}) that
 * carries a {@code String} title and a list of lines. That keeps the old event's binary contract
 * intact while giving the new API clean, unambiguous semantics.
 */
@Deprecated
public class McMMOScoreboardObjectiveEvent extends McMMOScoreboardEvent implements Cancellable {
    protected boolean cancelled;

    protected Objective targetObjective;
    protected final ScoreboardObjectiveEventReason objectiveEventReason;

    public McMMOScoreboardObjectiveEvent(Objective targetObjective,
            ScoreboardObjectiveEventReason objectiveEventReason, Scoreboard scoreboard,
            Scoreboard oldboard, Player targetPlayer, ScoreboardEventReason scoreboardEventReason) {
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
