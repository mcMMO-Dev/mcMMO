package com.gmail.nossr50.events.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Event fired when a custom block from a third-party plugin is broken.
 * <p>
 * This event allows custom block plugins (like Oraxen, ItemsAdder) to integrate
 * with mcMMO's skill system. Plugins can either:
 * <ul>
 *   <li>Pre-register blocks using {@link com.gmail.nossr50.util.blockmeta.CustomBlockRegistry}</li>
 *   <li>Listen to this event and modify the XP values dynamically</li>
 * </ul>
 * <p>
 * The event is fired after mcMMO determines it's a custom block but before
 * XP is awarded. Listeners can modify the skill type, XP amount, or cancel
 * the event entirely.
 * <p>
 * Example listener for a custom block plugin:
 * <pre>
 * {@code @EventHandler}
 * public void onCustomBlockBreak(McMMOCustomBlockBreakEvent event) {
 *     if (event.getCustomBlockId().startsWith("myplugin:")) {
 *         // Award extra XP for our plugin's blocks
 *         event.setXp(event.getXp() * 2);
 *     }
 * }
 * </pre>
 *
 * @since 2.2.026
 */
public class McMMOCustomBlockBreakEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Block block;
    private final String customBlockId;
    private PrimarySkillType skill;
    private float xp;
    private boolean cancelled;

    /**
     * Creates a new custom block break event.
     *
     * @param player        the player who broke the block
     * @param block         the block that was broken
     * @param customBlockId the custom block identifier (e.g., "oraxen:mythril_ore")
     * @param skill         the skill to award XP for
     * @param xp            the amount of XP to award
     */
    public McMMOCustomBlockBreakEvent(@NotNull Player player, @NotNull Block block,
                                      @NotNull String customBlockId, @Nullable PrimarySkillType skill, float xp) {
        this.player = player;
        this.block = block;
        this.customBlockId = customBlockId;
        this.skill = skill;
        this.xp = xp;
        this.cancelled = false;
    }

    /**
     * Gets the player who broke the block.
     *
     * @return the player
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the block that was broken.
     *
     * @return the block
     */
    @NotNull
    public Block getBlock() {
        return block;
    }

    /**
     * Gets the custom block identifier.
     * Format is typically "pluginname:blockid", e.g., "oraxen:mythril_ore".
     *
     * @return the custom block ID
     */
    @NotNull
    public String getCustomBlockId() {
        return customBlockId;
    }

    /**
     * Gets the plugin name from the custom block ID.
     *
     * @return the plugin name, or the full ID if no colon is present
     */
    @NotNull
    public String getPluginName() {
        int colonIndex = customBlockId.indexOf(':');
        return colonIndex > 0 ? customBlockId.substring(0, colonIndex) : customBlockId;
    }

    /**
     * Gets the block name from the custom block ID.
     *
     * @return the block name, or the full ID if no colon is present
     */
    @NotNull
    public String getBlockName() {
        int colonIndex = customBlockId.indexOf(':');
        return colonIndex > 0 ? customBlockId.substring(colonIndex + 1) : customBlockId;
    }

    /**
     * Gets the skill that will receive XP.
     *
     * @return the skill, or null if no skill is set
     */
    @Nullable
    public PrimarySkillType getSkill() {
        return skill;
    }

    /**
     * Sets the skill that will receive XP.
     *
     * @param skill the skill to award XP for
     */
    public void setSkill(@Nullable PrimarySkillType skill) {
        this.skill = skill;
    }

    /**
     * Gets the amount of XP that will be awarded.
     *
     * @return the XP amount
     */
    public float getXp() {
        return xp;
    }

    /**
     * Sets the amount of XP to award.
     *
     * @param xp the XP amount (must be non-negative)
     */
    public void setXp(float xp) {
        this.xp = Math.max(0, xp);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
