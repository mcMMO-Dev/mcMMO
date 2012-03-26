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
import com.gmail.nossr50.skills.Repair;
import com.gmail.nossr50.util.Page;

public class RepairCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage."); //TODO: Needs more locale.
			return true;
		}

		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);

		float skillvalue = (float) PP.getSkillLevel(SkillType.REPAIR);
		String percentage = String.valueOf((skillvalue / 1000) * 100);
		String repairmastery = String.valueOf((skillvalue / 500) * 100);
		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.SkillRepair") }));
		player.sendMessage(mcLocale.getString("m.XPGain", new Object[] { mcLocale.getString("m.XPGainRepair") }));

		if (mcPermissions.getInstance().repair(player))
			player.sendMessage(mcLocale.getString("m.LVL", new Object[] { PP.getSkillLevel(SkillType.REPAIR), PP.getSkillXpLevel(SkillType.REPAIR), PP.getXpToLevel(SkillType.REPAIR) }));

		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.Effects") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsRepair1_0"), mcLocale.getString("m.EffectsRepair1_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsRepair2_0"), mcLocale.getString("m.EffectsRepair2_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsRepair3_0"), mcLocale.getString("m.EffectsRepair3_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsRepair4_0", new Object[] { LoadProperties.repairdiamondlevel }), mcLocale.getString("m.EffectsRepair4_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsRepair5_0"), mcLocale.getString("m.EffectsRepair5_1") }));
		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.YourStats") }));
		player.sendMessage(mcLocale.getString("m.RepairRepairMastery", new Object[] { repairmastery }));
		player.sendMessage(mcLocale.getString("m.RepairSuperRepairChance", new Object[] { percentage }));
		player.sendMessage(mcLocale.getString("m.ArcaneForgingRank", new Object[] { Repair.getArcaneForgingRank(PP.getSkillLevel(SkillType.REPAIR)) }));
		player.sendMessage(mcLocale.getString("m.ArcaneEnchantKeepChance", new Object[] { Repair.getEnchantChance(Repair.getArcaneForgingRank(PP.getSkillLevel(SkillType.REPAIR))) }));
		player.sendMessage(mcLocale.getString("m.ArcaneEnchantDowngradeChance", new Object[] { Repair.getDowngradeChance(Repair.getArcaneForgingRank(PP.getSkillLevel(SkillType.REPAIR))) }));
		
		Page.grabGuidePageForSkill(SkillType.REPAIR, player, args);
		
		return true;
	}
}
