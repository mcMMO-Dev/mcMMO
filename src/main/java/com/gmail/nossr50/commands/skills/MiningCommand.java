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

public class MiningCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage."); //TODO: Needs more locale.
			return true;
		}

		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);

		float skillvalue = (float) PP.getSkillLevel(SkillType.MINING);
		String percentage = String.valueOf((skillvalue / 1000) * 100);
		int ticks = 2;
		int x = PP.getSkillLevel(SkillType.MINING);
		while (x >= 50) {
			x -= 50;
			ticks++;
		}
		
		int rank = 0;
		int damage = 0;
		int radius = 0;
		if(PP.getSkillLevel(SkillType.MINING) >= 125 && PP.getSkillLevel(SkillType.MINING) < 250)
			rank = 1;
		if(PP.getSkillLevel(SkillType.MINING) >= 250 && PP.getSkillLevel(SkillType.MINING) < 375){
			rank = 2;
			radius = 1;
		}
		if(PP.getSkillLevel(SkillType.MINING) >= 375 && PP.getSkillLevel(SkillType.MINING) < 500){
			rank = 3;
			radius = 1;
		}
		if(PP.getSkillLevel(SkillType.MINING) >= 500 && PP.getSkillLevel(SkillType.MINING) < 625){
			rank = 4;
			damage = 25;
			radius = 2;
		}
		if(PP.getSkillLevel(SkillType.MINING) >= 625 && PP.getSkillLevel(SkillType.MINING) < 750){
			rank = 5;
			damage = 25;
			radius = 2;
		}
		if(PP.getSkillLevel(SkillType.MINING) >= 750 && PP.getSkillLevel(SkillType.MINING) < 875){
			rank = 6;
			damage = 50;
			radius = 3;
		}
		if(PP.getSkillLevel(SkillType.MINING) >= 875 && PP.getSkillLevel(SkillType.MINING) < 1000){
			rank = 7;
			damage = 50;
			radius = 3;
		}
		if(PP.getSkillLevel(SkillType.MINING) >= 1000){
			rank = 8;
			damage = 100;
			radius = 4;
		}
		
		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.SkillMining") }));
		player.sendMessage(mcLocale.getString("m.XPGain", new Object[] { mcLocale.getString("m.XPGainMining") }));

		if (mcPermissions.getInstance().mining(player))
			player.sendMessage(mcLocale.getString("m.LVL", new Object[] { PP.getSkillLevel(SkillType.MINING), PP.getSkillXpLevel(SkillType.MINING), PP.getXpToLevel(SkillType.MINING) }));

		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.Effects") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsMining1_0"), mcLocale.getString("m.EffectsMining1_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsMining2_0"), mcLocale.getString("m.EffectsMining2_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsMining3_0"), mcLocale.getString("m.EffectsMining3_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsMining4_0"), mcLocale.getString("m.EffectsMining4_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsMining5_0"), mcLocale.getString("m.EffectsMining5_1") }));
		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.YourStats") }));
		player.sendMessage(mcLocale.getString("m.MiningDoubleDropChance", new Object[] { percentage }));
		player.sendMessage(mcLocale.getString("m.MiningSuperBreakerLength", new Object[] { ticks }));
		if (PP.getSkillLevel(SkillType.MINING) < 125)
			player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] { mcLocale.getString("m.AbilLockMining1") }));
		else{
			switch (rank){
			case 1:
				player.sendMessage(mcLocale.getString("m.MiningBlastMining", new Object[] { rank, mcLocale.getString("m.BlastMining1") }));
				break;
			case 2:
				player.sendMessage(mcLocale.getString("m.MiningBlastMining", new Object[] { rank, mcLocale.getString("m.BlastMining2") }));
				break;
			case 3:
				player.sendMessage(mcLocale.getString("m.MiningBlastMining", new Object[] { rank, mcLocale.getString("m.BlastMining3") }));
				break;
			case 4:
				player.sendMessage(mcLocale.getString("m.MiningBlastMining", new Object[] { rank, mcLocale.getString("m.BlastMining4") }));
				break;
			case 5:
				player.sendMessage(mcLocale.getString("m.MiningBlastMining", new Object[] { rank, mcLocale.getString("m.BlastMining5") }));
				break;
			case 6:
				player.sendMessage(mcLocale.getString("m.MiningBlastMining", new Object[] { rank, mcLocale.getString("m.BlastMining6") }));
				break;
			case 7:
				player.sendMessage(mcLocale.getString("m.MiningBlastMining", new Object[] { rank, mcLocale.getString("m.BlastMining7") }));
				break;
			case 8:
				player.sendMessage(mcLocale.getString("m.MiningBlastMining", new Object[] { rank, mcLocale.getString("m.BlastMining8") }));
				break;
			}
		}
		if (PP.getSkillLevel(SkillType.MINING) < 250)
			player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] { mcLocale.getString("m.AbilLockMining2") }));
		else
			player.sendMessage(mcLocale.getString("m.MiningBiggerBombs", new Object[] { radius }));
		if (PP.getSkillLevel(SkillType.MINING) < 500)
			player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] { mcLocale.getString("m.AbilLockMining3") }));
		else
			player.sendMessage(mcLocale.getString("m.MiningDemolitionsExpertDamageDecrease", new Object[] { damage }));
		
		Page.grabGuidePageForSkill(SkillType.MINING, player, args);
		
		return true;
	}
}
