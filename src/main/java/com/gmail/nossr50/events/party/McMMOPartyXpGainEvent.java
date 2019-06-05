package com.gmail.nossr50.events.party;

import com.gmail.nossr50.datatypes.party.Party;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class McMMOPartyXpGainEvent extends Event implements Cancellable {
    /**
     * Rest of file is required boilerplate for custom events
     **/
    private static final HandlerList handlers = new HandlerList();
    private Party party;
    private double xpGained;
    private boolean cancelled;

    public McMMOPartyXpGainEvent(Party party, double xpGained) {
        this.party = party;
        this.xpGained = xpGained;
        this.cancelled = false;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Party getParty() {
        return party;
    }

    /**
     * @return The amount of experience gained in this event
     */
    public double getRawXpGained() {
        return xpGained;
    }

    /**
     * @param xpGained set amount of experience gained in this event
     */
    public void setRawXpGained(double xpGained) {
        this.xpGained = xpGained;
    }

    /**
     * @return int amount of experience gained in this event
     */
    @Deprecated
    public int getXpGained() {
        return (int) xpGained;
    }

    /**
     * @param xpGained set int amount of experience gained in this event
     */
    @Deprecated
    public void setXpGained(int xpGained) {
        this.xpGained = xpGained;
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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
