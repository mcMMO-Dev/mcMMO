package com.gmail.nossr50.skills.smelting;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.skills.mining.Mining;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;

public class FluxMiningEventHandler {
    private SmeltingManager manager;
    private Player player;
    private BlockBreakEvent event;
    private Block block;

    protected FluxMiningEventHandler(SmeltingManager manager, BlockBreakEvent event) {
        this.manager = manager;
        this.player = manager.getPlayer();
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
        Misc.dropItem(location, item);

        if (Permissions.secondSmelt(player)) {
            int chance = (int) ((Mining.doubleDropsMaxChance / Mining.doubleDropsMaxLevel) * (Misc.skillCheck(manager.getProfile().getSkillLevel(SkillType.MINING), Mining.doubleDropsMaxLevel)));
            Misc.randomDropItem(location, item, chance);
        }
    }

    protected void eventCancellationAndProcessing() {
        event.setCancelled(true);
        block.setType(Material.AIR);
    }

    protected void sendAbilityMessage() {
        player.sendMessage(LocaleLoader.getString("Smelting.FluxMining.Success"));
    }
}
