package com.gmail.nossr50.commands.party.teleport;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.datatypes.party.PartyTeleportRecord;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.runnables.items.TeleportationWarmup;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;

import com.google.common.collect.ImmutableList;

public class PtpCommand implements TabExecutor {
    public static final List<String> TELEPORT_SUBCOMMANDS = ImmutableList.of("toggle", "accept", "acceptany", "acceptall");

    private CommandExecutor ptpToggleCommand = new PtpToggleCommand();
    private CommandExecutor ptpAcceptAnyCommand = new PtpAcceptAnyCommand();
    private CommandExecutor ptpAcceptCommand = new PtpAcceptCommand();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        Player player = (Player) sender;

        if (!UserManager.hasPlayerDataKey(player)) {
            return true;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        if (!mcMMOPlayer.inParty()) {
            sender.sendMessage(LocaleLoader.getString("Commands.Party.None"));
            return true;
        }

        Party party = mcMMOPlayer.getParty();

        if (party.getLevel() < Config.getInstance().getPartyFeatureUnlockLevel(PartyFeature.TELEPORT)) {
            sender.sendMessage(LocaleLoader.getString("Party.Feature.Disabled.2"));
            return true;
        }

        switch (args.length) {
            case 1:
                if (args[0].equalsIgnoreCase("toggle")) {
                    return ptpToggleCommand.onCommand(sender, command, label, args);
                }

                if (args[0].equalsIgnoreCase("acceptany") || args[0].equalsIgnoreCase("acceptall")) {
                    return ptpAcceptAnyCommand.onCommand(sender, command, label, args);
                }

                long recentlyHurt = mcMMOPlayer.getRecentlyHurt();
                int hurtCooldown = Config.getInstance().getPTPCommandRecentlyHurtCooldown();

                if (hurtCooldown > 0) {
                    int timeRemaining = SkillUtils.calculateTimeLeft(recentlyHurt * Misc.TIME_CONVERSION_FACTOR, hurtCooldown, player);

                    if (timeRemaining > 0) {
                        player.sendMessage(LocaleLoader.getString("Item.Injured.Wait", timeRemaining));
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

                int ptpCooldown = Config.getInstance().getPTPCommandCooldown();
                long ptpLastUse = mcMMOPlayer.getPartyTeleportRecord().getLastUse();

                if (ptpCooldown > 0) {
                    int timeRemaining = SkillUtils.calculateTimeLeft(ptpLastUse * Misc.TIME_CONVERSION_FACTOR, ptpCooldown, player);

                    if (timeRemaining > 0) {
                        player.sendMessage(LocaleLoader.getString("Item.Generic.Wait", timeRemaining));
                        return true;
                    }
                }

                sendTeleportRequest(sender, player, CommandUtils.getMatchedPlayerName(args[0]));
                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                List<String> matches = StringUtil.copyPartialMatches(args[0], TELEPORT_SUBCOMMANDS, new ArrayList<String>(TELEPORT_SUBCOMMANDS.size()));

                if (matches.size() == 0) {
                    Player player = (Player) sender;
                    McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

                    if (!mcMMOPlayer.inParty()) {
                        return ImmutableList.of();
                    }

                    List<String> playerNames = mcMMOPlayer.getParty().getOnlinePlayerNames(player);
                    return StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<String>(playerNames.size()));
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

        player.sendMessage(LocaleLoader.getString("Commands.Invite.Success"));

        target.sendMessage(LocaleLoader.getString("Commands.ptp.Request1", player.getName()));
        target.sendMessage(LocaleLoader.getString("Commands.ptp.Request2", Config.getInstance().getPTPCommandTimeout()));
    }

    protected static boolean canTeleport(CommandSender sender, Player player, String targetName) {
        McMMOPlayer mcMMOTarget = UserManager.getPlayer(targetName);

        if (!CommandUtils.checkPlayerExistence(sender, targetName, mcMMOTarget)) {
            return false;
        }

        Player target = mcMMOTarget.getPlayer();

        if (player.equals(target)) {
            player.sendMessage(LocaleLoader.getString("Party.Teleport.Self"));
            return false;
        }

        if (!PartyManager.inSameParty(player, target)) {
            player.sendMessage(LocaleLoader.getString("Party.NotInYourParty", targetName));
            return false;
        }

        if (!mcMMOTarget.getPartyTeleportRecord().isEnabled()) {
            player.sendMessage(LocaleLoader.getString("Party.Teleport.Disabled", targetName));
            return false;
        }

        if (!target.isValid()) {
            player.sendMessage(LocaleLoader.getString("Party.Teleport.Dead"));
            return false;
        }

        return true;
    }

    protected static void handleTeleportWarmup(Player teleportingPlayer, Player targetPlayer) {
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(teleportingPlayer);
        McMMOPlayer mcMMOTarget = UserManager.getPlayer(targetPlayer);

        long warmup = Config.getInstance().getPTPCommandWarmup();

        mcMMOPlayer.actualizeTeleportCommenceLocation(teleportingPlayer);

        if (warmup > 0) {
            teleportingPlayer.sendMessage(LocaleLoader.getString("Teleport.Commencing", warmup));
            new TeleportationWarmup(mcMMOPlayer, mcMMOTarget).runTaskLater(mcMMO.p, 20 * warmup);
        }
        else {
            EventUtils.handlePartyTeleportEvent(teleportingPlayer, targetPlayer);
        }
    }
}
