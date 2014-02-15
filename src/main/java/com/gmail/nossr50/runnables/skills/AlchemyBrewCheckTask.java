package com.gmail.nossr50.runnables.skills;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.skills.alchemy.Alchemy;
import com.gmail.nossr50.skills.alchemy.AlchemyPotionBrewer;

public class AlchemyBrewCheckTask extends BukkitRunnable {
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
        Location location = brewingStand.getLocation();
        ItemStack[] newInventory = Arrays.copyOfRange(brewingStand.getInventory().getContents(), 0, 4);
        boolean validBrew = AlchemyPotionBrewer.isValidBrew(player, newInventory);

        if (Alchemy.brewingStandMap.containsKey(location)) {
            if (oldInventory[Alchemy.INGREDIENT_SLOT] == null || newInventory[Alchemy.INGREDIENT_SLOT] == null || !oldInventory[Alchemy.INGREDIENT_SLOT].isSimilar(newInventory[Alchemy.INGREDIENT_SLOT]) || !validBrew) {
                Alchemy.brewingStandMap.get(location).cancelBrew();
            }
        }
        else if (validBrew) {
            Alchemy.brewingStandMap.put(location, new AlchemyBrewTask(brewingStand, player));
        }
    }
}
