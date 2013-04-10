package com.gmail.nossr50.skills.unarmed;

import java.util.Iterator;

import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;

public class Unarmed {
    public static int ironArmMaxBonusDamage = AdvancedConfig.getInstance().getIronArmMaxBonus();
    public static int ironArmIncreaseLevel  = AdvancedConfig.getInstance().getIronArmIncreaseLevel();

    public static int    disarmMaxBonusLevel = AdvancedConfig.getInstance().getDisarmMaxBonusLevel();
    public static double disarmMaxChance     = AdvancedConfig.getInstance().getDisarmChanceMax();

    public static int    deflectMaxBonusLevel = AdvancedConfig.getInstance().getDeflectMaxBonusLevel();
    public static double deflectMaxChance     = AdvancedConfig.getInstance().getDeflectChanceMax();

    public static int    ironGripMaxBonusLevel = AdvancedConfig.getInstance().getIronGripMaxBonusLevel();
    public static double ironGripMaxChance     = AdvancedConfig.getInstance().getIronGripChanceMax();

    public static boolean blockCrackerSmoothBrick = Config.getInstance().getUnarmedBlockCrackerSmoothbrickToCracked();

    public static double berserkDamageModifier = 1.5;

    public static boolean handleItemPickup(PlayerInventory inventory, Item drop) {
        ItemStack dropStack = drop.getItemStack();
        int firstEmpty = inventory.firstEmpty();

        if (inventory.containsAtLeast(dropStack, 1)) {
            int dropAmount = dropStack.getAmount();
            int nextSlot = 0;

            for (Iterator<ItemStack> iterator = inventory.iterator(); iterator.hasNext();) {
                ItemStack itemstack = iterator.next();

                if (dropStack.isSimilar(itemstack)) {
                    int itemAmount = itemstack.getAmount();
                    int itemMax = itemstack.getMaxStackSize();

                    ItemStack addStack = itemstack.clone();

                    if (dropAmount +  itemAmount <= itemMax) {
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

            for (Iterator<ItemStack> iterator = inventory.iterator(nextSlot); iterator.hasNext();) {
                ItemStack itemstack = iterator.next();

                if (itemstack == null) {
                    drop.remove();
                    inventory.setItem(nextSlot, dropStack);
                    return true;
                }

                nextSlot++;
            }
        }

        return false;
    }
}
