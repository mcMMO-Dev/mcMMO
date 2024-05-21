package com.gmail.nossr50.commands.party.teleport;

import com.gmail.nossr50.datatypes.party.PartyTeleportRecord;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class PtpToggleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!Permissions.partyTeleportToggle(sender)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        PartyTeleportRecord ptpRecord = UserManager.getPlayer(sender.getName()).getPartyTeleportRecord();

        if (ptpRecord.isEnabled()) {
            sender.sendMessage(LocaleLoader.getString("Commands.ptp.Disabled"));
        } else {
            sender.sendMessage(LocaleLoader.getString("Commands.ptp.Enabled"));
        }

        ptpRecord.toggleEnabled();
        return true;
    }
}
