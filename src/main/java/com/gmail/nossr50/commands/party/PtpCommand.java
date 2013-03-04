package com.gmail.nossr50.commands.party;

import org.bukkit.World;
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
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;

public class PtpCommand implements CommandExecutor {
    private Player player;
    private McMMOPlayer mcMMOPlayer;

    private Player target;
    private McMMOPlayer mcMMOTarget;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        switch (args.length) {
            case 1:
                player = (Player) sender;
                mcMMOPlayer = UserManager.getPlayer(player);

                if (args[0].equalsIgnoreCase("toggle")) {
                    if (!Permissions.partyTeleportToggle(sender)) {
                        sender.sendMessage(command.getPermissionMessage());
                        return true;
                    }

                    return togglePartyTeleportation();
                }

                if (args[0].equalsIgnoreCase("acceptany") || args[0].equalsIgnoreCase("acceptall")) {
                    if (!Permissions.partyTeleportAcceptAll(sender)) {
                        sender.sendMessage(command.getPermissionMessage());
                        return true;
                    }

                    return acceptAnyTeleportRequest();
                }

                int ptpCooldown = Config.getInstance().getPTPCommandCooldown();
                long recentlyHurt = UserManager.getPlayer(player).getRecentlyHurt();

                if ((recentlyHurt * Misc.TIME_CONVERSION_FACTOR + ptpCooldown * Misc.TIME_CONVERSION_FACTOR) > System.currentTimeMillis()) {
                    player.sendMessage(LocaleLoader.getString("Party.Teleport.Hurt", ptpCooldown));
                    return true;
                }

                if (args[0].equalsIgnoreCase("accept")) {
                    if (!Permissions.partyTeleportAccept(sender)) {
                        sender.sendMessage(command.getPermissionMessage());
                        return true;
                    }

                    return acceptTeleportRequest();
                }

                return sendTeleportRequest(args[0]);

            default:
                return false;
        }
    }

    private boolean sendTeleportRequest(String targetName) {
        if (!canTeleport(targetName)) {
            return true;
        }

        if (!mcMMOTarget.getPtpConfirmRequired()) {
            return handlePartyTeleportEvent(player, target);
        }

        mcMMOTarget.setPtpRequest(player);
        mcMMOTarget.actualizePtpTimeout();
        player.sendMessage(LocaleLoader.getString("Commands.Invite.Success"));

        int ptpRequestExpire = Config.getInstance().getPTPCommandTimeout();

        target.sendMessage(LocaleLoader.getString("Commands.ptp.Request1", player.getName()));
        target.sendMessage(LocaleLoader.getString("Commands.ptp.Request2", ptpRequestExpire));
        return true;
    }

    private boolean acceptTeleportRequest() {
        if (!mcMMOPlayer.hasPtpRequest()) {
            player.sendMessage(LocaleLoader.getString("Commands.ptp.NoRequests"));
            return true;
        }

        int ptpRequestExpire = Config.getInstance().getPTPCommandTimeout();

        if ((mcMMOPlayer.getPtpTimeout() + ptpRequestExpire) * Misc.TIME_CONVERSION_FACTOR < System.currentTimeMillis()) {
            mcMMOPlayer.removePtpRequest();
            player.sendMessage(LocaleLoader.getString("Commands.ptp.RequestExpired"));
            return true;
        }

        target = mcMMOPlayer.getPtpRequest();
        mcMMOPlayer.removePtpRequest();

        if (!canTeleport(target.getName())) {
            return true;
        }

        if (Config.getInstance().getPTPCommandWorldPermissions()) {
            World targetWorld = target.getWorld();
            World playerWorld = player.getWorld();

            if (!Permissions.partyTeleportAllWorlds(target)) {
                if (!Permissions.partyTeleportWorld(target, targetWorld)) {
                    target.sendMessage(LocaleLoader.getString("Commands.ptp.NoWorldPermissions", targetWorld.getName()));
                    return true;
                }
                else if (targetWorld != playerWorld && !Permissions.partyTeleportWorld(target, playerWorld)) {
                    target.sendMessage(LocaleLoader.getString("Commands.ptp.NoWorldPermissions", playerWorld.getName()));
                    return true;
                }
            }
        }

        return handlePartyTeleportEvent(target, player);
    }

    private boolean acceptAnyTeleportRequest() {
        if (mcMMOPlayer.getPtpConfirmRequired()) {
            player.sendMessage(LocaleLoader.getString("Commands.ptp.AcceptAny.Disabled"));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Commands.ptp.AcceptAny.Enabled"));
        }

        mcMMOPlayer.togglePtpConfirmRequired();
        return true;
    }

    private boolean togglePartyTeleportation() {
        if (mcMMOPlayer.getPtpEnabled()) {
            player.sendMessage(LocaleLoader.getString("Commands.ptp.Disabled"));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Commands.ptp.Enabled"));
        }

        mcMMOPlayer.togglePtpUse();
        return true;
    }

    private boolean canTeleport(String targetName) {
        if (!mcMMO.p.getServer().getOfflinePlayer(targetName).isOnline()) {
            player.sendMessage(LocaleLoader.getString("Party.NotOnline", targetName));
            return false;
        }

        mcMMOTarget = UserManager.getPlayer(targetName);

        if (mcMMOTarget == null) {
            player.sendMessage(LocaleLoader.getString("Party.Player.Invalid"));
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
            player.sendMessage(LocaleLoader.getString("Party.Teleport.Disabled", target.getName()));
            return false;
        }

        if (target.isDead()) {
            player.sendMessage(LocaleLoader.getString("Party.Teleport.Dead"));
            return false;
        }

        return true;
    }

    private boolean handlePartyTeleportEvent(Player player, Player target) {
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        McMMOPartyTeleportEvent event = new McMMOPartyTeleportEvent(player, target, mcMMOPlayer.getParty().getName());

        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return true;
        }

        player.teleport(target);
        player.sendMessage(LocaleLoader.getString("Party.Teleport.Player", target.getName()));
        target.sendMessage(LocaleLoader.getString("Party.Teleport.Target", player.getName()));
        mcMMOPlayer.actualizeRecentlyHurt();
        return true;
    }
}
