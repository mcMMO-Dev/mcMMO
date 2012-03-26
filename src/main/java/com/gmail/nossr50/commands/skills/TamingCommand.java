package com.gmail.nossr50.commands.skills;

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
import com.gmail.nossr50.util.Page;

public class TamingCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage."); //TODO: Needs more locale.
			return true;
		}

		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);

		float skillvalue = (float) PP.getSkillLevel(SkillType.TAMING);
		String percentage = String.valueOf((skillvalue / 1000) * 100);

		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.SkillTaming") }));
		player.sendMessage(mcLocale.getString("m.XPGain", new Object[] { mcLocale.getString("m.XPGainTaming") }));

		if (mcPermissions.getInstance().taming(player))
			player.sendMessage(mcLocale.getString("m.LVL", new Object[] { PP.getSkillLevel(SkillType.TAMING), PP.getSkillXpLevel(SkillType.TAMING), PP.getXpToLevel(SkillType.TAMING) }));

		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.Effects") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsTaming1_0"), mcLocale.getString("m.EffectsTaming1_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsTaming2_0"), mcLocale.getString("m.EffectsTaming2_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsTaming3_0"), mcLocale.getString("m.EffectsTaming3_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsTaming4_0"), mcLocale.getString("m.EffectsTaming4_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsTaming5_0"), mcLocale.getString("m.EffectsTaming5_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsTaming6_0"), mcLocale.getString("m.EffectsTaming6_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsTaming7_0"), mcLocale.getString("m.EffectsTaming7_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsTaming8_0"), mcLocale.getString("m.EffectsTaming8_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTaming7_2", new Object[] { LoadProperties.bonesConsumedByCOTW }));
		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.YourStats") }));

		if (PP.getSkillLevel(SkillType.TAMING) < 100)
			player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] { mcLocale.getString("m.AbilLockTaming1") }));
		else
			player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] { mcLocale.getString("m.AbilBonusTaming1_0"), mcLocale.getString("m.AbilBonusTaming1_1") }));

		if (PP.getSkillLevel(SkillType.TAMING) < 250)
			player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] { mcLocale.getString("m.AbilLockTaming2") }));
		else
			player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] { mcLocale.getString("m.AbilBonusTaming2_0"), mcLocale.getString("m.AbilBonusTaming2_1") }));

		if (PP.getSkillLevel(SkillType.TAMING) < 500)
			player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] { mcLocale.getString("m.AbilLockTaming3") }));
		else
			player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] { mcLocale.getString("m.AbilBonusTaming3_0"), mcLocale.getString("m.AbilBonusTaming3_1") }));

		if (PP.getSkillLevel(SkillType.TAMING) < 750)
			player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] { mcLocale.getString("m.AbilLockTaming4") }));
		else
			player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] { mcLocale.getString("m.AbilBonusTaming4_0"), mcLocale.getString("m.AbilBonusTaming4_1") }));
		if (PP.getSkillLevel(SkillType.TAMING) < 50)
            player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] { mcLocale.getString("m.AbilLockTaming5") }));
		else
	          player.sendMessage(mcLocale.getString("m.AbilityBonusTemplate", new Object[] { mcLocale.getString("m.AbilBonusTaming5_0"), mcLocale.getString("m.AbilBonusTaming5_1") }));
		
		player.sendMessage(mcLocale.getString("m.TamingGoreChance", new Object[] { percentage }));
		
		Page.grabGuidePageForSkill(SkillType.TAMING, player, args);

		return true;
	}
}
