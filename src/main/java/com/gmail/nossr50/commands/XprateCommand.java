package com.gmail.nossr50.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;

public class XprateCommand implements CommandExecutor {
    private static double originalRate = Config.getInstance().getExperienceGainsGlobalMultiplier();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                if (!args[0].equalsIgnoreCase("reset")) {
                    return false;
                }

                if (!Permissions.xprateReset(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (mcMMO.p.isXPEventEnabled()) {
                    mcMMO.p.getServer().broadcastMessage(LocaleLoader.getString("Commands.xprate.over"));
                    mcMMO.p.toggleXpEventEnabled();
                }

                Config.getInstance().setExperienceGainsGlobalMultiplier(originalRate);
                return true;

            case 2:
                if (!StringUtils.isInt(args[0])) {
                    return false;
                }

                if (!Permissions.xprateSet(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (!args[1].equalsIgnoreCase("true") && !args[1].equalsIgnoreCase("false")) {
                    return false;
                }

                mcMMO.p.setXPEventEnabled(Boolean.valueOf(args[1]));
                int newXpRate = Integer.parseInt(args[0]);
                Config.getInstance().setExperienceGainsGlobalMultiplier(newXpRate);

                if (mcMMO.p.isXPEventEnabled()) {
                    mcMMO.p.getServer().broadcastMessage(LocaleLoader.getString("Commands.xprate.started.0"));
                    mcMMO.p.getServer().broadcastMessage(LocaleLoader.getString("Commands.xprate.started.1", newXpRate));
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.xprate.modified", newXpRate));
                }

                return true;

            default:
                return false;
        }
    }
}
