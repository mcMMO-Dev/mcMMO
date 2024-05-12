package com.gmail.nossr50.events.skills.alchemy;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public class McMMOPlayerBrewEvent extends McMMOPlayerSkillEvent implements Cancellable {
    private final BlockState brewingStand;

    private boolean cancelled;

    @Deprecated(forRemoval = true, since = "2.2.010")
    public McMMOPlayerBrewEvent(Player player, BlockState brewingStand) {
        super(requireNonNull(UserManager.getPlayer(player)), PrimarySkillType.ALCHEMY);
        this.brewingStand = brewingStand;
        cancelled = false;
    }

    public McMMOPlayerBrewEvent(@NotNull McMMOPlayer mmoPlayer, @NotNull BlockState brewingStand) {
        super(mmoPlayer, PrimarySkillType.ALCHEMY);
        this.brewingStand = requireNonNull(brewingStand);
        cancelled = false;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean newValue) {
        this.cancelled = newValue;
    }

    public Block getBrewingStandBlock() {
        return brewingStand.getBlock();
    }

    public BrewingStand getBrewingStand() {
        return (BrewingStand) brewingStand;
    }
}
