package com.gmail.nossr50.config.treasure;

import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.datatypes.treasure.FishingTreasure;
import com.gmail.nossr50.datatypes.treasure.HylianTreasure;
import com.gmail.nossr50.datatypes.treasure.ShakeTreasure;
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

    public static ShakeTreasure makeShakeTreasure(Material material, int dropAmount, int xpReward, double dropChance, int dropLevel, String customName, ConfigurationNode customLore)
    {
        ItemStack treasure = makeItemStack(material, dropAmount, customName, customLore);

        return new ShakeTreasure(treasure, xpReward, dropChance, dropLevel);
    }

    public static FishingTreasure makeFishingTreasure(Material material, int dropAmount, int xpReward, String customName, ConfigurationNode customLore)
    {
        ItemStack treasure = makeItemStack(material, dropAmount, customName, customLore);

        return new FishingTreasure(treasure, xpReward);
    }

    public static HylianTreasure makeHylianTreasure(Material material, int dropAmount, int xpReward, double dropChance, int dropLevel, String customName, ConfigurationNode customLore)
    {
        ItemStack treasure = makeItemStack(material, dropAmount, customName, customLore);

        return new HylianTreasure(treasure, xpReward, dropChance, dropLevel);
    }

    private static ItemStack makeItemStack(Material material, int dropAmount, String customName, ConfigurationNode customLore) {
        ItemStack treasure = new ItemStack(material, dropAmount);

        /* IF FOR SOME REASON ITS A POTION */

        /*if(isPotion(material))
            treasure = makePotionItemStack(material, dropAmount, customName, customLore);*/

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



        //TODO: Do this later

        return treasure;
    }

    private static boolean isPotion(Material material)
    {
        switch(material)
        {
            case POTION:
            case SPLASH_POTION:
            case LINGERING_POTION:
                return true;
            default:
                return false;
        }
    }

    /*private static ItemStack makePotionItemStack(ItemStack itemStack, Material material, int dropAmount, String customName, ConfigurationNode customLore)
    {
        //TODO: Rewrite this...
        Material mat = Material.matchMaterial(materialName);

        itemStack = new ItemStack(mat, amount, data);
        PotionMeta itemMeta = (PotionMeta) itemStack.getItemMeta();

        PotionType potionType = null;
        try {
            potionType = PotionType.valueOf(config.getString(type + "." + treasureName + ".PotionData.PotionType", "WATER"));
        } catch (IllegalArgumentException ex) {
            reason.add("Invalid Potion_Type: " + config.getString(type + "." + treasureName + ".PotionData.PotionType", "WATER"));
        }
        boolean extended = config.getBoolean(type + "." + treasureName + ".PotionData.Extended", false);
        boolean upgraded = config.getBoolean(type + "." + treasureName + ".PotionData.Upgraded", false);
        itemMeta.setBasePotionData(new PotionData(potionType, extended, upgraded));

        if (config.contains(type + "." + treasureName + ".Custom_Name")) {
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString(type + "." + treasureName + ".Custom_Name")));
        }

        if (config.contains(type + "." + treasureName + ".Lore")) {
            List<String> lore = new ArrayList<String>();
            for (String s : config.getStringList(type + "." + treasureName + ".Lore")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            itemMeta.setLore(lore);
        }
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }*/
}
