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

public class AcrobaticsCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player))
		{
			sender.sendMessage("This command does not support console useage."); //TODO: Needs more locale.
			return true;
		}

		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);

		String dodgepercentage;
		float skillvalue = (float) PP.getSkillLevel(SkillType.ACROBATICS);
		String percentage = String.valueOf((skillvalue / 1000) * 100);
		String gracepercentage = String.valueOf(((skillvalue / 1000) * 100) * 2);

		if (PP.getSkillLevel(SkillType.ACROBATICS) <= 800)
			dodgepercentage = String.valueOf((skillvalue / 4000 * 100));
		else
			dodgepercentage = "20";

		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.SkillAcrobatics") }));
		player.sendMessage(mcLocale.getString("m.XPGain", new Object[] { mcLocale.getString("m.XPGainAcrobatics") }));

		if (mcPermissions.getInstance().acrobatics(player))
			player.sendMessage(mcLocale.getString("m.LVL", new Object[] { PP.getSkillLevel(SkillType.ACROBATICS), PP.getSkillXpLevel(SkillType.ACROBATICS), PP.getXpToLevel(SkillType.ACROBATICS) }));

		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.Effects") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsAcrobatics1_0"), mcLocale.getString("m.EffectsAcrobatics1_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsAcrobatics2_0"), mcLocale.getString("m.EffectsAcrobatics2_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsAcrobatics3_0"), mcLocale.getString("m.EffectsAcrobatics3_1") }));
		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.YourStats") }));
		player.sendMessage(mcLocale.getString("m.AcrobaticsRollChance", new Object[] { percentage }));
		player.sendMessage(mcLocale.getString("m.AcrobaticsGracefulRollChance", new Object[] { gracepercentage }));
		player.sendMessage(mcLocale.getString("m.AcrobaticsDodgeChance", new Object[] { dodgepercentage }));
		
		Page.grabGuidePageForSkill(SkillType.ACROBATICS, player, args);
		
		return true;
	}
}