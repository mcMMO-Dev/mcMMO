package com.gmail.nossr50.commands;

import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class AbilityToggleCommand extends ToggleCommand {
    @Override
    protected boolean hasOtherPermission(@NotNull CommandSender sender) {
        return Permissions.mcabilityOthers(sender);
    }

    @Override
    protected boolean hasSelfPermission(@NotNull CommandSender sender) {
        return Permissions.mcability(sender);
    }

    @Override
    protected void applyCommandAction(@NotNull OnlineMMOPlayer mmoPlayer) {
        Misc.adaptPlayer(mmoPlayer).sendMessage(LocaleLoader.getString("Commands.Ability." + (mmoPlayer.getSuperAbilityManager().getAbilityActivationPermission() ? "Off" : "On")));
        mmoPlayer.getSuperAbilityManager().toggleAbilityActivationPermission();
    }

    @Override
    protected void sendSuccessMessage(@NotNull CommandSender sender, @NotNull String playerName) {
        sender.sendMessage(LocaleLoader.getString("Commands.Ability.Toggle", playerName));
    }
}
