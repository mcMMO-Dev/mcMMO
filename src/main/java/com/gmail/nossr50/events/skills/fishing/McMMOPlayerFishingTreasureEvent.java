package com.gmail.nossr50.events.skills.fishing;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

public class McMMOPlayerFishingTreasureEvent extends McMMOPlayerFishingEvent {
    private ItemStack treasure;
    private int xp;

    @Deprecated(forRemoval = true, since = "2.2.010")
    public McMMOPlayerFishingTreasureEvent(Player player, ItemStack treasure, int xp) {
        this(requireNonNull(UserManager.getPlayer(player)), treasure, xp);
    }

    public McMMOPlayerFishingTreasureEvent(@NotNull McMMOPlayer mmoPlayer, @Nullable ItemStack treasure, int xp) {
        super(mmoPlayer);
        this.treasure = treasure;
        this.xp = xp;
    }

    public @Nullable ItemStack getTreasure() {
        return treasure;
    }

    public void setTreasure(@Nullable ItemStack item) {
        this.treasure = item;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }
}
