package com.gmail.nossr50.commands.party;

import org.bukkit.ChatColor;
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
import com.gmail.nossr50.util.Users;

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
            PlayerProfile profile = Users.getProfile(player);

            if (profile.getRecentlyHurt() + (Config.getInstance().getPTPCommandCooldown() * 1000) > System.currentTimeMillis()) {
                player.sendMessage(LocaleLoader.getString("Party.Teleport.Hurt", new Object[] { Config.getInstance().getPTPCommandCooldown() }));
                return true;
            }

            Player target = plugin.getServer().getPlayer(args[0]);

            if (player.equals(target)) {
                player.sendMessage("You can't teleport to yourself!"); //TODO: Use locale
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

            if (PartyManager.getInstance().inSameParty(player, target)) {
                McMMOPartyTeleportEvent event = new McMMOPartyTeleportEvent(player, target, profile.getParty().getName());
                plugin.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return true;
                }

                player.teleport(target);
                player.sendMessage(LocaleLoader.getString("Party.Teleport.Player", new Object[] { target.getName() }));
                target.sendMessage(LocaleLoader.getString("Party.Teleport.Target", new Object[] { player.getName() }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Party.NotInYourParty", new Object[] { target.getName() }));
                return true;
            }

            return true;

        default:
            sender.sendMessage(usage);
            return true;
        }
   }
}
