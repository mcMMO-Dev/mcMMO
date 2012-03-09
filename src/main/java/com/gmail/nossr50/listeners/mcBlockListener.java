package com.gmail.nossr50.listeners;

import com.gmail.nossr50.BlockChecks;
import com.gmail.nossr50.ItemChecks;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.inventory.ItemStack;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundEffect;

import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.skills.*;
import com.gmail.nossr50.events.FakeBlockBreakEvent;

public class mcBlockListener implements Listener {
    private final mcMMO plugin;

    public mcBlockListener(final mcMMO plugin) {
        this.plugin = plugin;
    }

    /**
     * Monitor BlockPlace events.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block;
        Player player = event.getPlayer();

        //When blocks are placed on snow this event reports the wrong block.
        if (event.getBlockReplacedState() != null && event.getBlockReplacedState().getType().equals(Material.SNOW)) {
            block = event.getBlockAgainst();
        }
        else {
            block = event.getBlock();
        }

        int id = block.getTypeId();
        Material mat = block.getType();

        //Check if the blocks placed should be monitored so they do not give out XP in the future
        if (BlockChecks.shouldBeWatched(mat)) {
            BlockChecks.watchBlock(mat, block, plugin);
        }

        if (id == LoadProperties.anvilID && LoadProperties.anvilmessages) {
            PlayerProfile PP = Users.getProfile(player);

            if (!PP.getPlacedAnvil()) {
                if (LoadProperties.spoutEnabled) {
                    SpoutPlayer sPlayer = SpoutManager.getPlayer(player);

                    if (sPlayer.isSpoutCraftEnabled()) {
                        sPlayer.sendNotification("[mcMMO] Anvil Placed", "Right click to repair!", Material.getMaterial(id));
                    }
                }
                else {
                    event.getPlayer().sendMessage(mcLocale.getString("mcBlockListener.PlacedAnvil"));
                }

                PP.togglePlacedAnvil();
            }
        }
    }

    /**
     * Monitor BlockBreak events.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerProfile PP = Users.getProfile(player);
        Block block = event.getBlock();
        Material mat = block.getType();
        ItemStack inhand = player.getItemInHand();

        if(event instanceof FakeBlockBreakEvent) {
            return;
        }

        /*
         * HERBALISM
         */

        //Green Terra
        if (PP.getHoePreparationMode() && mcPermissions.getInstance().herbalismAbility(player) && ((mat.equals(Material.CROPS) && block.getData() == (byte) 0x7) || Herbalism.canBeGreenTerra(block))) {
            Skills.abilityCheck(player, SkillType.HERBALISM);
        }

        //Wheat && Triple drops
        if (PP.getGreenTerraMode() && Herbalism.canBeGreenTerra(block)) {
            Herbalism.herbalismProcCheck(block, player, event, plugin);
            Herbalism.herbalismProcCheck(block, player, event, plugin); //Called twice for triple drop functionality
        }

        if (mcPermissions.getInstance().herbalism(player) && block.getData() != (byte) 0x5 && Herbalism.canBeGreenTerra(block)) {
            Herbalism.herbalismProcCheck(block, player, event, plugin);
        }

        /*
         * MINING
         */

        if (mcPermissions.getInstance().mining(player) && Mining.canBeSuperBroken(block)) {
            if (LoadProperties.miningrequirespickaxe && ItemChecks.isMiningPick(inhand)) {
                Mining.miningBlockCheck(player, block, plugin);
            }
            else if (!LoadProperties.miningrequirespickaxe) {
                Mining.miningBlockCheck(player, block, plugin);
            }
        }

        /*
         * WOOD CUTTING
         */

        if (mcPermissions.getInstance().woodcutting(player) && mat.equals(Material.LOG)) {
            if (LoadProperties.woodcuttingrequiresaxe && ItemChecks.isAxe(inhand)) {
                WoodCutting.woodcuttingBlockCheck(player, block, plugin);
            }
            else if (!LoadProperties.woodcuttingrequiresaxe) {
                WoodCutting.woodcuttingBlockCheck(player, block, plugin);
            }
        }

        if (PP.getTreeFellerMode() && mcPermissions.getInstance().woodCuttingAbility(player)) {
            WoodCutting.treeFeller(event, plugin);
        }

        /*
         * EXCAVATION
         */

        if (Excavation.canBeGigaDrillBroken(block) && mcPermissions.getInstance().excavation(player) && block.getData() != (byte) 0x5) {
            if (LoadProperties.excavationRequiresShovel && ItemChecks.isShovel(inhand)) {
                Excavation.excavationProcCheck(block, player);
            }
            else if (!LoadProperties.excavationRequiresShovel) {
                Excavation.excavationProcCheck(block, player);
            }
        }

