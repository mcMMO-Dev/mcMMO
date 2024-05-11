package com.gmail.nossr50.skills.alchemy;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.alchemy.AlchemyPotion;
import com.gmail.nossr50.datatypes.skills.alchemy.PotionStage;
import com.gmail.nossr50.events.fake.FakeBrewEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.player.PlayerUpdateInventoryTask;
import com.gmail.nossr50.runnables.skills.AlchemyBrewCheckTask;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.Bukkit;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AlchemyPotionBrewer {
    public static boolean isValidBrew(Player player, ItemStack[] contents) {
        if (!isValidIngredient(player, contents[Alchemy.INGREDIENT_SLOT])) {
            return false;
        }

        for (int i = 0; i < 3; i++) {
            if (contents[i] == null || contents[i].getType() != Material.POTION
                    && contents[i].getType() != Material.SPLASH_POTION
                    && contents[i].getType() != Material.LINGERING_POTION) {
                continue;
            }

            final AlchemyPotion potion = mcMMO.p.getPotionConfig().getPotion(contents[i]);
            if (getChildPotion(potion, contents[Alchemy.INGREDIENT_SLOT]) != null) {
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
        if(inventory.getIngredient() == null)
            return;

        ItemStack ingredient = inventory.getIngredient().clone();

        if (!isEmpty(ingredient) && isValidIngredient(player, ingredient)) {
            if (ingredient.getAmount() <= 1) {
                inventory.setIngredient(null);
            }
            else {
                ingredient.setAmount(ingredient.getAmount() - 1);
                inventory.setIngredient(ingredient);
            }
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
        if(player == null || UserManager.getPlayer(player) == null) {
            return mcMMO.p.getPotionConfig().getIngredients(1);
        }

        return mcMMO.p.getPotionConfig().getIngredients(!Permissions.isSubSkillEnabled(player, SubSkillType.ALCHEMY_CONCOCTIONS) ? 1 : UserManager.getPlayer(player).getAlchemyManager().getTier());
    }

    public static void finishBrewing(BlockState brewingStand, Player player, boolean forced) {
        // Check if the brewing stand block state is an actual brewing stand
        if (!(brewingStand instanceof BrewingStand)) {
            return;
        }

        // Retrieve the inventory of the brewing stand and clone the current ingredient for safe manipulation
        final BrewerInventory inventory = ((BrewingStand) brewingStand).getInventory();
        final ItemStack ingredient = inventory.getIngredient() == null ? null : inventory.getIngredient().clone();

        // Check if the brewing stand has a valid ingredient; if not, exit the method
        if (!hasIngredient(inventory, player)) {
            // debug
            return;
        }

        // Initialize lists to hold the potions before and after brewing, initially setting them to null
        List<AlchemyPotion> inputList = new ArrayList<>(Collections.nCopies(3, null));
        List<ItemStack> outputList = new ArrayList<>(Collections.nCopies(3, null));

        // Process each of the three slots in the brewing stand
        for (int i = 0; i < 3; i++) {
            ItemStack item = inventory.getItem(i);

            // Skip the slot if it's empty, contains a glass bottle, or holds an invalid potion
            if (isEmpty(item)
                    || item.getType() == Material.GLASS_BOTTLE
                    || !mcMMO.p.getPotionConfig().isValidPotion(item)) {
                // debug
                continue;
            }

            // Retrieve the potion configurations for the input and resulting output potion
            AlchemyPotion input = mcMMO.p.getPotionConfig().getPotion(item);
            AlchemyPotion output = input.getChild(ingredient);

            // Update the input list with the current potion
            inputList.set(i, input);

            // If there is a valid output potion, add it to the output list
            if (output != null) {
                outputList.set(i, output.toItemStack(item.getAmount()).clone());
            }
        }

        // Create a fake brewing event and pass it to the plugin's event system
        FakeBrewEvent event = new FakeBrewEvent(brewingStand.getBlock(), inventory, outputList, ((BrewingStand) brewingStand).getFuelLevel());
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        // If the event is cancelled or there are no potions processed, exit the method
        if (event.isCancelled() || inputList.isEmpty()) {
            // debug
            return;
        }

        // Update the brewing inventory with the new potions
        for (int i = 0; i < 3; i++) {
            if(outputList.get(i) != null) {
                inventory.setItem(i, outputList.get(i));
            }
        }

        // Remove the used ingredient from the brewing inventory
        removeIngredient(inventory, player);

        // Handle potion brewing success and related effects for each potion processed
        for (AlchemyPotion input : inputList) {
            if (input == null) continue;

            AlchemyPotion output = input.getChild(ingredient);

            if (output != null && player != null) {
                PotionStage potionStage = PotionStage.getPotionStage(input, output);

                // Update player alchemy skills or effects based on brewing success
                if (UserManager.getPlayer(player) != null) {
                    UserManager.getPlayer(player).getAlchemyManager().handlePotionBrewSuccesses(potionStage, 1);
                }
            }
        }

        // If the brewing was not forced by external conditions, schedule a new update
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
        mcMMO.p.getFoliaLib().getImpl().runAtEntity(player, new AlchemyBrewCheckTask(player, brewingStand));
    }

    public static void scheduleUpdate(Inventory inventory) {
        for (HumanEntity humanEntity : inventory.getViewers()) {
            if (humanEntity instanceof Player) {
                mcMMO.p.getFoliaLib().getImpl().runAtEntity(humanEntity, new PlayerUpdateInventoryTask((Player) humanEntity));
            }
        }
    }
}
