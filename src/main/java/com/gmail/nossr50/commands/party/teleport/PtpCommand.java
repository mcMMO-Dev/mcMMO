package com.gmail.nossr50.commands.party.teleport;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.events.party.McMMOPartyTeleportEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.runnables.items.TeleportationWarmup;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.google.common.collect.ImmutableList;

public class PtpCommand implements TabExecutor {
    private static Player target;
    private static McMMOPlayer mcMMOTarget;

    public static final List<String> TELEPORT_SUBCOMMANDS = ImmutableList.of("toggle", "accept", "acceptany", "acceptall");

    private CommandExecutor ptpToggleCommand = new PtpToggleCommand();
    private CommandExecutor ptpAcceptAnyCommand = new PtpAcceptAnyCommand();
    private CommandExecutor ptpAcceptCommand = new PtpAcceptCommand();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
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

                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(sender.getName());
                Player player = mcMMOPlayer.getPlayer();

                long recentlyHurt = mcMMOPlayer.getRecentlyHurt();
                int recentlyhurt_cooldown = Config.getInstance().getPTPCommandRecentlyHurtCooldown();

                if (!SkillUtils.cooldownOver(recentlyHurt * Misc.TIME_CONVERSION_FACTOR, recentlyhurt_cooldown, player)) {
                    player.sendMessage(LocaleLoader.getString("Item.Injured.Wait", SkillUtils.calculateTimeLeft(recentlyHurt * Misc.TIME_CONVERSION_FACTOR, recentlyhurt_cooldown, player)));
                    return true;
                }

                if (args[0].equalsIgnoreCase("accept")) {
                    return ptpAcceptCommand.onCommand(sender, command, label, args);
                }

                if (!Permissions.partyTeleportSend(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                int ptpCooldown = Config.getInstance().getPTPCommandCooldown();
                long lastTeleport = mcMMOPlayer.getLastTeleport();

                if (!SkillUtils.cooldownOver(lastTeleport * Misc.TIME_CONVERSION_FACTOR, ptpCooldown, player)) {
                    player.sendMessage(LocaleLoader.getString("Item.Generic.Wait", SkillUtils.calculateTimeLeft(lastTeleport * Misc.TIME_CONVERSION_FACTOR, ptpCooldown, player)));
                    return true;
                }

                sendTeleportRequest(sender, player, args[0]);
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
                    Set<String> playerNames = UserManager.getPlayers().keySet();
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

        if (!mcMMOTarget.getPtpConfirmRequired()) {
            handleTeleportWarmup(player, target);
            return;
        }

        mcMMOTarget.setPtpRequest(player);
        mcMMOTarget.actualizePtpTimeout();

        player.sendMessage(LocaleLoader.getString("Commands.Invite.Success"));

        target.sendMessage(LocaleLoader.getString("Commands.ptp.Request1", player.getName()));
        target.sendMessage(LocaleLoader.getString("Commands.ptp.Request2", Config.getInstance().getPTPCommandTimeout()));
    }

    protected static boolean canTeleport(CommandSender sender, Player player, String targetName) {
        mcMMOTarget = UserManager.getPlayer(targetName);

        if (!CommandUtils.checkPlayerExistence(sender, targetName, mcMMOTarget)) {
            return false;
        }

        target = mcMMOTarget.getPlayer();
        targetName = target.getName();

        if (player.equals(target)) {
            player.sendMessage(LocaleLoader.getString("Party.Teleport.Self"));
            return false;
        }

        if (!PartyManager.inSameParty(player, target)) {
            player.sendMessage(LocaleLoader.getString("Party.NotInYourParty", targetName));
            return false;
        }

        if (!mcMMOTarget.getPtpEnabled()) {
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
        mcMMOTarget = UserManager.getPlayer(targetPlayer);

        long warmup = Config.getInstance().getPTPCommandWarmup();

        mcMMOPlayer.actualizeTeleportCommenceLocation(teleportingPlayer);

        if (warmup > 0) {
            teleportingPlayer.sendMessage(LocaleLoader.getString("Teleport.Commencing", warmup));
            new TeleportationWarmup(mcMMOPlayer, mcMMOTarget).runTaskLater(mcMMO.p, 20 * warmup);
        }
        else {
            handlePartyTeleportEvent(teleportingPlayer, targetPlayer);
        }
    }

    public static void handlePartyTeleportEvent(Player teleportingPlayer, Player targetPlayer) {
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(teleportingPlayer);
        McMMOPartyTeleportEvent event = new McMMOPartyTeleportEvent(teleportingPlayer, targetPlayer, mcMMOPlayer.getParty().getName());

        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        teleportingPlayer.teleport(targetPlayer);

        teleportingPlayer.sendMessage(LocaleLoader.getString("Party.Teleport.Player", targetPlayer.getName()));
        targetPlayer.sendMessage(LocaleLoader.getString("Party.Teleport.Target", teleportingPlayer.getName()));

        mcMMOPlayer.actualizeRecentlyHurt();
    }
}
