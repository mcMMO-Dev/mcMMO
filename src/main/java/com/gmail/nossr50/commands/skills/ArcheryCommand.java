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

public class ArcheryCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage."); //TODO: Needs more locale.
			return true;
		}

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.skills.archery")) {
            return true;
        }
		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);

		float skillvalue = (float) PP.getSkillLevel(SkillType.ARCHERY);
		String percentage = String.valueOf((skillvalue / 1000) * 100);
		
		double dmgBonusPercent = ((PP.getSkillLevel(SkillType.ARCHERY) / 50) * 0.1D);
        
        /* Cap maximum bonus at 200% */
        if(dmgBonusPercent > 2)
            dmgBonusPercent = 2;
        
        dmgBonusPercent = dmgBonusPercent * 100; //Convert to percentage

		String percentagedaze;
		if (PP.getSkillLevel(SkillType.ARCHERY) < 1000)
			percentagedaze = String.valueOf((skillvalue / 2000) * 100);
		else
			percentagedaze = "50";

		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.SkillArchery") }));
		player.sendMessage(mcLocale.getString("m.XPGain", new Object[] { mcLocale.getString("m.XPGainArchery") }));

		if (mcPermissions.getInstance().archery(player))
			player.sendMessage(mcLocale.getString("m.LVL", new Object[] { PP.getSkillLevel(SkillType.ARCHERY), PP.getSkillXpLevel(SkillType.ARCHERY), PP.getXpToLevel(SkillType.ARCHERY) }));

		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.Effects") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsArchery1_0"), mcLocale.getString("m.EffectsArchery1_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsArchery2_0"), mcLocale.getString("m.EffectsArchery2_1") }));
		player.sendMessage(mcLocale.getString("m.EffectsTemplate", new Object[] { mcLocale.getString("m.EffectsArchery4_0"), mcLocale.getString("m.EffectsArchery4_1") }));
		player.sendMessage(mcLocale.getString("m.SkillHeader", new Object[] { mcLocale.getString("m.YourStats") }));
		player.sendMessage(mcLocale.getString("m.ArcherySkillShot", new Object[] { dmgBonusPercent }));
		player.sendMessage(mcLocale.getString("m.ArcheryDazeChance", new Object[] { percentagedaze }));
		player.sendMessage(mcLocale.getString("m.ArcheryRetrieveChance", new Object[] { percentage }));
		
		Page.grabGuidePageForSkill(SkillType.ARCHERY, player, args);

		return true;
	}
}
