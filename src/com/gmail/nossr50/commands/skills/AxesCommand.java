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

public class AxesCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage.");
			return true;
		}

		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);

		String percentage;

		float skillvalue = (float) PP.getSkillLevel(SkillType.AXES);
		if (PP.getSkillLevel(SkillType.AXES) < 750)
			percentage = String.valueOf((skillvalue / 1000) * 100);
		else
			percentage = "75";

		int ticks = 2;
		int x = PP.getSkillLevel(SkillType.AXES);
		while (x >= 50) {
			x -= 50;
			ticks++;
		}

		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.SkillAxes") }));
		player.sendMessage(mcLocale.getString("m.XPGain", new Object[] { mcLocale.getString("m.XPGainAxes") }));

		if (mcPermissions.getInstance().axes(player))
			player.sendMessage(mcLocale.getString("m.LVL", new Object[] { PP.getSkillLevel(SkillType.AXES), PP.getSkillXpLevel(SkillType.AXES), PP.getXpToLevel(SkillType.AXES) }));

		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.Effects") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsAxes1_0"), mcLocale.getString("m.EffectsAxes1_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsAxes2_0"), mcLocale.getString("m.EffectsAxes2_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsAxes3_0"), mcLocale.getString("m.EffectsAxes3_1") }));
		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.YourStats") }));
		player.sendMessage(mcLocale.getString("m.AxesCritChance", new Object[] { percentage }));

		if (PP.getSkillLevel(SkillType.AXES) < 500)
			player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] { mcLocale.getString("m.AbilLockAxes1") }));
		else
			player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] { mcLocale.getString("m.AbilBonusAxes1_0"), mcLocale.getString("m.AbilBonusAxes1_1") }));

		player.sendMessage(mcLocale.getString("m.AxesSkullLength", new Object[] { ticks }));

		return true;
	}
}
