package com.gmail.nossr50.skills.smelting;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.skills.mining.Mining;
import com.gmail.nossr50.util.Misc;

public class FluxMiningEventHandler {
    private SmeltingManager manager;
    private BlockBreakEvent event;
    private Block block;

    protected FluxMiningEventHandler(SmeltingManager manager, BlockBreakEvent event) {
        this.manager = manager;
        this.event = event;
        this.block = event.getBlock();
    }

    protected void processDrops() {
        ItemStack item = null;

        switch (block.getType()) {
        case IRON_ORE:
            item = new ItemStack(Material.IRON_INGOT);
            break;

        case GOLD_ORE:
            item = new ItemStack(Material.GOLD_INGOT);
            break;

        default:
            break;
        }

        if (item == null) {
            return;
        }

        Location location = block.getLocation();
        int chance = (int) ((Mining.doubleDropsMaxChance / Mining.doubleDropsMaxLevel) * (Misc.skillCheck(manager.getProfile().getSkillLevel(SkillType.MINING), Mining.doubleDropsMaxLevel)));
        Misc.dropItem(location, item);
        Misc.randomDropItem(location, item, chance);
    }

    protected void eventCancellationAndProcessing() {
        event.setCancelled(true);
        block.setType(Material.AIR);
    }

    protected void sendAbilityMessage() {
        manager.getPlayer().sendMessage(LocaleLoader.getString("Smelting.FluxMining.Success"));
    }
}
