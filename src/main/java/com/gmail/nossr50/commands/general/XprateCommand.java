package com.gmail.nossr50.commands.general;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.mcLocale;

public class XprateCommand implements CommandExecutor {
    private final mcMMO plugin;
    private static int oldrate = Config.xpGainMultiplier;
    public static boolean xpevent = false;

    public XprateCommand (mcMMO plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String usage1 = mcLocale.getString("Commands.xprate.proper.0");
        String usage2 = mcLocale.getString("Commands.xprate.proper.1");
        String usage3 = mcLocale.getString("Commands.xprate.proper.2");

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.admin")) {
            return true;
        }

        switch (args.length) {
        case 1:
            if (args[0].equalsIgnoreCase("reset")) {
                if (xpevent) {
                    for (Player x : plugin.getServer().getOnlinePlayers()) {
                        x.sendMessage(mcLocale.getString("Commands.xprate.over"));
                    }

                    xpevent = !xpevent;
                    Config.xpGainMultiplier = oldrate;
                }
                else {
                    Config.xpGainMultiplier = oldrate;
                }
            }
            else if (m.isInt(args[0])) {
                sender.sendMessage(usage3);
            }
            else {
                sender.sendMessage(usage2);
            }

            return true;

        case 2:
            if (m.isInt(args[0])) {
                oldrate = Config.xpGainMultiplier;

                if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")) {
                    xpevent = Boolean.valueOf(args[1]);
                }
                else {
                    sender.sendMessage(usage3);
                }

                Config.xpGainMultiplier = m.getInt(args[0]);

                if (xpevent) {
                    for (Player x : plugin.getServer().getOnlinePlayers()) {
                        x.sendMessage(mcLocale.getString("Commands.xprate.started.0"));
                        x.sendMessage(mcLocale.getString("Commands.xprate.started.1", new Object[] {Config.xpGainMultiplier}));
                    }
                }
                else {
                    sender.sendMessage("The XP RATE was modified to " + Config.xpGainMultiplier); //TODO: Locale
                }
            }
            else {
                sender.sendMessage(usage1);
                sender.sendMessage(usage2);
            }

            return true;

        default:
            sender.sendMessage(usage1);
            sender.sendMessage(usage2);
            return true;
        }
    }
}
