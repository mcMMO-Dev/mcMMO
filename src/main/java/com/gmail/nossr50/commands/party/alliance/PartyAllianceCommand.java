package com.gmail.nossr50.commands.party.alliance;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;

import com.google.common.collect.ImmutableList;

public class PartyAllianceCommand implements TabExecutor {
    private Player player;
    private Party playerParty;
    private Party targetParty;

    public static final List<String> ALLIANCE_SUBCOMMANDS = ImmutableList.of("invite", "accept", "disband");

    private CommandExecutor partyAllianceInviteCommand = new PartyAllianceInviteCommand();
    private CommandExecutor partyAllianceAcceptCommand = new PartyAllianceAcceptCommand();
    private CommandExecutor partyAllianceDisbandCommand = new PartyAllianceDisbandCommand();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        player = (Player) sender;
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        playerParty = mcMMOPlayer.getParty();

        switch (args.length) {
            case 1:
                if (playerParty.getLevel() < Config.getInstance().getPartyFeatureUnlockLevel(PartyFeature.ALLIANCE)) {
                    sender.sendMessage(LocaleLoader.getString("Party.Feature.Disabled.3"));
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
                if (playerParty.getLevel() < Config.getInstance().getPartyFeatureUnlockLevel(PartyFeature.ALLIANCE)) {
                    sender.sendMessage(LocaleLoader.getString("Party.Feature.Disabled.3"));
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

    private boolean printUsage() {
        player.sendMessage(LocaleLoader.getString("Commands.Party.Alliance.Help.0"));
        player.sendMessage(LocaleLoader.getString("Commands.Party.Alliance.Help.1"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                List<String> matches = StringUtil.copyPartialMatches(args[0], ALLIANCE_SUBCOMMANDS, new ArrayList<String>(ALLIANCE_SUBCOMMANDS.size()));

                if (matches.size() == 0) {
                    List<String> playerNames = CommandUtils.getOnlinePlayerNames(commandSender);
                    return StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<String>(playerNames.size()));
                }

                return matches;
            default:
                return ImmutableList.of();
        }
    }

    private void displayPartyHeader() {
        player.sendMessage(LocaleLoader.getString("Commands.Party.Alliance.Header"));
        player.sendMessage(LocaleLoader.getString("Commands.Party.Alliance.Ally", playerParty.getName(), targetParty.getName()));
    }

    private void displayMemberInfo(McMMOPlayer mcMMOPlayer) {
        List<Player> nearMembers = PartyManager.getNearMembers(mcMMOPlayer);
        player.sendMessage(LocaleLoader.getString("Commands.Party.Alliance.Members.Header"));
        player.sendMessage(playerParty.createMembersList(player.getName(), nearMembers));
        player.sendMessage(ChatColor.DARK_GRAY + "----------------------------");
        player.sendMessage(targetParty.createMembersList(player.getName(), nearMembers));
    }
}
