package com.gmail.nossr50.skills.alchemy;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.skills.alchemy.PotionConfig;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.alchemy.AlchemyPotion;
import com.gmail.nossr50.datatypes.skills.alchemy.PotionStage;
import com.gmail.nossr50.events.fake.FakeBrewEvent;
import com.gmail.nossr50.runnables.player.PlayerUpdateInventoryTask;
import com.gmail.nossr50.runnables.skills.AlchemyBrewCheckTask;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;

public final class AlchemyPotionBrewer {
    public static boolean isValidBrew(Player player, ItemStack[] contents) {
        if (!isValidIngredient(player, contents[Alchemy.INGREDIENT_SLOT])) {
            return false;
        }

        for (int i = 0; i < 3; i++) {
            if (contents[i] == null || contents[i].getType() != Material.POTION && contents[i].getType() != Material.SPLASH_POTION && contents[i].getType() != Material.LINGERING_POTION) {
                continue;
            }

            if (getChildPotion(PotionConfig.getInstance().getPotion(contents[i]), contents[Alchemy.INGREDIENT_SLOT]) != null) {
                return true;
            }
        }

        return false;
    }

    private static AlchemyPotion getChildPotion(AlchemyPotion potion, ItemStack ingredient) {
        if (potion != null) {
            return potion.getChild(ingredient);
        }

        return null;
    }

    public static boolean isEmpty(ItemStack item) {
        return item == null || item.getType() == Material.AIR || item.getAmount() == 0;
    }

    private static void removeIngredient(BrewerInventory inventory, Player player) {
        ItemStack ingredient = inventory.getIngredient() == null ? null : inventory.getIngredient().clone();

        if (isEmpty(ingredient) || !isValidIngredient(player, ingredient)) {
            return;
        }
        else if (ingredient.getAmount() <= 1) {
            inventory.setIngredient(null);
            return;
        }
        else {
            ingredient.setAmount(ingredient.getAmount() - 1);
            inventory.setIngredient(ingredient);
            return;
        }
    }

    private static boolean hasIngredient(BrewerInventory inventory, Player player) {
        ItemStack ingredient = inventory.getIngredient() == null ? null : inventory.getIngredient().clone();

        return !isEmpty(ingredient) && isValidIngredient(player, ingredient);
    }

    public static boolean isValidIngredient(Player player, ItemStack item) {
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
        return PotionConfig.getInstance().getIngredients((player == null || !Permissions.secondaryAbilityEnabled(player, SecondaryAbility.CONCOCTIONS)) ? 1 : UserManager.getPlayer(player).getAlchemyManager().getTier());
    }

    public static void finishBrewing(BlockState brewingStand, Player player, boolean forced) {
        if (!(brewingStand instanceof BrewingStand)) {
            return;
        }

        BrewerInventory inventory = ((BrewingStand) brewingStand).getInventory();
        ItemStack ingredient = inventory.getIngredient() == null ? null : inventory.getIngredient().clone();

        if (!hasIngredient(inventory, player)) {
            return;
        }

        List<AlchemyPotion> inputList = new ArrayList<AlchemyPotion>();

        for (int i = 0; i < 3; i++) {
            ItemStack item = inventory.getItem(i);

            if (isEmpty(item) || item.getType() == Material.GLASS_BOTTLE || !PotionConfig.getInstance().isValidPotion(item)) {
                continue;
            }

            AlchemyPotion input = PotionConfig.getInstance().getPotion(item);
            AlchemyPotion output = input.getChild(ingredient);

            inputList.add(input);

            if (output != null) {
                inventory.setItem(i, output.toItemStack(item.getAmount()).clone());
            }
        }

        FakeBrewEvent event = new FakeBrewEvent(brewingStand.getBlock(), inventory, ((BrewingStand) brewingStand).getFuelLevel());
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled() || inputList.isEmpty()) {
            return;
        }

        removeIngredient(inventory, player);

        for (AlchemyPotion input : inputList) {
            AlchemyPotion output = input.getChild(ingredient);

            if (output != null && player != null) {
                PotionStage potionStage = PotionStage.getPotionStage(input, output);

                if (UserManager.hasPlayerDataKey(player)) {
                    UserManager.getPlayer(player).getAlchemyManager().handlePotionBrewSuccesses(potionStage, 1);
                }
            }
        }

        if (!forced) {
            scheduleUpdate(inventory);
        }
    }

    public static boolean transferItems(InventoryView view, int fromSlot, ClickType click) {
        boolean success = false;

        if (click.isLeftClick()) {
            success = transferItems(view, fromSlot);
        }
        else if (click.isRightClick()) {
            success = transferOneItem(view, fromSlot);
        }

        return success;
    }

    private static boolean transferOneItem(InventoryView view, int fromSlot) {
        ItemStack from = view.getItem(fromSlot).clone();
        ItemStack to = view.getItem(Alchemy.INGREDIENT_SLOT).clone();

        if (isEmpty(from)) {
            return false;
        }

        boolean emptyTo = isEmpty(to);
        int fromAmount = from.getAmount();

        if (!emptyTo && fromAmount >= from.getType().getMaxStackSize()) {
            return false;
        }
        else if (emptyTo || from.isSimilar(to)) {
            if (emptyTo) {
                to = from.clone();
                to.setAmount(1);
            }
            else {
                to.setAmount(to.getAmount() + 1);
            }

            from.setAmount(fromAmount - 1);
            view.setItem(Alchemy.INGREDIENT_SLOT, to);
            view.setItem(fromSlot, from);

            return true;
        }

        return false;
    }

    /**
     * Transfer items between two ItemStacks, returning the leftover status
     */
    private static boolean transferItems(InventoryView view, int fromSlot) {
        ItemStack from = view.getItem(fromSlot).clone();
        ItemStack to = view.getItem(Alchemy.INGREDIENT_SLOT).clone();

        if (isEmpty(from)) {
            return false;
        }
        else if (isEmpty(to)) {
            view.setItem(Alchemy.INGREDIENT_SLOT, from);
            view.setItem(fromSlot, null);

            return true;
        }
        else if (from.isSimilar(to)) {
            int fromAmount = from.getAmount();
            int toAmount = to.getAmount();
            int maxSize = to.getType().getMaxStackSize();

            if (fromAmount + toAmount > maxSize) {
                int left = fromAmount + toAmount - maxSize;

                to.setAmount(maxSize);
                view.setItem(Alchemy.INGREDIENT_SLOT, to);

                from.setAmount(left);
                view.setItem(fromSlot, from);

                return true;
            }

            to.setAmount(fromAmount + toAmount);
            view.setItem(fromSlot, null);
            view.setItem(Alchemy.INGREDIENT_SLOT, to);

            return true;
        }

        return false;
    }

    public static void scheduleCheck(Player player, BrewingStand brewingStand) {
        new AlchemyBrewCheckTask(player, brewingStand).runTask(mcMMO.p);
    }

    public static void scheduleUpdate(Inventory inventory) {
        for (HumanEntity humanEntity : inventory.getViewers()) {
            if (humanEntity instanceof Player) {
                new PlayerUpdateInventoryTask((Player) humanEntity).runTask(mcMMO.p);
            }
        }
    }
}
