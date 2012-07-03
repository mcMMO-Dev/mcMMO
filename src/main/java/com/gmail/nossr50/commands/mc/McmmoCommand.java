package com.gmail.nossr50.commands.mc;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;

public class McmmoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String description = LocaleLoader.getString("mcMMO.Description");
        String[] mcSplit = description.split(",");
        sender.sendMessage(mcSplit);

        if (Config.getInstance().getDonateMessageEnabled()) {
            if (mcMMO.spoutEnabled && sender instanceof SpoutPlayer) {
                SpoutPlayer spoutPlayer = (SpoutPlayer) sender;

                spoutPlayer.sendNotification(ChatColor.YELLOW + "[mcMMO]" + ChatColor.GOLD + " Donate!", ChatColor.GREEN + "mcmmodev@gmail.com", Material.DIAMOND);
            }

            sender.sendMessage(ChatColor.DARK_AQUA + "Donation Info:");
            sender.sendMessage(ChatColor.GOLD + " - " + ChatColor.GREEN + "mcmmodev@gmail.com" + ChatColor.GOLD + " Paypal");
        }

        return true;
    }
}
