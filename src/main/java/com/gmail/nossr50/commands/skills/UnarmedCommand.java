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

public class UnarmedCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage.");
			return true;
		}

		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);

		String percentage, arrowpercentage;
		float skillvalue = (float) PP.getSkillLevel(SkillType.UNARMED);

		if (PP.getSkillLevel(SkillType.UNARMED) < 1000)
			percentage = String.valueOf((skillvalue / 4000) * 100);
		else
			percentage = "25";

		if (PP.getSkillLevel(SkillType.UNARMED) < 1000)
			arrowpercentage = String.valueOf(((skillvalue / 1000) * 100) / 2);
		else
			arrowpercentage = "50";

		int ticks = 2;
		int x = PP.getSkillLevel(SkillType.UNARMED);
		while (x >= 50) {
			x -= 50;
			ticks++;
		}

		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.SkillUnarmed") }));
		player.sendMessage(mcLocale.getString("m.XPGain", new Object[] { mcLocale.getString("m.XPGainUnarmed") }));

		if (mcPermissions.getInstance().unarmed(player))
			player.sendMessage(mcLocale.getString("m.LVL", new Object[] { PP.getSkillLevel(SkillType.UNARMED), PP.getSkillXpLevel(SkillType.UNARMED), PP.getXpToLevel(SkillType.UNARMED) }));

		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.Effects") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsUnarmed1_0"), mcLocale.getString("m.EffectsUnarmed1_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsUnarmed2_0"), mcLocale.getString("m.EffectsUnarmed2_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsUnarmed3_0"), mcLocale.getString("m.EffectsUnarmed3_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsUnarmed4_0"), mcLocale.getString("m.EffectsUnarmed4_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsUnarmed5_0"), mcLocale.getString("m.EffectsUnarmed5_1") }));
		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.YourStats") }));
		player.sendMessage(mcLocale.getString("m.UnarmedArrowDeflectChance", new Object[] { arrowpercentage }));
		player.sendMessage(mcLocale.getString("m.UnarmedDisarmChance", new Object[] { percentage }));

		if (PP.getSkillLevel(SkillType.UNARMED) < 250) {
			player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] { mcLocale.getString("m.AbilLockUnarmed1") }));
		} else if (PP.getSkillLevel(SkillType.UNARMED) >= 250 && PP.getSkillLevel(SkillType.UNARMED) < 500) {
			player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] { mcLocale.getString("m.AbilBonusUnarmed1_0"), mcLocale.getString("m.AbilBonusUnarmed1_1") }));
			player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] { mcLocale.getString("m.AbilLockUnarmed2") }));
		} else {
			player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] { mcLocale.getString("m.AbilBonusUnarmed2_0"), mcLocale.getString("m.AbilBonusUnarmed2_1") }));
		}

		player.sendMessage(mcLocale.getString("m.UnarmedBerserkLength", new Object[] { ticks }));

		return true;
	}
}
