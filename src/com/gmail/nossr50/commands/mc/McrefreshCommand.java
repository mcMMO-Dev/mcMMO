package com.gmail.nossr50.commands.mc;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.mcLocale;

public class McrefreshCommand implements CommandExecutor {
	private final mcMMO plugin;

	public McrefreshCommand(mcMMO instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!LoadProperties.mcrefreshEnable) {
			sender.sendMessage("This command is not enabled.");
			return true;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage.");
			return true;
		}

		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);

		if (!mcPermissions.getInstance().mcrefresh(player)) {
			player.sendMessage(ChatColor.YELLOW + "[mcMMO] " + ChatColor.DARK_RED + mcLocale.getString("mcPlayerListener.NoPermission"));
			return true;
		}
		if (args.length >= 1 && (plugin.getServer().getPlayer(args[0]) != null)) {
			player.sendMessage("You have refreshed " + args[0] + "'s cooldowns!");
			player = plugin.getServer().getPlayer(args[0]);
		}

		/*
		 * PREP MODES
		 */
		PP = Users.getProfile(player);
		PP.setRecentlyHurt((long) 0);
		PP.setHoePreparationMode(false);
		PP.setAxePreparationMode(false);
		PP.setFistsPreparationMode(false);
		PP.setSwordsPreparationMode(false);
		PP.setPickaxePreparationMode(false);
		/*
		 * GREEN TERRA
		 */
		PP.setGreenTerraMode(false);
		PP.setGreenTerraDeactivatedTimeStamp((long) 0);

		/*
		 * GIGA DRILL BREAKER
		 */
		PP.setGigaDrillBreakerMode(false);
		PP.setGigaDrillBreakerDeactivatedTimeStamp((long) 0);
		/*
		 * SERRATED STRIKE
		 */
		PP.setSerratedStrikesMode(false);
		PP.setSerratedStrikesDeactivatedTimeStamp((long) 0);
		/*
		 * SUPER BREAKER
		 */
		PP.setSuperBreakerMode(false);
		PP.setSuperBreakerDeactivatedTimeStamp((long) 0);
		/*
		 * TREE FELLER
		 */
		PP.setTreeFellerMode(false);
		PP.setTreeFellerDeactivatedTimeStamp((long) 0);
		/*
		 * BERSERK
		 */
		PP.setBerserkMode(false);
		PP.setBerserkDeactivatedTimeStamp((long) 0);

		player.sendMessage(mcLocale.getString("mcPlayerListener.AbilitiesRefreshed"));

		return true;
	}
}
