package com.gmail.nossr50.commands.party;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.chat.PartyChatCommand;
import com.gmail.nossr50.commands.party.teleport.PtpCommand;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.google.common.collect.ImmutableList;

public class PartyCommand implements TabExecutor {
    private McMMOPlayer mcMMOPlayer;
    private Player player;

    private static final List<String> PARTY_SUBCOMMANDS;
    private static final List<String> EXPSHARE_COMPLETIONS = ImmutableList.of("none", "equal");
    private static final List<String> ITEMSHARE_COMPLETIONS = ImmutableList.of("none", "equal", "random", "loot", "mining", "herbalism", "woodcutting", "misc");

    static {
        ArrayList<String> subcommands = new ArrayList<String>();

        for (PartySubcommandType subcommand : PartySubcommandType.values()) {
            subcommands.add(subcommand.toString());
        }

        Collections.sort(subcommands);
        PARTY_SUBCOMMANDS = ImmutableList.copyOf(subcommands);
    }

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
    private CommandExecutor partyTeleportCommand       = mcMMO.p.getCommand("ptp").getExecutor();
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

        mcMMOPlayer = UserManager.getPlayer(sender.getName());
        player = mcMMOPlayer.getPlayer();

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
        if (!mcMMOPlayer.getParty().getLeader().equalsIgnoreCase(player.getName())) {
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return StringUtil.copyPartialMatches(args[0], PARTY_SUBCOMMANDS, new ArrayList<String>(PARTY_SUBCOMMANDS.size()));
            case 2:
                PartySubcommandType subcommand = PartySubcommandType.getSubcommand(args[0]);

                if (subcommand == null) {
                    return ImmutableList.of();
                }

                switch (subcommand) {
                    case JOIN:
                    case INVITE:
                    case KICK:
                    case OWNER:
                        Set<String> playerNames = UserManager.getPlayers().keySet();
                        return StringUtil.copyPartialMatches(args[1], playerNames, new ArrayList<String>(playerNames.size()));
                    case EXPSHARE:
                        return StringUtil.copyPartialMatches(args[1], EXPSHARE_COMPLETIONS, new ArrayList<String>(EXPSHARE_COMPLETIONS.size()));
                    case ITEMSHARE:
                        return StringUtil.copyPartialMatches(args[1], ITEMSHARE_COMPLETIONS, new ArrayList<String>(ITEMSHARE_COMPLETIONS.size()));
                    case LOCK:
                    case CHAT:
                        return StringUtil.copyPartialMatches(args[1], CommandUtils.TRUE_FALSE_OPTIONS, new ArrayList<String>(CommandUtils.TRUE_FALSE_OPTIONS.size()));
                    case PASSWORD:
                        return StringUtil.copyPartialMatches(args[1], CommandUtils.RESET_OPTIONS, new ArrayList<String>(CommandUtils.RESET_OPTIONS.size()));
                    case TELEPORT:
                        List<String> matches = StringUtil.copyPartialMatches(args[1], PtpCommand.TELEPORT_SUBCOMMANDS, new ArrayList<String>(PtpCommand.TELEPORT_SUBCOMMANDS.size()));

                        if (matches.size() == 0) {
                            playerNames = UserManager.getPlayers().keySet();
                            return StringUtil.copyPartialMatches(args[1], playerNames, new ArrayList<String>(playerNames.size()));
                        }

                        return matches;
                    default:
                        return ImmutableList.of();
                }
            case 3:
                if (PartySubcommandType.getSubcommand(args[0]) == PartySubcommandType.ITEMSHARE && isItemShareCategory(args[1])) {
                    return StringUtil.copyPartialMatches(args[2], CommandUtils.TRUE_FALSE_OPTIONS, new ArrayList<String>(CommandUtils.TRUE_FALSE_OPTIONS.size()));
                }

                return ImmutableList.of();
            default:
                return ImmutableList.of();
        }
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

    private boolean isItemShareCategory(String category) {
        return category.equalsIgnoreCase("loot") || category.equalsIgnoreCase("mining") || category.equalsIgnoreCase("herbalism") || category.equalsIgnoreCase("woodcutting") || category.equalsIgnoreCase("misc");
    }
}

