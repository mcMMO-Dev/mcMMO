package com.gmail.nossr50.listeners;

import com.gmail.nossr50.config.MainConfig;
import com.gmail.nossr50.config.WorldBlacklist;
import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.player.PlayerUpdateInventoryTask;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.worldguard.WorldGuardUtils;
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
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class InventoryListener implements Listener {
    private final mcMMO pluginRef;

    public InventoryListener(final mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getPlayer().getWorld()))
            return;

        Block furnaceBlock = processInventoryOpenOrCloseEvent(event.getInventory());

        if (furnaceBlock == null) {
            return;
        }

        HumanEntity player = event.getPlayer();

        if (!pluginRef.getUserManager().hasPlayerDataKey(player)) {
            return;
        }

        //Profile not loaded
        if (pluginRef.getUserManager().getPlayer((Player) player) == null) {
            return;
        }

        if (!furnaceBlock.hasMetadata(MetadataConstants.FURNACE_TRACKING_METAKEY) && furnaceBlock.getMetadata(MetadataConstants.FURNACE_TRACKING_METAKEY).size() == 0)
            furnaceBlock.setMetadata(MetadataConstants.FURNACE_TRACKING_METAKEY, pluginRef.getUserManager().getPlayer((Player) player).getPlayerMetadata());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getPlayer().getWorld()))
            return;

        Block furnaceBlock = processInventoryOpenOrCloseEvent(event.getInventory());

        if (furnaceBlock == null || furnaceBlock.hasMetadata(MetadataConstants.FURNACE_TRACKING_METAKEY)) {
            return;
        }

        HumanEntity player = event.getPlayer();

        if (!pluginRef.getUserManager().hasPlayerDataKey(player)) {
            return;
        }

        furnaceBlock.removeMetadata(MetadataConstants.FURNACE_TRACKING_METAKEY, pluginRef);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceBurnEvent(FurnaceBurnEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld()))
            return;

        Block furnaceBlock = event.getBlock();
        BlockState furnaceState = furnaceBlock.getState();
        ItemStack smelting = furnaceState instanceof Furnace ? ((Furnace) furnaceState).getInventory().getSmelting() : null;

        if (!ItemUtils.isSmeltable(smelting)) {
            return;
        }

        Player player = getPlayerFromFurnace(furnaceBlock);

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!pluginRef.getWorldGuardManager().hasMainFlag(player))
                return;
        }

        if (!pluginRef.getUserManager().hasPlayerDataKey(player) || !Permissions.isSubSkillEnabled(player, SubSkillType.SMELTING_FUEL_EFFICIENCY)) {
            return;
        }

        //Profile not loaded
        if (pluginRef.getUserManager().getPlayer(player) == null) {
            return;
        }

        event.setBurnTime(pluginRef.getUserManager().getPlayer(player).getSmeltingManager().fuelEfficiency(event.getBurnTime()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceSmeltEvent(FurnaceSmeltEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld()))
            return;

        Block furnaceBlock = event.getBlock();
        ItemStack smelting = event.getSource();

        if (!ItemUtils.isSmeltable(smelting)) {
            return;
        }

        Player player = getPlayerFromFurnace(furnaceBlock);

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!pluginRef.getWorldGuardManager().hasMainFlag(player))
                return;
        }

        if (!pluginRef.getUserManager().hasPlayerDataKey(player) || !PrimarySkillType.SMELTING.getPermissions(player)) {
            return;
        }

        //Profile not loaded
        if (pluginRef.getUserManager().getPlayer(player) == null) {
            return;
        }

        event.setResult(pluginRef.getUserManager().getPlayer(player).getSmeltingManager().smeltProcessing(smelting, event.getResult()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceExtractEvent(FurnaceExtractEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getPlayer().getWorld()))
            return;

        Block furnaceBlock = event.getBlock();

        if (!ItemUtils.isSmelted(new ItemStack(event.getItemType(), event.getItemAmount()))) {
            return;
        }

        Player player = getPlayerFromFurnace(furnaceBlock);

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!pluginRef.getWorldGuardManager().hasMainFlag(player))
                return;
        }

        if (!pluginRef.getUserManager().hasPlayerDataKey(player) || !Permissions.vanillaXpBoost(player, PrimarySkillType.SMELTING)) {
            return;
        }

        //Profile not loaded
        if (pluginRef.getUserManager().getPlayer(player) == null) {
            return;
        }

        int xpToDrop = event.getExpToDrop();
        int exp = pluginRef.getUserManager().getPlayer(player).getSmeltingManager().vanillaXPBoost(xpToDrop);
        event.setExpToDrop(exp);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClickEventNormal(InventoryClickEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getWhoClicked().getWorld()))
            return;

        Inventory inventory = event.getInventory();

        if (event.getWhoClicked() instanceof Player) {
            Player player = ((Player) event.getWhoClicked()).getPlayer();
            Block furnaceBlock = processInventoryOpenOrCloseEvent(event.getInventory());

            if (furnaceBlock != null) {
                if (furnaceBlock.getMetadata(MetadataConstants.FURNACE_TRACKING_METAKEY).size() > 0)
                    furnaceBlock.removeMetadata(MetadataConstants.FURNACE_TRACKING_METAKEY, pluginRef);

                //Profile not loaded
                if (pluginRef.getUserManager().getPlayer(player) == null) {
                    return;
                }

                furnaceBlock.setMetadata(MetadataConstants.FURNACE_TRACKING_METAKEY, pluginRef.getUserManager().getPlayer(player).getPlayerMetadata());
            }
        }

        if (!(inventory instanceof BrewerInventory)) {
            return;
        }

        InventoryHolder holder = inventory.getHolder();

        if (!(holder instanceof BrewingStand)) {
            return;
        }

        HumanEntity whoClicked = event.getWhoClicked();

        //TODO: This is where Alchemy permissions used to be checked
        //TODO: This is where Alchemy permissions used to be checked
        //TODO: This is where Alchemy permissions used to be checked
        //TODO: This is where Alchemy permissions used to be checked
        /*if (!pluginRef.getUserManager().hasPlayerDataKey(event.getWhoClicked()) || !Permissions.isSubSkillEnabled(whoClicked, SubSkillType.ALCHEMY_CONCOCTIONS)) {
            return;
        }*/

        Player player = (Player) whoClicked;

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!pluginRef.getWorldGuardManager().hasMainFlag(player))
                return;
        }

