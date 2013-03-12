package com.gmail.nossr50.commands.party.teleport;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.events.party.McMMOPartyTeleportEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;

public class PtpCommand implements CommandExecutor {
    private static Player target;
    private static McMMOPlayer mcMMOTarget;

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

                int ptpCooldown = Config.getInstance().getPTPCommandCooldown();
                long recentlyHurt = mcMMOPlayer.getRecentlyHurt();

                if (((recentlyHurt * Misc.TIME_CONVERSION_FACTOR) + (ptpCooldown * Misc.TIME_CONVERSION_FACTOR)) > System.currentTimeMillis()) {
                    player.sendMessage(LocaleLoader.getString("Party.Teleport.Hurt", ptpCooldown));
                    return true;
                }

                if (args[0].equalsIgnoreCase("accept")) {
                    return ptpAcceptCommand.onCommand(sender, command, label, args);
                }

                sendTeleportRequest(sender, player, args[0]);
                return true;

            default:
                return false;
        }
    }

    private void sendTeleportRequest(CommandSender sender, Player player, String targetName) {
        if (!canTeleport(sender, player, targetName)) {
            return;
        }

        if (!mcMMOTarget.getPtpConfirmRequired()) {
            handlePartyTeleportEvent(player, target);
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

        if (CommandUtils.checkPlayerExistence(sender, targetName, mcMMOTarget)) {
            return false;
        }

        target = mcMMOTarget.getPlayer();

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

    protected static void handlePartyTeleportEvent(Player teleportingPlayer, Player targetPlayer) {
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
