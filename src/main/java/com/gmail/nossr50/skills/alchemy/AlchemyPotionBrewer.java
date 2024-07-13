package com.gmail.nossr50.skills.alchemy;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.alchemy.AlchemyPotion;
import com.gmail.nossr50.datatypes.skills.alchemy.PotionStage;
import com.gmail.nossr50.events.fake.FakeBrewEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.player.PlayerUpdateInventoryTask;
import com.gmail.nossr50.runnables.skills.AlchemyBrewCheckTask;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO: Update to use McMMOPlayer
public final class AlchemyPotionBrewer {
    /*
     * Compatibility with older versions where InventoryView used to be an abstract class and became an interface.
     * This was introduced in Minecraft 1.21 if we drop support for versions older than 1.21 this can be removed.
     */
    private static final Method getItem, setItem;
    static {
        try {
            final Class<?> clazz = Class.forName("org.bukkit.inventory.InventoryView");
            getItem = clazz.getDeclaredMethod("getItem", int.class);
            setItem = clazz.getDeclaredMethod("setItem", int.class, ItemStack.class);
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated(forRemoval = true, since = "2.2.010")
    public static boolean isValidBrew(Player player, ItemStack[] contents) {
        if (!isValidIngredientByPlayer(player, contents[Alchemy.INGREDIENT_SLOT])) {
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

    public static boolean isValidBrew(int ingredientLevel, ItemStack[] contents) {
        if (!isValidIngredientByLevel(ingredientLevel, contents[Alchemy.INGREDIENT_SLOT])) {
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
        if (inventory.getIngredient() == null)
            return;

        ItemStack ingredient = inventory.getIngredient().clone();

        if (!isEmpty(ingredient) && isValidIngredientByPlayer(player, ingredient)) {
            if (ingredient.getAmount() <= 1) {
                inventory.setIngredient(null);
            } else {
                ingredient.setAmount(ingredient.getAmount() - 1);
                inventory.setIngredient(ingredient);
            }
        }
    }

    private static boolean hasIngredient(BrewerInventory inventory, Player player) {
        ItemStack ingredient = inventory.getIngredient() == null ? null : inventory.getIngredient().clone();

        return !isEmpty(ingredient) && isValidIngredientByPlayer(player, ingredient);
    }

    public static boolean isValidIngredientByPlayer(Player player, ItemStack item) {
        if (isEmpty(item)) {
            return false;
        }

        for (ItemStack ingredient : getValidIngredients(UserManager.getPlayer(player))) {
            if (item.isSimilar(ingredient)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isValidIngredientByLevel(int ingredientLevel, ItemStack item) {
        if (isEmpty(item)) {
            return false;
        }

        // TODO: Update this when we fix loading from hoppers
        for (ItemStack ingredient : mcMMO.p.getPotionConfig().getIngredients(ingredientLevel)) {
            if (item.isSimilar(ingredient)) {
                return true;
            }
        }

        return false;
    }

    private static List<ItemStack> getValidIngredients(@Nullable McMMOPlayer mmoPlayer) {
        if (mmoPlayer == null) {
            return mcMMO.p.getPotionConfig().getIngredients(1);
        }

        return mcMMO.p.getPotionConfig().getIngredients(!Permissions.isSubSkillEnabled(mmoPlayer, SubSkillType.ALCHEMY_CONCOCTIONS)
                ? 1 : mmoPlayer.getAlchemyManager().getTier());
    }

    public static void finishBrewing(BlockState brewingStand, @Nullable McMMOPlayer mmoPlayer, boolean forced) {
        // Check if the brewing stand block state is an actual brewing stand
        if (!(brewingStand instanceof BrewingStand)) {
            return;
        }

        // Retrieve the inventory of the brewing stand and clone the current ingredient for safe manipulation
        final BrewerInventory inventory = ((BrewingStand) brewingStand).getInventory();
        final ItemStack ingredient = inventory.getIngredient() == null ? null : inventory.getIngredient().clone();
        Player player = mmoPlayer != null ? mmoPlayer.getPlayer() : null;

        if (ingredient == null) {
            return;
        }

        // Check if the brewing stand has a valid ingredient; if not, exit the method
        if (player == null
                || !hasIngredient(inventory, player)) {
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
            if (outputList.get(i) != null) {
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

        try {
            if (click.isLeftClick()) {
                success = transferItems(view, fromSlot);
            } else if (click.isRightClick()) {
                success = transferOneItem(view, fromSlot);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return success;
    }

    private static boolean transferOneItem(InventoryView view, int fromSlot)
            throws InvocationTargetException, IllegalAccessException {
        final ItemStack from = ((ItemStack) getItem.invoke(view, fromSlot)).clone();
        ItemStack to = ((ItemStack) getItem.invoke(view, Alchemy.INGREDIENT_SLOT)).clone();

        if (isEmpty(from)) {
            return false;
        }

        boolean emptyTo = isEmpty(to);
        int fromAmount = from.getAmount();

        if (!emptyTo && fromAmount >= from.getType().getMaxStackSize()) {
            return false;
        } else if (emptyTo || from.isSimilar(to)) {
            if (emptyTo) {
                to = from.clone();
                to.setAmount(1);
            } else {
                to.setAmount(to.getAmount() + 1);
            }

            from.setAmount(fromAmount - 1);
            setItem.invoke(view, Alchemy.INGREDIENT_SLOT, to);
            setItem.invoke(view, fromSlot, from);

            return true;
        }

        return false;
    }

    /**
     * Transfer items between two ItemStacks, returning the leftover status
     */
    private static boolean transferItems(InventoryView view, int fromSlot)
            throws InvocationTargetException, IllegalAccessException {
        final ItemStack from = ((ItemStack) getItem.invoke(view, fromSlot)).clone();
        final ItemStack to = ((ItemStack) getItem.invoke(view, Alchemy.INGREDIENT_SLOT)).clone();
        if (isEmpty(from)) {
            return false;
        } else if (isEmpty(to)) {
            setItem.invoke(view, Alchemy.INGREDIENT_SLOT, from);
            setItem.invoke(view, fromSlot, null);
            return true;
        } else if (from.isSimilar(to)) {
            int fromAmount = from.getAmount();
            int toAmount = to.getAmount();
            int maxSize = to.getType().getMaxStackSize();

            if (fromAmount + toAmount > maxSize) {
                int left = fromAmount + toAmount - maxSize;

                to.setAmount(maxSize);
                setItem.invoke(view, Alchemy.INGREDIENT_SLOT, to);

                from.setAmount(left);
                setItem.invoke(view, fromSlot, from);

                return true;
            }

            to.setAmount(fromAmount + toAmount);
            setItem.invoke(view, fromSlot, null);
            setItem.invoke(view, Alchemy.INGREDIENT_SLOT, to);

            return true;
        }

        return false;
    }

    public static void scheduleCheck(@NotNull BrewingStand brewingStand) {
        mcMMO.p.getFoliaLib().getImpl().runAtLocation(
                brewingStand.getLocation(), new AlchemyBrewCheckTask(brewingStand));
    }

    public static void scheduleUpdate(Inventory inventory) {
        for (HumanEntity humanEntity : inventory.getViewers()) {
            if (humanEntity instanceof Player) {
                mcMMO.p.getFoliaLib().getImpl().runAtEntity(humanEntity, new PlayerUpdateInventoryTask((Player) humanEntity));
            }
        }
    }
}
