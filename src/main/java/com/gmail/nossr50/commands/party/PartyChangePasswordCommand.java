package com.gmail.nossr50.commands.party;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.player.UserManager;

public class PartyChangePasswordCommand implements CommandExecutor {
    private Party playerParty;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        playerParty = UserManager.getPlayer(sender.getName()).getParty();

        switch (args.length) {
            case 1:
                unprotectParty(sender);
                return true;

            case 2:
                if (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("reset")) {
                    unprotectParty(sender);
                    return true;
                }

                protectParty(sender, args[1]);
                return true;

            default:
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "password", "[clear|reset]"));
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "password", "<" + LocaleLoader.getString("Commands.Usage.Password") + ">"));
                return true;
        }
    }

    private void unprotectParty(CommandSender sender) {
        playerParty.setLocked(true);
        playerParty.setPassword(null);
        sender.sendMessage(LocaleLoader.getString("Party.Password.Removed"));
    }

    private void protectParty(CommandSender sender, String password) {
        playerParty.setLocked(true);
        playerParty.setPassword(password);
        sender.sendMessage(LocaleLoader.getString("Party.Password.Set", password));
    }
}
