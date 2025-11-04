package com.gmail.nossr50.skills.unarmed;

import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class Unarmed {
    public static double berserkDamageModifier = 1.5;

    public static void handleItemPickup(Player player, EntityPickupItemEvent event) {
        ItemStack[] storageContents = player.getInventory().getStorageContents();
        ItemStack itemDrop = event.getItem().getItemStack();
        int heldItemSlotID = player.getInventory().getHeldItemSlot();

        int amount = itemDrop.getAmount();
        boolean grabbedItem = false;

        for (int i = 0; i <= storageContents.length - 1; i++) {
            if (amount <= 0) {
                break;
            }

            if (i == heldItemSlotID) {
                continue;
            }

            //EMPTY SLOT!
            if (storageContents[i] == null) {
                player.getInventory().setItem(i, itemDrop);
                amount = 0;
                grabbedItem = true;
                break;
            } else if (itemDrop.isSimilar(storageContents[i])
                    && storageContents[i].getAmount() < storageContents[i].getMaxStackSize()) {
                //If we can fit this whole itemstack into this item
                if (amount + storageContents[i].getAmount()
                        <= storageContents[i].getMaxStackSize()) {
                    ItemStack modifiedAmount = storageContents[i];
                    modifiedAmount.setAmount(amount + storageContents[i].getAmount());

                    player.getInventory().setItem(i, modifiedAmount);
                    grabbedItem = true;
                    amount = 0;
                } else {
                    //Add what we can from this stack
                    ItemStack modifiedAmount = storageContents[i];
                    int amountThatCanFit =
                            storageContents[i].getMaxStackSize() - storageContents[i].getAmount();
                    modifiedAmount.setAmount(amountThatCanFit);

                    player.getInventory().setItem(i, modifiedAmount);

                    //Remove the amount we've added
                    grabbedItem = true;
                    amount -= amountThatCanFit;
                }
            }
        }

        if (amount <= 0) {
            event.getItem().remove(); //Cleanup Item
        } else {
            event.getItem().getItemStack().setAmount(amount);
        }

        event.setCancelled(true);

        if (grabbedItem) {
            SoundManager.sendSound(player, player.getLocation(), SoundType.POP);
            player.updateInventory();
        }
    }
}
