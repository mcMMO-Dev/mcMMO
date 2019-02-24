package com.gmail.nossr50.config.treasure;

import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.datatypes.treasure.HylianTreasure;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles creating treasures for various skill loot tables
 */
public class TreasureFactory {

    public static final String CHANGE_ME = "ChangeMe";

    /**
     * Make a new ExcavationTreasure
     * @param material
     * @param dropAmount
     * @param xpReward
     * @param dropChance
     * @param dropLevel
     * @param customName
     * @param customLore
     * @return
     */
    public static ExcavationTreasure makeExcavationTreasure(Material material, int dropAmount, int xpReward, double dropChance, int dropLevel, String customName, ConfigurationNode customLore)
    {
        ItemStack treasure = makeItemStack(material, dropAmount, customName, customLore);

        return new ExcavationTreasure(treasure, xpReward, dropChance, dropLevel);
    }

    public static HylianTreasure makeHylianTreasure(Material material, int dropAmount, int xpReward, double dropChance, int dropLevel, String customName, ConfigurationNode customLore)
    {
        ItemStack treasure = makeItemStack(material, dropAmount, customName, customLore);

        return new HylianTreasure(treasure, xpReward, dropChance, dropLevel);
    }

    private static ItemStack makeItemStack(Material material, int dropAmount, String customName, ConfigurationNode customLore) {
        ItemStack treasure = new ItemStack(material, dropAmount);

        /* ADD CUSTOM NAME */
        if(customName != null)
        {
            ItemMeta itemMeta = treasure.getItemMeta();
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', customName));
            treasure.setItemMeta(itemMeta);
        }

        /* ADD CUSTOM LORE */
        if(customLore != null && !customLore.getString().equalsIgnoreCase(CHANGE_ME))
        {
            ItemMeta itemMeta = treasure.getItemMeta();
            List<String> lore = new ArrayList<String>();

            try {
                //TODO: Not sure how this will be handled by Configurate
                for (String loreLine : customLore.getList(TypeToken.of(String.class))) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
                }

                itemMeta.setLore(lore);
                treasure.setItemMeta(itemMeta);
            } catch (ObjectMappingException e) {
                e.printStackTrace();
            }
        }

        /* IF FOR SOME REASON ITS A POTION */

        //TODO: Do this later

        return treasure;
    }
}
