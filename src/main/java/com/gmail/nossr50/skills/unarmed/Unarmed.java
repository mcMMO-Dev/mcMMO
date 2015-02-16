package com.gmail.nossr50.skills.unarmed;

import java.util.Iterator;

import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;

public class Unarmed {
    public static double ironArmMinBonusDamage = AdvancedConfig.getInstance().getIronArmMinBonus();
    public static double ironArmMaxBonusDamage = AdvancedConfig.getInstance().getIronArmMaxBonus();
    public static int    ironArmIncreaseLevel  = AdvancedConfig.getInstance().getIronArmIncreaseLevel();

    public static boolean blockCrackerSmoothBrick = Config.getInstance().getUnarmedBlockCrackerSmoothbrickToCracked();

    public static double berserkDamageModifier = 1.5;

    public static boolean handleItemPickup(PlayerInventory inventory, Item drop) {
        ItemStack dropStack = drop.getItemStack();
        int firstEmpty = inventory.firstEmpty();
        int dropAmount = dropStack.getAmount();

        if (inventory.containsAtLeast(dropStack, 1)) {
            int nextSlot = 0;

            for (ItemStack itemstack : inventory) {
                if (dropStack.isSimilar(itemstack)) {
                    int itemAmount = itemstack.getAmount();
                    int itemMax = itemstack.getMaxStackSize();

                    ItemStack addStack = itemstack.clone();

                    if (dropAmount + itemAmount <= itemMax) {
                        drop.remove();
                        addStack.setAmount(dropAmount + itemAmount);
                        inventory.setItem(nextSlot, addStack);
                        return true;
                    }

                    addStack.setAmount(itemMax);
                    dropAmount = dropAmount + itemAmount - itemMax;
                    inventory.setItem(nextSlot, addStack);
                }

                if (dropAmount == 0) {
                    drop.remove();
                    return true;
                }

                nextSlot++;
            }
        }

        if (firstEmpty == inventory.getHeldItemSlot()) {
            int nextSlot = firstEmpty + 1;

            for (Iterator<ItemStack> iterator = inventory.iterator(nextSlot); iterator.hasNext(); ) {
                ItemStack itemstack = iterator.next();

                if (itemstack == null) {
                    drop.remove();
                    dropStack.setAmount(dropAmount);
                    inventory.setItem(nextSlot, dropStack);
                    return true;
                }

                nextSlot++;
            }

            // Inventory is full - cancel the item pickup
            if (dropStack.getAmount() == dropAmount) {
                return false;
            } else {
                drop.remove();
                dropStack.setAmount(dropAmount);
                ((Item) drop.getWorld().dropItem(drop.getLocation(), dropStack)).setPickupDelay(0);
                return true;
            }
        }
        else if (firstEmpty != -1) {
            drop.remove();
            dropStack.setAmount(dropAmount);
            inventory.setItem(firstEmpty, dropStack);
            return true;
        }

        drop.remove();
        return true;
    }
}
