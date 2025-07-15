package com.gmail.nossr50.events.skills.taming;

import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerExperienceEvent;
import com.google.common.base.Preconditions;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player has tamed an entity, and we are about to award them experience for it.
 */
public class McMMOPlayerTameEntityEvent extends McMMOPlayerExperienceEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final McMMOPlayer mmoPlayer;
    private float xpGained;
    private final Entity tamedEntity;

    public McMMOPlayerTameEntityEvent(@NotNull McMMOPlayer mmoPlayer, float xp,
            @NotNull Entity tamedEntity) {
        super(mmoPlayer.getPlayer(), PrimarySkillType.TAMING, XPGainReason.PVE);
        this.mmoPlayer = mmoPlayer;
        this.xpGained = xp;
        this.tamedEntity = tamedEntity;
    }

    /**
     * @return The {@link McMMOPlayer} associated with this event.
     */
    @NotNull
    public McMMOPlayer getMcMMOPlayer() {
        return mmoPlayer;
    }

    /**
     * @return The raw experience that the player will receive.
     */
    public float getXpGained() {
        return this.xpGained;
    }

    /**
     * @param xpGained The raw experience that the player will receive.
     * @throws IllegalArgumentException if xpGained is NaN, infinite, or not positive.
     */
    public void setXpGained(float xpGained) {
        Preconditions.checkArgument(Float.isFinite(xpGained), "new gained xp must be a number");
        Preconditions.checkArgument(xpGained > 0, "new gained xp must be a positive number");

        this.xpGained = xpGained;
    }

    @NotNull
    public Entity getTamedEntity() {
        return tamedEntity;
    }

    /**
     * @apiNote Cancelling this event prevents experience from being awarded, but the entity will
     * remain tamed.
     */
    @Override
    public void setCancelled(boolean cancelled) {
        super.setCancelled(cancelled);
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
