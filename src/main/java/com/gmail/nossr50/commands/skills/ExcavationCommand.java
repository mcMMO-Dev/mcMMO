package com.gmail.nossr50.commands.skills;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;

public class ExcavationCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage.");
			return true;
		}

		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);

		int ticks = 2;
		int x = PP.getSkillLevel(SkillType.EXCAVATION);
		while (x >= 50) {
			x -= 50;
			ticks++;
		}


		player.sendMessage("");
		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.SkillExcavation") }));
		player.sendMessage(mcLocale.getString("m.XPGain", new Object[] { mcLocale.getString("m.XPGainExcavation") }));

		if (mcPermissions.getInstance().excavation(player))
			player.sendMessage(mcLocale.getString("m.LVL", new Object[] { PP.getSkillLevel(SkillType.EXCAVATION), PP.getSkillXpLevel(SkillType.EXCAVATION), PP.getXpToLevel(SkillType.EXCAVATION) }));

		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.Effects") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsExcavation1_0"), mcLocale.getString("m.EffectsExcavation1_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsExcavation2_0"), mcLocale.getString("m.EffectsExcavation2_1") }));
		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.YourStats") }));
		player.sendMessage(mcLocale.getString("m.ExcavationGigaDrillBreakerLength", new Object[] { ticks }));

		if (args.length >= 1)
		{
			if(args[0].equals("?"))
			{
				if(args[1].equals("1"))
				{
					player.sendMessage("==EXCAVATION==");
					player.sendMessage("");
					player.sendMessage("==XP Gain==");
					player.sendMessage("Base XP: " + LoadProperties.mbase);
					player.sendMessage(ChatColor.GRAY + "Awarded for digging Dirt, Grass, Sand, Gravel, ");
					player.sendMessage(ChatColor.GRAY + "Soul Sand, Mycelium, and Clay.");
					player.sendMessage("Treasures: Varies by item");
					player.sendMessage(ChatColor.GRAY + "Awarded for finding items while digging.");
					player.sendMessage("");
					player.sendMessage("==Abilities==");
					player.sendMessage("Giga Drill Breaker");
					player.sendMessage(ChatColor.GRAY + "Right-click with a shovel in hand to prep this ability.");
					player.sendMessage(ChatColor.GRAY + "Allows for instabreaking of associated blocks.");
					player.sendMessage("");
					player.sendMessage("==Subskills==");
					player.sendMessage("Treasure Hunter");
					player.sendMessage(ChatColor.GRAY + "Randomly find valuable items while digging.");
					player.sendMessage(ChatColor.GRAY + "Items found vary depending on skill level.");
					player.sendMessage("");
					player.sendMessage("==PAGE 1 of 1==");
				}
				
			}
			else
			{
				player.sendMessage(ChatColor.RED + "Usage is /excavation ? [page]");
			}
				
		}
		
		return true;
	}
}
