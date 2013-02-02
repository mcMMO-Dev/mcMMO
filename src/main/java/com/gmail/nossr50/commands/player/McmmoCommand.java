package com.gmail.nossr50.commands.player;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Anniversary;

public class McmmoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String description = LocaleLoader.getString("mcMMO.Description");
        String[] mcSplit = description.split(",");
        sender.sendMessage(mcSplit);

        if (Config.getInstance().getDonateMessageEnabled()) {
            if (mcMMO.spoutEnabled && sender instanceof SpoutPlayer) {
                SpoutPlayer spoutPlayer = (SpoutPlayer) sender;

                spoutPlayer.sendNotification(LocaleLoader.getString("Spout.Donate"), ChatColor.GREEN + "gjmcferrin@gmail.com", Material.DIAMOND);
            }

            sender.sendMessage(LocaleLoader.getString("MOTD.Donate"));
            sender.sendMessage(ChatColor.GOLD + " - " + ChatColor.GREEN + "gjmcferrin@gmail.com" + ChatColor.GOLD + " Paypal");
        }
        sender.sendMessage(LocaleLoader.getString("MOTD.Version", mcMMO.p.getDescription().getVersion()));

        Anniversary.anniversaryCheck(sender);
        return true;
    }
}
