package com.gmail.nossr50.party.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class PartyCommand implements CommandExecutor {
    private McMMOPlayer mcMMOPlayer;
    private Player player;

    private CommandExecutor partyJoinCommand = new PartyJoinCommand();
    private CommandExecutor partyAcceptCommand = new PartyAcceptCommand();
    private CommandExecutor partyCreateCommand = new PartyCreateCommand();
    private CommandExecutor partyQuitCommand = new PartyQuitCommand();
    private CommandExecutor partyExpShareCommand = new PartyExpShareCommand();
    private CommandExecutor partyItemShareCommand = new PartyItemShareCommand();
    private CommandExecutor partyInviteCommand = new PartyInviteCommand();
    private CommandExecutor partyKickCommand = new PartyKickCommand();
    private CommandExecutor partyDisbandCommand = new PartyDisbandCommand();
    private CommandExecutor partyChangeOwnerCommand = new PartyChangeOwnerCommand();
    private CommandExecutor partyLockCommand = new PartyLockCommand();
    private CommandExecutor partyChangePasswordCommand = new PartyChangePasswordCommand();
    private CommandExecutor partyRenameCommand = new PartyRenameCommand();
    private CommandExecutor partyInfoCommand = new PartyInfoCommand();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (!Permissions.hasPermission(sender, "mcmmo.commands.party")) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        player = (Player) sender;
        mcMMOPlayer = Users.getPlayer(player);

        if (args.length < 1 || args[0].equalsIgnoreCase("info")) {
            return partyInfoCommand.onCommand(sender, command, label, args);
        }
        else if (args[0].equalsIgnoreCase("join")) {
            return partyJoinCommand.onCommand(sender, command, label, args);
        }
        else if (args[0].equalsIgnoreCase("accept")) {
            return partyAcceptCommand.onCommand(sender, command, label, args);
        }
        else if (args[0].equalsIgnoreCase("create")) {
            return partyCreateCommand.onCommand(sender, command, label, args);
        }
        else if (args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")) {
            return printHelp();
        }

        if (!mcMMOPlayer.inParty()) {
            sender.sendMessage(LocaleLoader.getString("Commands.Party.None"));
            return true;
        }

        if (args[0].equalsIgnoreCase("quit") || args[0].equalsIgnoreCase("q") || args[0].equalsIgnoreCase("leave")) {
            return partyQuitCommand.onCommand(sender, command, label, args);
        }
        else if (args[0].equalsIgnoreCase("expshare") || args[0].equalsIgnoreCase("xpshare") || args[0].equalsIgnoreCase("sharexp") || args[0].equalsIgnoreCase("shareexp")) {
            return partyExpShareCommand.onCommand(sender, command, label, args);
        }
        else if (args[0].equalsIgnoreCase("itemshare") || args[0].equalsIgnoreCase("shareitem") || args[0].equalsIgnoreCase("shareitems")) {
            return partyItemShareCommand.onCommand(sender, command, label, args);
        }
        else if (args[0].equalsIgnoreCase("invite")) {
            return partyInviteCommand.onCommand(sender, command, label, args);
        }

        if (!mcMMOPlayer.getParty().getLeader().equals(player.getName())) {
            sender.sendMessage(LocaleLoader.getString("Party.NotOwner"));
            return true;
        }

        if (args[0].equalsIgnoreCase("kick")) {
            return partyKickCommand.onCommand(sender, command, label, args);
        }
        else if (args[0].equalsIgnoreCase("disband")) {
            return partyDisbandCommand.onCommand(sender, command, label, args);
        }
        else if (args[0].equalsIgnoreCase("owner")) {
            return partyChangeOwnerCommand.onCommand(sender, command, label, args);
        }
        else if (args[0].equalsIgnoreCase("lock") || args[0].equalsIgnoreCase("unlock")) {
            return partyLockCommand.onCommand(sender, command, label, args);
        }
        else if (args[0].equalsIgnoreCase("password")) {
            return partyChangePasswordCommand.onCommand(sender, command, label, args);
        }
        else if (args[0].equalsIgnoreCase("rename")) {
            return partyRenameCommand.onCommand(sender, command, label, args);
        }
        else {
            return printUsage();
        }
    }

    private boolean printUsage() {
        player.sendMessage(LocaleLoader.getString("Party.Help.0"));
        player.sendMessage(LocaleLoader.getString("Party.Help.1"));
        player.sendMessage(LocaleLoader.getString("Party.Help.2"));
        return true;
    }

    private boolean printHelp() {
        player.sendMessage(LocaleLoader.getString("Party.Help.3"));
        player.sendMessage(LocaleLoader.getString("Party.Help.1"));
        player.sendMessage(LocaleLoader.getString("Party.Help.4"));
        player.sendMessage(LocaleLoader.getString("Party.Help.5"));
        player.sendMessage(LocaleLoader.getString("Party.Help.6"));
        player.sendMessage(LocaleLoader.getString("Party.Help.7"));
        player.sendMessage(LocaleLoader.getString("Party.Help.8"));
        return true;
    }
}
