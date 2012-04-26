package com.gmail.nossr50.commands.party;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.events.party.McMMOPartyTeleportEvent;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;

public class PtpCommand implements CommandExecutor {
    private final mcMMO plugin;

    public PtpCommand(mcMMO instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String usage = ChatColor.RED + "Proper usage is /ptp <player>"; //TODO: Needs more locale.

        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.ptp")) {
            return true;
        }

        switch (args.length) {
        case 1:
            Player player = (Player) sender;
            PlayerProfile PP = Users.getProfile(player);

            if (!Party.getInstance().isInParty(player, PP)) {
                player.sendMessage(mcLocale.getString("Commands.Party.None"));
                return true;
            }

            if (PP.getRecentlyHurt() + (Config.getPTPCommandCooldown() * 1000) > System.currentTimeMillis()) {
                player.sendMessage(mcLocale.getString("Party.Teleport.Hurt", new Object[] { Config.getPTPCommandCooldown() }));
                return true;
            }

            Player target = plugin.getServer().getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(mcLocale.getString("Party.Teleport.Invalid"));
                return true;
            }

            if (target.isDead()) {
                player.sendMessage(mcLocale.getString("Party.Teleport.Dead"));
                return true;
            }

            if (Party.getInstance().inSameParty(player, target)) {
                McMMOPartyTeleportEvent event = new McMMOPartyTeleportEvent(player, target, PP.getParty());
                plugin.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return true;
                }

                player.teleport(target);
                player.sendMessage(mcLocale.getString("Party.Teleport.Player", new Object[] { target.getName() }));
                target.sendMessage(mcLocale.getString("Party.Teleport.Target", new Object[] { player.getName() }));
            }
            else {
                player.sendMessage(mcLocale.getString("Party.NotInYourParty", new Object[] { target.getName() }));
                return true;
            }

            return true;

        default:
            sender.sendMessage(usage);
            return true;
        }
   }
}