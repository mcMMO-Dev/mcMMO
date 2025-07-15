package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.commands.party.alliance.PartyAllianceCommand;
import com.gmail.nossr50.commands.party.teleport.PtpCommand;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

public class PartyCommand implements TabExecutor {
    private final List<String> PARTY_SUBCOMMANDS;
    private final List<String> XPSHARE_COMPLETIONS = ImmutableList.of("none", "equal");
    private final List<String> ITEMSHARE_COMPLETIONS = ImmutableList.of("none", "equal", "random",
            "loot", "mining", "herbalism", "woodcutting", "misc");
    private final CommandExecutor partyJoinCommand;
    private final CommandExecutor partyAcceptCommand;
    private final CommandExecutor partyCreateCommand;
    private final CommandExecutor partyQuitCommand;
    private final CommandExecutor partyXpShareCommand;
    private final CommandExecutor partyItemShareCommand;
    private final CommandExecutor partyInviteCommand;
    private final CommandExecutor partyKickCommand;
    private final CommandExecutor partyDisbandCommand;
    private final CommandExecutor partyChangeOwnerCommand;
    private final CommandExecutor partyLockCommand;
    private final CommandExecutor partyChangePasswordCommand;
    private final CommandExecutor partyRenameCommand;
    private final CommandExecutor partyInfoCommand;
    private final CommandExecutor partyHelpCommand;
    private final CommandExecutor partyTeleportCommand;
    private final CommandExecutor partyAllianceCommand;

    public PartyCommand() {
        partyJoinCommand = new PartyJoinCommand();
        partyAcceptCommand = new PartyAcceptCommand();
        partyCreateCommand = new PartyCreateCommand();
        partyQuitCommand = new PartyQuitCommand();
        partyXpShareCommand = new PartyXpShareCommand();
        partyItemShareCommand = new PartyItemShareCommand();
        partyInviteCommand = new PartyInviteCommand();
        partyKickCommand = new PartyKickCommand();
        partyDisbandCommand = new PartyDisbandCommand();
        partyChangeOwnerCommand = new PartyChangeOwnerCommand();
        partyLockCommand = new PartyLockCommand();
        partyChangePasswordCommand = new PartyChangePasswordCommand();
        partyRenameCommand = new PartyRenameCommand();
        partyInfoCommand = new PartyInfoCommand();
        partyHelpCommand = new PartyHelpCommand();
        partyTeleportCommand = new PtpCommand();
        partyAllianceCommand = new PartyAllianceCommand();

        ArrayList<String> subcommands = new ArrayList<>();

        for (PartySubcommandType subcommand : PartySubcommandType.values()) {
            subcommands.add(subcommand.toString());
        }

        Collections.sort(subcommands);
        PARTY_SUBCOMMANDS = ImmutableList.copyOf(subcommands);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        if (!Permissions.party(sender)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        final Player player = (Player) sender;

        if (!UserManager.hasPlayerDataKey(player)) {
            return true;
        }

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
        if (mmoPlayer == null) {
            player.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));
            return true;
        }

        if (args.length < 1) {
            if (!mmoPlayer.inParty()) {
                sender.sendMessage(LocaleLoader.getString("Commands.Party.None"));
                return printUsage(player);
            }

            return partyInfoCommand.onCommand(sender, command, label, args);
        }

        PartySubcommandType subcommand = PartySubcommandType.getSubcommand(args[0]);

        if (subcommand == null) {
            return printUsage(player);
        }

