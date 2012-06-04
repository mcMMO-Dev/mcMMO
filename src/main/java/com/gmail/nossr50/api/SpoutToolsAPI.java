package com.gmail.nossr50.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.datatypes.ToolType;

public class SpoutToolsAPI {
    public static List<ItemStack> spoutSwords = new ArrayList<ItemStack>();
    public static List<ItemStack> spoutAxes = new ArrayList<ItemStack>();
    public static List<ItemStack> spoutPickaxes = new ArrayList<ItemStack>();
    public static List<ItemStack> spoutHoes = new ArrayList<ItemStack>();
    public static List<ItemStack> spoutShovels = new ArrayList<ItemStack>();

    /**
     * Add a custom Spout tool to mcMMO for XP gain & ability use.
     * </br>
     * This function is designed for API usage.
     *
     * @param spoutTool The tool to add
     * @param type The type of tool to add
     */
    public void addCustomTool(ItemStack spoutTool, ToolType type) {
        switch (type) {
        case AXE:
            spoutAxes.add(spoutTool);
            break;

        case HOE:
            spoutHoes.add(spoutTool);
            break;

        case PICKAXE:
            spoutPickaxes.add(spoutTool);
            break;

        case SHOVEL:
            spoutShovels.add(spoutTool);
            break;

        case SWORD:
            spoutSwords.add(spoutTool);
            break;

        default:
            break;
        }
    }
}
