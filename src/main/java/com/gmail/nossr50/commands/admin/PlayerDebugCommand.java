package com.gmail.nossr50.commands.admin;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerDebugCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label,
            String[] args) {
        if (sender instanceof Player) {
            final McMMOPlayer mmoPlayer = UserManager.getPlayer((Player) sender);
            mmoPlayer.toggleDebugMode(); //Toggle debug mode
            NotificationManager.sendPlayerInformationChatOnlyPrefixed(mmoPlayer.getPlayer(),
                    "Commands.Mmodebug.Toggle", String.valueOf(mmoPlayer.isDebugMode()));
            return true;
        } else {
            return false;
        }
    }

}
