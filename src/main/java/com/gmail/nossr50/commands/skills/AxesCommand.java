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
		
		int bonusDmg = Users.getProfile(player).getSkillLevel(SkillType.AXES)/50;
		if(bonusDmg > 4)
		    bonusDmg = 4;

		int ticks = 2;
		short durDmg = 5;
		durDmg+=Users.getProfile(player).getSkillLevel(SkillType.AXES)/30;
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
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsAxes4_0"), mcLocale.getString("m.EffectsAxes4_1") }));
        player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsAxes5_0"), mcLocale.getString("m.EffectsAxes5_1") }));
		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.YourStats") }));
		player.sendMessage(mcLocale.getString("m.AxesCritChance", new Object[] { percentage }));
		
		player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] { mcLocale.getString("m.AbilBonusAxes1_0"), mcLocale.getString("m.AbilBonusAxes1_1", new Object[] {bonusDmg}) }));
        player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] { mcLocale.getString("m.AbilBonusAxes2_0"), mcLocale.getString("m.AbilBonusAxes2_1", new Object[] {durDmg}) }));
        player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] { mcLocale.getString("m.AbilBonusAxes3_0"), mcLocale.getString("m.AbilBonusAxes3_1", new Object[] {1}) }));
        player.sendMessage(mcLocale.getString("m.AxesSkullLength", new Object[] { ticks }));

		return true;
	}
}
