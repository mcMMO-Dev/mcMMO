/*
	This file is part of mcMMO.

    mcMMO is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    mcMMO is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with mcMMO.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.gmail.nossr50.datatypes;

import org.bukkit.entity.Player;

import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;

public enum SkillType 
{
	ACROBATICS(LoadProperties.levelCapAcrobatics),
	ALL, //This one is just for convenience
	ARCHERY(LoadProperties.levelCapArchery),
	AXES(AbilityType.SKULL_SPLIITER, LoadProperties.levelCapAxes, ToolType.AXE),
	EXCAVATION(AbilityType.GIGA_DRILL_BREAKER, LoadProperties.levelCapExcavation, ToolType.SHOVEL),
	FISHING(LoadProperties.levelCapFishing),
	HERBALISM(AbilityType.GREEN_TERRA, LoadProperties.levelCapHerbalism, ToolType.HOE),
	MINING(AbilityType.SUPER_BREAKER, LoadProperties.levelCapMining, ToolType.PICKAXE),
	REPAIR(LoadProperties.levelCapRepair),
	SWORDS(AbilityType.SERRATED_STRIKES, LoadProperties.levelCapSwords, ToolType.SWORD),
	TAMING(LoadProperties.levelCapTaming),
	UNARMED(AbilityType.BERSERK, LoadProperties.levelCapUnarmed, ToolType.FISTS),
	WOODCUTTING(AbilityType.TREE_FELLER, LoadProperties.levelCapWoodcutting, ToolType.AXE);
	
	private AbilityType ability;
	private int maxLevel;
	private ToolType tool;
	
	private SkillType()
	{
		this.ability = null;
		this.maxLevel = 0;
		this.tool = null;
	}
	
	private SkillType(AbilityType ability, int maxLevel, ToolType tool)
	{
		this.ability = ability;
		this.maxLevel = maxLevel;
		this.tool = tool;
	}
	
	private SkillType(int maxLevel)
	{
		this(null, maxLevel, null);
	}
	
	public AbilityType getAbility()
	{
		return this.ability;
	}
	
	public int getMaxLevel()
	{
		if(maxLevel > 0)
			return maxLevel;
		else
			return Integer.MAX_VALUE;
	}
	
	public ToolType getTool()
	{
		return this.tool;
	}
	
	public boolean getPermissions(Player player)
	{
		switch(this)
		{
		case ACROBATICS:
			return mcPermissions.getInstance().acrobatics(player);
		case ARCHERY:
			return mcPermissions.getInstance().archery(player);
		case AXES:
			return mcPermissions.getInstance().axes(player);
		case EXCAVATION:
			return mcPermissions.getInstance().excavation(player);
		case FISHING:
			return mcPermissions.getInstance().fishing(player);
		case HERBALISM:
			return mcPermissions.getInstance().herbalism(player);
		case MINING:
			return mcPermissions.getInstance().mining(player);
		case REPAIR:
			return mcPermissions.getInstance().repair(player);
		case SWORDS:
			return mcPermissions.getInstance().swords(player);
		case TAMING:
			return mcPermissions.getInstance().taming(player);
		case UNARMED:
			return mcPermissions.getInstance().unarmed(player);
		case WOODCUTTING:
			return mcPermissions.getInstance().woodcutting(player);
		}
		return false;
	}
}
