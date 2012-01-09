package com.gmail.nossr50.commands.general;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.skills.Skills;

public class WhoisCommand implements CommandExecutor {
	private final mcMMO plugin;

	public WhoisCommand(mcMMO instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!LoadProperties.whoisEnable) {
			sender.sendMessage("This command is not enabled.");
			return true;
		}

		Player player = (Player) sender;

		if (!mcPermissions.getInstance().whois(player)) {
			player.sendMessage(ChatColor.YELLOW + "[mcMMO] " + ChatColor.DARK_RED + mcLocale.getString("mcPlayerListener.NoPermission"));
			return true;
		}

		if (args.length < 1) {
			player.sendMessage(ChatColor.RED + "Proper usage is /" + LoadProperties.whois + " <playername>");
			return true;
		}
		// if split[1] is a player
		if (plugin.getServer().getPlayer(args[0]) != null) {
			Player target = plugin.getServer().getPlayer(args[0]);
			PlayerProfile PPt = Users.getProfile(target);

			player.sendMessage(ChatColor.GREEN + "~~WHOIS RESULTS~~");
			player.sendMessage(target.getName());
			if (PPt.inParty())
				player.sendMessage("Party: " + PPt.getParty());
			player.sendMessage("Health: " + target.getHealth() + ChatColor.GRAY + " (20 is full health)");
			player.sendMessage("OP: " + target.isOp());
			player.sendMessage(ChatColor.GREEN + "mcMMO Stats for " + ChatColor.YELLOW + target.getName());

			player.sendMessage(ChatColor.GOLD + "-=GATHERING SKILLS=-");
			if (mcPermissions.getInstance().excavation(target))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.ExcavationSkill"), PPt.getSkillLevel(SkillType.EXCAVATION), PPt.getSkillXpLevel(SkillType.EXCAVATION), PPt.getXpToLevel(SkillType.EXCAVATION)));
			if (mcPermissions.getInstance().fishing(target))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.FishingSkill"), PPt.getSkillLevel(SkillType.FISHING), PPt.getSkillXpLevel(SkillType.FISHING), PPt.getXpToLevel(SkillType.FISHING)));
			if (mcPermissions.getInstance().herbalism(target))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.HerbalismSkill"), PPt.getSkillLevel(SkillType.HERBALISM), PPt.getSkillXpLevel(SkillType.HERBALISM), PPt.getXpToLevel(SkillType.HERBALISM)));
			if (mcPermissions.getInstance().mining(target))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.MiningSkill"), PPt.getSkillLevel(SkillType.MINING), PPt.getSkillXpLevel(SkillType.MINING), PPt.getXpToLevel(SkillType.MINING)));
			if (mcPermissions.getInstance().woodcutting(target))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.WoodcuttingSkill"), PPt.getSkillLevel(SkillType.WOODCUTTING), PPt.getSkillXpLevel(SkillType.WOODCUTTING), PPt.getXpToLevel(SkillType.WOODCUTTING)));

			player.sendMessage(ChatColor.GOLD + "-=COMBAT SKILLS=-");
			if (mcPermissions.getInstance().axes(target))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.AxesSkill"), PPt.getSkillLevel(SkillType.AXES), PPt.getSkillXpLevel(SkillType.AXES), PPt.getXpToLevel(SkillType.AXES)));
			if (mcPermissions.getInstance().archery(player))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.ArcherySkill"), PPt.getSkillLevel(SkillType.ARCHERY), PPt.getSkillXpLevel(SkillType.ARCHERY), PPt.getXpToLevel(SkillType.ARCHERY)));
			// if(mcPermissions.getInstance().sorcery(target))
			// player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.SorcerySkill"), PPt.getSkill("sorcery"), PPt.getSkill("sorceryXP"), PPt.getXpToLevel("excavation")));
			if (mcPermissions.getInstance().swords(target))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.SwordsSkill"), PPt.getSkillLevel(SkillType.SWORDS), PPt.getSkillXpLevel(SkillType.SWORDS), PPt.getXpToLevel(SkillType.SWORDS)));
			if (mcPermissions.getInstance().taming(target))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.TamingSkill"), PPt.getSkillLevel(SkillType.TAMING), PPt.getSkillXpLevel(SkillType.TAMING), PPt.getXpToLevel(SkillType.TAMING)));
			if (mcPermissions.getInstance().unarmed(target))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.UnarmedSkill"), PPt.getSkillLevel(SkillType.UNARMED), PPt.getSkillXpLevel(SkillType.UNARMED), PPt.getXpToLevel(SkillType.UNARMED)));

			player.sendMessage(ChatColor.GOLD + "-=MISC SKILLS=-");
			if (mcPermissions.getInstance().acrobatics(target))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.AcrobaticsSkill"), PPt.getSkillLevel(SkillType.ACROBATICS), PPt.getSkillXpLevel(SkillType.ACROBATICS), PPt.getXpToLevel(SkillType.ACROBATICS)));
			// if(mcPermissions.getInstance().alchemy(target))
			// player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.AlchemySkill"), PPt.getSkillLevel(SkillType.ALCHEMY), PPt.getSkillXpLevel(SkillType.ALCHEMY), PPt.getXpToLevel(SkillType.ALCHEMY)));
			// if(mcPermissions.getInstance().enchanting(target))
			// player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.EnchantingSkill"), PPt.getSkillLevel(SkillType.ENCHANTING), PPt.getSkillXpLevel(SkillType.ENCHANTING), PPt.getXpToLevel(SkillType.ENCHANTING)));
			if (mcPermissions.getInstance().repair(target))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.RepairSkill"), PPt.getSkillLevel(SkillType.REPAIR), PPt.getSkillXpLevel(SkillType.REPAIR), PPt.getXpToLevel(SkillType.REPAIR)));

			player.sendMessage(mcLocale.getString("mcPlayerListener.PowerLevel") + ChatColor.GREEN + (m.getPowerLevel(target)));
		}

		return true;
	}
}
