package com.gmail.nossr50.party.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class PartyChangeOwnerCommand implements CommandExecutor {
    private Player player;
    private Party playerParty;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.hasPermission(sender, "mcmmo.commands.party.owner")) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        switch (args.length) {
        case 2:
            player = (Player) sender;
            playerParty = Users.getPlayer(player).getParty();

            if (!playerParty.getLeader().equals(player.getName())) {
                sender.sendMessage(LocaleLoader.getString("Party.NotOwner"));
                return true;
            }

            if (!playerParty.getMembers().contains(mcMMO.p.getServer().getOfflinePlayer(args[1]))) {
                player.sendMessage(LocaleLoader.getString("Party.NotInYourParty", args[1]));
                return true;
            }

            PartyManager.setPartyLeader(args[1], playerParty);
            return true;

        default:
            sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "owner", "<" + LocaleLoader.getString("Commands.Usage.Player") + ">"));
            return true;
        }
    }
}
