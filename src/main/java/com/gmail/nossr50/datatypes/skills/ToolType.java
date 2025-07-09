package com.gmail.nossr50.datatypes.skills;

import com.gmail.nossr50.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ToolType {
    AXE("Axes.Ability.Lower", "Axes.Ability.Ready"),
    FISTS("Unarmed.Ability.Lower", "Unarmed.Ability.Ready"),
    HOE("Herbalism.Ability.Lower", "Herbalism.Ability.Ready"),
    PICKAXE("Mining.Ability.Lower", "Mining.Ability.Ready"),
    SHOVEL("Excavation.Ability.Lower", "Excavation.Ability.Ready"),
    SWORD("Swords.Ability.Lower", "Swords.Ability.Ready"),
    CROSSBOW("Crossbows.Ability.Lower", "Crossbows.Ability.Ready"),
    BOW("Archery.Ability.Lower", "Archery.Ability.Ready"),
    TRIDENTS("Tridents.Ability.Lower", "Tridents.Ability.Ready"),
    MACES("Maces.Ability.Lower", "Maces.Ability.Ready");

    private final String lowerTool;
    private final String raiseTool;

    ToolType(String lowerTool, String raiseTool) {
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
     * @param itemStack The item to check
     * @return true if the item is the right type, false otherwise
     */
    public boolean inHand(ItemStack itemStack) {
        switch (this) {
            case AXE:
                return ItemUtils.isAxe(itemStack);
            case CROSSBOW:
                return ItemUtils.isCrossbow(itemStack);
            case TRIDENTS:
                return ItemUtils.isTrident(itemStack);
            case MACES:
                return ItemUtils.isMace(itemStack);

            case FISTS:
                return itemStack.getType() == Material.AIR;

            case HOE:
                return ItemUtils.isHoe(itemStack);

            case PICKAXE:
                return ItemUtils.isPickaxe(itemStack);

            case SHOVEL:
                return ItemUtils.isShovel(itemStack);

            case SWORD:
                return ItemUtils.isSword(itemStack);

            default:
                return false;
        }
    }
}
