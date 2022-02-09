package com.gmail.nossr50.events.party;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called just before a player teleports using the /ptp command.
 */
public class McMMOPartyTeleportEvent extends PlayerTeleportEvent {
    private final String party;
    private final Player target;

    public McMMOPartyTeleportEvent(Player player, Player target, String party) {
        super(player, player.getLocation(), target.getLocation(), TeleportCause.COMMAND);
        this.party = party;
        this.target = target;
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

    /** Rest of file is required boilerplate for custom events **/
    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
