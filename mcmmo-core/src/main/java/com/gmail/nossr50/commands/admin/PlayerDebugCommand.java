package com.gmail.nossr50.commands.admin;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerDebugCommand implements CommandExecutor {

    private final mcMMO pluginRef;

    public PlayerDebugCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer((Player) sender);
            mcMMOPlayer.toggleDebugMode(); //Toggle debug mode
            pluginRef.getNotificationManager().sendPlayerInformationChatOnlyPrefixed(mcMMOPlayer.getPlayer(), "Commands.Mmodebug.Toggle", String.valueOf(mcMMOPlayer.isDebugMode()));
            return true;
        } else {
            return false;
        }
    }

}
