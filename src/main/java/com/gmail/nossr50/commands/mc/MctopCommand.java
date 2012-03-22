package com.gmail.nossr50.commands.mc;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.gmail.nossr50.Leaderboard;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.skills.Skills;

public class MctopCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (LoadProperties.useMySQL == false) {
			/*
			 * POWER LEVEL INFO RETRIEVAL
			 */
			if (args.length == 0) {
				int p = 1;
				String[] info = Leaderboard.retrieveInfo(SkillType.ALL.toString(), p);
				sender.sendMessage(mcLocale.getString("mcPlayerListener.PowerLevelLeaderboard"));
				int n = 1 * p; // Position
				for (String x : info) {
					if (x != null) {
						String digit = String.valueOf(n);
						if (n < 10)
							digit = "0" + String.valueOf(n);
						String[] splitx = x.split(":");
						// Format: 1. Playername - skill value
						sender.sendMessage(digit + ". " + ChatColor.GREEN + splitx[1] + " - " + ChatColor.WHITE + splitx[0]);
						n++;
					}
				}
			}
			if (args.length >= 1 && m.isInt(args[0])) {
				int p = 1;
				// Grab page value if specified
				if (args.length >= 1) {
					if (m.isInt(args[0])) {
						p = Integer.valueOf(args[0]);
					}
				}
				int pt = p;
				if (p > 1) {
					pt -= 1;
					pt += (pt * 10);
					pt = 10;
				}
				String[] info = Leaderboard.retrieveInfo(SkillType.ALL.toString(), p);
				sender.sendMessage(mcLocale.getString("mcPlayerListener.PowerLevelLeaderboard"));
				int n = 1 * pt; // Position
				for (String x : info) {
					if (x != null) {
						String digit = String.valueOf(n);
						if (n < 10)
							digit = "0" + String.valueOf(n);
						String[] splitx = x.split(":");
						// Format: 1. Playername - skill value
						sender.sendMessage(digit + ". " + ChatColor.GREEN + splitx[1] + " - " + ChatColor.WHITE + splitx[0]);
						n++;
					}
				}
			}
			/*
			 * SKILL SPECIFIED INFO RETRIEVAL
			 */
			if (args.length >= 1 && Skills.isSkill(args[0])) {
				int p = 1;
				// Grab page value if specified
				if (args.length >= 2) {
					if (m.isInt(args[1])) {
						p = Integer.valueOf(args[1]);
					}
				}
				int pt = p;
				if (p > 1) {
					pt -= 1;
					pt += (pt * 10);
					pt = 10;
				}
				String firstLetter = args[0].substring(0, 1); // Get first letter
				String remainder = args[0].substring(1); // Get remainder of word.
				String capitalized = firstLetter.toUpperCase() + remainder.toLowerCase();

				String[] info = Leaderboard.retrieveInfo(args[0].toUpperCase(), p);
				sender.sendMessage(mcLocale.getString("mcPlayerListener.SkillLeaderboard", new Object[] { capitalized }));
				int n = 1 * pt; // Position
				for (String x : info) {
					if (x != null) {
						String digit = String.valueOf(n);
						if (n < 10)
							digit = "0" + String.valueOf(n);
						String[] splitx = x.split(":");
						// Format: 1. Playername - skill value
						sender.sendMessage(digit + ". " + ChatColor.GREEN + splitx[1] + " - " + ChatColor.WHITE + splitx[0]);
						n++;
					}
				}
			}
		} else {
			/*
			 * MYSQL LEADERBOARDS
			 */
			String powerlevel = "taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing";
			if (args.length >= 1 && Skills.isSkill(args[0])) {
				/*
				 * Create a nice consistent capitalized leaderboard name
				 */
				String lowercase = args[0].toLowerCase(); // For the query
				String firstLetter = args[0].substring(0, 1); // Get first letter
				String remainder = args[0].substring(1); // Get remainder of word.
				String capitalized = firstLetter.toUpperCase() + remainder.toLowerCase();

				sender.sendMessage(mcLocale.getString("mcPlayerListener.SkillLeaderboard", new Object[] { capitalized }));
				if (args.length >= 2 && m.isInt(args[1])) {
					int n = 1; // For the page number
					int n2 = Integer.valueOf(args[1]);
					if (n2 > 1) {
						// Figure out the 'page' here
						n = 10;
						n = n * (n2 - 1);
					}
					// If a page number is specified
					HashMap<Integer, ArrayList<String>> userslist = mcMMO.database.read("SELECT " + lowercase + ", user_id FROM " + LoadProperties.MySQLtablePrefix + "skills WHERE " + lowercase + " > 0 ORDER BY `" + LoadProperties.MySQLtablePrefix + "skills`.`" + lowercase + "` DESC ");

					for (int i = n; i <= n + 10; i++) {
						if (i > userslist.size() || mcMMO.database.read("SELECT user FROM " + LoadProperties.MySQLtablePrefix + "users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'") == null)
							break;
						HashMap<Integer, ArrayList<String>> username = mcMMO.database.read("SELECT user FROM " + LoadProperties.MySQLtablePrefix + "users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'");
						sender.sendMessage(String.valueOf(i) + ". " + ChatColor.GREEN + userslist.get(i).get(0) + " - " + ChatColor.WHITE + username.get(1).get(0));
					}
					return true;
				}
				// If no page number is specified
				HashMap<Integer, ArrayList<String>> userslist = mcMMO.database.read("SELECT " + lowercase + ", user_id FROM " + LoadProperties.MySQLtablePrefix + "skills WHERE " + lowercase + " > 0 ORDER BY `" + LoadProperties.MySQLtablePrefix + "skills`.`" + lowercase + "` DESC ");
				for (int i = 1; i <= 10; i++) { // i<=userslist.size()
					if (i > userslist.size() || mcMMO.database.read("SELECT user FROM " + LoadProperties.MySQLtablePrefix + "users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'") == null)
						break;
					HashMap<Integer, ArrayList<String>> username = mcMMO.database.read("SELECT user FROM " + LoadProperties.MySQLtablePrefix + "users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'");
					sender.sendMessage(String.valueOf(i) + ". " + ChatColor.GREEN + userslist.get(i).get(0) + " - " + ChatColor.WHITE + username.get(1).get(0));
				}
				return true;
			}
			if (args.length >= 0) {
				sender.sendMessage(mcLocale.getString("mcPlayerListener.PowerLevelLeaderboard"));
				if (args.length >= 1 && m.isInt(args[0])) {
					int n = 1; // For the page number
					int n2 = Integer.valueOf(args[0]);
					if (n2 > 1) {
						// Figure out the 'page' here
						n = 10;
						n = n * (n2 - 1);
					}
					// If a page number is specified
					HashMap<Integer, ArrayList<String>> userslist = mcMMO.database.read("SELECT " + powerlevel + ", user_id FROM " + LoadProperties.MySQLtablePrefix + "skills WHERE " + powerlevel + " > 0 ORDER BY " + powerlevel + " DESC ");
					for (int i = n; i <= n + 10; i++) {
						if (i > userslist.size() || mcMMO.database.read("SELECT user FROM " + LoadProperties.MySQLtablePrefix + "users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'") == null)
							break;
						HashMap<Integer, ArrayList<String>> username = mcMMO.database.read("SELECT user FROM " + LoadProperties.MySQLtablePrefix + "users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'");
						sender.sendMessage(String.valueOf(i) + ". " + ChatColor.GREEN + userslist.get(i).get(0) + " - " + ChatColor.WHITE + username.get(1).get(0));
					}
					return true;
				}
				HashMap<Integer, ArrayList<String>> userslist = mcMMO.database.read("SELECT " + powerlevel + ", user_id FROM " + LoadProperties.MySQLtablePrefix + "skills WHERE " + powerlevel + " > 0 ORDER BY " + powerlevel + " DESC ");
				for (int i = 1; i <= 10; i++) {
					if (i > userslist.size() || mcMMO.database.read("SELECT user FROM " + LoadProperties.MySQLtablePrefix + "users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'") == null)
						break;
					HashMap<Integer, ArrayList<String>> username = mcMMO.database.read("SELECT user FROM " + LoadProperties.MySQLtablePrefix + "users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'");
					sender.sendMessage(String.valueOf(i) + ". " + ChatColor.GREEN + userslist.get(i).get(0) + " - " + ChatColor.WHITE + username.get(1).get(0));
					// System.out.println(username.get(1).get(0));
					// System.out.println("Mining : " + userslist.get(i).get(0) + ", User id : " + userslist.get(i).get(1));
				}
			}
		}

		return true;
	}
}
