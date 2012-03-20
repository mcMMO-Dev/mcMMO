package com.gmail.nossr50.skills;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

import com.gmail.nossr50.BlockChecks;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;

public class BlastMining {

    /**
     * Handler for what blocks drop from the explosion.
     *
     * @param ores List of ore blocks destroyed by the explosion
     * @param debris List of non-ore blocks destroyed by the explosion
     * @param yield Percentage of blocks to drop
     * @param oreBonus Percentage bonus for ore drops
     * @param debrisReduction Percentage reduction for non-ore drops
     * @param extraDrops Number of times to drop each block
     * @return A list of blocks dropped from the explosion
     */
    private static List<Block> explosionYields(List<Block> ores, List<Block> debris, float yield, float oreBonus, float debrisReduction, int extraDrops) {
        Iterator<Block> iterator2 = ores.iterator();
        List<Block> blocksDropped = new ArrayList<Block>();

        while (iterator2.hasNext()) {
            Block temp = iterator2.next();

            if ((float) Math.random() < (yield + oreBonus)) {
                blocksDropped.add(temp);
                Mining.miningDrops(temp);

                if (!temp.hasMetadata("mcmmoPlacedBlock")) {
                    if (extraDrops == 2) {
                        blocksDropped.add(temp);
                        Mining.miningDrops(temp);
                    }
                    if (extraDrops == 3) {
                        blocksDropped.add(temp);
                        Mining.miningDrops(temp);
                    }
                }
            }
        }

        if (yield - debrisReduction != 0) {
            Iterator<Block> iterator3 = debris.iterator();

            while (iterator3.hasNext()) {
                Block temp = iterator3.next();

                if ((float) Math.random() < (yield - debrisReduction))
                    Mining.miningDrops(temp);
            }
        }

        return blocksDropped;
    }

    /**
     * Handler for explosion drops and XP gain.
     *
     * @param player Player triggering the explosion
     * @param event Event whose explosion is being processed
     */
    public static void dropProcessing(Player player, EntityExplodeEvent event) {
        final int RANK_1_LEVEL = 125;
        final int RANK_2_LEVEL = 250;
        final int RANK_3_LEVEL = 375;
        final int RANK_4_LEVEL = 500;
        final int RANK_5_LEVEL = 625;
        final int RANK_6_LEVEL = 750;
        final int RANK_7_LEVEL = 875;
        final int RANK_8_LEVEL = 1000;

        int skillLevel = Users.getProfile(player).getSkillLevel(SkillType.MINING);
        float yield = event.getYield();
        List<Block> blocks = event.blockList();
        Iterator<Block> iterator = blocks.iterator();

        List<Block> ores = new ArrayList<Block>();
        List<Block> debris = new ArrayList<Block>();
        List<Block> xp = new ArrayList<Block>();

        while (iterator.hasNext()) {
            Block temp = iterator.next();

            if (BlockChecks.isOre(temp.getType())) {
                ores.add(temp);
            }
            else {
                debris.add(temp);
            }
        }

        //Normal explosion
        if (skillLevel < RANK_1_LEVEL) {
            return;
        }

        event.setYield(0);

        //Triple Drops, No debris, +70% ores
        if (skillLevel >= RANK_8_LEVEL) {
            xp = explosionYields(ores, debris, yield, .70f, .30f, 3);
        }

        //Triple Drops, No debris, +65% ores
        else if (skillLevel >= RANK_7_LEVEL) {
            xp = explosionYields(ores, debris, yield, .65f, .30f, 3);
        }

        //Double Drops, No Debris, +60% ores
        else if (skillLevel >= RANK_6_LEVEL) {
            xp = explosionYields(ores, debris, yield, .60f, .30f, 2);
        }

        //Double Drops, No Debris, +55% ores
        else if (skillLevel >= RANK_5_LEVEL) {
            xp = explosionYields(ores, debris, yield, .55f, .30f, 2);
        }

        //No debris, +50% ores
        else if (skillLevel >= RANK_4_LEVEL) {
            xp = explosionYields(ores, debris, yield, .50f, .30f, 1);
        }

        //No debris, +45% ores
        else if (skillLevel >= RANK_3_LEVEL) {
            xp = explosionYields(ores, debris, yield, .45f, .30f, 1);
        }

        //+40% ores, -20% debris
        else if (skillLevel >= RANK_2_LEVEL) {
            xp = explosionYields(ores, debris, yield, .40f, .20f, 1);
        }

        //+35% ores, -10% debris
        else if (skillLevel >= RANK_1_LEVEL) {
            xp = explosionYields(ores, debris, yield, .35f, .10f, 1);
        }

        for (Block block : xp) {
            if (!block.hasMetadata("mcmmoPlacedBlock")) {
                Mining.miningXP(player, block);
            }
        }
    }

