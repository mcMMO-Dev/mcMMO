package com.gmail.nossr50.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.party.PartySubcommandType;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;

public class McmmoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
                if (!Permissions.mcmmoDescription(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

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
                    if (!Permissions.mcmmoHelp(sender)) {
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
        if (Permissions.party(sender)) {
            sender.sendMessage(LocaleLoader.getString("Commands.Party.Commands"));
            sender.sendMessage("/party create <" + LocaleLoader.getString("Commands.Usage.PartyName") + "> " + LocaleLoader.getString("Commands.Party1"));
            sender.sendMessage("/party join <" + LocaleLoader.getString("Commands.Usage.Player") + "> " + LocaleLoader.getString("Commands.Party2"));
            sender.sendMessage("/party quit " + LocaleLoader.getString("Commands.Party.Quit"));

            if (Permissions.partyChat(sender)) {
                sender.sendMessage("/party chat " + LocaleLoader.getString("Commands.Party.Toggle"));
            }

            sender.sendMessage("/party invite <" + LocaleLoader.getString("Commands.Usage.Player") + "> " + LocaleLoader.getString("Commands.Party.Invite"));
            sender.sendMessage("/party accept " + LocaleLoader.getString("Commands.Party.Accept"));

            if (Permissions.partySubcommand(sender, PartySubcommandType.TELEPORT)) {
                sender.sendMessage("/party teleport " + LocaleLoader.getString("Commands.Party.Teleport"));
            }
        }
    }

    private void displayOtherCommands(CommandSender sender) {
        sender.sendMessage(LocaleLoader.getString("Commands.Other"));
        sender.sendMessage("/mcstats " + LocaleLoader.getString("Commands.Stats"));
        sender.sendMessage("/mctop " + LocaleLoader.getString("Commands.Leaderboards"));

        if (Permissions.skillreset(sender)) {
            sender.sendMessage("/skillreset <skill|all> " + LocaleLoader.getString("Commands.Reset"));
        }

        if (Permissions.mcability(sender)) {
            sender.sendMessage("/mcability " + LocaleLoader.getString("Commands.ToggleAbility"));
        }

        if (Permissions.adminChat(sender)) {
            sender.sendMessage("/adminchat " + LocaleLoader.getString("Commands.AdminToggle"));
        }

        if (Permissions.inspect(sender)) {
            sender.sendMessage("/inspect " + LocaleLoader.getString("Commands.Inspect"));
        }

        if (Permissions.mmoedit(sender)) {
            sender.sendMessage("/mmoedit " + LocaleLoader.getString("Commands.mmoedit"));
        }

        if (Permissions.mcgod(sender)) {
            sender.sendMessage("/mcgod " + LocaleLoader.getString("Commands.mcgod"));
        }

        sender.sendMessage(LocaleLoader.getString("Commands.SkillInfo"));
    }
}
