package com.gmail.nossr50.commands.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;

public class XprateCommand implements CommandExecutor {
    private static double originalRate = Config.getInstance().getExperienceGainsGlobalMultiplier();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String usage1 = LocaleLoader.getString("Commands.xprate.proper.0");
        String usage2 = LocaleLoader.getString("Commands.xprate.proper.1");
        String usage3 = LocaleLoader.getString("Commands.xprate.proper.2");

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.xprate")) {
            return true;
        }

        boolean xpEventEnabled = mcMMO.p.isXPEventEnabled();

        switch (args.length) {
        case 1:
            if (args[0].equalsIgnoreCase("reset")) {
                if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.xprate.reset")) {
                    return true;
                }

                if (xpEventEnabled) {
                    for (Player x : mcMMO.p.getServer().getOnlinePlayers()) {
                        x.sendMessage(LocaleLoader.getString("Commands.xprate.over"));
                    }

                    mcMMO.p.setXPEventEnabled(!xpEventEnabled);
                    Config.getInstance().setExperienceGainsGlobalMultiplier(originalRate);
                }
                else {
                    Config.getInstance().setExperienceGainsGlobalMultiplier(originalRate);
                }
            }
            else if (Misc.isInt(args[0])) {
                sender.sendMessage(usage3);
            }
            else {
                sender.sendMessage(usage2);
            }

            return true;

        case 2:
            if (Misc.isInt(args[0])) {
                if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.xprate.set")) {
                    return true;
                }
                if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")) {
                    mcMMO.p.setXPEventEnabled(Boolean.valueOf(args[1]));
                }
                else {
                    sender.sendMessage(usage3);
                }

                int newRate = Misc.getInt(args[0]);
                Config.getInstance().setExperienceGainsGlobalMultiplier(newRate);

                if (xpEventEnabled) {
                    for (Player x : mcMMO.p.getServer().getOnlinePlayers()) {
                        x.sendMessage(LocaleLoader.getString("Commands.xprate.started.0"));
                        x.sendMessage(LocaleLoader.getString("Commands.xprate.started.1", new Object[] {newRate}));
                    }
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.xprate.modified", new Object[] {newRate}));
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
