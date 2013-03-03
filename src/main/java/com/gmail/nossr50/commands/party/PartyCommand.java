package com.gmail.nossr50.commands.party;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.commands.chat.PartyChatCommand;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;

public class PartyCommand implements CommandExecutor {
    private McMMOPlayer mcMMOPlayer;
    private Player player;

    private CommandExecutor partyJoinCommand           = new PartyJoinCommand();
    private CommandExecutor partyAcceptCommand         = new PartyAcceptCommand();
    private CommandExecutor partyCreateCommand         = new PartyCreateCommand();
    private CommandExecutor partyQuitCommand           = new PartyQuitCommand();
    private CommandExecutor partyExpShareCommand       = new PartyExpShareCommand();
    private CommandExecutor partyItemShareCommand      = new PartyItemShareCommand();
    private CommandExecutor partyInviteCommand         = new PartyInviteCommand();
    private CommandExecutor partyKickCommand           = new PartyKickCommand();
    private CommandExecutor partyDisbandCommand        = new PartyDisbandCommand();
    private CommandExecutor partyChangeOwnerCommand    = new PartyChangeOwnerCommand();
    private CommandExecutor partyLockCommand           = new PartyLockCommand();
    private CommandExecutor partyChangePasswordCommand = new PartyChangePasswordCommand();
    private CommandExecutor partyRenameCommand         = new PartyRenameCommand();
    private CommandExecutor partyInfoCommand           = new PartyInfoCommand();
    private CommandExecutor partyHelpCommand           = new PartyHelpCommand();
    private CommandExecutor partyTeleportCommand       = new PtpCommand();
    private CommandExecutor partyChatCommand           = new PartyChatCommand();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        if (!Permissions.party(sender)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        player = (Player) sender;
        mcMMOPlayer = UserManager.getPlayer(player);

        if (args.length < 1) {
            if (!mcMMOPlayer.inParty()) {
                sender.sendMessage(LocaleLoader.getString("Commands.Party.None"));
                return printUsage();
            }

            return partyInfoCommand.onCommand(sender, command, label, args);
        }

        PartySubcommandType subcommand = PartySubcommandType.getSubcommand(args[0]);

        if (subcommand == null) {
            return printUsage();
        }

        // Can't use this for lock/unlock since they're handled by the same command
        if (subcommand != PartySubcommandType.LOCK && subcommand != PartySubcommandType.UNLOCK && !Permissions.partySubcommand(sender, subcommand)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        switch (subcommand) {
            case JOIN:
                return partyJoinCommand.onCommand(sender, command, label, args);
            case ACCEPT:
                return partyAcceptCommand.onCommand(sender, command, label, args);
            case CREATE:
                return partyCreateCommand.onCommand(sender, command, label, args);
            case HELP:
                return partyHelpCommand.onCommand(sender, command, label, args);
            default:
                break;
        }

        // Party member commands
        if (!mcMMOPlayer.inParty()) {
            sender.sendMessage(LocaleLoader.getString("Commands.Party.None"));
            return printUsage();
        }

        switch (subcommand) {
            case INFO:
                return partyInfoCommand.onCommand(sender, command, label, args);
            case QUIT:
                return partyQuitCommand.onCommand(sender, command, label, args);
            case INVITE:
                return partyInviteCommand.onCommand(sender, command, label, args);
            case TELEPORT:
                return partyTeleportCommand.onCommand(sender, command, label, extractArgs(args));
            case CHAT:
                return partyChatCommand.onCommand(sender, command, label, extractArgs(args));
            default:
                break;
        }

        // Party leader commands
        if (!mcMMOPlayer.getParty().getLeader().equals(player.getName())) {
            sender.sendMessage(LocaleLoader.getString("Party.NotOwner"));
            return true;
        }

        switch (subcommand) {
            case EXPSHARE:
                return partyExpShareCommand.onCommand(sender, command, label, args);
            case ITEMSHARE:
                return partyItemShareCommand.onCommand(sender, command, label, args);
            case KICK:
                return partyKickCommand.onCommand(sender, command, label, args);
            case DISBAND:
                return partyDisbandCommand.onCommand(sender, command, label, args);
            case OWNER:
                return partyChangeOwnerCommand.onCommand(sender, command, label, args);
            case LOCK:
                // Fallthrough
            case UNLOCK:
                return partyLockCommand.onCommand(sender, command, label, args);
            case PASSWORD:
                return partyChangePasswordCommand.onCommand(sender, command, label, args);
            case RENAME:
                return partyRenameCommand.onCommand(sender, command, label, args);
            default:
                break;
        }

        return true;
    }

    private boolean printUsage() {
        player.sendMessage(LocaleLoader.getString("Party.Help.0", "/party join"));
        player.sendMessage(LocaleLoader.getString("Party.Help.1", "/party create"));
        player.sendMessage(LocaleLoader.getString("Party.Help.2", "/party ?"));
        return true;
    }

    private String[] extractArgs(String[] args) {
        String[] newArgs = new String[args.length - 1];

        for (int i = 1; i < args.length; i++) {
            newArgs[i - 1] = args[i];
        }

        return newArgs;
    }
}
