package com.gmail.nossr50.datatypes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.ItemChecks;
import com.gmail.nossr50.locale.mcLocale;

public enum ToolType {
    AXE(mcLocale.getString("Skills.LowerAxe"), mcLocale.getString("Skills.ReadyAxe")),
    FISTS(mcLocale.getString("Skills.LowerFists"), mcLocale.getString("Skills.ReadyFists")),
    HOE(mcLocale.getString("Skills.LowerHoe"), mcLocale.getString("Skills.ReadyHoe")),
    PICKAXE(mcLocale.getString("Skills.LowerPickAxe"), mcLocale.getString("Skills.ReadyPickAxe")),
    SHOVEL(mcLocale.getString("Skills.LowerShovel"), mcLocale.getString("Skills.ReadyShovel")),
    SWORD(mcLocale.getString("Skills.LowerSword"), mcLocale.getString("Skills.ReadySword"));

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
