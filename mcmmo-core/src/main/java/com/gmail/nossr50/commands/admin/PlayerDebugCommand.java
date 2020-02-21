package com.gmail.nossr50.commands.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Description;
import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.mcMMO;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@CommandAlias("mmodebug")
@Description("Puts the player into debug mode, which helps problem solve bugs in mcMMO.")
public class PlayerDebugCommand extends BaseCommand {

    @Dependency
    private mcMMO plugin;

    @Default
    public void onCommand(CommandSender sender) {
        if(sender instanceof Player) {
            BukkitMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer((Player) sender);
            mcMMOPlayer.toggleDebugMode(); //Toggle debug mode
            pluginRef.getNotificationManager().sendPlayerInformationChatOnlyPrefixed(mcMMOPlayer.getNative(), "Commands.Mmodebug.Toggle", String.valueOf(mcMMOPlayer.isDebugMode()));
        } else {
            //TODO: Localize
            sender.sendMessage("Players only");
        }
    }

}
