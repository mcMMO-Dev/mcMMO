package com.gmail.nossr50.commands.mc;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.mcMMO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class McmmoCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!LoadProperties.mcmmoEnable) {
			sender.sendMessage("This command is not enabled.");
			return true;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage.");
			return true;
		}

		Player player = (Player) sender;

		player.sendMessage(ChatColor.RED + "-----[]" + ChatColor.GREEN + "mcMMO" + ChatColor.RED + "[]-----");
		String description = mcLocale.getString("mcMMO.Description", new Object[] { "/mcc" });
		String[] mcSplit = description.split(",");

		for (String x : mcSplit) {
			player.sendMessage(x);
		}

		if (LoadProperties.spoutEnabled && player instanceof SpoutPlayer) {
			SpoutPlayer sPlayer = (SpoutPlayer) player;
			if (LoadProperties.donateMessage)
				player.sendMessage(ChatColor.GREEN + "[mcMMO] Donate! Paypal theno1yeti@gmail.com");
		} else {
			if (LoadProperties.donateMessage)
				player.sendMessage(ChatColor.GREEN + "If you like my work you can donate via Paypal: theno1yeti@gmail.com");
		}

        GregorianCalendar cakedayStart =  new GregorianCalendar(2012, Calendar.FEBRUARY, 3);
        GregorianCalendar cakedayEnd = new GregorianCalendar(2012, Calendar.FEBRUARY, 6);
        GregorianCalendar day = new GregorianCalendar();
        int cakeCheck = 0;

        for (String cake : mcMMO.gotCake)
        {
            if (player.getName().equalsIgnoreCase(cake)) {
                cakeCheck = 1;
            }
        }

        if (cakeCheck == 0) {
             if (getDateRange(day.getTime(), cakedayStart.getTime(), cakedayEnd.getTime()))
            {
                player.sendMessage(ChatColor.BLUE + "Happy 1 Year Anniversary!  In honor of all of");
                player.sendMessage(ChatColor.BLUE + "nossr50's work and all the devs, have some cake!");
            }
            mcMMO.gotCake.add(player.getName());
            player.getInventory().addItem(new ItemStack(Material.CAKE_BLOCK, 1));
        }

		return true;
	}

    private boolean getDateRange(Date date, Date start, Date end)
    {
        return !(date.before(start) || date.after(end));
    }
}
