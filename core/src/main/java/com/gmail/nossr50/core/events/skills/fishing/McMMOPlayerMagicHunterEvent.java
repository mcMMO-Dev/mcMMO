package com.gmail.nossr50.core.events.skills.fishing;

import com.gmail.nossr50.core.mcmmo.entity.Player;
import com.gmail.nossr50.core.mcmmo.item.ItemStack;


public class McMMOPlayerMagicHunterEvent extends McMMOPlayerFishingTreasureEvent {
    private Map<Enchantment, Integer> enchants;

    public McMMOPlayerMagicHunterEvent(Player player, ItemStack treasure, int xp, Map<Enchantment, Integer> enchants) {
        super(player, treasure, xp);
        this.enchants = enchants;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return enchants;
    }
}
