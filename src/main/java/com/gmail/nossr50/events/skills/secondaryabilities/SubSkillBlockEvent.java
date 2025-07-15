package com.gmail.nossr50.events.skills.secondaryabilities;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SubSkillBlockEvent extends SubSkillEvent {
    private final @NotNull Block block;

    public SubSkillBlockEvent(@NotNull Player player, @NotNull SubSkillType subSkillType,
            @NotNull Block block) {
        super(player, subSkillType);
        this.block = block;
    }

    /**
     * Get the block associated with this event
     *
     * @return the block associated with this event
     */
    public @NotNull Block getBlock() {
        return block;
    }
}
