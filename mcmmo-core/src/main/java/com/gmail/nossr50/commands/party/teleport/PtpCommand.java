package com.gmail.nossr50.commands.party.teleport;

import com.gmail.nossr50.commands.CommandConstants;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.datatypes.party.PartyTeleportRecord;
import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.google.common.collect.ImmutableList;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PtpCommand implements TabExecutor {

    private final mcMMO pluginRef;
    private CommandExecutor ptpToggleCommand;
    private CommandExecutor ptpAcceptAnyCommand;
    private CommandExecutor ptpAcceptCommand;

    public PtpCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;

        //Init SubCommands
        ptpToggleCommand = new PtpToggleCommand(pluginRef);
        ptpAcceptAnyCommand = new PtpAcceptAnyCommand(pluginRef);
        ptpAcceptCommand = new PtpAcceptCommand(pluginRef);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (pluginRef.getCommandTools().noConsoleUsage(sender)) {
            return true;
        }

        Player player = (Player) sender;

        /* WORLD GUARD MAIN FLAG CHECK */
        if (pluginRef.getWorldGuardUtils().isWorldGuardLoaded()) {
            if (!pluginRef.getWorldGuardManager().hasMainFlag(player))
                return true;
        }

        /* WORLD BLACKLIST CHECK */
        if (pluginRef.getDynamicSettingsManager().isWorldBlacklisted(player.getWorld().getName()))
            return true;

        if (!pluginRef.getUserManager().hasPlayerDataKey(player)) {
            return true;
        }

        if (pluginRef.getUserManager().getPlayer((Player) sender) == null) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
            return true;
        }

        BukkitMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);

        if (!mcMMOPlayer.inParty()) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.None"));
            return true;
        }

        Party party = mcMMOPlayer.getParty();

        if (party.getLevel() < pluginRef.getPartyManager().getPartyFeatureUnlockLevel(PartyFeature.TELEPORT)) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Party.Feature.Disabled.2"));
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("toggle")) {
                return ptpToggleCommand.onCommand(sender, command, label, args);
            }

            if (args[0].equalsIgnoreCase("acceptany") || args[0].equalsIgnoreCase("acceptall")) {
                return ptpAcceptAnyCommand.onCommand(sender, command, label, args);
            }

            long recentlyHurt = mcMMOPlayer.getRecentlyHurt();
            int hurtCooldown = pluginRef.getConfigManager().getConfigParty().getPTP().getPtpRecentlyHurtCooldown();

            if (hurtCooldown > 0) {
                int timeRemaining = pluginRef.getSkillTools().calculateTimeLeft(recentlyHurt * pluginRef.getMiscTools().TIME_CONVERSION_FACTOR, hurtCooldown, player);

                if (timeRemaining > 0) {
                    player.sendMessage(pluginRef.getLocaleManager().getString("Item.Injured.Wait", timeRemaining));
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("accept")) {
                return ptpAcceptCommand.onCommand(sender, command, label, args);
            }

            if (!pluginRef.getPermissionTools().partyTeleportSend(sender)) {
                sender.sendMessage(command.getPermissionMessage());
                return true;
            }

            int ptpCooldown = pluginRef.getConfigManager().getConfigParty().getPTP().getPtpCooldown();
            long ptpLastUse = mcMMOPlayer.getPartyTeleportRecord().getLastUse();

            if (ptpCooldown > 0) {
                int timeRemaining = pluginRef.getSkillTools().calculateTimeLeft(ptpLastUse * pluginRef.getMiscTools().TIME_CONVERSION_FACTOR, ptpCooldown, player);

                if (timeRemaining > 0) {
                    player.sendMessage(pluginRef.getLocaleManager().getString("Item.Generic.Wait", timeRemaining));
                    return true;
                }
            }

            sendTeleportRequest(sender, player, pluginRef.getCommandTools().getMatchedPlayerName(args[0]));
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                List<String> matches = StringUtil.copyPartialMatches(args[0], CommandConstants.TELEPORT_SUBCOMMANDS, new ArrayList<>(CommandConstants.TELEPORT_SUBCOMMANDS.size()));

                if (matches.size() == 0) {
                    if (pluginRef.getUserManager().getPlayer((Player) sender) == null) {
                        sender.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
                        return ImmutableList.of();
                    }

                    Player player = (Player) sender;
                    BukkitMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);

                    if (!mcMMOPlayer.inParty()) {
                        return ImmutableList.of();
                    }

                    List<String> playerNames = mcMMOPlayer.getParty().getOnlinePlayerNames(player);
                    return StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<>(playerNames.size()));
                }

                return matches;
            default:
                return ImmutableList.of();
        }
    }

    private void sendTeleportRequest(CommandSender sender, Player player, String targetName) {
        if (!pluginRef.getPartyManager().canTeleport(sender, player, targetName)) {
            return;
        }

        BukkitMMOPlayer mcMMOTarget = pluginRef.getUserManager().getPlayer(targetName);
        Player target = mcMMOTarget.getPlayer();

        if (pluginRef.getConfigManager().getConfigParty().getPTP().isPtpWorldBasedPermissions()) {
            World targetWorld = target.getWorld();
            World playerWorld = player.getWorld();

            if (!pluginRef.getPermissionTools().partyTeleportAllWorlds(player)) {
                if (!pluginRef.getPermissionTools().partyTeleportWorld(target, targetWorld)) {
                    player.sendMessage(pluginRef.getLocaleManager().formatString("Commands.ptp.NoWorldPermissions", targetWorld.getName()));
                    return;
                }
                else if (targetWorld != playerWorld && !pluginRef.getPermissionTools().partyTeleportWorld(player, targetWorld)) {
                    player.sendMessage(pluginRef.getLocaleManager().formatString("Commands.ptp.NoWorldPermissions", targetWorld.getName()));
                    return;
                }
            }
        }

        PartyTeleportRecord ptpRecord = mcMMOTarget.getPartyTeleportRecord();

        if (!ptpRecord.isConfirmRequired()) {
            pluginRef.getPartyManager().handleTeleportWarmup(player, target);
            return;
        }

        ptpRecord.setRequestor(player);
        ptpRecord.actualizeTimeout();

        player.sendMessage(pluginRef.getLocaleManager().getString("Commands.Invite.Success"));

        target.sendMessage(pluginRef.getLocaleManager().getString("Commands.ptp.Request1", player.getName()));
        target.sendMessage(pluginRef.getLocaleManager().getString("Commands.ptp.Request2", pluginRef.getConfigManager().getConfigParty().getPTP().getPtpRequestTimeout()));
    }
}
