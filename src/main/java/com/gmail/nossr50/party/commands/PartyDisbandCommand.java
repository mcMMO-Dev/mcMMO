package com.gmail.nossr50.party.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class PartyDisbandCommand implements CommandExecutor {
    private Player player;
    private Party playerParty;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.hasPermission(sender, "mcmmo.commands.party.disband")) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        player = (Player) sender;
        playerParty = Users.getPlayer(player).getParty();

        if (!playerParty.getLeader().equals(player.getName())) {
            sender.sendMessage(LocaleLoader.getString("Party.NotOwner"));
            return true;
        }

        for (Player member : playerParty.getOnlineMembers()) {
            if (!PartyManager.handlePartyChangeEvent(member, playerParty.getName(), null, EventReason.KICKED_FROM_PARTY)) {
                return true;
            }

            member.sendMessage(LocaleLoader.getString("Party.Disband"));
        }

        PartyManager.disbandParty(playerParty);
        return true;

    }

}
