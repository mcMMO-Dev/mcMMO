package com.gmail.nossr50.commands.server;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            if (!Permissions.reload(sender))
                return false;
        }

        Bukkit.broadcastMessage(LocaleLoader.getString("Commands.Reload.Start"));
        mcMMO.getConfigManager().reloadConfigs();
        Bukkit.broadcastMessage(LocaleLoader.getString("Commands.Reload.Finished"));
        return true;
    }
}