        //Change the byte back when broken
        if (block.getData() == (byte) 0x5 && BlockChecks.shouldBeWatched(mat)) {
            block.setData((byte) 0x0);
        }
        else if(plugin.misc.blockWatchList.contains(block)) {
            plugin.misc.blockWatchList.remove(block);
        }
    }

    /**
     * Monitor BlockDamage events.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        PlayerProfile PP = Users.getProfile(player);
        ItemStack inhand = player.getItemInHand();
        Block block = event.getBlock();
        Material mat = block.getType();

        /*
         * ABILITY PREPARATION CHECKS
         */
        if (BlockChecks.abilityBlockCheck(mat)) {
            if (PP.getHoePreparationMode() && Herbalism.canBeGreenTerra(block)) {
                Skills.abilityCheck(player, SkillType.HERBALISM);
            }
            else if (PP.getAxePreparationMode() && mat.equals(Material.LOG) && mcPermissions.getInstance().woodCuttingAbility(player)) {  //Why are we checking the permissions here?
                Skills.abilityCheck(player, SkillType.WOODCUTTING);
            }
            else if (PP.getPickaxePreparationMode() && Mining.canBeSuperBroken(block)) {
                Skills.abilityCheck(player, SkillType.MINING);
            }
            else if (PP.getShovelPreparationMode() && Excavation.canBeGigaDrillBroken(block)) {
                Skills.abilityCheck(player, SkillType.EXCAVATION);
            }
            else if (PP.getFistsPreparationMode() && (Excavation.canBeGigaDrillBroken(block) || mat.equals(Material.SNOW))) {
                Skills.abilityCheck(player, SkillType.UNARMED);
            }
        }

        /* TREE FELLER SOUNDS */
        if (LoadProperties.spoutEnabled && mat.equals(Material.LOG) && PP.getTreeFellerMode()) {
            SpoutStuff.playSoundForPlayer(SoundEffect.FIZZ, player, block.getLocation());
        }

        /*
         * ABILITY TRIGGER CHECKS
         */
        if (Skills.triggerCheck(player, block, AbilityType.GREEN_TERRA)) {
            Herbalism.greenTerra(player, block);
        }
        else if (Skills.triggerCheck(player, block, AbilityType.GIGA_DRILL_BREAKER)) {
            if (LoadProperties.excavationRequiresShovel && ItemChecks.isShovel(inhand)) {
                event.setInstaBreak(true);
                Excavation.gigaDrillBreaker(player, block);
            }
            else if (!LoadProperties.excavationRequiresShovel) {
                event.setInstaBreak(true);
                Excavation.gigaDrillBreaker(player, block);
            }
        }
        else if (Skills.triggerCheck(player, block, AbilityType.BERSERK)) {
            if (inhand.getType().equals(Material.AIR)) {
                PlayerAnimationEvent armswing = new PlayerAnimationEvent(player);
                Bukkit.getPluginManager().callEvent(armswing);

                event.setInstaBreak(true);
            }

            if (LoadProperties.spoutEnabled) {
                SpoutStuff.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
            }
        }
        else if (Skills.triggerCheck(player, block, AbilityType.SUPER_BREAKER)) {
            if (LoadProperties.miningrequirespickaxe && ItemChecks.isMiningPick(inhand)) {
                event.setInstaBreak(true);
                Mining.SuperBreakerBlockCheck(player, block, plugin);
            }
            else if (!LoadProperties.miningrequirespickaxe) {
                event.setInstaBreak(true);
                Mining.SuperBreakerBlockCheck(player, block, plugin);
            }
        }
        else if (Skills.triggerCheck(player, block, AbilityType.LEAF_BLOWER) && PP.getSkillLevel(SkillType.WOODCUTTING) >= 100) {
            if (LoadProperties.woodcuttingrequiresaxe && ItemChecks.isAxe(inhand)) {
                event.setInstaBreak(true);
                WoodCutting.leafBlower(player, block);
            }
            else if (!LoadProperties.woodcuttingrequiresaxe && inhand.getType().equals(Material.SHEARS)) {
                event.setInstaBreak(true);
                WoodCutting.leafBlower(player, block);
            }
        }
    }

    /**
     * Monitor BlockFromTo events.
     *
     * @param event The event to monitor
     */
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        Block blockFrom = event.getBlock();
        Block blockTo = event.getToBlock();

        if (BlockChecks.shouldBeWatched(blockFrom.getType()) && blockFrom.getData() == (byte) 5) {
            blockTo.setData((byte) 5);
        }
    }
}
