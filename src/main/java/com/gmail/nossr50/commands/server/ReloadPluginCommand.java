package com.gmail.nossr50.commands.server;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReloadPluginCommand implements CommandExecutor {

    private final mcMMO pluginRef;

    public ReloadPluginCommand(mcMMO plugin) {
        this.pluginRef = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            if (!Permissions.reload(sender))
                return false;
        }

        Bukkit.broadcastMessage(pluginRef.getLocaleManager().getString("Commands.Reload.Start"));
        pluginRef.reload();
        Bukkit.broadcastMessage(pluginRef.getLocaleManager().getString("Commands.Reload.Finished"));
        return true;
    }

}
