package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.skills.PerksUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AbilityDisableTask extends BukkitRunnable {
    private final OnlineMMOPlayer mmoPlayer;
    private final SuperAbilityType ability;

    public AbilityDisableTask(OnlineMMOPlayer mmoPlayer, SuperAbilityType ability) {
        this.mmoPlayer = mmoPlayer;
        this.ability = ability;
    }

    @Override
    public void run() {
        if (!mmoPlayer.getSuperAbilityManager().getAbilityMode(ability)) {
            return;
        }

        Player player = Misc.adaptPlayer(mmoPlayer);

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

        mmoPlayer.getSuperAbilityManager().setAbilityMode(ability, false);
        mmoPlayer.getSuperAbilityManager().setAbilityInformed(ability, false);

        ParticleEffectUtils.playAbilityDisabledEffect(player);

        if (mmoPlayer.hasSkillChatNotifications()) {
            //player.sendMessage(ability.getAbilityOff());
            NotificationManager.sendPlayerInformation(player, NotificationType.ABILITY_OFF, ability.getAbilityOff());
        }

        if (mcMMO.p.getAdvancedConfig().sendAbilityNotificationToOtherPlayers()) {
            SkillUtils.sendSkillMessage(player, NotificationType.SUPER_ABILITY_ALERT_OTHERS, ability.getAbilityPlayerOff());
        }
        if(!mcMMO.isServerShutdownExecuted()) {
            new AbilityCooldownTask(mmoPlayer, ability).runTaskLater(mcMMO.p, PerksUtils.handleCooldownPerks(player, ability.getCooldown()) * Misc.TICK_CONVERSION_FACTOR);
        }
    }

    private void resendChunkRadiusAt(Player player) {
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
