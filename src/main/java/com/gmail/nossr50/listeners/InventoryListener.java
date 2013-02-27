package com.gmail.nossr50.listeners;

import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.skills.SkillManagerStore;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.ItemChecks;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class InventoryListener implements Listener{
    private final mcMMO plugin;

    public InventoryListener(final mcMMO plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryOpen(InventoryOpenEvent event) {
        HumanEntity player = event.getPlayer();

        if (Misc.isNPCEntity(player)) {
            return;
        }

        Inventory inventory = event.getInventory();

        if (inventory instanceof FurnaceInventory) {
            Furnace furnace = (Furnace) inventory.getHolder();

            if (furnace == null) {
                return;
            }

            BlockState furnaceBlock = furnace.getBlock().getState();

            if (furnace.getBurnTime() == 0 && !plugin.furnaceIsTracked(furnaceBlock)) {
                plugin.addToOpenFurnaceTracker(furnaceBlock, player.getName());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        HumanEntity player = event.getPlayer();

        if (Misc.isNPCEntity(player)) {
            return;
        }

        Inventory inventory = event.getInventory();

        if (inventory instanceof FurnaceInventory) {
            Furnace furnace = (Furnace) inventory.getHolder();

            if (furnace == null) {
                return;
            }

            BlockState furnaceBlock = furnace.getBlock().getState();

            if (furnace.getBurnTime() == 0 && plugin.furnaceIsTracked(furnaceBlock)) {
                plugin.removeFromFurnaceTracker(furnaceBlock);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceBurnEvent(FurnaceBurnEvent event) {
        BlockState furnaceBlock = event.getBlock().getState();

        if (furnaceBlock instanceof Furnace) {
            ItemStack smelting = ((Furnace) furnaceBlock).getInventory().getSmelting();

            if (plugin.furnaceIsTracked(furnaceBlock) && smelting != null && ItemChecks.isSmeltable(smelting)) {
                Player player = plugin.getFurnacePlayer(furnaceBlock);

                if (!Misc.isNPCEntity(player)) {
                    return;
                }

                if (Permissions.fuelEfficiency(player)) {
                    event.setBurnTime(SkillManagerStore.getInstance().getSmeltingManager(player.getName()).fuelEfficiency(event.getBurnTime()));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceSmeltEvent(FurnaceSmeltEvent event) {
        BlockState furnaceBlock = event.getBlock().getState();

        if (furnaceBlock instanceof Furnace) {
            ItemStack smelting = ((Furnace) furnaceBlock).getInventory().getSmelting();
    
            if (plugin.furnaceIsTracked(furnaceBlock) && smelting != null && ItemChecks.isSmeltable(smelting)) {
                Player player = plugin.getFurnacePlayer(furnaceBlock);
    
                if (!Misc.isNPCEntity(player)) {
                    return;
                }

                if (Permissions.skillEnabled(player, SkillType.SMELTING)) {
                    SkillManagerStore.getInstance().getSmeltingManager(player.getName()).smeltProcessing(event.getSource().getType(), event.getResult());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceExtractEvent(FurnaceExtractEvent event) {
        BlockState furnaceBlock = event.getBlock().getState();

        if (furnaceBlock instanceof Furnace) {
            ItemStack result = ((Furnace) furnaceBlock).getInventory().getResult();
    
            if (plugin.furnaceIsTracked(furnaceBlock) && result != null && ItemChecks.isSmelted(result)) {
                McMMOPlayer mcMMOPlayer = Users.getPlayer(event.getPlayer());

                if (mcMMOPlayer.getPlayer().equals(plugin.getFurnacePlayer(furnaceBlock))) {
                    SkillManagerStore.getInstance().getSmeltingManager(event.getPlayer().getName()).vanillaXPBoost(event.getExpToDrop());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClickEvent(InventoryClickEvent event) {
        SkillTools.removeAbilityBuff(event.getCurrentItem());
    }
}
