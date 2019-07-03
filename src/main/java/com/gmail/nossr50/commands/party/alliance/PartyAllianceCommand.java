package com.gmail.nossr50.commands.party.alliance;

import com.gmail.nossr50.commands.CommandConstants;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PartyAllianceCommand implements TabExecutor {

    private mcMMO pluginRef;
    private Player player;
    private Party playerParty;
    private Party targetParty;
    private CommandExecutor partyAllianceInviteCommand;
    private CommandExecutor partyAllianceAcceptCommand;
    private CommandExecutor partyAllianceDisbandCommand;

    public PartyAllianceCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;

        //Init SubCommands
        partyAllianceInviteCommand = new PartyAllianceInviteCommand(pluginRef);
        partyAllianceAcceptCommand = new PartyAllianceAcceptCommand(pluginRef);
        partyAllianceDisbandCommand = new PartyAllianceDisbandCommand(pluginRef);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (pluginRef.getCommandTools().noConsoleUsage(sender)) {
            return true;
        }

        if (pluginRef.getUserManager().getPlayer((Player) sender) == null) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
            return true;
        }

        player = (Player) sender;
        McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);

        playerParty = mcMMOPlayer.getParty();

        switch (args.length) {
            case 1:
                if (playerParty.getLevel() < pluginRef.getPartyManager().getPartyFeatureUnlockLevel(PartyFeature.ALLIANCE)) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Party.Feature.Disabled.3"));
                    return true;
                }

                if (playerParty.getAlly() == null) {
                    printUsage();
                    return true;
                }

                targetParty = playerParty.getAlly();

                displayPartyHeader();
                displayMemberInfo(mcMMOPlayer);
                return true;

            case 2:
            case 3:
                if (playerParty.getLevel() < pluginRef.getPartyManager().getPartyFeatureUnlockLevel(PartyFeature.ALLIANCE)) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Party.Feature.Disabled.3"));
                    return true;
                }

                if (args[1].equalsIgnoreCase("invite")) {
                    return partyAllianceInviteCommand.onCommand(sender, command, label, args);
                }

                if (args[1].equalsIgnoreCase("accept")) {
                    return partyAllianceAcceptCommand.onCommand(sender, command, label, args);
                }

                if (args[1].equalsIgnoreCase("disband")) {
                    return partyAllianceDisbandCommand.onCommand(sender, command, label, args);
                }

                if (playerParty.getAlly() == null) {
                    printUsage();
                    return true;
                }

                targetParty = playerParty.getAlly();

                displayPartyHeader();
                displayMemberInfo(mcMMOPlayer);
                return true;

            default:
                return false;
        }
    }

    private void printUsage() {
        player.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.Alliance.Help.0"));
        player.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.Alliance.Help.1"));
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                List<String> matches = StringUtil.copyPartialMatches(args[0], CommandConstants.ALLIANCE_SUBCOMMANDS, new ArrayList<>(CommandConstants.ALLIANCE_SUBCOMMANDS.size()));

                if (matches.size() == 0) {
                    List<String> playerNames = pluginRef.getCommandTools().getOnlinePlayerNames(commandSender);
                    return StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<>(playerNames.size()));
                }

                return matches;
            default:
                return ImmutableList.of();
        }
    }

    private void displayPartyHeader() {
        player.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.Alliance.Header"));
        player.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.Alliance.Ally", playerParty.getName(), targetParty.getName()));
    }

    private void displayMemberInfo(McMMOPlayer mcMMOPlayer) {
        player.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.Alliance.Members.Header"));
        player.sendMessage(playerParty.createMembersList(player));
        player.sendMessage(ChatColor.DARK_GRAY + "----------------------------");
        player.sendMessage(targetParty.createMembersList(player));
    }
}
