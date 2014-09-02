package com.gmail.nossr50.skills;

import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.util.Permissions;

public class SkillAbilityManager extends SkillManager {

	public SkillAbilityManager(McMMOPlayer mcMMOPlayer, SkillType skill) {
		super(mcMMOPlayer, skill);
	}
    
    public void doAbilityPreparationCheck(BlockState blockState) {
    	ToolType tool = skill.getTool();
    	AbilityType ability = skill.getAbility();
    	if (mcMMOPlayer.getToolPreparationMode(tool) && tool.inHand(getPlayer().getItemInHand()) && ability.blockCheck(blockState) && Permissions.skillAbility(getPlayer(), skill) && canAbilityActivate(getPlayer(), getPlayer().getItemInHand(), blockState)) {
    		mcMMOPlayer.checkAbilityActivation(skill);
    	}
    }
    
    public boolean canAbilityActivate(Player player, ItemStack itemInHand, BlockState blockState) {
    	return true;
    }
    
    public void onAbilityActivated() {
    }
    
    public void onAbilityDeactivated() {
    }

}
