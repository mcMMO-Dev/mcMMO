package com.gmail.nossr50.core.events.party;

import com.gmail.nossr50.core.mcmmo.entity.Player;

/**
 * Called just before a player teleports using the /ptp command.
 */
public class McMMOPartyTeleportEvent extends PlayerTeleportEvent {
    /**
     * Rest of file is required boilerplate for custom events
     **/
    private static final HandlerList handlers = new HandlerList();
    private String party;
    private Player target;

    public McMMOPartyTeleportEvent(Player player, Player target, String party) {
        super(player, player.getLocation(), target.getLocation(), TeleportCause.COMMAND);
        this.party = party;
        this.target = target;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * @return The party the teleporting player is in
     */
    public String getParty() {
        return party;
    }

    /**
     * @return The player being teleported to
     */
    public Player getTarget() {
        return target;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
