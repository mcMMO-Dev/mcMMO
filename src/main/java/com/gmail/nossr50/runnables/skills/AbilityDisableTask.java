package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.skills.PerksUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class AbilityDisableTask extends CancellableRunnable {
    private final McMMOPlayer mmoPlayer;
    private final SuperAbilityType ability;

    public AbilityDisableTask(McMMOPlayer mmoPlayer, SuperAbilityType ability) {
        this.mmoPlayer = mmoPlayer;
        this.ability = ability;
    }

    @Override
    public void run() {
        if (!mmoPlayer.getAbilityMode(ability)) {
            return;
        }

        Player player = mmoPlayer.getPlayer();

        switch (ability) {
            case SUPER_BREAKER:
            case GIGA_DRILL_BREAKER:
                SkillUtils.removeAbilityBoostsFromInventory(player);
                // Fallthrough

            case BERSERK:
                if (mcMMO.p.getGeneralConfig().getRefreshChunksEnabled()) {
                    resendChunkRadiusAt(player);
                }
                // Fallthrough

            default:
                break;
        }

        EventUtils.callAbilityDeactivateEvent(player, ability);

        mmoPlayer.setAbilityMode(ability, false);
        mmoPlayer.setAbilityInformed(ability, false);

//        ParticleEffectUtils.playAbilityDisabledEffect(player);

        if (mmoPlayer.useChatNotifications()) {
            //player.sendMessage(ability.getAbilityOff());
            NotificationManager.sendPlayerInformation(player, NotificationType.ABILITY_OFF,
                    ability.getAbilityOff());
        }

        if (mcMMO.p.getAdvancedConfig().sendAbilityNotificationToOtherPlayers()) {
            SkillUtils.sendSkillMessage(player, NotificationType.SUPER_ABILITY_ALERT_OTHERS,
                    ability.getAbilityPlayerOff());
        }
        if (!mcMMO.isServerShutdownExecuted()) {
            mcMMO.p.getFoliaLib().getScheduler()
                    .runAtEntityLater(player, new AbilityCooldownTask(mmoPlayer, ability),
                            (long) PerksUtils.handleCooldownPerks(player, ability.getCooldown())
                                    * Misc.TICK_CONVERSION_FACTOR);
        }
    }

    private void resendChunkRadiusAt(Entity player) {
        Chunk chunk = player.getLocation().getChunk();
        World world = player.getWorld();

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        int radius = 1;

        for (int x = chunkX - radius; x <= chunkX + radius; x++) {
            for (int z = chunkZ - radius; z <= chunkZ + radius; z++) {
                world.refreshChunk(x, z);
            }
        }
    }
}
