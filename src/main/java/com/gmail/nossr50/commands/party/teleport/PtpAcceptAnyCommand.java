package com.gmail.nossr50.commands.party.teleport;

import com.gmail.nossr50.datatypes.party.PartyTeleportRecord;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PtpAcceptAnyCommand implements CommandExecutor {
    private mcMMO pluginRef;

    public PtpAcceptAnyCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.partyTeleportAcceptAll(sender)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        PartyTeleportRecord ptpRecord = pluginRef.getUserManager().getPlayer(sender.getName()).getPartyTeleportRecord();

        if (ptpRecord.isConfirmRequired()) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.ptp.AcceptAny.Disabled"));
        } else {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.ptp.AcceptAny.Enabled"));
        }

        ptpRecord.toggleConfirmRequired();
        return true;
    }
}
