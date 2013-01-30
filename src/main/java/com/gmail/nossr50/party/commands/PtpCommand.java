package com.gmail.nossr50.party.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.events.party.McMMOPartyTeleportEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Users;

public class PtpCommand implements CommandExecutor {
    private final mcMMO plugin;
    private Player player;
    private PlayerProfile playerProfile;

    public PtpCommand(mcMMO instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String usage = LocaleLoader.getString("Commands.Usage.1", new Object[] { "ptp", "<" + LocaleLoader.getString("Commands.Usage.Player") + ">" });

        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.ptp")) {
            return true;
        }

        switch (args.length) {
        case 1:
            this.player = (Player) sender;
            this.playerProfile = Users.getProfile(player);

            if (args[0].equalsIgnoreCase("toggle")) {
                return togglePartyTeleportation();
            }
            else if (args[0].equalsIgnoreCase("acceptany") || args[0].equalsIgnoreCase("acceptall")) {
                return acceptAnyTeleportRequest();
            }

            int ptpCooldown = Config.getInstance().getPTPCommandCooldown();

            if (playerProfile.getRecentlyHurt() + (ptpCooldown * Misc.TIME_CONVERSION_FACTOR) > System.currentTimeMillis()) {
                player.sendMessage(LocaleLoader.getString("Party.Teleport.Hurt", new Object[] { ptpCooldown }));
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
        Player target = plugin.getServer().getPlayer(args);

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
            PlayerProfile targetProfile = Users.getProfile(target);

            if (!targetProfile.getPtpEnabled()) {
                player.sendMessage(LocaleLoader.getString("Party.Teleport.Disabled", new Object[] { target.getName() }));
                return true;
            }

            if (!Users.getProfile(target).getPtpConfirmRequired()) {
                McMMOPartyTeleportEvent event = new McMMOPartyTeleportEvent(player, target, playerProfile.getParty().getName());
                plugin.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return true;
                }

                player.teleport(target);
                player.sendMessage(LocaleLoader.getString("Party.Teleport.Player", new Object[] { player.getName() }));
                target.sendMessage(LocaleLoader.getString("Party.Teleport.Target", new Object[] { target.getName() }));
                playerProfile.setRecentlyHurt(System.currentTimeMillis());
            } else {
                targetProfile.setPtpRequest(player);
                targetProfile.actualizePtpTimeout();
                player.sendMessage(LocaleLoader.getString("Commands.Invite.Success"));

                int ptpRequestExpire = Config.getInstance().getPTPCommandTimeout();
                target.sendMessage(LocaleLoader.getString("Commands.ptp.Request1", new Object[] { player.getName() }));
                target.sendMessage(LocaleLoader.getString("Commands.ptp.Request2", new Object[] { ptpRequestExpire }));
            }
        }
        else {
            player.sendMessage(LocaleLoader.getString("Party.NotInYourParty", new Object[] { target.getName() }));
        }
        return true;
    }

    private boolean acceptTeleportRequest() {
        if (!playerProfile.hasPtpRequest()) {
            player.sendMessage(LocaleLoader.getString("Commands.ptp.NoRequests"));
            return true;
        }

        int ptpRequestExpire = Config.getInstance().getPTPCommandTimeout();

        if ((playerProfile.getPtpTimeout() + ptpRequestExpire) * Misc.TIME_CONVERSION_FACTOR < System.currentTimeMillis()) {
            playerProfile.removePtpRequest();
            player.sendMessage(LocaleLoader.getString("Commands.ptp.RequestExpired"));
            return true;
        }

        Player target = playerProfile.getPtpRequest();

        if (target == null) {
            player.sendMessage(LocaleLoader.getString("Party.Player.Invalid"));
            return true;
        }

        if (target.isDead()) {
            player.sendMessage(LocaleLoader.getString("Party.Teleport.Dead"));
            return true;
        }

        McMMOPartyTeleportEvent event = new McMMOPartyTeleportEvent(player, target, playerProfile.getParty().getName());
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return true;
        }

        target.teleport(player);
        target.sendMessage(LocaleLoader.getString("Party.Teleport.Player", new Object[] { player.getName() }));
        player.sendMessage(LocaleLoader.getString("Party.Teleport.Target", new Object[] { target.getName() }));
        playerProfile.setRecentlyHurt(System.currentTimeMillis());
        return true;
    }

    private boolean acceptAnyTeleportRequest() {
        if (playerProfile.getPtpConfirmRequired()) {
            player.sendMessage(LocaleLoader.getString("Commands.ptp.AcceptAny.Disabled"));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Commands.ptp.AcceptAny.Enabled"));
        }

        playerProfile.togglePtpConfirmRequired();
        return true;
    }

    private boolean togglePartyTeleportation() {
        if (playerProfile.getPtpEnabled()) {
            player.sendMessage(LocaleLoader.getString("Commands.ptp.Disabled"));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Commands.ptp.Enabled"));
        }

        playerProfile.togglePtpUse();
        return true;
    }
}
