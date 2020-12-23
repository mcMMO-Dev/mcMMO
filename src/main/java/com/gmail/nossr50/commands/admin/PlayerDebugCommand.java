package com.gmail.nossr50.commands.admin;

import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.NotificationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerDebugCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(sender instanceof Player) {
            OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer((Player) sender);

            if(mmoPlayer == null) {
                sender.sendMessage(LocaleLoader.getString("Commands.NotLoaded"));
                return true;
            }

            mmoPlayer.toggleDebugMode(); //Toggle debug mode
            NotificationManager.sendPlayerInformationChatOnlyPrefixed(Misc.adaptPlayer(mmoPlayer), "Commands.Mmodebug.Toggle", String.valueOf(mmoPlayer.isDebugMode()));
            return true;
        } else {
            return false;
        }
    }

}
