package com.gmail.nossr50.skills.mining;

import java.util.HashSet;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.player.PlayerInteractEvent;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Skills;
import com.gmail.nossr50.util.Users;

public class BlastMining {
    private static AdvancedConfig advancedConfig = AdvancedConfig.getInstance();
    private static Random random = new Random();

    public final static int BLAST_MINING_RANK_1 = advancedConfig.getBlastMiningRank1();
    public final static int BLAST_MINING_RANK_2 = advancedConfig.getBlastMiningRank2();
    public final static int BLAST_MINING_RANK_3 = advancedConfig.getBlastMiningRank3();
    public final static int BLAST_MINING_RANK_4 = advancedConfig.getBlastMiningRank4();
    public final static int BLAST_MINING_RANK_5 = advancedConfig.getBlastMiningRank5();
    public final static int BLAST_MINING_RANK_6 = advancedConfig.getBlastMiningRank6();
    public final static int BLAST_MINING_RANK_7 = advancedConfig.getBlastMiningRank7();
    public final static int BLAST_MINING_RANK_8 = advancedConfig.getBlastMiningRank8();

    /**
     * Detonate TNT for Blast Mining
     *
     * @param event The PlayerInteractEvent
     * @param player Player detonating the TNT
     * @param plugin mcMMO plugin instance
     */
    public static void detonate(PlayerInteractEvent event, Player player, mcMMO plugin) {
        if(player == null)
            return;

        PlayerProfile profile = Users.getProfile(player);

        if (profile.getSkillLevel(SkillType.MINING) < 125)
            return;

        Block block = event.getClickedBlock();

        if (block == null || block.getType() != Material.TNT) {
            final byte SNOW = 78;
            final byte AIR = 0;
            final int BLOCKS_AWAY = 100;

            HashSet<Byte> transparent = new HashSet<Byte>();

            transparent.add(SNOW);
            transparent.add(AIR);

            block = player.getTargetBlock(transparent, BLOCKS_AWAY);

            if (block.getType() != Material.TNT) {
                return;
            }
        }
        else if (block.getType() == Material.TNT) {
            event.setCancelled(true); // This is the only way I know to avoid the original TNT to be triggered (in case the player is close to it)
        }

        if (!Misc.blockBreakSimulate(block, player, true)) {
            return;
        }

        final double MAX_DISTANCE_AWAY = 10.0;
        final int TIME_CONVERSION_FACTOR = 1000;

        AbilityType ability = AbilityType.BLAST_MINING;

        /* Check Cooldown */
        if (!Skills.cooldownOver(profile.getSkillDATS(ability) * TIME_CONVERSION_FACTOR, ability.getCooldown(), player)) {
            player.sendMessage(LocaleLoader.getString("Skills.TooTired") + ChatColor.YELLOW + " (" + Skills.calculateTimeLeft(profile.getSkillDATS(ability) * TIME_CONVERSION_FACTOR, ability.getCooldown(), player) + "s)");

            return;
        }

        /* Send message to nearby players */
        for (Player y : player.getWorld().getPlayers()) {
            if (y != player && Misc.isNear(player.getLocation(), y.getLocation(), MAX_DISTANCE_AWAY)) {
                y.sendMessage(ability.getAbilityPlayer(player));
            }
        }

        player.sendMessage(LocaleLoader.getString("Mining.Blast.Boom"));

        /* Create the TNT entity */
        //        TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(block.getLocation(), EntityType.PRIMED_TNT);
        TNTPrimed tnt = player.getWorld().spawn(block.getLocation(), TNTPrimed.class);
        plugin.addToTNTTracker(tnt.getEntityId(), player.getName());
        tnt.setFuseTicks(0);

        /* Disable the original one */
        block.setType(Material.AIR);

        profile.setSkillDATS(ability, System.currentTimeMillis()); //Save DATS for Blast Mining
        profile.setAbilityInformed(ability, false);
    }

    protected static Random getRandom() {
        return random;
    }
}