    /**
     * Increases the blast radius of the explosion.
     *
     * @param player Player triggering the explosion
     * @param event Event whose explosion radius is being changed
     */
    public static void biggerBombs(Player player, ExplosionPrimeEvent event) {
        final int RANK_1_LEVEL = 250;
        final int RANK_2_LEVEL = 500;
        final int RANK_3_LEVEL = 750;
        final int RANK_4_LEVEL = 1000;

        int skillLevel = Users.getProfile(player).getSkillLevel(SkillType.MINING);
        float radius = event.getRadius();

        if (skillLevel < RANK_1_LEVEL) {
            return;
        }

        if (skillLevel >= RANK_1_LEVEL) {
            radius++;
        }

        if (skillLevel >= RANK_2_LEVEL) {
            radius++;
        }

        if (skillLevel >= RANK_3_LEVEL) {
            radius++;
        }

        if (skillLevel >= RANK_4_LEVEL) {
            radius++;
        }

        event.setRadius(radius);
    }

    /**
     * Decreases damage dealt by the explosion.
     *
     * @param player Player triggering the explosion
     * @param event Event whose explosion damage is being reduced
     */
    public static void demolitionsExpertise(Player player, EntityDamageEvent event) {
        final int RANK_1_LEVEL = 500;
        final int RANK_2_LEVEL = 750;
        final int RANK_3_LEVEL = 1000;

        int skill = Users.getProfile(player).getSkillLevel(SkillType.MINING);
        int damage = event.getDamage();

        if (skill < RANK_1_LEVEL) {
            return;
        }

        if (skill >= RANK_3_LEVEL) {
            damage = 0;
        }
        else if (skill >= RANK_2_LEVEL) {
            damage = damage / 2;
        }
        else if (skill >= RANK_1_LEVEL) {
            damage = damage/4;
        }

        event.setDamage(damage);
    }

    /**
     * Remotely detonate TNT for Blast Mining.
     *
     * @param player Player detonating the TNT
     * @param plugin mcMMO plugin instance
     */
    public static void remoteDetonation(Player player, mcMMO plugin) {
        final byte SNOW = 78;
        final byte AIR = 0;
        final int BLOCKS_AWAY = 100;
        final int TIME_CONVERSION_FACTOR = 1000;

        PlayerProfile PP = Users.getProfile(player);
        HashSet<Byte> transparent = new HashSet<Byte>();

        transparent.add(SNOW);
        transparent.add(AIR);

        Block block = player.getTargetBlock(transparent, BLOCKS_AWAY);

        if (block.getType().equals(Material.TNT) && m.blockBreakSimulate(block, player, true) && PP.getSkillLevel(SkillType.MINING) >= 125) {
            final int MAX_DISTANCE_AWAY = 10;
            AbilityType ability = AbilityType.BLAST_MINING;

            /* Check Cooldown */
            if(!Skills.cooldownOver(PP.getSkillDATS(ability) * TIME_CONVERSION_FACTOR, ability.getCooldown())) {
                player.sendMessage(mcLocale.getString("Skills.TooTired") + ChatColor.YELLOW + " (" + Skills.calculateTimeLeft(PP.getSkillDATS(ability) * TIME_CONVERSION_FACTOR, ability.getCooldown()) + "s)");
                return;
            }

            /* Send message to nearby players */
            for(Player y : player.getWorld().getPlayers()) {
                if(y != player && m.isNear(player.getLocation(), y.getLocation(), MAX_DISTANCE_AWAY)) {
                    y.sendMessage(ability.getAbilityPlayer(player));
                }
            }

            player.sendMessage(mcLocale.getString("BlastMining.Boom"));

            /* Create the TNT entity */
            TNTPrimed tnt = player.getWorld().spawn(block.getLocation(), TNTPrimed.class);
            plugin.tntTracker.put(tnt.getEntityId(), player);
            block.setType(Material.AIR);
            tnt.setFuseTicks(0);

            PP.setSkillDATS(ability, System.currentTimeMillis()); //Save DATS for Blast Mining
            PP.setBlastMiningInformed(false);
        }
    }
}
