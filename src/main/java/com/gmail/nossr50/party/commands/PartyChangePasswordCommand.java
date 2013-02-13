package com.gmail.nossr50.party.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class PartyChangePasswordCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.hasPermission(sender, "mcmmo.commands.party.password")) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        Party playerParty = Users.getPlayer((Player) sender).getParty();
        switch (args.length) {
        case 1:
            unprotectParty(sender, playerParty);
            return true;

        case 2:
            if (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("reset")) {
                unprotectParty(sender, playerParty);
                return true;
            }

            protectParty(sender, playerParty, args[1]);
            return true;

        default:
            sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "password", "[clear|reset]"));
            sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "password", "<" + LocaleLoader.getString("Commands.Usage.Password") + ">"));
            return true;
        }
    }

    private void unprotectParty(CommandSender sender, Party playerParty) {
        playerParty.setLocked(true);
        playerParty.setPassword(null);
        sender.sendMessage(LocaleLoader.getString("Party.Password.Removed"));
    }

    private void protectParty(CommandSender sender, Party playerParty, String password) {
        playerParty.setLocked(true);
        playerParty.setPassword(password);
        sender.sendMessage(LocaleLoader.getString("Party.Password.Set", password));
    }
}
