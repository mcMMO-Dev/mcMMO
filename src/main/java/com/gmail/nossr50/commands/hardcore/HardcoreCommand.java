package com.gmail.nossr50.commands.hardcore;

import java.text.DecimalFormat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;

public class HardcoreCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
                if (!Permissions.hardcoreToggle(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (Config.getInstance().getHardcoreEnabled()) {
                    disableHardcore();
                }
                else {
                    enableHardcore();
                }

                return true;

            case 1:
                if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("enabled")) {
                    if (!Permissions.hardcoreToggle(sender)) {
                        sender.sendMessage(command.getPermissionMessage());
                        return true;
                    }

                    enableHardcore();
                    return true;
                }

                if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("false") || args[0].equalsIgnoreCase("disabled")) {
                    if (!Permissions.hardcoreToggle(sender)) {
                        sender.sendMessage(command.getPermissionMessage());
                        return true;
                    }

                    disableHardcore();
                    return true;
                }

                if (!StringUtils.isDouble(args[0])) {
                    return false;
                }

                if (!Permissions.hardcoreModify(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                DecimalFormat percent = new DecimalFormat("##0.00%");
                double newPercent = Double.parseDouble(args[0]);

                Config.getInstance().setHardcoreDeathStatPenaltyPercentage(newPercent);
                sender.sendMessage(LocaleLoader.getString("Hardcore.PercentageChanged", percent.format(newPercent / 100D)));
                return true;

            default:
                return false;
        }
    }

    private void disableHardcore() {
        Config.getInstance().setHardcoreEnabled(false);
        mcMMO.p.getServer().broadcastMessage(LocaleLoader.getString("Hardcore.Disabled"));
    }

    private void enableHardcore() {
        Config.getInstance().setHardcoreEnabled(true);
        mcMMO.p.getServer().broadcastMessage(LocaleLoader.getString("Hardcore.Enabled"));
    }
}
