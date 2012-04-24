package com.gmail.nossr50.datatypes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.ItemChecks;
import com.gmail.nossr50.locale.mcLocale;

public enum ToolType {
    AXE(mcLocale.getString("Axes.Ability.Lower"), mcLocale.getString("Axes.Ability.Ready")),
    FISTS(mcLocale.getString("Unarmed.Ability.Lower"), mcLocale.getString("Unarmed.Ability.Ready")),
    HOE(mcLocale.getString("Herbalism.Ability.Lower"), mcLocale.getString("Herbalism.Ability.Ready")),
    PICKAXE(mcLocale.getString("Mining.Ability.Lower"), mcLocale.getString("Mining.Ability.Ready")),
    SHOVEL(mcLocale.getString("Excavation.Ability.Lower"), mcLocale.getString("Excavation.Ability.Ready")),
    SWORD(mcLocale.getString("Swords.Ability.Lower"), mcLocale.getString("Swords.Ability.Ready"));

    private String lowerTool;
    private String raiseTool;

    private ToolType(String lowerTool, String raiseTool) {
        this.lowerTool = lowerTool;
        this.raiseTool = raiseTool;
    }

    public String getLowerTool() {
        return lowerTool;
    }

    public String getRaiseTool() {
        return raiseTool;
    }

    /**
     * Check to see if the item is of the appropriate type.
     *
     * @param is The item to check
     * @return true if the item is the right type, false otherwise
     */
    public boolean inHand(ItemStack is) {
        switch (this) {
        case AXE:
            return ItemChecks.isAxe(is);

        case FISTS:
            return is.getType().equals(Material.AIR);

        case HOE:
            return ItemChecks.isHoe(is);

        case PICKAXE:
            return ItemChecks.isMiningPick(is);

        case SHOVEL:
            return ItemChecks.isShovel(is);

        case SWORD:
            return ItemChecks.isSword(is);

        default:
            return false;
        }
    }
}
