package com.gmail.nossr50.skills.taming;

import com.gmail.nossr50.datatypes.skills.subskills.taming.CallOfTheWildType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.Misc;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TrackedTamingEntity extends CancellableRunnable {
    private final @NotNull LivingEntity livingEntity;
    private final @NotNull CallOfTheWildType callOfTheWildType;
    private final @NotNull Player player;

    protected TrackedTamingEntity(@NotNull LivingEntity livingEntity, @NotNull CallOfTheWildType callOfTheWildType, @NotNull Player player) {
        this.player = player;
        this.callOfTheWildType = callOfTheWildType;
        this.livingEntity = livingEntity;

        int tamingCOTWLength = mcMMO.p.getGeneralConfig().getTamingCOTWLength(callOfTheWildType.getConfigEntityTypeEntry());

        if (tamingCOTWLength > 0) {
            int length = tamingCOTWLength * Misc.TICK_CONVERSION_FACTOR;
            mcMMO.p.getFoliaLib().getImpl().runAtEntityLater(livingEntity, this, length + 1);
        }
    }

    @Override
    public void run() {
        mcMMO.getTransientEntityTracker().removeSummon(this.getLivingEntity(), player, true);
        this.cancel();
    }

    public @NotNull CallOfTheWildType getCallOfTheWildType() {
        return callOfTheWildType;
    }

    public @NotNull LivingEntity getLivingEntity() {
        return livingEntity;
    }
}
