package com.gmail.nossr50.listeners;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
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
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.runnables.PlayerUpdateInventoryTask;
import com.gmail.nossr50.skills.smelting.SmeltingManager;
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
            furnaceBlock.setMetadata(mcMMO.furnaceMetadataKey, new FixedMetadataValue(plugin, player.getName()));
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
        BlockState furnaceState = furnaceBlock.getState();

        if (!(furnaceState instanceof Furnace)) {
            return;
        }

        List<MetadataValue> metadata = furnaceBlock.getMetadata(mcMMO.furnaceMetadataKey);

        if (metadata.isEmpty()) {
            return;
        }

        ItemStack smelting = ((Furnace) furnaceState).getInventory().getSmelting();

        if (smelting == null || !ItemUtils.isSmeltable(smelting)) {
            return;
        }

        // We can make this assumption because we (should) be the only ones using this exact metadata
        Player player = plugin.getServer().getPlayer(metadata.get(0).asString());

        if (Misc.isNPCEntity(player) || !Permissions.fuelEfficiency(player)) {
            return;
        }

        event.setBurnTime(UserManager.getPlayer(player).getSmeltingManager().fuelEfficiency(event.getBurnTime()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceSmeltEvent(FurnaceSmeltEvent event) {
        Block furnaceBlock = event.getBlock();
        BlockState furnaceState = furnaceBlock.getState();

        if (!(furnaceState instanceof Furnace)) {
            return;
        }

        List<MetadataValue> metadata = furnaceBlock.getMetadata(mcMMO.furnaceMetadataKey);

        if (metadata.isEmpty()) {
            return;
        }

        ItemStack smelting = ((Furnace) furnaceState).getInventory().getSmelting();

        if (smelting == null || !ItemUtils.isSmeltable(smelting)) {
            return;
        }

        // We can make this assumption because we (should) be the only ones using this exact metadata
        Player player = plugin.getServer().getPlayer(metadata.get(0).asString());

        if (Misc.isNPCEntity(player) || !Permissions.skillEnabled(player, SkillType.SMELTING)) {
            return;
        }

        event.setResult(UserManager.getPlayer(player).getSmeltingManager().smeltProcessing(smelting.getType(), event.getResult()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceExtractEvent(FurnaceExtractEvent event) {
        Block furnaceBlock = event.getBlock();
        BlockState furnaceState = furnaceBlock.getState();

        if (!(furnaceState instanceof Furnace)) {
            return;
        }

        List<MetadataValue> metadata = furnaceBlock.getMetadata(mcMMO.furnaceMetadataKey);

        if (metadata.isEmpty()) {
            return;
        }

        ItemStack result = ((Furnace) furnaceState).getInventory().getResult();

        if (result == null || !ItemUtils.isSmelted(result)) {
            return;
        }

        // We can make this assumption because we (should) be the only ones using this exact metadata
        Player player = plugin.getServer().getPlayer(metadata.get(0).asString());

        if (Misc.isNPCEntity(player)) {
            return;
        }

        SmeltingManager smeltingManager = UserManager.getPlayer(player).getSmeltingManager();

        if (smeltingManager.canUseVanillaXpBoost()) {
            event.setExpToDrop(smeltingManager.vanillaXPBoost(event.getExpToDrop()));
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
}
