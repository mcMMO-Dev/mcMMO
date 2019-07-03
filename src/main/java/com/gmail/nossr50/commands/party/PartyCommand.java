package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.commands.CommandConstants;
import com.gmail.nossr50.commands.chat.PartyChatCommand;
import com.gmail.nossr50.commands.party.alliance.PartyAllianceCommand;
import com.gmail.nossr50.commands.party.teleport.PtpCommand;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//TODO: This class horrifies me, rewrite it at some point
public class PartyCommand implements TabExecutor {

    private mcMMO pluginRef;
    private CommandExecutor partyJoinCommand;
    private CommandExecutor partyAcceptCommand;
    private CommandExecutor partyCreateCommand;
    private CommandExecutor partyQuitCommand;
    private CommandExecutor partyXpShareCommand;
    private CommandExecutor partyItemShareCommand;
    private CommandExecutor partyInviteCommand;
    private CommandExecutor partyKickCommand;
    private CommandExecutor partyDisbandCommand;
    private CommandExecutor partyChangeOwnerCommand;
    private CommandExecutor partyLockCommand;
    private CommandExecutor partyChangePasswordCommand;
    private CommandExecutor partyRenameCommand;
    private CommandExecutor partyInfoCommand;
    private CommandExecutor partyHelpCommand;
    private CommandExecutor partyTeleportCommand;
    private CommandExecutor partyChatCommand;
    private CommandExecutor partyAllianceCommand;

    public PartyCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        initSubCommandList();

