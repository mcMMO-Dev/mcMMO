package com.gmail.nossr50.commands.general;

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
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.skills.Skills;

public class StatsCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!LoadProperties.statsEnable) {
			sender.sendMessage("This command is not enabled.");
			return true;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage.");
			return true;
		}

		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);

		player.sendMessage(mcLocale.getString("mcPlayerListener.YourStats"));

		if (mcPermissions.getEnabled())
			player.sendMessage(mcLocale.getString("mcPlayerListener.NoSkillNote"));

		ChatColor header = ChatColor.GOLD;

		if (Skills.hasGatheringSkills(player)) {
			player.sendMessage(header + "-=GATHERING SKILLS=-");
			if (mcPermissions.getInstance().excavation(player))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.ExcavationSkill"), PP.getSkillLevel(SkillType.EXCAVATION), PP.getSkillXpLevel(SkillType.EXCAVATION), PP.getXpToLevel(SkillType.EXCAVATION)));
			if (mcPermissions.getInstance().fishing(player))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.FishingSkill"), PP.getSkillLevel(SkillType.FISHING), PP.getSkillXpLevel(SkillType.FISHING), PP.getXpToLevel(SkillType.FISHING)));
			if (mcPermissions.getInstance().herbalism(player))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.HerbalismSkill"), PP.getSkillLevel(SkillType.HERBALISM), PP.getSkillXpLevel(SkillType.HERBALISM), PP.getXpToLevel(SkillType.HERBALISM)));
			if (mcPermissions.getInstance().mining(player))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.MiningSkill"), PP.getSkillLevel(SkillType.MINING), PP.getSkillXpLevel(SkillType.MINING), PP.getXpToLevel(SkillType.MINING)));
			if (mcPermissions.getInstance().woodcutting(player))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.WoodcuttingSkill"), PP.getSkillLevel(SkillType.WOODCUTTING), PP.getSkillXpLevel(SkillType.WOODCUTTING), PP.getXpToLevel(SkillType.WOODCUTTING)));
		}
		if (Skills.hasCombatSkills(player)) {
			player.sendMessage(header + "-=COMBAT SKILLS=-");
			if (mcPermissions.getInstance().axes(player))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.AxesSkill"), PP.getSkillLevel(SkillType.AXES), PP.getSkillXpLevel(SkillType.AXES), PP.getXpToLevel(SkillType.AXES)));
			if (mcPermissions.getInstance().archery(player))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.ArcherySkill"), PP.getSkillLevel(SkillType.ARCHERY), PP.getSkillXpLevel(SkillType.ARCHERY), PP.getXpToLevel(SkillType.ARCHERY)));
			// if(mcPermissions.getInstance().sorcery(player))
			// player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.SorcerySkill"), PP.getSkill("sorcery"), PP.getSkill("sorceryXP"), PP.getXpToLevel("excavation")));
			if (mcPermissions.getInstance().swords(player))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.SwordsSkill"), PP.getSkillLevel(SkillType.SWORDS), PP.getSkillXpLevel(SkillType.SWORDS), PP.getXpToLevel(SkillType.SWORDS)));
			if (mcPermissions.getInstance().taming(player))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.TamingSkill"), PP.getSkillLevel(SkillType.TAMING), PP.getSkillXpLevel(SkillType.TAMING), PP.getXpToLevel(SkillType.TAMING)));
			if (mcPermissions.getInstance().unarmed(player))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.UnarmedSkill"), PP.getSkillLevel(SkillType.UNARMED), PP.getSkillXpLevel(SkillType.UNARMED), PP.getXpToLevel(SkillType.UNARMED)));
		}

		if (Skills.hasMiscSkills(player)) {
			player.sendMessage(header + "-=MISC SKILLS=-");
			if (mcPermissions.getInstance().acrobatics(player))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.AcrobaticsSkill"), PP.getSkillLevel(SkillType.ACROBATICS), PP.getSkillXpLevel(SkillType.ACROBATICS), PP.getXpToLevel(SkillType.ACROBATICS)));
			// if(mcPermissions.getInstance().alchemy(player))
			// player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.AlchemySkill"), PP.getSkillLevel(SkillType.ALCHEMY), PP.getSkillXpLevel(SkillType.ALCHEMY), PP.getXpToLevel(SkillType.ALCHEMY)));
			// if(mcPermissions.getInstance().enchanting(player))
			// player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.EnchantingSkill"), PP.getSkillLevel(SkillType.ENCHANTING), PP.getSkillXpLevel(SkillType.ENCHANTING), PP.getXpToLevel(SkillType.ENCHANTING)));
			if (mcPermissions.getInstance().repair(player))
				player.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.RepairSkill"), PP.getSkillLevel(SkillType.REPAIR), PP.getSkillXpLevel(SkillType.REPAIR), PP.getXpToLevel(SkillType.REPAIR)));
		}
		player.sendMessage(mcLocale.getString("mcPlayerListener.PowerLevel") + ChatColor.GREEN + (m.getPowerLevel(player)));

		return true;
	}
}
