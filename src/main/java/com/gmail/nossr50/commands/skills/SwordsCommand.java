package com.gmail.nossr50.commands.skills;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.util.Page;

public class SwordsCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage."); //TODO: Needs more locale.
			return true;
		}

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.skills.swords")) {
            return true;
        }
		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);

		int bleedrank = 2;
		String percentage, counterattackpercentage;

		float skillvalue = (float) PP.getSkillLevel(SkillType.SWORDS);
		if (PP.getSkillLevel(SkillType.SWORDS) < 750)
			percentage = String.valueOf((skillvalue / 1000) * 100);
		else
			percentage = "75";

		if (skillvalue >= 750)
			bleedrank += 1;

		if (PP.getSkillLevel(SkillType.SWORDS) <= 600)
			counterattackpercentage = String.valueOf((skillvalue / 2000) * 100);
		else
			counterattackpercentage = "30";

		int ticks = 2;
		int x = PP.getSkillLevel(SkillType.SWORDS);
		while (x >= 50) {
			x -= 50;
			ticks++;
		}

		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.SkillSwords") }));
		player.sendMessage(mcLocale.getString("m.XPGain", new Object[] { mcLocale.getString("m.XPGainSwords") }));
		
		if (mcPermissions.getInstance().swords(player))
			player.sendMessage(mcLocale.getString("m.LVL", new Object[] { PP.getSkillLevel(SkillType.SWORDS), PP.getSkillXpLevel(SkillType.SWORDS), PP.getXpToLevel(SkillType.SWORDS) }));
		
		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.Effects") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsSwords1_0"), mcLocale.getString("m.EffectsSwords1_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsSwords2_0"), mcLocale.getString("m.EffectsSwords2_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsSwords3_0"), mcLocale.getString("m.EffectsSwords3_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsSwords5_0"), mcLocale.getString("m.EffectsSwords5_1") }));
		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.YourStats") }));
		player.sendMessage(mcLocale.getString("m.SwordsCounterAttChance", new Object[] { counterattackpercentage }));
		player.sendMessage(mcLocale.getString("m.SwordsBleedLength", new Object[] { bleedrank }));
		player.sendMessage(mcLocale.getString("m.SwordsTickNote"));
		player.sendMessage(mcLocale.getString("m.SwordsBleedChance", new Object[] { percentage }));
		player.sendMessage(mcLocale.getString("m.SwordsSSLength", new Object[] { ticks }));
		
		Page.grabGuidePageForSkill(SkillType.SWORDS, player, args);

		return true;
	}
}
