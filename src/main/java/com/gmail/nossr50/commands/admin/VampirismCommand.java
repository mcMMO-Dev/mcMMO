package com.gmail.nossr50.commands.admin;

import java.text.DecimalFormat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;

public class VampirismCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Config.getInstance().getHardcoreEnabled()) {
            sender.sendMessage(LocaleLoader.getString("Hardcore.Disabled"));
            return true;
        }

        switch (args.length) {
        case 0:
            if (!sender.hasPermission("mcmmo.commands.vampirism.toggle")) {
                sender.sendMessage(command.getPermissionMessage());
                return true;
            }

            if (Config.getInstance().getHardcoreVampirismEnabled()) {
                disableVampirism();
            }
            else {
                enableVampirism();
            }

            return true;

        case 1:
            if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("enabled")) {
                if (!sender.hasPermission("mcmmo.commands.vampirism.toggle")) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                enableVampirism();
                return true;
            }

            if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("false") || args[0].equalsIgnoreCase("disabled")) {
                if (!sender.hasPermission("mcmmo.commands.vampirism.toggle")) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                disableVampirism();
                return true;
            }

            if (!Misc.isDouble(args[0])) {
                return false;
            }

            if (!sender.hasPermission("mcmmo.commands.vampirism.modify")) {
                sender.sendMessage(command.getPermissionMessage());
                return true;
            }

            DecimalFormat percent = new DecimalFormat("##0.00%");
            double newPercent = Misc.getDouble(args[0]);

            Config.getInstance().setHardcoreVampirismStatLeechPercentage(newPercent);
            sender.sendMessage(LocaleLoader.getString("Vampirism.PercentageChanged", percent.format(newPercent / 100D)));
            return true;

        default:
            return false;
        }
    }

    private void disableVampirism() {
        Config.getInstance().setHardcoreVampirismEnabled(false);
        mcMMO.p.getServer().broadcastMessage(LocaleLoader.getString("Vampirism.Disabled"));
    }

    private void enableVampirism() {
        Config.getInstance().setHardcoreVampirismEnabled(true);
        mcMMO.p.getServer().broadcastMessage(LocaleLoader.getString("Vampirism.Enabled"));
    }
}
