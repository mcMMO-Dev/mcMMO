package com.gmail.nossr50.commands.skills;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.util.Page;

public class WoodcuttingCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage."); //TODO: Needs more locale.
			return true;
		}

		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);
		
		float skillvalue = (float) PP.getSkillLevel(SkillType.WOODCUTTING);
		int ticks = 2;
		int x = PP.getSkillLevel(SkillType.WOODCUTTING);
		while (x >= 50) {
			x -= 50;
			ticks++;
		}
		String percentage = String.valueOf((skillvalue / 1000) * 100);
		
		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.SkillWoodCutting") }));
		player.sendMessage(mcLocale.getString("m.XPGain", new Object[] { mcLocale.getString("m.XPGainWoodCutting") }));
		
		if (mcPermissions.getInstance().woodcutting(player))
			player.sendMessage(mcLocale.getString("m.LVL", new Object[] { PP.getSkillLevel(SkillType.WOODCUTTING), PP.getSkillXpLevel(SkillType.WOODCUTTING), PP.getXpToLevel(SkillType.WOODCUTTING) }));
		
		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.Effects") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsWoodCutting1_0"), mcLocale.getString("m.EffectsWoodCutting1_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsWoodCutting2_0"), mcLocale.getString("m.EffectsWoodCutting2_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsWoodCutting3_0"), mcLocale.getString("m.EffectsWoodCutting3_1") }));
		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.YourStats") }));
		
		if (PP.getSkillLevel(SkillType.WOODCUTTING) < 100)
			player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] { mcLocale.getString("m.AbilLockWoodCutting1") }));
		else
			player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] { mcLocale.getString("m.AbilBonusWoodCutting1_0"), mcLocale.getString("m.AbilBonusWoodCutting1_1") }));
		
		player.sendMessage(mcLocale.getString("m.WoodCuttingDoubleDropChance", new Object[] { percentage }));
		player.sendMessage(mcLocale.getString("m.WoodCuttingTreeFellerLength", new Object[] { ticks }));
		
		Page.grabGuidePageForSkill(SkillType.WOODCUTTING, player, args);
		
		return true;
	}
}
