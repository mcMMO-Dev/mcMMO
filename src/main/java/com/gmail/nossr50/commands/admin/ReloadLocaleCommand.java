package com.gmail.nossr50.commands.admin;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author Mark Vainomaa
 */
public final class ReloadLocaleCommand implements CommandExecutor {

    private final mcMMO pluginRef;

    public ReloadLocaleCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!Permissions.reloadlocale(sender)) {
                if(command.getPermissionMessage() != null)
                    sender.sendMessage(command.getPermissionMessage());

                return true;
            }

            pluginRef.getLocaleManager().reloadLocale();
            sender.sendMessage(pluginRef.getLocaleManager().getString("Locale.Reloaded"));

            return true;
        }
        return false;
    }
}
