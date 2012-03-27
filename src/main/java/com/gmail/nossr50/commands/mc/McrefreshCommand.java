package com.gmail.nossr50.commands.mc;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.ToolType;
import com.gmail.nossr50.locale.mcLocale;

public class McrefreshCommand implements CommandExecutor {
	private final mcMMO plugin;

	public McrefreshCommand(mcMMO instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage."); //TODO: Needs more locale.
			return true;
		}

		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);

		if (!mcPermissions.getInstance().mcrefresh(player)) {
			player.sendMessage(ChatColor.YELLOW + "[mcMMO] " + ChatColor.DARK_RED + mcLocale.getString("mcPlayerListener.NoPermission"));
			return true;
		}
		if (args.length >= 1 && (plugin.getServer().getPlayer(args[0]) != null)) {
			player.sendMessage("You have refreshed " + args[0] + "'s cooldowns!"); //TODO: Needs more locale.
			player = plugin.getServer().getPlayer(args[0]);
		}

		/*
		 * PREP MODES
		 */
		PP = Users.getProfile(player);
		PP.setRecentlyHurt((long) 0);
		PP.setToolPreparationMode(ToolType.AXE, false);
		PP.setToolPreparationMode(ToolType.FISTS, false);
		PP.setToolPreparationMode(ToolType.HOE, false);
		PP.setToolPreparationMode(ToolType.PICKAXE, false);
		PP.setToolPreparationMode(ToolType.SWORD, false);

		//RESET COOLDOWNS
		PP.resetCooldowns();
		PP.setAbilityMode(AbilityType.GREEN_TERRA, false);
		PP.setAbilityMode(AbilityType.GIGA_DRILL_BREAKER, false);
		PP.setAbilityMode(AbilityType.SERRATED_STRIKES, false);
		PP.setAbilityMode(AbilityType.SUPER_BREAKER, false);
		PP.setAbilityMode(AbilityType.TREE_FELLER, false);
		PP.setAbilityMode(AbilityType.BERSERK, false);

		player.sendMessage(mcLocale.getString("mcPlayerListener.AbilitiesRefreshed"));

		return true;
	}
}
