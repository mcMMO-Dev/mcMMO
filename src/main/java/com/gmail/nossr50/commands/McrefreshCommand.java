package com.gmail.nossr50.commands;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class McrefreshCommand extends ToggleCommand {
    @Override
    protected boolean hasOtherPermission(@NotNull CommandSender sender) {
        return Permissions.mcrefreshOthers(sender);
    }

    @Override
    protected boolean hasSelfPermission(@NotNull CommandSender sender) {
        return Permissions.mcrefresh(sender);
    }

    @Override
    protected void applyCommandAction(@NotNull OnlineMMOPlayer mmoPlayer) {
        mmoPlayer.setRecentlyHurtTimestamp(0);
        mmoPlayer.getSuperAbilityManager().resetCooldowns();
        mmoPlayer.getSuperAbilityManager().unprimeAllAbilityTools();
        mmoPlayer.getSuperAbilityManager().disableSuperAbilities();

        Misc.adaptPlayer(mmoPlayer).sendMessage(LocaleLoader.getString("Ability.Generic.Refresh"));
    }

    @Override
    protected void sendSuccessMessage(@NotNull CommandSender sender, @NotNull String playerName) {
        sender.sendMessage(LocaleLoader.getString("Commands.mcrefresh.Success", playerName));
    }
}
