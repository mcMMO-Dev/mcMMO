package com.gmail.nossr50.commands.admin;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * @author Mark Vainomaa
 */
public final class McmmoReloadLocaleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label,
            String[] args) {
        if (args.length == 0) {
            if (!Permissions.reloadlocale(sender)) {
                sender.sendMessage(command.getPermissionMessage());
                return true;
            }

            LocaleLoader.reloadLocale();
            sender.sendMessage(LocaleLoader.getString("Locale.Reloaded"));

            return true;
        }
        return false;
    }
}