//        BrewingStand stand = (BrewingStand) holder;
//        ItemStack clicked = event.getCurrentItem();
//        ItemStack cursor = event.getCursor();

//        if ((clicked != null && (clicked.getType() == Material.POTION || clicked.getType() == Material.SPLASH_POTION || clicked.getType() == Material.LINGERING_POTION)) || (cursor != null && (cursor.getType() == Material.POTION || cursor.getType() == Material.SPLASH_POTION || cursor.getType() == Material.LINGERING_POTION))) {
//            AlchemyPotionBrewer.scheduleCheck(player, stand);
//            return;
//        }
//
//        ClickType click = event.getClick();
//        InventoryType.SlotType slot = event.getSlotType();

//        if (click.isShiftClick()) {
//            switch (slot) {
//                case FUEL:
//                    AlchemyPotionBrewer.scheduleCheck(player, stand);
//                    return;
//                case CONTAINER:
//                case QUICKBAR:
//                    if (!AlchemyPotionBrewer.isValidIngredient(player, clicked)) {
//                        return;
//                    }
//
//                    if (!AlchemyPotionBrewer.transferItems(event.getView(), event.getRawSlot(), click)) {
//                        return;
//                    }
//
//                    event.setCancelled(true);
//                    AlchemyPotionBrewer.scheduleUpdate(inventory);
//                    AlchemyPotionBrewer.scheduleCheck(player, stand);
//                    return;
//                default:
//            }
//        } else if (slot == InventoryType.SlotType.FUEL) {
//            boolean emptyClicked = AlchemyPotionBrewer.isEmpty(clicked);
//
//            if (AlchemyPotionBrewer.isEmpty(cursor)) {
//                if (emptyClicked && click == ClickType.NUMBER_KEY) {
//                    AlchemyPotionBrewer.scheduleCheck(player, stand);
//                    return;
//                }
//
//                AlchemyPotionBrewer.scheduleCheck(player, stand);
//            } else if (emptyClicked) {
//                if (AlchemyPotionBrewer.isValidIngredient(player, cursor)) {
//                    int amount = cursor.getAmount();
//
//                    if (click == ClickType.LEFT || (click == ClickType.RIGHT && amount == 1)) {
//                        event.setCancelled(true);
//                        event.setCurrentItem(cursor.clone());
//                        event.setCursor(null);
//
//                        AlchemyPotionBrewer.scheduleUpdate(inventory);
//                        AlchemyPotionBrewer.scheduleCheck(player, stand);
//                    } else if (click == ClickType.RIGHT) {
//                        event.setCancelled(true);
//
//                        ItemStack one = cursor.clone();
//                        one.setAmount(1);
//
//                        ItemStack rest = cursor.clone();
//                        rest.setAmount(amount - 1);
//
//                        event.setCurrentItem(one);
//                        event.setCursor(rest);
//
//                        AlchemyPotionBrewer.scheduleUpdate(inventory);
//                        AlchemyPotionBrewer.scheduleCheck(player, stand);
//                    }
//                }
//            }
//        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryDragEvent(InventoryDragEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getWhoClicked().getWorld()))
            return;

        Inventory inventory = event.getInventory();

        if (!(inventory instanceof BrewerInventory)) {
            return;
        }

        InventoryHolder holder = inventory.getHolder();

        if (!(holder instanceof BrewingStand)) {
            return;
        }

        HumanEntity whoClicked = event.getWhoClicked();

        //TODO: This is where alchemy permissions used to be checked
        //TODO: This is where alchemy permissions used to be checked
        //TODO: This is where alchemy permissions used to be checked
        //TODO: This is where alchemy permissions used to be checked
        /*if (!pluginRef.getUserManager().hasPlayerDataKey(event.getWhoClicked()) || !Permissions.isSubSkillEnabled(whoClicked, SubSkillType.ALCHEMY_CONCOCTIONS)) {
            return;
        }*/

