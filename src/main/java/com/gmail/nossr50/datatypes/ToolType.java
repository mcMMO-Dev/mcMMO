package com.gmail.nossr50.datatypes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.ItemChecks;
import com.gmail.nossr50.locale.mcLocale;

public enum ToolType
{
	AXE(mcLocale.getString("Skills.LowerAxe"), mcLocale.getString("Skills.ReadyAxe")),
	FISTS(mcLocale.getString("Skills.LowerFists"), mcLocale.getString("Skills.ReadyFists")),
	HOE(mcLocale.getString("Skills.LowerHoe"), mcLocale.getString("Skills.ReadyHoe")),
	PICKAXE(mcLocale.getString("Skills.LowerPickAxe"), mcLocale.getString("Skills.ReadyPickAxe")),
	SHOVEL(mcLocale.getString("Skills.LowerShovel"), mcLocale.getString("Skills.ReadyShovel")),
	SWORD(mcLocale.getString("Skills.LowerSword"), mcLocale.getString("Skills.ReadySword"));
	
	private String lowerTool;
	private String raiseTool;
	
	private ToolType(String lowerTool, String raiseTool)
	{
		this.lowerTool = lowerTool;
		this.raiseTool = raiseTool;
	}
	
	public String getLowerTool()
	{
		return this.lowerTool;
	}
	
	public String getRaiseTool()
	{
		return this.raiseTool;
	}
	
	public boolean getToolMode(PlayerProfile PP)
	{
		switch(this)
		{
		case AXE:
			return PP.getAxePreparationMode();
		case FISTS:
			return PP.getFistsPreparationMode();
		case HOE:
			return PP.getHoePreparationMode();
		case PICKAXE:
			return PP.getPickaxePreparationMode();
		case SHOVEL:
			return PP.getShovelPreparationMode();
		case SWORD:
			return PP.getSwordsPreparationMode();
		}
		return false;
	}
	
	public void setToolMode(PlayerProfile PP, boolean bool)
	{
		switch(this)
		{
		case AXE:
			 PP.setAxePreparationMode(bool);
			 break;
		case FISTS:
			 PP.setFistsPreparationMode(bool);
			 break;
		case HOE:
			 PP.setHoePreparationMode(bool);
			 break;
		case PICKAXE:
			 PP.setPickaxePreparationMode(bool);
			 break;
		case SHOVEL:
			 PP.setShovelPreparationMode(bool);
			 break;
		case SWORD:
			PP.setSwordsPreparationMode(bool);
			break;
		}
	}
	
	public long getToolATS(PlayerProfile PP)
	{
		switch(this)
		{
		case AXE:
			return PP.getAxePreparationATS();
		case FISTS:
			return PP.getFistsPreparationATS();
		case HOE:
			return PP.getHoePreparationATS();
		case PICKAXE:
			return PP.getPickaxePreparationATS();
		case SHOVEL:
			return PP.getShovelPreparationATS();
		case SWORD:
			return PP.getSwordsPreparationATS();
		}
		return 0;
	}
	
	public void setToolATS(PlayerProfile PP, long ats)
	{
		switch(this)
		{
		case AXE:
			PP.setAxePreparationATS(ats);
			break;
		case FISTS:
			PP.setFistsPreparationATS(ats);
			break;
		case HOE:
			PP.setHoePreparationATS(ats);
			break;
		case PICKAXE:
			PP.setPickaxePreparationATS(ats);
			break;
		case SHOVEL:
			PP.setShovelPreparationATS(ats);
			break;
		case SWORD:
			PP.setSwordsPreparationATS(ats);
			break;
		}
	}
	
	public boolean inHand(ItemStack is)
	{
		switch(this)
		{
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
		}
		return false;
	}
	
}
