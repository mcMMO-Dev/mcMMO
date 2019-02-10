package com.gmail.nossr50.commands.party.teleport;

import com.gmail.nossr50.core.data.UserManager;
import com.gmail.nossr50.core.datatypes.party.PartyTeleportRecord;
import com.gmail.nossr50.core.locale.LocaleLoader;
import com.gmail.nossr50.core.util.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PtpAcceptAnyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
