package com.gmail.nossr50.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;

public class XprateCommand implements CommandExecutor {
    private double originalRate;

    public XprateCommand() {
        originalRate = Config.getInstance().getExperienceGainsGlobalMultiplier();
    }

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
                if (CommandUtils.isInvalidInteger(sender, args[0])) {
                    return true;
                }

                if (!Permissions.xprateSet(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (CommandUtils.shouldDisableToggle(args[1])) {
                    mcMMO.p.setXPEventEnabled(false);
                }
                else if (CommandUtils.shouldEnableToggle(args[1])) {
                    mcMMO.p.setXPEventEnabled(true);
                }
                else {
                    return false;
                }

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