        partyJoinCommand = new PartyJoinCommand(pluginRef);
        partyAcceptCommand = new PartyAcceptCommand(pluginRef);
        partyCreateCommand = new PartyCreateCommand(pluginRef);
        partyQuitCommand = new PartyQuitCommand(pluginRef);
        partyXpShareCommand = new PartyXpShareCommand(pluginRef);
        partyItemShareCommand = new PartyItemShareCommand(pluginRef);
        partyInviteCommand = new PartyInviteCommand(pluginRef);
        partyKickCommand = new PartyKickCommand(pluginRef);
        partyDisbandCommand = new PartyDisbandCommand(pluginRef);
        partyChangeOwnerCommand = new PartyChangeOwnerCommand(pluginRef);
        partyLockCommand = new PartyLockCommand(pluginRef);
        partyChangePasswordCommand = new PartyChangePasswordCommand(pluginRef);
        partyRenameCommand = new PartyRenameCommand(pluginRef);
        partyInfoCommand = new PartyInfoCommand(pluginRef);
        partyHelpCommand = new PartyHelpCommand(pluginRef);
        partyTeleportCommand = new PtpCommand(pluginRef);
        partyChatCommand = new PartyChatCommand(pluginRef);
        partyAllianceCommand = new PartyAllianceCommand(pluginRef);
    }

    private List<String> PARTY_SUBCOMMANDS;
    private final List<String> XPSHARE_COMPLETIONS = ImmutableList.of("none", "equal");
    private final List<String> ITEMSHARE_COMPLETIONS = ImmutableList.of("none", "equal", "random", "loot", "mining", "herbalism", "woodcutting", "misc");

    private void initSubCommandList() {
        ArrayList<String> subcommands = new ArrayList<>();

        for (PartySubcommandType subcommand : PartySubcommandType.values()) {
            subcommands.add(subcommand.toString());
        }

        Collections.sort(subcommands);
        PARTY_SUBCOMMANDS = ImmutableList.copyOf(subcommands);
    }



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //If the party system is disabled, don't fire this command
        if (!pluginRef.getConfigManager().getConfigParty().isPartySystemEnabled())
            return true;

        if (pluginRef.getCommandTools().noConsoleUsage(sender)) {
            return true;
        }

        if (!Permissions.party(sender)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        Player player = (Player) sender;

        if (!pluginRef.getUserManager().hasPlayerDataKey(player)) {
            return true;
        }

        if (pluginRef.getUserManager().getPlayer(player) == null) {
            player.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
            return true;
        }

        McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);

        if (args.length < 1) {
            if (!mcMMOPlayer.inParty()) {
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.None"));
                return printUsage(player);
            }

            return partyInfoCommand.onCommand(sender, command, label, args);
        }

        PartySubcommandType subcommand = PartySubcommandType.getSubcommand(args[0]);

        if (subcommand == null) {
            return printUsage(player);
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
            sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.None"));
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
            case CHAT:
                return partyChatCommand.onCommand(sender, command, label, extractArgs(args));
            default:
                break;
        }

        // Party leader commands
        if (!mcMMOPlayer.getParty().getLeader().getUniqueId().equals(player.getUniqueId())) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Party.NotOwner"));
            return true;
        }

        switch (subcommand) {
            case XPSHARE:
                return partyXpShareCommand.onCommand(sender, command, label, args);
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
            case ALLIANCE:
                return partyAllianceCommand.onCommand(sender, command, label, args);
            default:
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return StringUtil.copyPartialMatches(args[0], PARTY_SUBCOMMANDS, new ArrayList<>(PARTY_SUBCOMMANDS.size()));
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
                        List<String> playerNames = pluginRef.getCommandTools().getOnlinePlayerNames(sender);
                        return StringUtil.copyPartialMatches(args[1], playerNames, new ArrayList<>(playerNames.size()));
                    case XPSHARE:
                        return StringUtil.copyPartialMatches(args[1], XPSHARE_COMPLETIONS, new ArrayList<>(XPSHARE_COMPLETIONS.size()));
                    case ITEMSHARE:
                        return StringUtil.copyPartialMatches(args[1], ITEMSHARE_COMPLETIONS, new ArrayList<>(ITEMSHARE_COMPLETIONS.size()));
                    case LOCK:
                    case CHAT:
                        return StringUtil.copyPartialMatches(args[1], CommandConstants.TRUE_FALSE_OPTIONS, new ArrayList<>(CommandConstants.TRUE_FALSE_OPTIONS.size()));
                    case PASSWORD:
                        return StringUtil.copyPartialMatches(args[1], CommandConstants.RESET_OPTIONS, new ArrayList<>(CommandConstants.RESET_OPTIONS.size()));
                    case TELEPORT:
                        List<String> matches = StringUtil.copyPartialMatches(args[1], CommandConstants.TELEPORT_SUBCOMMANDS, new ArrayList<>(CommandConstants.TELEPORT_SUBCOMMANDS.size()));

                        if (matches.size() == 0) {
                            Player player = (Player) sender;

                            //Not Loaded
                            if (pluginRef.getUserManager().getPlayer(player) == null) {
                                sender.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
                                return ImmutableList.of();
                            }

                            Party party = pluginRef.getUserManager().getPlayer(player).getParty();

                            playerNames = party.getOnlinePlayerNames(player);
                            return StringUtil.copyPartialMatches(args[1], playerNames, new ArrayList<>(playerNames.size()));
                        }

                        return matches;
                    default:
                        return ImmutableList.of();
                }
            case 3:
                if (PartySubcommandType.getSubcommand(args[0]) == PartySubcommandType.ITEMSHARE && isItemShareCategory(args[1])) {
                    return StringUtil.copyPartialMatches(args[2], CommandConstants.TRUE_FALSE_OPTIONS, new ArrayList<>(CommandConstants.TRUE_FALSE_OPTIONS.size()));
                }

                return ImmutableList.of();
            default:
                return ImmutableList.of();
        }
    }

    private boolean printUsage(Player player) {
        player.sendMessage(pluginRef.getLocaleManager().getString("Party.Help.0", "/party join"));
        player.sendMessage(pluginRef.getLocaleManager().getString("Party.Help.1", "/party create"));
        player.sendMessage(pluginRef.getLocaleManager().getString("Party.Help.2", "/party ?"));
        return true;
    }

    private String[] extractArgs(String[] args) {
        return Arrays.copyOfRange(args, 1, args.length);
    }

    private boolean isItemShareCategory(String category) {
        return category.equalsIgnoreCase("loot") || category.equalsIgnoreCase("mining") || category.equalsIgnoreCase("herbalism") || category.equalsIgnoreCase("woodcutting") || category.equalsIgnoreCase("misc");
    }
}

