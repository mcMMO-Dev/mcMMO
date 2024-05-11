package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.skills.alchemy.Alchemy;
import com.gmail.nossr50.skills.alchemy.AlchemyPotionBrewer;
import com.gmail.nossr50.util.CancellableRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class AlchemyBrewCheckTask extends CancellableRunnable {
    private final Player player;
    private final BrewingStand brewingStand;
    private final ItemStack[] oldInventory;

    public AlchemyBrewCheckTask(Player player, BrewingStand brewingStand) {
        this.player = player;
        this.brewingStand = brewingStand;
        this.oldInventory = Arrays.copyOfRange(brewingStand.getInventory().getContents(), 0, 4);
    }

    @Override
    public void run() {
        Location location = brewingStand.getLocation();
        ItemStack[] newInventory = Arrays.copyOfRange(brewingStand.getInventory().getContents(), 0, 4);
        boolean validBrew = brewingStand.getFuelLevel() > 0 && AlchemyPotionBrewer.isValidBrew(player, newInventory);

        if (Alchemy.brewingStandMap.containsKey(location)) {
            if (oldInventory[Alchemy.INGREDIENT_SLOT] == null
                    || newInventory[Alchemy.INGREDIENT_SLOT] == null
                    || !oldInventory[Alchemy.INGREDIENT_SLOT].isSimilar(newInventory[Alchemy.INGREDIENT_SLOT])
                    || !validBrew) {
                Alchemy.brewingStandMap.get(location).cancelBrew();
            }
        } else if (validBrew) {
            Alchemy.brewingStandMap.put(location, new AlchemyBrewTask(brewingStand, player));
        }
    }
}
