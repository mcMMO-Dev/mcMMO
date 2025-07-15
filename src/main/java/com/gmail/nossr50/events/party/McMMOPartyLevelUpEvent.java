package com.gmail.nossr50.events.party;

import com.gmail.nossr50.datatypes.party.Party;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class McMMOPartyLevelUpEvent extends Event implements Cancellable {
    private final Party party;
    private int levelsChanged;
    private boolean cancelled;

    public McMMOPartyLevelUpEvent(Party party, int levelsChanged) {
        this.party = party;
        this.levelsChanged = levelsChanged;
        this.cancelled = false;
    }

    public Party getParty() {
        return party;
    }

    public int getLevelsChanged() {
        return levelsChanged;
    }

    public void setLevelsChanged(int levelsChanged) {
        this.levelsChanged = levelsChanged;
    }

    /**
     * Following are required for Cancellable
     **/
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Rest of file is required boilerplate for custom events
     **/
    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
