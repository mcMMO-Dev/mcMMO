package com.gmail.nossr50.runnables.skills;

import java.util.Arrays;

import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.skills.alchemy.Alchemy;
import com.gmail.nossr50.skills.alchemy.AlchemyPotionBrewer;

public class AlchemyBrewCheckTask extends BukkitRunnable {
    private final static int INGREDIENT_SLOT = 3;

    private Player player;
    private BrewingStand brewingStand;
    private ItemStack[] oldInventory;

    public AlchemyBrewCheckTask(Player player, BrewingStand brewingStand) {
        this.player = player;
        this.brewingStand = brewingStand;
        this.oldInventory = Arrays.copyOfRange(brewingStand.getInventory().getContents(), 0, 4);
    }

    @Override
    public void run() {
        Block block = brewingStand.getBlock();
        ItemStack[] newInventory = Arrays.copyOfRange(((BrewingStand) block.getState()).getInventory().getContents(), 0, 4);

        if (Alchemy.brewingStandMap.containsKey(brewingStand)) {
            if (oldInventory[INGREDIENT_SLOT] == null || newInventory[INGREDIENT_SLOT] == null || !oldInventory[INGREDIENT_SLOT].isSimilar(newInventory[INGREDIENT_SLOT]) || !AlchemyPotionBrewer.isValidBrew(player, newInventory)) {
                Alchemy.brewingStandMap.get(brewingStand).cancelBrew();
            }
        }

        if (!Alchemy.brewingStandMap.containsKey(brewingStand) && AlchemyPotionBrewer.isValidBrew(player, newInventory)) {
            Alchemy.brewingStandMap.put(brewingStand, new AlchemyBrewTask(brewingStand, player));
        }
    }

}
