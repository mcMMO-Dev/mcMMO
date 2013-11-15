package com.gmail.nossr50.listeners;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Furnace;
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
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.runnables.PlayerUpdateInventoryTask;
import com.gmail.nossr50.skills.alchemy.AlchemyPotionBrewer;
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
        Block furnaceBlock = processInventoryOpenorCloseEvent(event.getInventory());

        if (furnaceBlock == null || furnaceBlock.hasMetadata(mcMMO.furnaceMetadataKey)) {
            return;
        }

        HumanEntity player = event.getPlayer();

        if (Misc.isNPCEntity(player)) {
            return;
        }

        furnaceBlock.setMetadata(mcMMO.furnaceMetadataKey, UserManager.getPlayer((Player) player).getPlayerMetadata());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        Block furnaceBlock = processInventoryOpenorCloseEvent(event.getInventory());

        if (furnaceBlock == null || furnaceBlock.hasMetadata(mcMMO.furnaceMetadataKey)) {
            return;
        }

        HumanEntity player = event.getPlayer();

        if (Misc.isNPCEntity(player)) {
            return;
        }

        furnaceBlock.removeMetadata(mcMMO.furnaceMetadataKey, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceBurnEvent(FurnaceBurnEvent event) {
        Block furnaceBlock = event.getBlock();
        BlockState furnaceState = furnaceBlock.getState();
        ItemStack smelting = furnaceState instanceof Furnace ? ((Furnace) furnaceState).getInventory().getSmelting() : null;

        if (!ItemUtils.isSmeltable(smelting)) {
            return;
        }

        Player player = getPlayerFromFurnace(furnaceBlock);

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

        Player player = getPlayerFromFurnace(furnaceBlock);

        if (Misc.isNPCEntity(player) || !SkillType.SMELTING.getPermissions(player)) {
            return;
        }

        event.setResult(UserManager.getPlayer(player).getSmeltingManager().smeltProcessing(smelting, event.getResult()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceExtractEvent(FurnaceExtractEvent event) {
        Block furnaceBlock = event.getBlock();
        BlockState furnaceState = furnaceBlock.getState();
        ItemStack result = furnaceState instanceof Furnace ? ((Furnace) furnaceState).getInventory().getResult() : null;

        if (!ItemUtils.isSmelted(result)) {
            return;
        }

        Player player = getPlayerFromFurnace(furnaceBlock);

        if (Misc.isNPCEntity(player) || !Permissions.vanillaXpBoost(player, SkillType.SMELTING)) {
            return;
        }

        event.setExpToDrop(UserManager.getPlayer(player).getSmeltingManager().vanillaXPBoost(event.getExpToDrop()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onAlchemyClickEvent(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.BREWING || !(event.getInventory().getHolder() instanceof BrewingStand)) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player) || Misc.isNPCEntity(event.getWhoClicked()) || !Permissions.concoctions(event.getWhoClicked())) {
            return;
        }

        AlchemyPotionBrewer.handleInventoryClick(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onAlchemyDragEvent(InventoryDragEvent event) {
        if (event.getInventory().getType() != InventoryType.BREWING || !(event.getInventory().getHolder() instanceof BrewingStand)) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player) || Misc.isNPCEntity(event.getWhoClicked()) || !Permissions.concoctions(event.getWhoClicked())) {
            return;
        }

        AlchemyPotionBrewer.handleInventoryDrag(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onAlchemyMoveItemEvent(InventoryMoveItemEvent event) {
        if (event.getDestination().getType() != InventoryType.BREWING || !(event.getDestination().getHolder() instanceof BrewingStand)) {
            return;
        }

        if (Config.getInstance().getPreventHopperTransfer()  && event.getItem() != null && event.getItem().getType() != Material.POTION) {
            event.setCancelled(true);
            return;
        }

        if (Config.getInstance().getEnabledForHoppers()) {
            AlchemyPotionBrewer.handleInventoryMoveItem(event);
        }
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

    private Block processInventoryOpenorCloseEvent(Inventory inventory) {
        if (!(inventory instanceof FurnaceInventory)) {
            return null;
        }

        Furnace furnace = (Furnace) inventory.getHolder();

        if (furnace == null || furnace.getBurnTime() != 0) {
            return null;
        }

        return furnace.getBlock();
    }

    private Player getPlayerFromFurnace(Block furnaceBlock) {
        List<MetadataValue> metadata = furnaceBlock.getMetadata(mcMMO.furnaceMetadataKey);

        if (metadata.isEmpty()) {
            return null;
        }

        return plugin.getServer().getPlayerExact(metadata.get(0).asString());
    }
}
