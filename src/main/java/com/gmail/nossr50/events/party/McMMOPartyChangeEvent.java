//package com.gmail.nossr50.events.party;
//
//import com.neetgames.mcmmo.party.PartyEventReason;
//import org.bukkit.entity.Player;
//import org.bukkit.event.Cancellable;
//import org.bukkit.event.HandlerList;
//import org.bukkit.event.player.PlayerEvent;
//import org.jetbrains.annotations.NotNull;
//
///**
// * Called when a player attempts to join, leave, or change parties.
// */
//public class McMMOPartyChangeEvent extends PlayerEvent implements Cancellable {
//    private final String oldParty;
//    private final String newParty;
//    private final PartyEventReason reason;
//    private boolean cancelled;
//
//    public McMMOPartyChangeEvent(@NotNull Player player, @NotNull String oldParty, @NotNull String newParty, PartyEventReason reason) {
//        super(player);
//
//        if (newParty != null) {
//            newParty = newParty.replace(":", ".");
//        }
//
//        this.oldParty = oldParty;
//        this.newParty = newParty;
//        this.reason = reason;
//        this.cancelled = false;
//    }
//
//    /**
//     * @return The party being left, or null if the player was not in a party
//     */
//    public String getOldParty() {
//        return oldParty;
//    }
//
//    /**
//     * @return The party being joined, or null if the player is not joining a new party
//     */
//    public String getNewParty() {
//        return newParty;
//    }
//
//    /**
//     * @return The reason for the event being fired
//     */
//    public PartyEventReason getReason() {
//        return reason;
//    }
//
//    /** Following are required for Cancellable **/
//    @Override
//    public boolean isCancelled() {
//        return cancelled;
//    }
//
//    @Override
//    public void setCancelled(boolean cancelled) {
//        this.cancelled = cancelled;
//    }
//
//    /** Rest of file is required boilerplate for custom events **/
//    private static final HandlerList handlers = new HandlerList();
//
//    @Override
//    public @NotNull HandlerList getHandlers() {
//        return handlers;
//    }
//
//    public static HandlerList getHandlerList() {
//        return handlers;
//    }
//}
