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

public class HerbalismCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage."); //TODO: Needs more locale.
			return true;
		}

		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);

		int bonus = 0;
		if (PP.getSkillLevel(SkillType.HERBALISM) >= 200)
			bonus++;
		if (PP.getSkillLevel(SkillType.HERBALISM) >= 400)
			bonus++;
		if (PP.getSkillLevel(SkillType.HERBALISM) >= 600)
			bonus++;
		if (PP.getSkillLevel(SkillType.HERBALISM) >= 800)
			bonus++;
		if (PP.getSkillLevel(SkillType.HERBALISM) >= 1000)
			bonus++;

		int ticks = 2;
		int x = PP.getSkillLevel(SkillType.HERBALISM);
		while (x >= 50) {
			x -= 50;
			ticks++;
		}

		float skillvalue = (float) PP.getSkillLevel(SkillType.HERBALISM);

		String percentage = String.valueOf((skillvalue / 1000) * 100);
		String gpercentage = String.valueOf((skillvalue / 1500) * 100);
		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.SkillHerbalism") }));
		player.sendMessage(mcLocale.getString("m.XPGain", new Object[] { mcLocale.getString("m.XPGainHerbalism") }));

		if (mcPermissions.getInstance().herbalism(player))
			player.sendMessage(mcLocale.getString("m.LVL", new Object[] { PP.getSkillLevel(SkillType.HERBALISM), PP.getSkillXpLevel(SkillType.HERBALISM), PP.getXpToLevel(SkillType.HERBALISM) }));

		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.Effects") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsHerbalism1_0"), mcLocale.getString("m.EffectsHerbalism1_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsHerbalism2_0"), mcLocale.getString("m.EffectsHerbalism2_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsHerbalism3_0"), mcLocale.getString("m.EffectsHerbalism3_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsHerbalism4_0"), mcLocale.getString("m.EffectsHerbalism4_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsHerbalism5_0"), mcLocale.getString("m.EffectsHerbalism5_1") }));
		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.YourStats") }));
		player.sendMessage(mcLocale.getString("m.HerbalismGreenTerraLength", new Object[] { ticks }));
		player.sendMessage(mcLocale.getString("m.HerbalismGreenThumbChance", new Object[] { gpercentage }));
		player.sendMessage(mcLocale.getString("m.HerbalismGreenThumbStage", new Object[] { bonus }));
		player.sendMessage(mcLocale.getString("m.HerbalismFoodPlus", new Object[] { bonus } ));
		player.sendMessage(mcLocale.getString("m.HerbalismDoubleDropChance", new Object[] { percentage }));
		
		Page.grabGuidePageForSkill(SkillType.HERBALISM, player, args);

		return true;
	}
}
