package com.gmail.nossr50.commands.party;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;

public class PartyCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!LoadProperties.partyEnable) {
			sender.sendMessage("This command is not enabled.");
			return true;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage.");
			return true;
		}

		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);

		if (!mcPermissions.getInstance().party(player)) {
			player.sendMessage(ChatColor.YELLOW + "[mcMMO] " + ChatColor.DARK_RED + mcLocale.getString("mcPlayerListener.NoPermission"));
			return true;
		}

		Party Pinstance = Party.getInstance();

		if (PP.inParty() && (!Pinstance.isParty(PP.getParty()) || !Pinstance.isInParty(player, PP))) {
			Pinstance.addToParty(player, PP, PP.getParty(), false);
		}

		if (args.length == 0 && !PP.inParty()) {
			player.sendMessage(mcLocale.getString("Party.Help1", new Object[] { "/party "}));
			player.sendMessage(mcLocale.getString("Party.Help2", new Object[] { "/party "}));
			player.sendMessage(mcLocale.getString("Party.Help3", new Object[] { "/party " }));
			return true;
		} else if (args.length == 0 && PP.inParty()) {
			String tempList = "";
			int x = 0;
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				if (PP.getParty().equals(Users.getProfile(p).getParty())) {
					if (p != null && x + 1 >= Pinstance.partyCount(player, Bukkit.getServer().getOnlinePlayers())) {
						if (Pinstance.isPartyLeader(p.getName(), PP.getParty())) {
							tempList += ChatColor.GOLD + p.getName();
							x++;
						} else {
							tempList += ChatColor.WHITE + p.getName();
							x++;
						}
					}
					if (p != null && x < Pinstance.partyCount(player, Bukkit.getServer().getOnlinePlayers())) {
						if (Pinstance.isPartyLeader(p.getName(), PP.getParty())) {
							tempList += ChatColor.GOLD + p.getName() + ", ";
							x++;
						} else {
							tempList += ChatColor.WHITE + p.getName() + ", ";
							x++;
						}
					}
				}
			}
			player.sendMessage(mcLocale.getString("mcPlayerListener.YouAreInParty", new Object[] { PP.getParty() }));
			player.sendMessage(mcLocale.getString("mcPlayerListener.PartyMembers") + " (" + tempList + ChatColor.GREEN + ")");
			return true;
		} else if (args.length == 1) {
			if (args[0].equals("q") && PP.inParty()) {
				Pinstance.removeFromParty(player, PP);

				player.sendMessage(mcLocale.getString("mcPlayerListener.LeftParty"));
				return true;
			} else if (args[0].equalsIgnoreCase("?")) {
				player.sendMessage(mcLocale.getString("Party.Help4", new Object[] { "/party " }));
				player.sendMessage(mcLocale.getString("Party.Help2", new Object[] { "/party " }));
				player.sendMessage(mcLocale.getString("Party.Help5", new Object[] { "/party " }));
				player.sendMessage(mcLocale.getString("Party.Help6", new Object[] { "/party " }));
				player.sendMessage(mcLocale.getString("Party.Help7", new Object[] { "/party " }));
				player.sendMessage(mcLocale.getString("Party.Help8", new Object[] { "/party " }));
				player.sendMessage(mcLocale.getString("Party.Help9", new Object[] { "/party " }));
			} else if (args[0].equalsIgnoreCase("lock")) {
				if (PP.inParty()) {
					if (Pinstance.isPartyLeader(player.getName(), PP.getParty())) {
						Pinstance.lockParty(PP.getParty());
						player.sendMessage(mcLocale.getString("Party.Locked"));
					} else {
						player.sendMessage(mcLocale.getString("Party.NotOwner"));
					}
				} else {
					player.sendMessage(mcLocale.getString("Party.InvalidName"));
				}
			} else if (args[0].equalsIgnoreCase("unlock")) {
				if (PP.inParty()) {
					if (Pinstance.isPartyLeader(player.getName(), PP.getParty())) {
						Pinstance.unlockParty(PP.getParty());
						player.sendMessage(mcLocale.getString("Party.Unlocked"));
					} else {
						player.sendMessage(mcLocale.getString("Party.NotOwner"));
					}
				} else {
					player.sendMessage(mcLocale.getString("Party.InvalidName"));
				}
				// Party debugging command.
				// } else if (args[0].equalsIgnoreCase("dump")) {
				// Pinstance.dump(player);
			} else {
				if (PP.inParty()) {
					Pinstance.removeFromParty(player, PP);
				}
				Pinstance.addToParty(player, PP, args[0], false);
				return true;
			}
		} else if (args.length == 2 && PP.inParty()) {
			if (args[0].equalsIgnoreCase("password")) {
				if (Pinstance.isPartyLeader(player.getName(), PP.getParty())) {
					if (Pinstance.isPartyLocked(PP.getParty())) {
						Pinstance.setPartyPassword(PP.getParty(), args[1]);
						player.sendMessage(mcLocale.getString("Party.PasswordSet", new Object[] { args[1] }));
					} else {
						player.sendMessage(mcLocale.getString("Party.IsntLocked"));
					}
				} else {
					player.sendMessage(mcLocale.getString("Party.NotOwner"));
				}
			} else if (args[0].equalsIgnoreCase("kick")) {
				if (Pinstance.isPartyLeader(player.getName(), PP.getParty())) {
					if (Pinstance.isPartyLocked(PP.getParty())) {
						Player tPlayer = null;
						if (Bukkit.getServer().getPlayer(args[1]) != null)
							tPlayer = Bukkit.getServer().getPlayer(args[1]);
						if (tPlayer == null) {
							player.sendMessage(mcLocale.getString("Party.CouldNotKick", new Object[] { args[1] }));
						}
						if (!Pinstance.inSameParty(player, tPlayer)) {
							player.sendMessage(mcLocale.getString("Party.NotInYourParty", new Object[] { tPlayer.getName() }));
						} else {
							// Not an admin
							if (!mcPermissions.getInstance().admin(player)) {
								// Can't kick an admin
								if (mcPermissions.getInstance().admin(tPlayer)) {
									player.sendMessage(mcLocale.getString("Party.CouldNotKick", new Object[] { tPlayer.getName() }));
								}
							}
							PlayerProfile tPP = Users.getProfile(tPlayer);

							Pinstance.removeFromParty(tPlayer, tPP);

							tPlayer.sendMessage(mcLocale.getString("mcPlayerListener.LeftParty"));
						}
					} else {
						player.sendMessage(mcLocale.getString("Party.IsntLocked"));
					}
				} else {
					player.sendMessage(mcLocale.getString("Party.NotOwner"));
				}
			} else if (args[0].equalsIgnoreCase("owner")) {
				if (Pinstance.isPartyLeader(player.getName(), PP.getParty())) {
					Player tPlayer = null;
					if (Bukkit.getServer().getPlayer(args[1]) != null)
						tPlayer = Bukkit.getServer().getPlayer(args[1]);
					if (tPlayer == null) {
						player.sendMessage(mcLocale.getString("Party.CouldNotSetOwner", new Object[] { args[1] }));
					}
					if (!Pinstance.inSameParty(player, tPlayer)) {
						player.sendMessage(mcLocale.getString("Party.CouldNotSetOwner", new Object[] { tPlayer.getName() }));
					} else {
						Pinstance.setPartyLeader(PP.getParty(), tPlayer.getName());
					}
				} else {
					player.sendMessage(mcLocale.getString("Party.NotOwner"));
				}
			} else {
				Pinstance.removeFromParty(player, PP);
				Pinstance.addToParty(player, PP, args[0], false, args[1]);
			}
		} else if (args.length == 2 && !PP.inParty()) {
			Pinstance.addToParty(player, PP, args[0], false, args[1]);
		}

		return true;
	}
}
