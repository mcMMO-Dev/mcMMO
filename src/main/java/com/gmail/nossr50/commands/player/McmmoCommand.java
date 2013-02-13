package com.gmail.nossr50.commands.player;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;

public class McmmoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        switch (args.length) {
        case 0:
            String description = LocaleLoader.getString("mcMMO.Description");
            String[] mcSplit = description.split(",");
            sender.sendMessage(mcSplit);

            if (Config.getInstance().getDonateMessageEnabled()) {
                if (mcMMO.spoutEnabled && sender instanceof SpoutPlayer) {
                    SpoutPlayer spoutPlayer = (SpoutPlayer) sender;
                    spoutPlayer.sendNotification(LocaleLoader.getString("Spout.Donate"), ChatColor.GREEN + "gjmcferrin@gmail.com", Material.DIAMOND);
                }

                sender.sendMessage(LocaleLoader.getString("MOTD.Donate"));
                sender.sendMessage(ChatColor.GOLD + " - " + ChatColor.GREEN + "gjmcferrin@gmail.com" + ChatColor.GOLD + " Paypal");
            }

            sender.sendMessage(LocaleLoader.getString("MOTD.Version", mcMMO.p.getDescription().getVersion()));
            return true;

        case 1:
            if (args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("commands")) {
                if (!sender.hasPermission("mcmmo.commands.mcmmo.help")) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                sender.sendMessage(LocaleLoader.getString("Commands.mcc.Header"));
                displayPartyCommands(sender);
                displayOtherCommands(sender);

            }
            return true;

        default:
            return false;
        }
    }

    private void displayPartyCommands(CommandSender sender) {
        if (sender.hasPermission("mcmmo.commands.party")) {
            sender.sendMessage(LocaleLoader.getString("Commands.Party.Commands"));
            sender.sendMessage("/party create <" + LocaleLoader.getString("Commands.Usage.PartyName") + "> " + LocaleLoader.getString("Commands.Party1"));
            sender.sendMessage("/party join <" + LocaleLoader.getString("Commands.Usage.Player") + "> " + LocaleLoader.getString("Commands.Party2"));
            sender.sendMessage("/party quit " + LocaleLoader.getString("Commands.Party.Quit"));

            if (sender.hasPermission("mcmmo.chat.party")) {
                sender.sendMessage("/party chat " + LocaleLoader.getString("Commands.Party.Toggle"));
            }

            sender.sendMessage("/party invite <" + LocaleLoader.getString("Commands.Usage.Player") + "> " + LocaleLoader.getString("Commands.Party.Invite"));
            sender.sendMessage("/party accept " + LocaleLoader.getString("Commands.Party.Accept"));

            if (sender.hasPermission("mcmmo.commands.ptp")) {
                sender.sendMessage("/party teleport " + LocaleLoader.getString("Commands.Party.Teleport"));
            }
        }
    }

    private void displayOtherCommands(CommandSender sender) {
        sender.sendMessage(LocaleLoader.getString("Commands.Other"));
        sender.sendMessage("/mcstats " + LocaleLoader.getString("Commands.Stats"));
        sender.sendMessage("/mctop " + LocaleLoader.getString("Commands.Leaderboards"));

        if (sender.hasPermission("mcmmo.commands.skillreset")) {
            sender.sendMessage("/skillreset <skill|all> " + LocaleLoader.getString("Commands.Reset"));
        }

        if (sender.hasPermission("mcmmo.commands.mcability")) {
            sender.sendMessage("/mcability " + LocaleLoader.getString("Commands.ToggleAbility"));
        }

        if (sender.hasPermission("mcmmo.chat.admin")) {
            sender.sendMessage("/adminchat " + LocaleLoader.getString("Commands.AdminToggle"));
        }

        if (sender.hasPermission("mcmmo.commands.inspect")) {
            sender.sendMessage("/inspect " + LocaleLoader.getString("Commands.Inspect"));
        }

        if (sender.hasPermission("mcmmo.commands.mmoedit")) {
            sender.sendMessage("/mmoedit " + LocaleLoader.getString("Commands.mmoedit"));
        }

        if (sender.hasPermission("mcmmo.commands.mcgod")) {
            sender.sendMessage("/mcgod " + LocaleLoader.getString("Commands.mcgod"));
        }

        sender.sendMessage(LocaleLoader.getString("Commands.SkillInfo"));
    }
}
