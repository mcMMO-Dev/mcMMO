package com.gmail.nossr50.party.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.events.party.McMMOPartyTeleportEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class PtpCommand implements CommandExecutor {
    private Player player;
    private McMMOPlayer mcMMOPlayer;
    private PlayerProfile playerProfile;

    private Player target;
    private McMMOPlayer mcMMOTarget;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        switch (args.length) {
        case 1:
            player = (Player) sender;
            mcMMOPlayer = Users.getPlayer(player);
            playerProfile = mcMMOPlayer.getProfile();

            if (args[0].equalsIgnoreCase("toggle")) {
                if (!Permissions.hasPermission(sender, "mcmmo.commands.ptp.toggle")) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                return togglePartyTeleportation();
            }

            if (args[0].equalsIgnoreCase("acceptany") || args[0].equalsIgnoreCase("acceptall")) {
                if (!Permissions.hasPermission(sender, "mcmmo.commands.ptp.acceptall")) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                return acceptAnyTeleportRequest();
            }

            int ptpCooldown = Config.getInstance().getPTPCommandCooldown();

            if (playerProfile.getRecentlyHurt() + (ptpCooldown * Misc.TIME_CONVERSION_FACTOR) > System.currentTimeMillis()) {
                player.sendMessage(LocaleLoader.getString("Party.Teleport.Hurt", ptpCooldown));
                return true;
            }

            if (args[0].equalsIgnoreCase("accept")) {
                if (!Permissions.hasPermission(sender, "mcmmo.commands.ptp.accept")) {
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

        Player requestTarget = mcMMOPlayer.getPtpRequest();

        if (!canTeleport(requestTarget.getName())) {
            return true;
        }

        //TODO: Someone want to clarify what's going on with these dynamic permissions?
        if (Config.getInstance().getPTPCommandWorldPermissions()) {
            String perm = "mcmmo.commands.ptp.world.";

            if (!Permissions.hasDynamicPermission(requestTarget, perm + "all", "op")) {
                if (!Permissions.hasDynamicPermission(requestTarget, perm + requestTarget.getWorld().getName(), "op")) {
                    return true;
                }
                else if (requestTarget.getWorld() != player.getWorld() && !Permissions.hasDynamicPermission(requestTarget, perm + player.getWorld().getName(), "op")) {
                    return true;
                }
            }
        }

        return handlePartyTeleportEvent(player, requestTarget);
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

        mcMMOTarget = Users.getPlayer(targetName);

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
        McMMOPlayer mcMMOPlayer= Users.getPlayer(player);

        McMMOPartyTeleportEvent event = new McMMOPartyTeleportEvent(player, target, mcMMOPlayer.getParty().getName());
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return true;
        }

        player.teleport(target);
        player.sendMessage(LocaleLoader.getString("Party.Teleport.Player", player.getName()));
        target.sendMessage(LocaleLoader.getString("Party.Teleport.Target", target.getName()));
        mcMMOPlayer.getProfile().setRecentlyHurt(System.currentTimeMillis());
        return true;
    }
}
