package com.gmail.nossr50.commands.party.teleport;

import com.gmail.nossr50.datatypes.party.PartyTeleportRecord;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class PtpAcceptAnyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!Permissions.partyTeleportAcceptAll(sender)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        PartyTeleportRecord ptpRecord = UserManager.getPlayer(sender.getName()).getPartyTeleportRecord();

        if (ptpRecord.isConfirmRequired()) {
            sender.sendMessage(LocaleLoader.getString("Commands.ptp.AcceptAny.Disabled"));
        } else {
            sender.sendMessage(LocaleLoader.getString("Commands.ptp.AcceptAny.Enabled"));
        }

        ptpRecord.toggleConfirmRequired();
        return true;
    }
}
