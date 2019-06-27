package com.gmail.nossr50.commands;

import com.gmail.nossr50.commands.party.PartySubcommandType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class McmmoCommand implements CommandExecutor {

    private mcMMO pluginRef;

    public McmmoCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
                if (!Permissions.mcmmoDescription(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                String description = pluginRef.getLocaleManager().getString("mcMMO.Description");
                String[] mcSplit = description.split(",");
                sender.sendMessage(mcSplit);

                if (pluginRef.getConfigManager().getConfigAds().isShowDonationInfo()) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("MOTD.Donate"));
                    sender.sendMessage(ChatColor.GOLD + " - " + ChatColor.GREEN + "nossr50@gmail.com" + ChatColor.GOLD + " Paypal");
                }

                sender.sendMessage(pluginRef.getLocaleManager().getString("MOTD.Version", pluginRef.getDescription().getVersion()));

//                mcMMO.getHolidayManager().anniversaryCheck(sender);
                return true;

            case 1:
                if (args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("commands")) {
                    if (!Permissions.mcmmoHelp(sender)) {
                        sender.sendMessage(command.getPermissionMessage());
                        return true;
                    }

                    sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.mcc.Header"));
                    displayGeneralCommands(sender);
                    displayOtherCommands(sender);
                    displayPartyCommands(sender);
                }
                return true;

            default:
                return false;
        }
    }

    private void displayGeneralCommands(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_AQUA + " /mcstats " + pluginRef.getLocaleManager().getString("Commands.Stats"));
        sender.sendMessage(ChatColor.DARK_AQUA + " /<skill>" + pluginRef.getLocaleManager().getString("Commands.SkillInfo"));
        sender.sendMessage(ChatColor.DARK_AQUA + " /mctop " + pluginRef.getLocaleManager().getString("Commands.Leaderboards"));

        if (Permissions.inspect(sender)) {
            sender.sendMessage(ChatColor.DARK_AQUA + " /inspect " + pluginRef.getLocaleManager().getString("Commands.Inspect"));
        }

        if (Permissions.mcability(sender)) {
            sender.sendMessage(ChatColor.DARK_AQUA + " /mcability " + pluginRef.getLocaleManager().getString("Commands.ToggleAbility"));
        }
    }

    private void displayOtherCommands(CommandSender sender) {
        //Don't show them this category if they have none of the permissions
        if (!Permissions.skillreset(sender) && !Permissions.mmoedit(sender) && !Permissions.adminChat(sender) && !Permissions.mcgod(sender))
            return;

        sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Other"));

        if (Permissions.skillreset(sender)) {
            sender.sendMessage(ChatColor.DARK_AQUA + " /skillreset <skill|all> " + pluginRef.getLocaleManager().getString("Commands.Reset"));
        }

        if (Permissions.mmoedit(sender)) {
            sender.sendMessage(ChatColor.DARK_AQUA + " /mmoedit " + pluginRef.getLocaleManager().getString("Commands.mmoedit"));
        }

        if (Permissions.adminChat(sender)) {
            sender.sendMessage(ChatColor.DARK_AQUA + " /adminchat " + pluginRef.getLocaleManager().getString("Commands.AdminToggle"));
        }

        if (Permissions.mcgod(sender)) {
            sender.sendMessage(ChatColor.DARK_AQUA + " /mcgod " + pluginRef.getLocaleManager().getString("Commands.mcgod"));
        }
    }

    private void displayPartyCommands(CommandSender sender) {
        if (Permissions.party(sender)) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.Commands"));
            sender.sendMessage(ChatColor.DARK_AQUA + " /party create <" + pluginRef.getLocaleManager().getString("Commands.Usage.PartyName") + "> " + pluginRef.getLocaleManager().getString("Commands.Party1"));
            sender.sendMessage(ChatColor.DARK_AQUA + " /party join <" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + "> " + pluginRef.getLocaleManager().getString("Commands.Party2"));
            sender.sendMessage(ChatColor.DARK_AQUA + " /party quit " + pluginRef.getLocaleManager().getString("Commands.Party.Quit"));

            if (Permissions.partyChat(sender)) {
                sender.sendMessage(ChatColor.DARK_AQUA + " /party chat " + pluginRef.getLocaleManager().getString("Commands.Party.Toggle"));
            }

            sender.sendMessage(ChatColor.DARK_AQUA + " /party invite <" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + "> " + pluginRef.getLocaleManager().getString("Commands.Party.Invite"));
            sender.sendMessage(ChatColor.DARK_AQUA + " /party accept " + pluginRef.getLocaleManager().getString("Commands.Party.Accept"));

            if (Permissions.partySubcommand(sender, PartySubcommandType.TELEPORT)) {
                sender.sendMessage(ChatColor.DARK_AQUA + " /party teleport <" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + "> " + pluginRef.getLocaleManager().getString("Commands.Party.Teleport"));
            }
        }
    }
}
