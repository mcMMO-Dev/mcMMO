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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String usage = LocaleLoader.getString("Commands.Usage.1", "ptp", "<" + LocaleLoader.getString("Commands.Usage.Player") + ">");

        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.ptp")) {
            return true;
        }

        switch (args.length) {
        case 1:
            player = (Player) sender;
            mcMMOPlayer = Users.getPlayer(player);
            PlayerProfile playerProfile = mcMMOPlayer.getProfile();

            if (args[0].equalsIgnoreCase("toggle")) {
                return togglePartyTeleportation();
            }
            else if (args[0].equalsIgnoreCase("acceptany") || args[0].equalsIgnoreCase("acceptall")) {
                return acceptAnyTeleportRequest();
            }

            int ptpCooldown = Config.getInstance().getPTPCommandCooldown();

            if (playerProfile.getRecentlyHurt() + (ptpCooldown * Misc.TIME_CONVERSION_FACTOR) > System.currentTimeMillis()) {
                player.sendMessage(LocaleLoader.getString("Party.Teleport.Hurt", ptpCooldown));
                return true;
            }

            if (args[0].equalsIgnoreCase("accept")) {
                return acceptTeleportRequest();
            }

            return sendTeleportRequest(args[0]);

        default:
            sender.sendMessage(usage);
            return true;
        }
    }

    private boolean sendTeleportRequest(String args) {
        Player target = mcMMO.p.getServer().getPlayer(args);

        if (player.equals(target)) {
            player.sendMessage(LocaleLoader.getString("Party.Teleport.Self"));
            return true;
        }

        if (target == null) {
            player.sendMessage(LocaleLoader.getString("Party.Player.Invalid"));
            return true;
        }

        if (target.isDead()) {
            player.sendMessage(LocaleLoader.getString("Party.Teleport.Dead"));
            return true;
        }

        if (PartyManager.inSameParty(player, target)) {
            McMMOPlayer targetMcMMOPlayer = Users.getPlayer(target);

            if (!targetMcMMOPlayer.getPtpEnabled()) {
                player.sendMessage(LocaleLoader.getString("Party.Teleport.Disabled", target.getName()));
                return true;
            }

            if (!targetMcMMOPlayer.getPtpConfirmRequired()) {
                McMMOPartyTeleportEvent event = new McMMOPartyTeleportEvent(player, target, mcMMOPlayer.getParty().getName());
                mcMMO.p.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return true;
                }

                player.teleport(target);
                player.sendMessage(LocaleLoader.getString("Party.Teleport.Player", player.getName()));
                target.sendMessage(LocaleLoader.getString("Party.Teleport.Target", target.getName()));
                mcMMOPlayer.getProfile().setRecentlyHurt(System.currentTimeMillis());
            } else {
                targetMcMMOPlayer.setPtpRequest(player);
                targetMcMMOPlayer.actualizePtpTimeout();
                player.sendMessage(LocaleLoader.getString("Commands.Invite.Success"));

                int ptpRequestExpire = Config.getInstance().getPTPCommandTimeout();
                target.sendMessage(LocaleLoader.getString("Commands.ptp.Request1", player.getName()));
                target.sendMessage(LocaleLoader.getString("Commands.ptp.Request2", ptpRequestExpire));
            }
        }
        else {
            player.sendMessage(LocaleLoader.getString("Party.NotInYourParty", target.getName()));
        }
        return true;
    }

    private boolean acceptTeleportRequest() {
        if (!mcMMOPlayer.hasPtpRequest()) {
            player.sendMessage(LocaleLoader.getString("Commands.ptp.NoRequests"));
            return true;
        }

        if (CommandHelper.noCommandPermissions(player, "mcmmo.commands.ptp.accept")) {
            return true;
        }

        int ptpRequestExpire = Config.getInstance().getPTPCommandTimeout();

        if ((mcMMOPlayer.getPtpTimeout() + ptpRequestExpire) * Misc.TIME_CONVERSION_FACTOR < System.currentTimeMillis()) {
            mcMMOPlayer.removePtpRequest();
            player.sendMessage(LocaleLoader.getString("Commands.ptp.RequestExpired"));
            return true;
        }

        Player target = mcMMOPlayer.getPtpRequest();

        if (target == null) {
            player.sendMessage(LocaleLoader.getString("Party.Player.Invalid"));
            return true;
        }

        if (target.isDead()) {
            player.sendMessage(LocaleLoader.getString("Party.Teleport.Dead"));
            return true;
        }

        if(Config.getInstance().getPTPCommandWorldPermissions()) {
            String perm = "mcmmo.commands.ptp.world.";

            if(!Permissions.hasDynamicPermission(target, perm + "all", "op")) {
                if(!Permissions.hasDynamicPermission(target, perm + target.getWorld().getName(), "op")) {
                    return true;
                }
                else if(target.getWorld() != player.getWorld() && !Permissions.hasDynamicPermission(target, perm + player.getWorld().getName(), "op")) {
                    return true;
                }
            }
        }

        McMMOPartyTeleportEvent event = new McMMOPartyTeleportEvent(target, player, mcMMOPlayer.getParty().getName());
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return true;
        }

        target.teleport(player);
        target.sendMessage(LocaleLoader.getString("Party.Teleport.Player", player.getName()));
        player.sendMessage(LocaleLoader.getString("Party.Teleport.Target", target.getName()));
        mcMMOPlayer.getProfile().setRecentlyHurt(System.currentTimeMillis());
        return true;
    }

    private boolean acceptAnyTeleportRequest() {
        if (CommandHelper.noCommandPermissions(player, "mcmmo.commands.ptp.acceptall")) {
            return true;
        }

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
        if (CommandHelper.noCommandPermissions(player, "mcmmo.commands.ptp.toggle")) {
            return true;
        }

        if (mcMMOPlayer.getPtpEnabled()) {
            player.sendMessage(LocaleLoader.getString("Commands.ptp.Disabled"));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Commands.ptp.Enabled"));
        }

        mcMMOPlayer.togglePtpUse();
        return true;
    }
}
