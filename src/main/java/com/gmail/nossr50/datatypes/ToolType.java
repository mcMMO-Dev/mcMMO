package com.gmail.nossr50.datatypes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.ItemChecks;

public enum ToolType {
    AXE(LocaleLoader.getString("Axes.Ability.Lower"), LocaleLoader.getString("Axes.Ability.Ready")),
    FISTS(LocaleLoader.getString("Unarmed.Ability.Lower"), LocaleLoader.getString("Unarmed.Ability.Ready")),
    HOE(LocaleLoader.getString("Herbalism.Ability.Lower"), LocaleLoader.getString("Herbalism.Ability.Ready")),
    PICKAXE(LocaleLoader.getString("Mining.Ability.Lower"), LocaleLoader.getString("Mining.Ability.Ready")),
    SHOVEL(LocaleLoader.getString("Excavation.Ability.Lower"), LocaleLoader.getString("Excavation.Ability.Ready")),
    SWORD(LocaleLoader.getString("Swords.Ability.Lower"), LocaleLoader.getString("Swords.Ability.Ready"));

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
            return ItemChecks.isPickaxe(is);

        case SHOVEL:
            return ItemChecks.isShovel(is);

        case SWORD:
            return ItemChecks.isSword(is);

        default:
            return false;
        }
    }
}
