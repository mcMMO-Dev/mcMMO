package com.gmail.nossr50.events.skills;

import static java.util.Objects.requireNonNull;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Generic event for mcMMO skill handling.
 */
public abstract class McMMOPlayerSkillEvent extends PlayerEvent {
    protected @NotNull PrimarySkillType skill;
    protected int skillLevel;
    protected McMMOPlayer mmoPlayer;

    @Deprecated(forRemoval = true, since = "2.2.010")
    protected McMMOPlayerSkillEvent(@NotNull Player player, @NotNull PrimarySkillType skill) {
        super(player);
        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
        requireNonNull(mmoPlayer, "Player not found in UserManager," +
                "contact the dev and tell them to use the constructor for" +
                " McMMOPlayerSkillEvent(McMMOPlayer, PrimarySkillType) instead");
        this.skill = skill;
        this.skillLevel = UserManager.getPlayer(player).getSkillLevel(skill);
    }

    protected McMMOPlayerSkillEvent(@NotNull McMMOPlayer mmoPlayer,
            @NotNull PrimarySkillType primarySkillType) {
        super(mmoPlayer.getPlayer());
        requireNonNull(mmoPlayer, "mmoPlayer cannot be null");
        requireNonNull(primarySkillType, "primarySkillType cannot be null");
        this.skill = primarySkillType;
        this.skillLevel = mmoPlayer.getSkillLevel(primarySkillType);
    }

    /**
     * @return The skill involved in this event
     */
    public @NotNull PrimarySkillType getSkill() {
        return skill;
    }

    /**
     * @return The level of the skill involved in this event
     */
    public int getSkillLevel() {
        return skillLevel;
    }

    /**
     * Rest of file is required boilerplate for custom events
     **/
    private static final HandlerList handlers = new HandlerList();

    /**
     * Returns the {@link McMMOPlayer} associated with this event.
     *
     * @return The {@link McMMOPlayer} associated with this event.
     */
    public @NotNull McMMOPlayer getMcMMOPlayer() {
        return mmoPlayer;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
