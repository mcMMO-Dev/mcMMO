package com.gmail.nossr50.commands.chat;

import com.gmail.nossr50.commands.ToggleCommand;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class McChatSpy extends ToggleCommand {
    @Override
    protected boolean hasOtherPermission(@NotNull CommandSender sender) {
        return Permissions.adminChatSpyOthers(sender);
    }

    @Override
    protected boolean hasSelfPermission(@NotNull CommandSender sender) {
        return Permissions.adminChatSpy(sender);
    }

    @Override
    protected void applyCommandAction(@NotNull McMMOPlayer mmoPlayer) {
        mmoPlayer.getPlayer().sendMessage(LocaleLoader.getString("Commands.AdminChatSpy." + (mmoPlayer.isPartyChatSpying() ? "Disabled" : "Enabled")));
        mmoPlayer.togglePartyChatSpying();
    }

    @Override
    protected void sendSuccessMessage(@NotNull CommandSender sender, @NotNull String playerName) {
        sender.sendMessage(LocaleLoader.getString("Commands.AdminChatSpy.Toggle", playerName));
    }
}
