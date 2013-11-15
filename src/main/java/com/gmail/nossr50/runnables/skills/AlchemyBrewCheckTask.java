package com.gmail.nossr50.runnables.skills;

import java.util.Arrays;

import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.skills.alchemy.Alchemy;
import com.gmail.nossr50.skills.alchemy.AlchemyPotionBrewer;

public class AlchemyBrewCheckTask implements Runnable {
    private final static int INGREDIENT_SLOT = 3;

    private Player player;
    private Block brewingStand;
    private ItemStack[] oldInventory;
    private ItemStack[] newInventory;
    
    public AlchemyBrewCheckTask(Player player, BrewingStand brewingStand) {
        this.player = player;
        this.brewingStand = brewingStand.getBlock();
        this.oldInventory = Arrays.copyOfRange(brewingStand.getInventory().getContents(), 0, 4);
    }
    
    @Override
    public void run() {
        this.newInventory = Arrays.copyOfRange(((BrewingStand) brewingStand.getState()).getInventory().getContents(), 0, 4);
         
        if (Alchemy.brewingStandMap.containsKey(brewingStand)) {
            if (oldInventory[INGREDIENT_SLOT] == null || newInventory[INGREDIENT_SLOT] == null || !oldInventory[INGREDIENT_SLOT].isSimilar(newInventory[INGREDIENT_SLOT]) || !AlchemyPotionBrewer.isValidBrew(player, newInventory)) {
                Alchemy.brewingStandMap.get(brewingStand).cancel();
            }
        }
        if (!Alchemy.brewingStandMap.containsKey(brewingStand) && AlchemyPotionBrewer.isValidBrew(player, newInventory)) {
            Alchemy.brewingStandMap.put(brewingStand, new AlchemyBrewTask(brewingStand, player));
        }
    }

}
