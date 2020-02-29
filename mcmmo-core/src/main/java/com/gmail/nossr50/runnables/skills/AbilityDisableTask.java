package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AbilityDisableTask extends BukkitRunnable {
    private final mcMMO pluginRef;
    private BukkitMMOPlayer mcMMOPlayer;
    private SuperAbilityType superAbilityType;

    public AbilityDisableTask(mcMMO pluginRef, BukkitMMOPlayer mcMMOPlayer, SuperAbilityType superAbilityType) {
        this.pluginRef = pluginRef;
        this.mcMMOPlayer = mcMMOPlayer;
        this.superAbilityType = superAbilityType;
    }

    @Override
    public void run() {
        if (!mcMMOPlayer.getSuperAbilityMode(superAbilityType)) {
            return;
        }

        Player player = mcMMOPlayer.getNative();

        switch (superAbilityType) {
            case SUPER_BREAKER:
            case GIGA_DRILL_BREAKER:
                pluginRef.getSkillTools().handleAbilitySpeedDecrease(player);
                // Fallthrough

            case BERSERK:
                resendChunkRadiusAt(player);
                // Fallthrough

            default:
                break;
        }

        pluginRef.getEventManager().callAbilityDeactivateEvent(player, superAbilityType);

        mcMMOPlayer.setSuperAbilityMode(superAbilityType, false);
        mcMMOPlayer.setAbilityInformed(superAbilityType, false);

        if (mcMMOPlayer.useChatNotifications()) {
            //player.sendMessage(ability.getAbilityOff());
            pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.ABILITY_OFF,
                    pluginRef.getSkillTools().getSuperAbilityOffLocaleKey(superAbilityType));
        }


        pluginRef.getSkillTools().sendSkillMessage(player, NotificationType.SUPER_ABILITY_ALERT_OTHERS,
                pluginRef.getSkillTools().getSuperAbilityOtherPlayerDeactivationLocaleKey(superAbilityType));
        pluginRef.getPlatformProvider().getScheduler().getTaskBuilder()
                .setDelay(pluginRef.getPerkUtils().handleCooldownPerks(player, (pluginRef.getSkillTools().getSuperAbilityCooldown(superAbilityType) * pluginRef.getMiscTools().TICK_CONVERSION_FACTOR)))
                .setTask(new AbilityCooldownTask(pluginRef, mcMMOPlayer, superAbilityType))
                .schedule();

    }

    private void resendChunkRadiusAt(Player player) {
        Chunk chunk = player.getLocation().getChunk();
        World world = player.getWorld();

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        for (int x = chunkX - 1; x <= chunkX + 1; x++) {
            for (int z = chunkZ - 1; z <= chunkZ + 1; z++) {
                world.refreshChunk(x, z);
            }
        }
    }
}
