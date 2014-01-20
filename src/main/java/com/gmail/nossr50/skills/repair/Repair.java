package com.gmail.nossr50.skills.repair;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.ItemUtils;

public class Repair {
    public static int    repairMasteryMaxBonusLevel = AdvancedConfig.getInstance().getRepairMasteryMaxLevel();
    public static double repairMasteryMaxBonus      = AdvancedConfig.getInstance().getRepairMasteryMaxBonus();

    public static int salvageUnlockLevel = AdvancedConfig.getInstance().getSalvageUnlockLevel();

    public static Material salvageAnvilMaterial = Config.getInstance().getSalvageAnvilMaterial();
    public static Material repairAnvilMaterial  = Config.getInstance().getRepairAnvilMaterial();
    public static boolean  anvilMessagesEnabled = Config.getInstance().getRepairAnvilMessagesEnabled();

    /**
     * Checks if the item is salvageable.
     *
     * @param item Item to check
     *
     * @return true if the item is salvageable, false otherwise
     */
    public static boolean isSalvageable(ItemStack item) {
        return (Config.getInstance().getSalvageTools() && ItemUtils.isMinecraftTool(item)) || (Config.getInstance().getSalvageArmor() && !ItemUtils.isChainmailArmor(item) && ItemUtils.isMinecraftArmor(item));
    }

    public static String getAnvilMessage(Material type) {
        if (type == repairAnvilMaterial) {
            return LocaleLoader.getString("Repair.Listener.Anvil");
        }

        if (type == salvageAnvilMaterial) {
            return LocaleLoader.getString("Repair.Listener.Anvil2");
        }

        return "";
    }

    protected static Material getRepairAndSalvageItem(ItemStack inHand) {
        if (ItemUtils.isDiamondTool(inHand) || ItemUtils.isDiamondArmor(inHand)) {
            return Material.DIAMOND;
        }
        else if (ItemUtils.isGoldTool(inHand) || ItemUtils.isGoldArmor(inHand)) {
            return Material.GOLD_INGOT;
        }
        else if (ItemUtils.isIronTool(inHand) || ItemUtils.isIronArmor(inHand)) {
            return Material.IRON_INGOT;
        }
        else if (ItemUtils.isStoneTool(inHand)) {
            return Material.COBBLESTONE;
        }
        else if (ItemUtils.isWoodTool(inHand)) {
            return Material.WOOD;
        }
        else if (ItemUtils.isLeatherArmor(inHand)) {
            return Material.LEATHER;
        }
        else if (ItemUtils.isStringTool(inHand)) {
            return Material.STRING;
        }
        else {
            return null;
        }
    }

    public static int getRepairAndSalvageQuantities(ItemStack item) {
        return getRepairAndSalvageQuantities(item, getRepairAndSalvageItem(item), (byte) -1);
    }

    public static int getRepairAndSalvageQuantities(ItemStack item, Material repairMaterial, byte repairMetadata) {
        int quantity = 0;
        MaterialData repairData = repairMaterial != null ? new MaterialData(repairMaterial, repairMetadata) : null;
        List<Recipe> recipes = mcMMO.p.getServer().getRecipesFor(item);

        if (!recipes.isEmpty()) {
            Recipe recipe = recipes.get(0);

            if (recipe instanceof ShapelessRecipe) {
                for (ItemStack ingredient : ((ShapelessRecipe) recipe).getIngredientList()) {
                    if (ingredient != null && (repairMaterial == null || ingredient.getType() == repairMaterial) && (repairMetadata == -1 || ingredient.getData().equals(repairData))) {
                        quantity += ingredient.getAmount();
                    }
                }
            }
            else if (recipe instanceof ShapedRecipe) {
                for (ItemStack ingredient : ((ShapedRecipe) recipe).getIngredientMap().values()) {
                    if (ingredient != null && (repairMaterial == null || ingredient.getType() == repairMaterial) && (repairMetadata == -1 || ingredient.getData().equals(repairData))) {
                        quantity += ingredient.getAmount();
                    }
                }
            }
        }

        return quantity;
    }
}
