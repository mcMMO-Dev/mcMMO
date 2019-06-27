package com.gmail.nossr50.commands.party.teleport;

import com.gmail.nossr50.config.WorldBlacklist;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.datatypes.party.PartyTeleportRecord;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.items.TeleportationWarmup;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.worldguard.WorldGuardUtils;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PtpCommand implements TabExecutor {

    private mcMMO pluginRef;

    public PtpCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    public final List<String> TELEPORT_SUBCOMMANDS = ImmutableList.of("toggle", "accept", "acceptany", "acceptall");

    private CommandExecutor ptpToggleCommand = new PtpToggleCommand();
    private CommandExecutor ptpAcceptAnyCommand = new PtpAcceptAnyCommand();
    private CommandExecutor ptpAcceptCommand = new PtpAcceptCommand();

    protected boolean canTeleport(CommandSender sender, Player player, String targetName) {
        McMMOPlayer mcMMOTarget = UserManager.getPlayer(targetName);

        if (!CommandUtils.checkPlayerExistence(sender, targetName, mcMMOTarget)) {
            return false;
        }

        Player target = mcMMOTarget.getPlayer();

        if (player.equals(target)) {
            player.sendMessage(pluginRef.getLocaleManager().getString("Party.Teleport.Self"));
            return false;
        }

        if (!pluginRef.getPartyManager().inSameParty(player, target)) {
            player.sendMessage(pluginRef.getLocaleManager().getString("Party.NotInYourParty", targetName));
            return false;
        }

        if (!mcMMOTarget.getPartyTeleportRecord().isEnabled()) {
            player.sendMessage(pluginRef.getLocaleManager().getString("Party.Teleport.Disabled", targetName));
            return false;
        }

        if (!target.isValid()) {
            player.sendMessage(pluginRef.getLocaleManager().getString("Party.Teleport.Dead"));
            return false;
        }

        return true;
    }

    protected void handleTeleportWarmup(Player teleportingPlayer, Player targetPlayer) {
        if (UserManager.getPlayer(targetPlayer) == null) {
            targetPlayer.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
            return;
        }

        if (UserManager.getPlayer(teleportingPlayer) == null) {
            teleportingPlayer.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(teleportingPlayer);
        McMMOPlayer mcMMOTarget = UserManager.getPlayer(targetPlayer);

        long warmup = pluginRef.getConfigManager().getConfigParty().getPTP().getPtpWarmup();

        mcMMOPlayer.actualizeTeleportCommenceLocation(teleportingPlayer);

        if (warmup > 0) {
            teleportingPlayer.sendMessage(pluginRef.getLocaleManager().getString("Teleport.Commencing", warmup));
            new TeleportationWarmup(mcMMOPlayer, mcMMOTarget).runTaskLater(pluginRef, 20 * warmup);
        } else {
            EventUtils.handlePartyTeleportEvent(teleportingPlayer, targetPlayer);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        Player player = (Player) sender;

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!pluginRef.getWorldGuardManager().hasMainFlag(player))
                return true;
        }

        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(player.getWorld()))
            return true;

        if (!UserManager.hasPlayerDataKey(player)) {
            return true;
        }

        if (UserManager.getPlayer((Player) sender) == null) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
            return true;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

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
                int timeRemaining = SkillUtils.calculateTimeLeft(recentlyHurt * Misc.TIME_CONVERSION_FACTOR, hurtCooldown, player);

                if (timeRemaining > 0) {
                    player.sendMessage(pluginRef.getLocaleManager().getString("Item.Injured.Wait", timeRemaining));
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("accept")) {
                return ptpAcceptCommand.onCommand(sender, command, label, args);
            }

            if (!Permissions.partyTeleportSend(sender)) {
                sender.sendMessage(command.getPermissionMessage());
                return true;
            }

            int ptpCooldown = pluginRef.getConfigManager().getConfigParty().getPTP().getPtpCooldown();
            long ptpLastUse = mcMMOPlayer.getPartyTeleportRecord().getLastUse();

            if (ptpCooldown > 0) {
                int timeRemaining = SkillUtils.calculateTimeLeft(ptpLastUse * Misc.TIME_CONVERSION_FACTOR, ptpCooldown, player);

                if (timeRemaining > 0) {
                    player.sendMessage(pluginRef.getLocaleManager().getString("Item.Generic.Wait", timeRemaining));
                    return true;
                }
            }

            sendTeleportRequest(sender, player, CommandUtils.getMatchedPlayerName(args[0]));
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                List<String> matches = StringUtil.copyPartialMatches(args[0], TELEPORT_SUBCOMMANDS, new ArrayList<>(TELEPORT_SUBCOMMANDS.size()));

                if (matches.size() == 0) {
                    if (UserManager.getPlayer((Player) sender) == null) {
                        sender.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
                        return ImmutableList.of();
                    }

                    Player player = (Player) sender;
                    McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

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
        if (!canTeleport(sender, player, targetName)) {
            return;
        }

        McMMOPlayer mcMMOTarget = UserManager.getPlayer(targetName);
        Player target = mcMMOTarget.getPlayer();

        PartyTeleportRecord ptpRecord = mcMMOTarget.getPartyTeleportRecord();

        if (!ptpRecord.isConfirmRequired()) {
            handleTeleportWarmup(player, target);
            return;
        }

        ptpRecord.setRequestor(player);
        ptpRecord.actualizeTimeout();

        player.sendMessage(pluginRef.getLocaleManager().getString("Commands.Invite.Success"));

        target.sendMessage(pluginRef.getLocaleManager().getString("Commands.ptp.Request1", player.getName()));
        target.sendMessage(pluginRef.getLocaleManager().getString("Commands.ptp.Request2", pluginRef.getConfigManager().getConfigParty().getPTP().getPtpRequestTimeout()));
    }
}
