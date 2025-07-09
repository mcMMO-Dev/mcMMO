package com.gmail.nossr50.listeners;

import com.gmail.nossr50.config.WorldBlacklist;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.events.fake.FakeBrewEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.player.PlayerUpdateInventoryTask;
import com.gmail.nossr50.skills.alchemy.Alchemy;
import com.gmail.nossr50.skills.alchemy.AlchemyPotionBrewer;
import com.gmail.nossr50.util.ContainerMetadataUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.worldguard.WorldGuardManager;
import com.gmail.nossr50.worldguard.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Furnace;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryListener implements Listener {
    private final mcMMO plugin;

    public InventoryListener(final mcMMO plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceBurnEvent(FurnaceBurnEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld())) {
            return;
        }

        Block furnaceBlock = event.getBlock();
        BlockState furnaceState = furnaceBlock.getState();
        ItemStack smelting =
                furnaceState instanceof Furnace ? ((Furnace) furnaceState).getInventory()
                        .getSmelting() : null;

        if (!ItemUtils.isSmeltable(smelting) || event.getBurnTime() <= 0) {
            return;
        }

        Furnace furnace = (Furnace) furnaceState;
        OfflinePlayer offlinePlayer = ContainerMetadataUtils.getContainerOwner(furnace);
        Player player;

        if (offlinePlayer != null && offlinePlayer.isOnline() && offlinePlayer instanceof Player) {
            player = (Player) offlinePlayer;

            if (!Permissions.isSubSkillEnabled(player, SubSkillType.SMELTING_FUEL_EFFICIENCY)) {
                return;
            }

            final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

            if (mmoPlayer != null) {
                boolean debugMode = mmoPlayer.isDebugMode();

                if (debugMode) {
                    player.sendMessage("FURNACE FUEL EFFICIENCY DEBUG REPORT");
                    player.sendMessage("Furnace - " + furnace.hashCode());
                    player.sendMessage("Furnace Type: " + furnaceBlock.getType());
                    player.sendMessage("Burn Length before Fuel Efficiency is applied - "
                            + event.getBurnTime());
                }

                event.setBurnTime(
                        mmoPlayer.getSmeltingManager().fuelEfficiency(event.getBurnTime()));

                if (debugMode) {
                    player.sendMessage("New Furnace Burn Length (after applying fuel efficiency) "
                            + event.getBurnTime());
                    player.sendMessage("");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceSmeltEvent(FurnaceSmeltEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld())) {
            return;
        }

        BlockState blockState = event.getBlock()
                .getState(); //Furnaces can only be cast from a BlockState not a Block
        ItemStack smelting = event.getSource();

        if (!ItemUtils.isSmeltable(smelting)) {
            return;
        }

        if (blockState instanceof Furnace furnace) {
            OfflinePlayer offlinePlayer = ContainerMetadataUtils.getContainerOwner(furnace);

            if (offlinePlayer != null) {

                McMMOPlayer offlineProfile = UserManager.getOfflinePlayer(offlinePlayer);

                //Profile doesn't exist
                if (offlineProfile != null) {
                    //Process smelting
                    offlineProfile.getSmeltingManager().smeltProcessing(event, furnace);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceExtractEvent(FurnaceExtractEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getPlayer().getWorld())) {
            return;
        }

        BlockState furnaceBlock = event.getBlock().getState();

        if (!ItemUtils.isSmelted(new ItemStack(event.getItemType(), event.getItemAmount()))) {
            return;
        }

        Player player = event.getPlayer();

        if (furnaceBlock instanceof Furnace) {
            /* WORLD GUARD MAIN FLAG CHECK */
            if (WorldGuardUtils.isWorldGuardLoaded()) {
                if (!WorldGuardManager.getInstance().hasMainFlag(player)) {
                    return;
                }
            }

            if (!UserManager.hasPlayerDataKey(player) || !Permissions.vanillaXpBoost(player,
                    PrimarySkillType.SMELTING)) {
                return;
            }

            //Profile not loaded
            if (UserManager.getPlayer(player) == null) {
                return;
            }

            int xpToDrop = event.getExpToDrop();
            int exp = UserManager.getPlayer(player).getSmeltingManager().vanillaXPBoost(xpToDrop);
            event.setExpToDrop(exp);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClickEventNormal(InventoryClickEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getWhoClicked().getWorld())) {
            return;
        }

        //We should never care to do processing if the player clicks outside the window
//        if (isOutsideWindowClick(event))
//            return;

        Inventory inventory = event.getInventory();

        Player player = ((Player) event.getWhoClicked()).getPlayer();
        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        if (event.getInventory() instanceof FurnaceInventory furnaceInventory) {
            if (!mcMMO.p.getSkillTools()
                    .doesPlayerHaveSkillPermission(player, PrimarySkillType.SMELTING)) {
                return;
            }
            //Switch owners
            ContainerMetadataUtils.processContainerOwnership(furnaceInventory.getHolder(), player);
        }

        if (event.getInventory() instanceof BrewerInventory brewerInventory) {
            if (!mcMMO.p.getSkillTools()
                    .doesPlayerHaveSkillPermission(player, PrimarySkillType.ALCHEMY)) {
                return;
            }
            // switch owners
            ContainerMetadataUtils.processContainerOwnership(brewerInventory.getHolder(), player);
        }

        if (!(inventory instanceof BrewerInventory)) {
            return;
        }

        InventoryHolder holder = inventory.getHolder();

        if (!(holder instanceof BrewingStand stand)) {
            return;
        }

        HumanEntity whoClicked = event.getWhoClicked();

        if (mmoPlayer == null || !Permissions.isSubSkillEnabled(whoClicked,
                SubSkillType.ALCHEMY_CONCOCTIONS)) {
            return;
        }

        // TODO: Investigate why this WG check is all the way down here?
        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!WorldGuardManager.getInstance().hasMainFlag(player)) {
                return;
            }
        }

        final ItemStack clicked = event.getCurrentItem();
        final ItemStack cursor = event.getCursor();

        if ((clicked != null && (clicked.getType() == Material.POTION
                || clicked.getType() == Material.SPLASH_POTION
                || clicked.getType() == Material.LINGERING_POTION))
                || (cursor != null && (cursor.getType() == Material.POTION
                || cursor.getType() == Material.SPLASH_POTION
                || cursor.getType() == Material.LINGERING_POTION))) {
            AlchemyPotionBrewer.scheduleCheck(stand);
            return;
        }

        ClickType click = event.getClick();
        InventoryType.SlotType slot = event.getSlotType();

        if (click.isShiftClick()) {
            switch (slot) {
                case FUEL:
                    AlchemyPotionBrewer.scheduleCheck(stand);
                    return;
                case CONTAINER:
                case QUICKBAR:
                    if (!AlchemyPotionBrewer.isValidIngredientByPlayer(player, clicked)) {
                        return;
                    }

                    if (!AlchemyPotionBrewer.transferItems(event.getView(), event.getRawSlot(),
                            click)) {
                        return;
                    }

                    event.setCancelled(true);
                    AlchemyPotionBrewer.scheduleUpdate(inventory);
                    AlchemyPotionBrewer.scheduleCheck(stand);
                    return;
                default:
            }
        } else if (slot == InventoryType.SlotType.FUEL) {
            boolean emptyClicked = AlchemyPotionBrewer.isEmpty(clicked);

            if (AlchemyPotionBrewer.isEmpty(cursor)) {
                if (emptyClicked && click == ClickType.NUMBER_KEY) {
                    AlchemyPotionBrewer.scheduleCheck(stand);
                    return;
                }

                AlchemyPotionBrewer.scheduleCheck(stand);
            } else if (emptyClicked) {
                if (AlchemyPotionBrewer.isValidIngredientByPlayer(player, cursor)) {
                    int amount = cursor.getAmount();

                    if (click == ClickType.LEFT || (click == ClickType.RIGHT && amount == 1)) {
                        event.setCancelled(true);
                        event.setCurrentItem(cursor.clone());
                        event.setCursor(null);

                        AlchemyPotionBrewer.scheduleUpdate(inventory);
                        AlchemyPotionBrewer.scheduleCheck(stand);
                    } else if (click == ClickType.RIGHT) {
                        event.setCancelled(true);

                        ItemStack one = cursor.clone();
                        one.setAmount(1);

                        ItemStack rest = cursor.clone();
                        rest.setAmount(amount - 1);

                        event.setCurrentItem(one);
                        event.setCursor(rest);

                        AlchemyPotionBrewer.scheduleUpdate(inventory);
                        AlchemyPotionBrewer.scheduleCheck(stand);
                    }
                }
            }
        }
    }

    public boolean isOutsideWindowClick(InventoryClickEvent event) {
        return event.getHotbarButton() == -1;
    }


    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryDragEvent(InventoryDragEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getWhoClicked().getWorld())) {
            return;
        }

        Inventory inventory = event.getInventory();

        if (!(inventory instanceof BrewerInventory)) {
            return;
        }

        InventoryHolder holder = inventory.getHolder();

        if (!(holder instanceof BrewingStand)) {
            return;
        }

        HumanEntity whoClicked = event.getWhoClicked();

        if (!UserManager.hasPlayerDataKey(event.getWhoClicked()) || !Permissions.isSubSkillEnabled(
                whoClicked, SubSkillType.ALCHEMY_CONCOCTIONS)) {
            return;
        }

        if (!event.getInventorySlots().contains(Alchemy.INGREDIENT_SLOT)) {
            return;
        }

        ItemStack cursor = event.getCursor();
        ItemStack ingredient = ((BrewerInventory) inventory).getIngredient();

        if (AlchemyPotionBrewer.isEmpty(ingredient) || ingredient.isSimilar(cursor)) {
            final Player player = (Player) whoClicked;

            /* WORLD GUARD MAIN FLAG CHECK */
            if (WorldGuardUtils.isWorldGuardLoaded()) {
                if (!WorldGuardManager.getInstance().hasMainFlag(player)) {
                    return;
                }
            }

            if (AlchemyPotionBrewer.isValidIngredientByPlayer(player, cursor)) {
                // Not handled: dragging custom ingredients over ingredient slot (does not trigger any event)
                AlchemyPotionBrewer.scheduleCheck((BrewingStand) holder);
                return;
            }

            event.setCancelled(true);
            AlchemyPotionBrewer.scheduleUpdate(inventory);
        }
    }

    // Apparently sometimes vanilla brewing beats our task listener to the actual brew. We handle this by cancelling the vanilla event and finishing our brew ourselves.
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBrew(BrewEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld())) {
            return;
        }

        if (event instanceof FakeBrewEvent) {
            return;
        }

        Location location = event.getBlock().getLocation();
        if (Alchemy.brewingStandMap.containsKey(location)) {
            Alchemy.brewingStandMap.get(location).finishImmediately();
            event.setCancelled(true);
        }
    }

