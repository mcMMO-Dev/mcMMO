package com.gmail.nossr50.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.runnables.PlayerUpdateInventoryTask;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;

public class InventoryListener implements Listener {
    private final mcMMO plugin;

    public InventoryListener(final mcMMO plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        HumanEntity player = event.getPlayer();

        if (Misc.isNPCEntity(player)) {
            return;
        }

        Block furnaceBlock = Misc.processInventoryOpenorCloseEvent(event);

        if (furnaceBlock != null && !furnaceBlock.hasMetadata(mcMMO.furnaceMetadataKey)) {
            furnaceBlock.setMetadata(mcMMO.furnaceMetadataKey, UserManager.getPlayer((Player) player).getPlayerMetadata());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        HumanEntity player = event.getPlayer();

        if (Misc.isNPCEntity(player)) {
            return;
        }

        Block furnaceBlock = Misc.processInventoryOpenorCloseEvent(event);

        if (furnaceBlock != null && furnaceBlock.hasMetadata(mcMMO.furnaceMetadataKey)) {
            furnaceBlock.removeMetadata(mcMMO.furnaceMetadataKey, plugin);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceBurnEvent(FurnaceBurnEvent event) {
        Block furnaceBlock = event.getBlock();
        ItemStack smelting = Misc.getSmeltingFromFurnace(furnaceBlock);

        if (!ItemUtils.isSmeltable(smelting)) {
            return;
        }

        Player player = Misc.getPlayerFromFurnace(furnaceBlock);

        if (Misc.isNPCEntity(player) || !Permissions.secondaryAbilityEnabled(player, SecondaryAbility.FUEL_EFFICIENCY)) {
            return;
        }

        event.setBurnTime(UserManager.getPlayer(player).getSmeltingManager().fuelEfficiency(event.getBurnTime()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceSmeltEvent(FurnaceSmeltEvent event) {
        Block furnaceBlock = event.getBlock();
        ItemStack smelting = event.getSource();

        if (!ItemUtils.isSmeltable(smelting)) {
            return;
        }

        Player player = Misc.getPlayerFromFurnace(furnaceBlock);

        if (Misc.isNPCEntity(player) || !SkillType.SMELTING.getPermissions(player)) {
            return;
        }

        event.setResult(UserManager.getPlayer(player).getSmeltingManager().smeltProcessing(smelting, event.getResult()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceExtractEvent(FurnaceExtractEvent event) {
        Block furnaceBlock = event.getBlock();
        ItemStack result = Misc.getResultFromFurnace(furnaceBlock);

        if (!ItemUtils.isSmelted(result)) {
            return;
        }

        Player player = Misc.getPlayerFromFurnace(furnaceBlock);

        if (Misc.isNPCEntity(player) || !Permissions.vanillaXpBoost(player, SkillType.SMELTING)) {
            return;
        }

        event.setExpToDrop(UserManager.getPlayer(player).getSmeltingManager().vanillaXPBoost(event.getExpToDrop()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClickEvent(InventoryClickEvent event) {
        SkillUtils.removeAbilityBuff(event.getCurrentItem());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent event) {
        final HumanEntity whoClicked = event.getWhoClicked();

        if (Misc.isNPCEntity(whoClicked) || !(whoClicked instanceof Player)) {
            return;
        }

        ItemStack result = event.getRecipe().getResult();

        if (!ItemUtils.isMcMMOItem(result)) {
            return;
        }

        new PlayerUpdateInventoryTask((Player) whoClicked).runTaskLater(plugin, 0);
    }
}
