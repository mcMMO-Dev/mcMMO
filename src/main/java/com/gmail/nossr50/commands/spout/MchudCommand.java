package com.gmail.nossr50.commands.spout;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.HUDType;
import com.gmail.nossr50.datatypes.HUDmmo;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.spout.SpoutStuff;

public class MchudCommand implements CommandExecutor {
    private final mcMMO plugin;

    public MchudCommand (mcMMO plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String usage = ChatColor.RED + "Proper usage is /mchud <hud-type>"; //TODO: Locale
        String invalid = ChatColor.RED + "That is not a valid HUD type."; //TODO: Locale

        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (!Config.spoutEnabled || !Config.xpbar) {
            sender.sendMessage(mcLocale.getString("Commands.Disabled"));
            return true;
        }

        switch (args.length) {
        case 1:
            Player player = (Player) sender;
            PlayerProfile PP = Users.getProfile(player);
            HUDType hud;

            if (args[0].equalsIgnoreCase("disabled")) {
                hud = HUDType.DISABLED;
            }
            else if (args[0].equalsIgnoreCase("standard")) {
                hud = HUDType.STANDARD;
            }
            else if (args[0].equalsIgnoreCase("small")) {
                hud = HUDType.SMALL;
            }
            else if (args[0].equalsIgnoreCase("retro")) {
                hud = HUDType.RETRO;
            }
            else {
                player.sendMessage(invalid);
                return true;
            }

            if (SpoutStuff.playerHUDs.containsKey(player)) {
                SpoutStuff.playerHUDs.get(player).resetHUD();
                SpoutStuff.playerHUDs.remove(player);
                PP.setHUDType(hud);
                SpoutStuff.playerHUDs.put(player, new HUDmmo(player, plugin));
            }

            return true;

        default:
            sender.sendMessage(usage);
            return true;
        }
    }
}
