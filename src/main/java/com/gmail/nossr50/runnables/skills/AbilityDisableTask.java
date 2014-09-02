package com.gmail.nossr50.runnables.skills;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.skills.SkillAbilityManager;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.skills.PerksUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class AbilityDisableTask extends BukkitRunnable {
    private McMMOPlayer mcMMOPlayer;
    private AbilityType ability;

    public AbilityDisableTask(McMMOPlayer mcMMOPlayer, AbilityType ability) {
        this.mcMMOPlayer = mcMMOPlayer;
        this.ability = ability;
    }

    @Override
    public void run() {
        if (!mcMMOPlayer.getAbilityMode(ability)) {
            return;
        }

        Player player = mcMMOPlayer.getPlayer();

        if(ability == AbilityType.superBreaker || ability == AbilityType.gigaDrillBreaker) {
        	SkillUtils.handleAbilitySpeedDecrease(player);
        }
        else if(ability == AbilityType.berserk) {
            if (Config.getInstance().getRefreshChunksEnabled()) {
                resendChunkRadiusAt(player, 1);
            }
        }

        EventUtils.callAbilityDeactivateEvent(player, ability);

        mcMMOPlayer.setAbilityMode(ability, false);
        mcMMOPlayer.setAbilityInformed(ability, false);

        ParticleEffectUtils.playAbilityDisabledEffect(player);

        if (mcMMOPlayer.useChatNotifications()) {
            player.sendMessage(ability.getAbilityOff());
        }

        SkillUtils.sendSkillMessage(player, ability.getAbilityPlayerOff(player));
        SkillManager manager = mcMMOPlayer.getSkillManager(SkillType.byAbility(ability));
        if(manager instanceof SkillAbilityManager) {
        	((SkillAbilityManager) manager).onAbilityDeactivated();
        }
        new AbilityCooldownTask(mcMMOPlayer, ability).runTaskLaterAsynchronously(mcMMO.p, PerksUtils.handleCooldownPerks(player, ability.getCooldown()) * Misc.TICK_CONVERSION_FACTOR);
    }

    private void resendChunkRadiusAt(Player player, int radius) {
        Chunk chunk = player.getLocation().getChunk();
        World world = player.getWorld();

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        for (int x = chunkX - radius; x < chunkX + radius; x++) {
            for (int z = chunkZ - radius; z < chunkZ + radius; z++) {
                world.refreshChunk(x, z);
            }
        }
    }
}