//    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
//    public void onBrewStart(BrewingStartEvent event) {
//        /* WORLD BLACKLIST CHECK */
//        if (WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld()))
//            return;
//
//        if (event instanceof FakeEvent)
//            return;
//    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryMoveItemEvent(InventoryMoveItemEvent event) {
        /* WORLD BLACKLIST CHECK */

        if (event.getSource().getLocation() != null) {
            if (WorldBlacklist.isWorldBlacklisted(event.getSource().getLocation().getWorld())) {
                return;
            }
        }

        final Inventory inventory = event.getDestination();

        if (!(inventory instanceof BrewerInventory)) {
            return;
        }

        final InventoryHolder holder = inventory.getHolder();

        if (holder instanceof BrewingStand brewingStand) {

            ItemStack item = event.getItem();

            if (mcMMO.p.getGeneralConfig().getPreventHopperTransferIngredients()
                    && item.getType() != Material.POTION && item.getType() != Material.SPLASH_POTION
                    && item.getType() != Material.LINGERING_POTION) {
                event.setCancelled(true);
                return;
            }

            if (mcMMO.p.getGeneralConfig().getPreventHopperTransferBottles() && (
                    item.getType() == Material.POTION || item.getType() == Material.SPLASH_POTION
                            || item.getType() == Material.LINGERING_POTION)) {
                event.setCancelled(true);
                return;
            }
            int ingredientLevel = 1;

            OfflinePlayer offlinePlayer = ContainerMetadataUtils.getContainerOwner(brewingStand);
            if (offlinePlayer != null && offlinePlayer.isOnline()) {
                final McMMOPlayer mmoPlayer = UserManager.getPlayer(offlinePlayer.getPlayer());
                if (mmoPlayer != null) {
                    ingredientLevel = mmoPlayer.getAlchemyManager().getTier();
                }
            }

            if (mcMMO.p.getGeneralConfig().getEnabledForHoppers()
                    && AlchemyPotionBrewer.isValidIngredientByLevel(ingredientLevel, item)) {
                AlchemyPotionBrewer.scheduleCheck(brewingStand);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }

        SkillUtils.removeAbilityBuff(event.getCurrentItem());

        if (event.getAction() == InventoryAction.HOTBAR_SWAP) {
            if (isOutsideWindowClick(event)) {
                return;
            }

            PlayerInventory playerInventory = event.getWhoClicked().getInventory();

            if (playerInventory.getItem(event.getHotbarButton()) != null) {
                SkillUtils.removeAbilityBuff(playerInventory.getItem(event.getHotbarButton()));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryOpenEvent(InventoryOpenEvent event) {
        SkillUtils.removeAbilityBuff(event.getPlayer().getInventory().getItemInMainHand());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getWhoClicked().getWorld())) {
            return;
        }

        final HumanEntity whoClicked = event.getWhoClicked();

        if (!whoClicked.hasMetadata(MetadataConstants.METADATA_KEY_PLAYER_DATA)) {
            return;
        }

        ItemStack result = event.getRecipe().getResult();

        //TODO: Used for Chimaera Wing, but not sure it is still necessary
        if (!ItemUtils.isMcMMOItem(result)) {
            return;
        }

        final Player player = (Player) whoClicked;

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!WorldGuardManager.getInstance().hasMainFlag(player)) {
                return;
            }
        }

        mcMMO.p.getFoliaLib().getScheduler()
                .runAtEntity(whoClicked, new PlayerUpdateInventoryTask((Player) whoClicked));
    }

}
