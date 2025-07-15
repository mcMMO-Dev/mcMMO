package com.gmail.nossr50.skills.fishing;

import com.gmail.nossr50.config.treasure.FishingTreasureConfig;
import com.gmail.nossr50.datatypes.treasure.ShakeTreasure;
import com.gmail.nossr50.util.Misc;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public final class Fishing {

    static final HashMap<Material, List<Enchantment>> ENCHANTABLE_CACHE = new HashMap<>();

    private Fishing() {
    }

    /**
     * Finds the possible drops of an entity
     *
     * @param target Targeted entity
     * @return possibleDrops List of ItemStack that can be dropped
     */
    static List<ShakeTreasure> findPossibleDrops(LivingEntity target) {
        if (FishingTreasureConfig.getInstance().shakeMap.containsKey(target.getType())) {
            return FishingTreasureConfig.getInstance().shakeMap.get(target.getType());
        }

        return null;
    }

    /**
     * Randomly chooses a drop among the list
     *
     * @param possibleDrops List of ItemStack that can be dropped
     * @return Chosen ItemStack
     */
    static ItemStack chooseDrop(List<ShakeTreasure> possibleDrops) {
        int dropProbability = Misc.getRandom().nextInt(100);
        double cumulatedProbability = 0;

        for (ShakeTreasure treasure : possibleDrops) {
            cumulatedProbability += treasure.getDropChance();

            if (dropProbability < cumulatedProbability) {
                return treasure.getDrop().clone();
            }
        }

        return null;
    }
}
