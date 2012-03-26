package com.gmail.nossr50.commands.spout;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.spout.SpoutStuff;

public class XplockCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!LoadProperties.spoutEnabled || !LoadProperties.xpbar || !LoadProperties.xplockEnable) {
			sender.sendMessage("This command is not enabled."); //TODO: Needs more locale.
			return true;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage."); //TODO: Needs more locale.
			return true;
		}

		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);

		if (args.length >= 1 && Skills.isSkill(args[0]) && mcPermissions.getInstance().permission(player, "mcmmo.skills." + Skills.getSkillType(args[0]).toString().toLowerCase())) {
			if (PP.getXpBarLocked()) {
				PP.setSkillLock(Skills.getSkillType(args[0]));
				player.sendMessage(mcLocale.getString("Commands.xplock.locked", new Object[] { m.getCapitalized(PP.getSkillLock().toString()) }));
			} else {
				PP.setSkillLock(Skills.getSkillType(args[0]));
				PP.toggleXpBarLocked();
				player.sendMessage(mcLocale.getString("Commands.xplock.locked", new Object[] { m.getCapitalized(PP.getSkillLock().toString()) }));
			}
			SpoutStuff.updateXpBar(player);
		} else if (args.length < 1) {
			if (PP.getXpBarLocked()) {
				PP.toggleXpBarLocked();
				player.sendMessage(mcLocale.getString("Commands.xplock.unlocked"));
			} else if (PP.getLastGained() != null) {
				PP.toggleXpBarLocked();
				PP.setSkillLock(PP.getLastGained());
				player.sendMessage(mcLocale.getString("Commands.xplock.locked", new Object[] { m.getCapitalized(PP.getSkillLock().toString()) }));
			}
		} else if (args.length >= 1 && !Skills.isSkill(args[0])) {
			player.sendMessage("Commands.xplock.invalid");
		} else if (args.length >= 2 && Skills.isSkill(args[0]) && !mcPermissions.getInstance().permission(player, "mcmmo.skills." + Skills.getSkillType(args[0]).toString().toLowerCase())) {
			player.sendMessage(ChatColor.YELLOW + "[mcMMO] " + ChatColor.DARK_RED + mcLocale.getString("mcPlayerListener.NoPermission"));
			return true;
		}

		return true;
	}
}
