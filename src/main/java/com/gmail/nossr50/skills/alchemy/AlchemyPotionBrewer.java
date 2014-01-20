package com.gmail.nossr50.skills.alchemy;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.potion.PotionConfig;
import com.gmail.nossr50.datatypes.AlchemyPotion;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.runnables.PlayerUpdateInventoryTask;
import com.gmail.nossr50.runnables.skills.AlchemyBrewCheckTask;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;

public final class AlchemyPotionBrewer {
    private final static int[] BOTTLE_SLOTS = new int[]{0, 1, 2};
    private final static int INGREDIENT_SLOT = 3;

    public static boolean isValidBrew(Player player, ItemStack[] contents) {
        if (!isValidIngredient(player, contents[INGREDIENT_SLOT])) {
            return false;
        }

        for (int bottle : BOTTLE_SLOTS) {
            if (contents[bottle] != null && contents[bottle].getType() == Material.POTION) {
                AlchemyPotion potion = PotionConfig.getInstance().potionMap.get(contents[bottle].getDurability());

                if (getChildPotion(potion, contents[INGREDIENT_SLOT]) != null) {
                    return true;
                }
            }
        }

        return false;

    }

    private static AlchemyPotion getChildPotion(AlchemyPotion potion, ItemStack ingredient) {
        if (potion != null && potion.getChildDataValue(ingredient) != -1) {
            return PotionConfig.getInstance().potionMap.get(potion.getChildDataValue(ingredient));
        }

        return null;
    }

    private static boolean isEmpty(ItemStack item) {
        return item == null || item.getType() == Material.AIR || item.getAmount() == 0;
    }

    private static boolean removeIngredient(BrewerInventory inventory, Player player) {
        ItemStack ingredient = inventory.getIngredient();

        if (isEmpty(ingredient) || !isValidIngredient(player, ingredient)) {
            return false;
        }
        else if (ingredient.getAmount() <= 1) {
            inventory.setItem(INGREDIENT_SLOT, null);

            return true;
        }
        else {
            ingredient.setAmount(ingredient.getAmount() - 1);
            inventory.setItem(INGREDIENT_SLOT, ingredient);

            return true;
        }
    }

    private static boolean isValidIngredient(Player player, ItemStack item) {
        if (isEmpty(item)) {
            return false;
        }

        for (ItemStack ingredient : getValidIngredients(player)) {
            if (item.isSimilar(ingredient)) {
                return true;
            }
        }

        return false;
    }

    private static List<ItemStack> getValidIngredients(Player player) {
        if (player == null || !Permissions.secondaryAbilityEnabled(player, SecondaryAbility.CONCOCTIONS)) {
            return PotionConfig.getInstance().concoctionsIngredientsTierOne;
        }

        switch (UserManager.getPlayer(player).getAlchemyManager().getTier()) {
            case 8:
                return PotionConfig.getInstance().concoctionsIngredientsTierEight;
            case 7:
                return PotionConfig.getInstance().concoctionsIngredientsTierSeven;
            case 6:
                return PotionConfig.getInstance().concoctionsIngredientsTierSix;
            case 5:
                return PotionConfig.getInstance().concoctionsIngredientsTierFive;
            case 4:
                return PotionConfig.getInstance().concoctionsIngredientsTierFour;
            case 3:
                return PotionConfig.getInstance().concoctionsIngredientsTierThree;
            case 2:
                return PotionConfig.getInstance().concoctionsIngredientsTierTwo;
            default:
                return PotionConfig.getInstance().concoctionsIngredientsTierOne;
        }
    }

    public static void finishBrewing(Block brewingStand, Player player, boolean forced) {
        if (!(brewingStand.getState() instanceof BrewingStand)) {
            return;
        }

        BrewerInventory inventory = ((BrewingStand) brewingStand.getState()).getInventory();
        ItemStack ingredient = inventory.getIngredient() == null ? null : inventory.getIngredient().clone();

        if (!removeIngredient(inventory, player)) {
            return;
        }

        for (int bottle : BOTTLE_SLOTS) {
            if (!isEmpty(inventory.getItem(bottle)) && PotionConfig.getInstance().potionMap.containsKey(inventory.getItem(bottle).getDurability())) {
                AlchemyPotion input = PotionConfig.getInstance().potionMap.get(inventory.getItem(bottle).getDurability());
                AlchemyPotion output = PotionConfig.getInstance().potionMap.get(input.getChildDataValue(ingredient));

                if (output != null) {
                    inventory.setItem(bottle, output.toItemStack(inventory.getItem(bottle).getAmount()).clone());

                    if (player != null) {
                        UserManager.getPlayer(player).getAlchemyManager().handlePotionBrewSuccesses(1);
                    }
                }
            }
        }

        if (!forced) {
            scheduleUpdate(inventory);
        }
    }

    private static boolean transferOneItem(InventoryView view, int fromSlot, int toSlot) {
        ItemStack from = view.getItem(fromSlot).clone();
        ItemStack to = view.getItem(toSlot).clone();

        if (isEmpty(from)) {
            return false;
        }
        else if (!isEmpty(to) && from.getAmount() >= from.getType().getMaxStackSize()) {
            return false;
        }
        else if (isEmpty(to) || from.isSimilar(to)) {
            if (isEmpty(to)) {
                to = from.clone();
                to.setAmount(1);
            }
            else {
                to.setAmount(to.getAmount() + 1);
            }

            from.setAmount(from.getAmount() - 1);
            view.setItem(toSlot, isEmpty(to) ? null : to);
            view.setItem(fromSlot, isEmpty(from) ? null : from);

            return true;
        }

        return false;
    }