        // Can't use this for lock/unlock since they're handled by the same command
        if (subcommand != PartySubcommandType.LOCK && subcommand != PartySubcommandType.UNLOCK
                && !Permissions.partySubcommand(sender, subcommand)) {
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
        if (!mmoPlayer.inParty()) {
            sender.sendMessage(LocaleLoader.getString("Commands.Party.None"));
            return printUsage(player);
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
            default:
                break;
        }

        // Party leader commands
        if (!mmoPlayer.getParty().getLeader().getUniqueId().equals(player.getUniqueId())) {
            sender.sendMessage(LocaleLoader.getString("Party.NotOwner"));
            return true;
        }

        return switch (subcommand) {
            case XPSHARE -> partyXpShareCommand.onCommand(sender, command, label, args);
            case ITEMSHARE -> partyItemShareCommand.onCommand(sender, command, label, args);
            case KICK -> partyKickCommand.onCommand(sender, command, label, args);
            case DISBAND -> partyDisbandCommand.onCommand(sender, command, label, args);
            case OWNER -> partyChangeOwnerCommand.onCommand(sender, command, label, args);
            case LOCK, UNLOCK -> partyLockCommand.onCommand(sender, command, label, args);
            case PASSWORD -> partyChangePasswordCommand.onCommand(sender, command, label, args);
            case RENAME -> partyRenameCommand.onCommand(sender, command, label, args);
            case ALLIANCE -> partyAllianceCommand.onCommand(sender, command, label, args);
            default -> true;
        };
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String alias, String[] args) {
        switch (args.length) {
            case 1:
                return StringUtil.copyPartialMatches(args[0], PARTY_SUBCOMMANDS,
                        new ArrayList<>(PARTY_SUBCOMMANDS.size()));
            case 2:
                PartySubcommandType subcommand = PartySubcommandType.getSubcommand(args[0]);

                if (subcommand == null) {
                    return ImmutableList.of();
                }

                List<String> playerNames = CommandUtils.getOnlinePlayerNames(sender);

                switch (subcommand) {
                    case JOIN:
                    case INVITE:
                    case KICK:
                    case OWNER:
                        return StringUtil.copyPartialMatches(args[1], playerNames,
                                new ArrayList<>(playerNames.size()));
                    case XPSHARE:
                        return StringUtil.copyPartialMatches(args[1], XPSHARE_COMPLETIONS,
                                new ArrayList<>(XPSHARE_COMPLETIONS.size()));
                    case ITEMSHARE:
                        return StringUtil.copyPartialMatches(args[1], ITEMSHARE_COMPLETIONS,
                                new ArrayList<>(ITEMSHARE_COMPLETIONS.size()));
                    case LOCK:
                    case CHAT:
                        return StringUtil.copyPartialMatches(args[1],
                                CommandUtils.TRUE_FALSE_OPTIONS,
                                new ArrayList<>(CommandUtils.TRUE_FALSE_OPTIONS.size()));
                    case PASSWORD:
                        return StringUtil.copyPartialMatches(args[1], CommandUtils.RESET_OPTIONS,
                                new ArrayList<>(CommandUtils.RESET_OPTIONS.size()));
                    case TELEPORT:
                        List<String> matches = StringUtil.copyPartialMatches(args[1],
                                PtpCommand.TELEPORT_SUBCOMMANDS,
                                new ArrayList<>(PtpCommand.TELEPORT_SUBCOMMANDS.size()));

                        if (matches.isEmpty()) {
                            final Player player = (Player) sender;
                            final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

                            //Not Loaded
                            if (mmoPlayer == null) {
                                sender.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));
                                return ImmutableList.of();
                            }

                            if (mmoPlayer.getParty() == null) {
                                return ImmutableList.of();
                            }

                            final Party party = mmoPlayer.getParty();

                            playerNames = party.getOnlinePlayerNames(player);
                            return StringUtil.copyPartialMatches(args[1], playerNames,
                                    new ArrayList<>(playerNames.size()));
                        }

                        return matches;
                    default:
                        return ImmutableList.of();
                }
            case 3:
                if (PartySubcommandType.getSubcommand(args[0]) == PartySubcommandType.ITEMSHARE
                        && isItemShareCategory(args[1])) {
                    return StringUtil.copyPartialMatches(args[2], CommandUtils.TRUE_FALSE_OPTIONS,
                            new ArrayList<>(CommandUtils.TRUE_FALSE_OPTIONS.size()));
                }

                return ImmutableList.of();
            default:
                return ImmutableList.of();
        }
    }

    private boolean printUsage(Player player) {
        player.sendMessage(LocaleLoader.getString("Party.Help.0", "/party join"));
        player.sendMessage(LocaleLoader.getString("Party.Help.1", "/party create"));
        player.sendMessage(LocaleLoader.getString("Party.Help.2", "/party ?"));
        return true;
    }

    private String[] extractArgs(String[] args) {
        return Arrays.copyOfRange(args, 1, args.length);
    }

    private boolean isItemShareCategory(String category) {
        return category.equalsIgnoreCase("loot") || category.equalsIgnoreCase("mining")
                || category.equalsIgnoreCase("herbalism") || category.equalsIgnoreCase(
                "woodcutting") || category.equalsIgnoreCase("misc");
    }
}

