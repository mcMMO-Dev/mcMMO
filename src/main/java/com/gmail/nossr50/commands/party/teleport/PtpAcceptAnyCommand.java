package com.gmail.nossr50.commands.party.teleport;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.PartyMember;
import com.gmail.nossr50.datatypes.party.PartyTeleportRecord;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PtpAcceptAnyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!Permissions.partyTeleportAcceptAll(sender)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        if(sender instanceof ConsoleCommandSender)
            return false;

        Player playerSender = (Player) sender;
        McMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(playerSender);

        if(mmoPlayer == null) {
            playerSender.sendMessage(LocaleLoader.getString("Commands.NotLoaded"));
            return false;
        }

        Party party = mcMMO.getPartyManager().getParty(playerSender);
        if(party == null) {
            //TODO: Localize error message
            playerSender.sendMessage("You don't have a party!");
            return false;
        }

        PartyMember partyMember = party.getPartyMember(playerSender.getUniqueId());

        if(partyMember == null) {
            mcMMO.p.getLogger().severe("PartyMember ref didn't exist for player named "+playerSender.getName() + ", this should never happen!");
            return false;
        }

        PartyTeleportRecord ptpRecord = partyMember.getPartyTeleportRecord();

        if(ptpRecord == null) {

        }

        if (ptpRecord.isConfirmRequired()) {
            sender.sendMessage(LocaleLoader.getString("Commands.ptp.AcceptAny.Disabled"));
        }
        else {
            sender.sendMessage(LocaleLoader.getString("Commands.ptp.AcceptAny.Enabled"));
        }

        ptpRecord.toggleConfirmRequired();
        return true;
    }
}
