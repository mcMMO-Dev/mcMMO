package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.mcMMO;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyChangePasswordCommand implements CommandExecutor {

    private final mcMMO pluginRef;

    public PartyChangePasswordCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (pluginRef.getUserManager().getPlayer((Player) sender) == null) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
            return true;
        }

        Party party = pluginRef.getUserManager().getPlayer((Player) sender).getParty();

        switch (args.length) {
            case 1:
                unprotectParty(party, sender);
                return true;

            case 2:
                if (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("reset")) {
                    unprotectParty(party, sender);
                    return true;
                }

                protectParty(party, sender, args[1]);
                return true;

            default:
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Usage.2", "party", "password", "[clear|reset]"));
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Usage.2", "party", "password", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Password") + ">"));
                return true;
        }
    }

    private void unprotectParty(Party party, CommandSender sender) {
        party.setLocked(true);
        party.setPassword(null);
        sender.sendMessage(pluginRef.getLocaleManager().getString("Party.Password.Removed"));
    }

    private void protectParty(Party party, CommandSender sender, String password) {
        party.setLocked(true);
        party.setPassword(password);
        sender.sendMessage(pluginRef.getLocaleManager().getString("Party.Password.Set", password));
    }
}
