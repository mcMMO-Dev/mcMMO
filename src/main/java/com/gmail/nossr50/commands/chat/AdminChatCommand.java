package com.gmail.nossr50.commands.chat;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import com.gmail.nossr50.commands.CommandManager;
import com.gmail.nossr50.datatypes.chat.ChatChannel;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import org.jetbrains.annotations.NotNull;

@CommandPermission("mcmmo.chat.adminchat")
@CommandAlias("ac|a|adminchat|achat") //Kept for historical reasons
public class AdminChatCommand extends BaseCommand {
    private final @NotNull mcMMO pluginRef;

    public AdminChatCommand(@NotNull mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Default
    @Conditions(CommandManager.ADMIN_CONDITION)
    public void processCommand(String[] args) {
        final BukkitCommandIssuer bukkitCommandIssuer = (BukkitCommandIssuer) getCurrentCommandIssuer();
        if (args == null || args.length == 0) {
            //Process with no arguments
            if (bukkitCommandIssuer.isPlayer()) {
                final McMMOPlayer mmoPlayer = UserManager.getPlayer(
                        bukkitCommandIssuer.getPlayer());
                pluginRef.getChatManager().setOrToggleChatChannel(mmoPlayer, ChatChannel.ADMIN);
            } else {
                //Not support for console
                mcMMO.p.getLogger()
                        .info("You cannot switch chat channels as console, please provide full arguments.");
            }
        } else {
            if (bukkitCommandIssuer.isPlayer()) {
                final McMMOPlayer mmoPlayer = UserManager.getPlayer(
                        bukkitCommandIssuer.getPlayer());

                if (mmoPlayer == null) {
                    return;
                }

                //Message contains the original command so it needs to be passed to this method to trim it
                pluginRef.getChatManager().processPlayerMessage(mmoPlayer, args, ChatChannel.ADMIN);
            } else {
                pluginRef.getChatManager().processConsoleMessage(args);
            }
            //Arguments are greater than 0, therefore directly send message and skip toggles
        }
    }
}
