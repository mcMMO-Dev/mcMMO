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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.runnables.player.PlayerUpdateInventoryTask;
import com.gmail.nossr50.skills.alchemy.Alchemy;
import com.gmail.nossr50.skills.alchemy.AlchemyPotionBrewer;
import com.gmail.nossr50.util.ItemUtils;
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
        Block furnaceBlock = processInventoryOpenOrCloseEvent(event.getInventory());

        if (furnaceBlock == null || furnaceBlock.hasMetadata(mcMMO.furnaceMetadataKey)) {
            return;
        }

        HumanEntity player = event.getPlayer();

        if (!UserManager.hasPlayerDataKey(player)) {
            return;
        }

        furnaceBlock.setMetadata(mcMMO.furnaceMetadataKey, UserManager.getPlayer((Player) player).getPlayerMetadata());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        Block furnaceBlock = processInventoryOpenOrCloseEvent(event.getInventory());

        if (furnaceBlock == null || furnaceBlock.hasMetadata(mcMMO.furnaceMetadataKey)) {
            return;
        }

        HumanEntity player = event.getPlayer();

        if (!UserManager.hasPlayerDataKey(player)) {
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

        if (!UserManager.hasPlayerDataKey(player) || !Permissions.secondaryAbilityEnabled(player, SecondaryAbility.FUEL_EFFICIENCY)) {
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

        if (!UserManager.hasPlayerDataKey(player) || !SkillType.SMELTING.getPermissions(player)) {
            return;
        }

        event.setResult(UserManager.getPlayer(player).getSmeltingManager().smeltProcessing(smelting, event.getResult()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceExtractEvent(FurnaceExtractEvent event) {
        Block furnaceBlock = event.getBlock();

        if (!ItemUtils.isSmelted(new ItemStack(event.getItemType(), event.getItemAmount()))) {
            return;
        }

        Player player = getPlayerFromFurnace(furnaceBlock);

        if (!UserManager.hasPlayerDataKey(player) || !Permissions.vanillaXpBoost(player, SkillType.SMELTING)) {
            return;
        }

        int exp = UserManager.getPlayer(player).getSmeltingManager().vanillaXPBoost(event.getExpToDrop());
        event.setExpToDrop(exp);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClickEventNormal(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();

        if (!(inventory instanceof BrewerInventory)) {
            return;
        }

        InventoryHolder holder = inventory.getHolder();

        if (!(holder instanceof BrewingStand)) {
            return;
        }

        HumanEntity whoClicked = event.getWhoClicked();

        if (!UserManager.hasPlayerDataKey(event.getWhoClicked()) || !Permissions.secondaryAbilityEnabled(whoClicked, SecondaryAbility.CONCOCTIONS)) {
            return;
        }

        Player player = (Player) whoClicked;
        BrewingStand stand = (BrewingStand) holder;
        ItemStack clicked = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        if ((clicked != null && (clicked.getType() == Material.POTION || clicked.getType() == Material.SPLASH_POTION || clicked.getType() == Material.LINGERING_POTION)) || (cursor != null && (cursor.getType() == Material.POTION || cursor.getType() == Material.SPLASH_POTION || cursor.getType() == Material.LINGERING_POTION))) {
            AlchemyPotionBrewer.scheduleCheck(player, stand);
            return;
        }

        ClickType click = event.getClick();
        InventoryType.SlotType slot = event.getSlotType();

        if (click.isShiftClick()) {
            switch (slot) {
                case FUEL:
                    AlchemyPotionBrewer.scheduleCheck(player, stand);
                    return;
                case CONTAINER:
                case QUICKBAR:
                    if (!AlchemyPotionBrewer.isValidIngredient(player, clicked)) {
                        return;
                    }

                    if (!AlchemyPotionBrewer.transferItems(event.getView(), event.getRawSlot(), click)) {
                        return;
                    }

                    event.setCancelled(true);
                    AlchemyPotionBrewer.scheduleUpdate(inventory);
                    AlchemyPotionBrewer.scheduleCheck(player, stand);
                    return;
                default:
                    return;
            }
        }
        else if (slot == InventoryType.SlotType.FUEL) {
            boolean emptyClicked = AlchemyPotionBrewer.isEmpty(clicked);

            if (AlchemyPotionBrewer.isEmpty(cursor)) {
                if (emptyClicked && click == ClickType.NUMBER_KEY) {
                    AlchemyPotionBrewer.scheduleCheck(player, stand);
                    return;
                }

                AlchemyPotionBrewer.scheduleCheck(player, stand);
            }
            else if (emptyClicked) {
                if (AlchemyPotionBrewer.isValidIngredient(player, cursor)) {
                    int amount = cursor.getAmount();

                    if (click == ClickType.LEFT || (click == ClickType.RIGHT && amount == 1)) {
                        event.setCancelled(true);
                        event.setCurrentItem(cursor.clone());
                        event.setCursor(null);

                        AlchemyPotionBrewer.scheduleUpdate(inventory);
                        AlchemyPotionBrewer.scheduleCheck(player, stand);
                    }
                    else if (click == ClickType.RIGHT) {
                        event.setCancelled(true);

                        ItemStack one = cursor.clone();
                        one.setAmount(1);

                        ItemStack rest = cursor.clone();
                        rest.setAmount(amount - 1);

                        event.setCurrentItem(one);
                        event.setCursor(rest);

                        AlchemyPotionBrewer.scheduleUpdate(inventory);
                        AlchemyPotionBrewer.scheduleCheck(player, stand);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryDragEvent(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();

        if (!(inventory instanceof BrewerInventory)) {
            return;
        }

        InventoryHolder holder = inventory.getHolder();

        if (!(holder instanceof BrewingStand)) {
            return;
        }

        HumanEntity whoClicked = event.getWhoClicked();

        if (!UserManager.hasPlayerDataKey(event.getWhoClicked()) || !Permissions.secondaryAbilityEnabled(whoClicked, SecondaryAbility.CONCOCTIONS)) {
            return;
        }

        if (!event.getInventorySlots().contains(Alchemy.INGREDIENT_SLOT)) {
            return;
        }

        ItemStack cursor = event.getCursor();
        ItemStack ingredient = ((BrewerInventory) inventory).getIngredient();

        if (AlchemyPotionBrewer.isEmpty(ingredient) || ingredient.isSimilar(cursor)) {
            Player player = (Player) whoClicked;

            if (AlchemyPotionBrewer.isValidIngredient(player, cursor)) {
                // Not handled: dragging custom ingredients over ingredient slot (does not trigger any event)
                AlchemyPotionBrewer.scheduleCheck(player, (BrewingStand) holder);
                return;
            }

            event.setCancelled(true);
            AlchemyPotionBrewer.scheduleUpdate(inventory);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryMoveItemEvent(InventoryMoveItemEvent event) {
        Inventory inventory = event.getDestination();

        if (!(inventory instanceof BrewerInventory)) {
            return;
        }

        InventoryHolder holder = inventory.getHolder();

        if (!(holder instanceof BrewingStand)) {
            return;
        }

        ItemStack item = event.getItem();

        if (Config.getInstance().getPreventHopperTransferIngredients() && item.getType() != Material.POTION && item.getType() != Material.SPLASH_POTION && item.getType() != Material.LINGERING_POTION) {
            event.setCancelled(true);
            return;
        }

        if (Config.getInstance().getPreventHopperTransferBottles() && (item.getType() == Material.POTION || item.getType() == Material.SPLASH_POTION || item.getType() == Material.LINGERING_POTION)) {
            event.setCancelled(true);
            return;
        }

        if (Config.getInstance().getEnabledForHoppers() && AlchemyPotionBrewer.isValidIngredient(null, item)) {
            AlchemyPotionBrewer.scheduleCheck(null, (BrewingStand) holder);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClickEvent(InventoryClickEvent event) {
        SkillUtils.removeAbilityBuff(event.getCurrentItem());
        if (event.getAction() == InventoryAction.HOTBAR_SWAP) {
            SkillUtils.removeAbilityBuff(event.getWhoClicked().getInventory().getItem(event.getHotbarButton()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryOpenEvent(InventoryOpenEvent event) {
        SkillUtils.removeAbilityBuff(event.getPlayer().getInventory().getItemInMainHand());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent event) {
        final HumanEntity whoClicked = event.getWhoClicked();

        if (!whoClicked.hasMetadata(mcMMO.playerDataKey)) {
            return;
        }

        ItemStack result = event.getRecipe().getResult();

        if (!ItemUtils.isMcMMOItem(result)) {
            return;
        }

        new PlayerUpdateInventoryTask((Player) whoClicked).runTaskLater(plugin, 0);
    }

    private Block processInventoryOpenOrCloseEvent(Inventory inventory) {
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
