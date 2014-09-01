package com.gmail.nossr50.datatypes.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.ItemUtils;

public class ToolType {
	private static List<ToolType> tools = new ArrayList<ToolType>();
	
	public static final ToolType axe = new ToolType("Axes") {
		@Override
		public boolean inHand(ItemStack itemStack) {
			return ItemUtils.isAxe(itemStack);
		}
	};
	public static final ToolType fists = createToolType("Unarmed", Material.AIR);
	public static final ToolType hoe = new ToolType("Herbalism") {
		@Override
		public boolean inHand(ItemStack itemStack) {
			return ItemUtils.isHoe(itemStack);
		}
	};
	public static final ToolType pickaxe = new ToolType("Mining") {
		@Override
		public boolean inHand(ItemStack itemStack) {
			return ItemUtils.isPickaxe(itemStack);
		}
	};
	public static final ToolType shovel = new ToolType("Excavation") {
		@Override
		public boolean inHand(ItemStack itemStack) {
			return ItemUtils.isShovel(itemStack);
		}
	};
	public static final ToolType sword = new ToolType("Swords") {
		@Override
		public boolean inHand(ItemStack itemStack) {
			return ItemUtils.isSword(itemStack);
		}
	};
	
	static {
		tools.add(axe);
		tools.add(hoe);
		tools.add(pickaxe);
		tools.add(shovel);
		tools.add(sword);
	}

    private String lowerTool;
    private String raiseTool;

    private ToolType(String lowerTool, String raiseTool) {
        this.lowerTool = lowerTool;
        this.raiseTool = raiseTool;
    }
    
    private ToolType(String toolName) {
    	this(LocaleLoader.getString(toolName + ".Ability.Lower"), LocaleLoader.getString(toolName + ".Ability.Ready"));
    }

    public String getLowerTool() {
        return lowerTool;
    }

    public String getRaiseTool() {
        return raiseTool;
    }

    public static ToolType createToolType(String toolName, final Material... materials) {
    	ToolType tool = new ToolType(toolName) {
    		@Override
    		public boolean inHand(ItemStack itemStack) {
    			for(Material material : materials) {
    				if(itemStack.getType() == material) {
    					return true;
    				}
    			}
    			return false;
    		}
    	};
    	tools.add(tool);
    	return tool;
    }
    
    /**
     * Check to see if the item is of the appropriate type.
     *
     * @param itemStack The item to check
     * @return true if the item is the right type, false otherwise
     */
    public boolean inHand(ItemStack itemStack) {
        return false;
    }

	public static List<ToolType> getToolList() {
		return tools;
	}
}
