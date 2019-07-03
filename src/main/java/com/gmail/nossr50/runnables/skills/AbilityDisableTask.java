package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.util.EventManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.skills.PerksUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AbilityDisableTask extends BukkitRunnable {
    private McMMOPlayer mcMMOPlayer;
    private SuperAbilityType ability;

    public AbilityDisableTask(McMMOPlayer mcMMOPlayer, SuperAbilityType ability) {
        this.mcMMOPlayer = mcMMOPlayer;
        this.ability = ability;
    }

    @Override
    public void run() {
        if (!mcMMOPlayer.getAbilityMode(ability)) {
            return;
        }

        Player player = mcMMOPlayer.getPlayer();

        switch (ability) {
            case SUPER_BREAKER:
            case GIGA_DRILL_BREAKER:
                SkillUtils.handleAbilitySpeedDecrease(player);
                // Fallthrough

            case BERSERK:
                resendChunkRadiusAt(player);
                // Fallthrough

            default:
                break;
        }

        pluginRef.getEventManager().callAbilityDeactivateEvent(player, ability);

        mcMMOPlayer.setAbilityMode(ability, false);
        mcMMOPlayer.setAbilityInformed(ability, false);

        if (mcMMOPlayer.useChatNotifications()) {
            //player.sendMessage(ability.getAbilityOff());
            pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.ABILITY_OFF, ability.getAbilityOff());
        }


        SkillUtils.sendSkillMessage(player, NotificationType.SUPER_ABILITY_ALERT_OTHERS, ability.getAbilityPlayerOff());
        new AbilityCooldownTask(mcMMOPlayer, ability).runTaskLater(pluginRef, PerksUtils.handleCooldownPerks(player, ability.getCooldown()) * Misc.TICK_CONVERSION_FACTOR);
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