    /**
     * Transfer items between two ItemStacks, returning the leftover status
     */
    private static boolean transferItems(InventoryView view, int fromSlot, int toSlot) {
        if (isEmpty(view.getItem(fromSlot))) {
            return false;
        }
        else if (isEmpty(view.getItem(toSlot))) {
            view.setItem(toSlot, view.getItem(fromSlot).clone());
            view.setItem(fromSlot, null);

            return true;
        }
        else if (view.getItem(fromSlot).isSimilar(view.getItem(toSlot))) {
            if (view.getItem(fromSlot).getAmount() + view.getItem(toSlot).getAmount() > view.getItem(toSlot).getType().getMaxStackSize()) {
                int left = view.getItem(fromSlot).getAmount() + view.getItem(toSlot).getAmount() - view.getItem(toSlot).getType().getMaxStackSize();

                ItemStack to = new ItemStack(view.getItem(toSlot));
                to.setAmount(to.getType().getMaxStackSize());
                view.setItem(toSlot, to);

                ItemStack from = new ItemStack(view.getItem(fromSlot));
                from.setAmount(left);
                view.setItem(fromSlot, from);

                return true;
            }

            ItemStack to = new ItemStack(view.getItem(toSlot));
            to.setAmount(view.getItem(fromSlot).getAmount() + view.getItem(toSlot).getAmount());
            view.setItem(fromSlot, null);
            view.setItem(toSlot, to);

            return true;
        }
        return false;
    }

    public static void handleInventoryClick(InventoryClickEvent event) {
        Player player = event.getWhoClicked() instanceof Player ? (Player) event.getWhoClicked() : null;
        BrewingStand brewingStand = (BrewingStand) event.getInventory().getHolder();

        ItemStack cursor = event.getCursor();
        ItemStack clicked = event.getCurrentItem();

        if (clicked != null && clicked.getType() == Material.POTION) {
            scheduleCheck(player, brewingStand);

            return;
        }
        if (event.isShiftClick()) {
            if (event.getSlotType() == SlotType.FUEL) {
                scheduleCheck(player, brewingStand);

                return;
            }
            else if (event.getSlotType() == SlotType.CONTAINER || event.getSlotType() == SlotType.QUICKBAR) {
                if (isValidIngredient(player, clicked)) {
                    if (event.isLeftClick()) {
                        transferItems(event.getView(), event.getRawSlot(), INGREDIENT_SLOT);
                    }
                    else if (event.isRightClick()) {
                        transferOneItem(event.getView(), event.getRawSlot(), INGREDIENT_SLOT);
                    }

                    event.setCancelled(true);

                    scheduleUpdate(brewingStand.getInventory());
                    scheduleCheck(player, brewingStand);

                    return;
                }
            }
        }
        else if (event.getRawSlot() == INGREDIENT_SLOT) {
            if (isEmpty(cursor) && isEmpty(clicked)) {
                return;
            }
            else if (isEmpty(cursor)) {
                scheduleCheck(player, brewingStand);

                return;
            }
            else if (isEmpty(clicked)) {
                if (isValidIngredient(player, event.getCursor())) {
                    if (event.getClick() == ClickType.LEFT || (event.getClick() == ClickType.RIGHT && event.getCursor().getAmount() == 1)) {
                        event.setCancelled(true);

                        event.setCurrentItem(event.getCursor().clone());
                        event.setCursor(null);

                        scheduleUpdate(brewingStand.getInventory());
                        scheduleCheck(player, brewingStand);

                        return;
                    }
                    else if (event.getClick() == ClickType.RIGHT) {
                        event.setCancelled(true);

                        ItemStack one = event.getCursor().clone();
                        one.setAmount(1);

                        ItemStack rest = event.getCursor().clone();
                        rest.setAmount(event.getCursor().getAmount() - 1);

                        event.setCurrentItem(one);
                        event.setCursor(rest);

                        scheduleUpdate(brewingStand.getInventory());
                        scheduleCheck(player, brewingStand);

                        return;
                    }
                }
                return;
            }
        }
    }

    public static void handleInventoryDrag(InventoryDragEvent event) {
        Player player = event.getWhoClicked() instanceof Player ? (Player) event.getWhoClicked() : null;
        BrewingStand brewingStand = (BrewingStand) event.getInventory().getHolder();

        ItemStack cursor = event.getCursor();
        ItemStack ingredient = brewingStand.getInventory().getIngredient();

        if (!event.getInventorySlots().contains(INGREDIENT_SLOT)) {
            return;
        }

        if (isEmpty(ingredient) || ingredient.isSimilar(cursor)) {
            if (isValidIngredient(player, cursor)) {
                // Not handled: dragging custom ingredients over ingredient slot (does not trigger any event)
                scheduleCheck(player, brewingStand);

                return;
            }
            else {
                event.setCancelled(true);

                scheduleUpdate(brewingStand.getInventory());

                return;
            }
        }

    }

    public static void handleInventoryMoveItem(InventoryMoveItemEvent event) {
        Player player = null;
        BrewingStand brewingStand = (BrewingStand) event.getDestination().getHolder();

        if (isValidIngredient(player, event.getItem())) {
            scheduleCheck(player, brewingStand);

            return;
        }
    }

    private static void scheduleCheck(Player player, BrewingStand brewingStand) {
        new AlchemyBrewCheckTask(player, brewingStand).runTask(mcMMO.p);
    }

    private static void scheduleUpdate(Inventory inventory) {
        for (HumanEntity humanEntity : inventory.getViewers()) {
            if (humanEntity instanceof Player) {
                new PlayerUpdateInventoryTask((Player) humanEntity).runTask(mcMMO.p);
            }
        }
    }
}
