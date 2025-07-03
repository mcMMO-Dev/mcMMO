package com.gmail.nossr50.commands;

import com.gmail.nossr50.commands.party.PartySubcommandType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class McmmoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, String[] args) {
        switch (args.length) {
            case 0:
                if (!Permissions.mcmmoDescription(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                String description = LocaleLoader.getString("mcMMO.Description");
                String[] mcSplit = description.split(",");
                sender.sendMessage(mcSplit);
                sender.sendMessage(LocaleLoader.getString("mcMMO.Description.FormerDevs"));

                if (mcMMO.p.getGeneralConfig().getDonateMessageEnabled()) {
                    sender.sendMessage(LocaleLoader.getString("MOTD.Donate"));
                    sender.sendMessage(
                            ChatColor.GOLD + " - " + ChatColor.GREEN + "nossr50@gmail.com"
                                    + ChatColor.GOLD + " Paypal");
                }

                if (Permissions.showversion(sender)) {
                    sender.sendMessage(LocaleLoader.getString("MOTD.Version",
                            mcMMO.p.getDescription().getVersion()));
                }

//                mcMMO.getHolidayManager().anniversaryCheck(sender);
                return true;

            case 1:
                if (args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")
                        || args[0].equalsIgnoreCase("commands")) {
                    if (!Permissions.mcmmoHelp(sender)) {
                        sender.sendMessage(command.getPermissionMessage());
                        return true;
                    }

                    sender.sendMessage(LocaleLoader.getString("Commands.mcc.Header"));
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
        sender.sendMessage(LocaleLoader.getString("Commands.Stats"));
        sender.sendMessage(LocaleLoader.getString("Commands.SkillInfo"));
        sender.sendMessage(LocaleLoader.getString("Commands.Leaderboards"));

        if (Permissions.inspect(sender)) {
            sender.sendMessage(LocaleLoader.getString("Commands.Inspect"));
        }

        if (Permissions.mcability(sender)) {
            sender.sendMessage(LocaleLoader.getString("Commands.ToggleAbility"));
        }
    }

    private void displayOtherCommands(CommandSender sender) {
        //Don't show them this category if they have none of the permissions
        if (!Permissions.skillreset(sender) && !Permissions.mmoedit(sender)
                && !Permissions.adminChat(sender) && !Permissions.mcgod(sender)) {
            return;
        }

        sender.sendMessage(LocaleLoader.getString("Commands.Other"));

        if (Permissions.skillreset(sender)) {
            sender.sendMessage(LocaleLoader.getString("Commands.Reset"));
        }

        if (Permissions.mmoedit(sender)) {
            sender.sendMessage(LocaleLoader.getString("Commands.mmoedit"));
        }

        if (Permissions.adminChat(sender)) {
            sender.sendMessage(LocaleLoader.getString("Commands.AdminToggle"));
        }

        if (Permissions.mcgod(sender)) {
            sender.sendMessage(LocaleLoader.getString("Commands.mcgod"));
        }
    }

    private void displayPartyCommands(CommandSender sender) {
        if (Permissions.party(sender)) {
            sender.sendMessage(LocaleLoader.getString("Commands.Party.Commands"));
            sender.sendMessage(LocaleLoader.getString("Commands.Party1"));
            sender.sendMessage(LocaleLoader.getString("Commands.Party2"));
            sender.sendMessage(LocaleLoader.getString("Commands.Party.Quit"));

            if (Permissions.partyChat(sender)) {
                sender.sendMessage(LocaleLoader.getString("Commands.Party.Toggle"));
            }

            sender.sendMessage(LocaleLoader.getString("Commands.Party.Invite"));
            sender.sendMessage(LocaleLoader.getString("Commands.Party.Accept"));

            if (Permissions.partySubcommand(sender, PartySubcommandType.TELEPORT)) {
                sender.sendMessage(LocaleLoader.getString("Commands.Party.Teleport"));
            }
        }
    }
}
