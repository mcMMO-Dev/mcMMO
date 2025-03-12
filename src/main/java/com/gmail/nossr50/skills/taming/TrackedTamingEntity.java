package com.gmail.nossr50.skills.taming;

import com.gmail.nossr50.datatypes.skills.subskills.taming.CallOfTheWildType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.Misc;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TrackedTamingEntity extends CancellableRunnable {
    private final @NotNull LivingEntity livingEntity;
    private final @NotNull CallOfTheWildType callOfTheWildType;
    private final @NotNull Player player;
    private final @NotNull UUID playerUUID;

    public TrackedTamingEntity(@NotNull LivingEntity livingEntity, @NotNull CallOfTheWildType callOfTheWildType,
                               @NotNull Player player) {
        this.player = player;
        this.playerUUID = player.getUniqueId();
        this.callOfTheWildType = callOfTheWildType;
        this.livingEntity = livingEntity;

        int tamingCOTWLength = mcMMO.p.getGeneralConfig().getTamingCOTWLength(callOfTheWildType.getConfigEntityTypeEntry());

        if (tamingCOTWLength > 0) {
            int length = tamingCOTWLength * Misc.TICK_CONVERSION_FACTOR;
            mcMMO.p.getFoliaLib().getScheduler().runAtEntityLater(livingEntity, this, length);
        }
    }

    @Override
    public void run() {
        mcMMO.getTransientEntityTracker().killSummonAndCleanMobFlags(this.getLivingEntity(), player, true);
        mcMMO.getTransientEntityTracker().removeSummonFromTracker(playerUUID, this);
        this.cancel();
    }

    public @NotNull CallOfTheWildType getCallOfTheWildType() {
        return callOfTheWildType;
    }

    public @NotNull LivingEntity getLivingEntity() {
        return livingEntity;
    }
}
