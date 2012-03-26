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
import com.gmail.nossr50.skills.Fishing;
import com.gmail.nossr50.util.Page;

public class FishingCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage."); //TODO: Needs more locale.
			return true;
		}

		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);

		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.SkillFishing") }));
		player.sendMessage(mcLocale.getString("m.XPGain", new Object[] { mcLocale.getString("m.XPGainFishing") }));

		if (mcPermissions.getInstance().fishing(player))
			player.sendMessage(mcLocale.getString("m.LVL", new Object[] { PP.getSkillLevel(SkillType.FISHING), PP.getSkillXpLevel(SkillType.FISHING), PP.getXpToLevel(SkillType.FISHING) }));

		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.Effects") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsFishing1_0"), mcLocale.getString("m.EffectsFishing1_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsFishing2_0"), mcLocale.getString("m.EffectsFishing2_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsFishing3_0"), mcLocale.getString("m.EffectsFishing3_1") }));
		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.YourStats") }));
		player.sendMessage(mcLocale.getString("m.FishingRank", new Object[] { Fishing.getFishingLootTier(PP) }));
		player.sendMessage(mcLocale.getString("m.FishingMagicInfo"));

		if (PP.getSkillLevel(SkillType.FISHING) < 150)
			player.sendMessage(mcLocale.getString("m.AbilityLockTemplate", new Object[] { mcLocale.getString("m.AbilLockFishing1") }));
		else
			player.sendMessage(mcLocale.getString("m.ShakeInfo", new Object[] { Fishing.getFishingLootTier(PP) }));
		
		Page.grabGuidePageForSkill(SkillType.FISHING, player, args);

		return true;
	}
}