//        if (!event.getInventorySlots().contains(Alchemy.INGREDIENT_SLOT)) {
//            return;
//        }
//
//        ItemStack cursor = event.getCursor();
//        ItemStack ingredient = ((BrewerInventory) inventory).getIngredient();
//
//        if (AlchemyPotionBrewer.isEmpty(ingredient) || ingredient.isSimilar(cursor)) {
//            Player player = (Player) whoClicked;
//
//            /* WORLD GUARD MAIN FLAG CHECK */
//            if (WorldGuardUtils.isWorldGuardLoaded()) {
//                if (!plugin.getWorldGuardManager().hasMainFlag(player))
//                    return;
//            }
//
//            if (AlchemyPotionBrewer.isValidIngredient(player, cursor)) {
//                // Not handled: dragging custom ingredients over ingredient slot (does not trigger any event)
//                AlchemyPotionBrewer.scheduleCheck(player, (BrewingStand) holder);
//                return;
//            }
//
//            event.setCancelled(true);
//            AlchemyPotionBrewer.scheduleUpdate(inventory);
//        }
    }

    // Apparently sometimes vanilla brewing beats our task listener to the actual brew. We handle this by cancelling the vanilla event and finishing our brew ourselves.
//    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
//    public void onBrew(BrewEvent event) {
//        /* WORLD BLACKLIST CHECK */
//        if (WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld()))
//            return;
//
//        if (event instanceof FakeBrewEvent)
//            return;
//        Location location = event.getBlock().getLocation();
//        if (Alchemy.brewingStandMap.containsKey(location)) {
//            Alchemy.brewingStandMap.get(location).finishImmediately();
//            event.setCancelled(true);
//        }
//    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryMoveItemEvent(InventoryMoveItemEvent event) {
        /* WORLD BLACKLIST CHECK */

        //Location can be null here
        if (event.getSource().getLocation() != null)
            if (WorldBlacklist.isWorldBlacklisted(event.getSource().getLocation().getWorld()))
                return;

        Inventory inventory = event.getDestination();

        if (!(inventory instanceof BrewerInventory)) {
            return;
        }

        InventoryHolder holder = inventory.getHolder();

        if (!(holder instanceof BrewingStand)) {
            return;
        }

        ItemStack item = event.getItem();

        if (MainConfig.getInstance().getPreventHopperTransferIngredients() && item.getType() != Material.POTION && item.getType() != Material.SPLASH_POTION && item.getType() != Material.LINGERING_POTION) {
            event.setCancelled(true);
            return;
        }

        if (MainConfig.getInstance().getPreventHopperTransferBottles() && (item.getType() == Material.POTION || item.getType() == Material.SPLASH_POTION || item.getType() == Material.LINGERING_POTION)) {
            event.setCancelled(true);
            return;
        }

//        if (MainConfig.getInstance().getEnabledForHoppers() && AlchemyPotionBrewer.isValidIngredient(null, item)) {
//            AlchemyPotionBrewer.scheduleCheck(null, (BrewingStand) holder);
//        }
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
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getWhoClicked().getWorld()))
            return;

        final HumanEntity whoClicked = event.getWhoClicked();

        if (!whoClicked.hasMetadata(MetadataConstants.PLAYER_DATA_METAKEY)) {
            return;
        }

        ItemStack result = event.getRecipe().getResult();

        if (!ItemUtils.isMcMMOItem(result)) {
            return;
        }

        Player player = (Player) whoClicked;

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!pluginRef.getWorldGuardManager().hasMainFlag(player))
                return;
        }

        new PlayerUpdateInventoryTask((Player) whoClicked).runTaskLater(pluginRef, 0);
    }

    private Block processInventoryOpenOrCloseEvent(Inventory inventory) {
        if (!(inventory instanceof FurnaceInventory)) {
            return null;
        }

        Furnace furnace = (Furnace) inventory.getHolder();

        if (furnace == null) {
            return null;
        }

        return furnace.getBlock();
    }

    private Player getPlayerFromFurnace(Block furnaceBlock) {
        List<MetadataValue> metadata = furnaceBlock.getMetadata(MetadataConstants.FURNACE_TRACKING_METAKEY);

        if (metadata.isEmpty()) {
            return null;
        }

        return pluginRef.getServer().getPlayerExact(metadata.get(0).asString());
    }
}
