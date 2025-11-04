package com.gmail.nossr50.commands;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import org.bukkit.command.CommandSender;

public class McgodCommand extends ToggleCommand {
    @Override
    protected boolean hasOtherPermission(CommandSender sender) {
        return Permissions.mcgodOthers(sender);
    }

    @Override
    protected boolean hasSelfPermission(CommandSender sender) {
        return Permissions.mcgod(sender);
    }

    @Override
    protected void applyCommandAction(McMMOPlayer mmoPlayer) {
        mmoPlayer.getPlayer().sendMessage(LocaleLoader.getString(
                "Commands.GodMode." + (mmoPlayer.getGodMode() ? "Disabled" : "Enabled")));
        mmoPlayer.toggleGodMode();
    }

    @Override
    protected void sendSuccessMessage(CommandSender sender, String playerName) {
        sender.sendMessage(LocaleLoader.getString("Commands.GodMode.Toggle", playerName));
    }
}
