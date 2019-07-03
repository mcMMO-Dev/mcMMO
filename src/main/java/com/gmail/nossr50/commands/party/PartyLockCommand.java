package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyLockCommand implements CommandExecutor {

    private mcMMO pluginRef;

    public PartyLockCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                if (args[0].equalsIgnoreCase("lock")) {
                    togglePartyLock(sender, true);
                } else if (args[0].equalsIgnoreCase("unlock")) {
                    togglePartyLock(sender, false);
                }

                return true;

            case 2:
                if (!args[0].equalsIgnoreCase("lock")) {
                    sendUsageStrings(sender);
                    return true;
                }

                if (pluginRef.getCommandTools().shouldEnableToggle(args[1])) {
                    togglePartyLock(sender, true);
                } else if (pluginRef.getCommandTools().shouldDisableToggle(args[1])) {
                    togglePartyLock(sender, false);
                } else {
                    sendUsageStrings(sender);
                }

                return true;

            default:
                sendUsageStrings(sender);
                return true;
        }
    }

    private void sendUsageStrings(CommandSender sender) {
        sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Usage.2", "party", "lock", "[on|off]"));
        sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Usage.1", "party", "unlock"));
    }

    private void togglePartyLock(CommandSender sender, boolean lock) {
        if (pluginRef.getUserManager().getPlayer((Player) sender) == null) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
            return;
        }

        Party party = pluginRef.getUserManager().getPlayer((Player) sender).getParty();

        if (!Permissions.partySubcommand(sender, lock ? PartySubcommandType.LOCK : PartySubcommandType.UNLOCK)) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("mcMMO.NoPermission"));
            return;
        }

        if (lock ? party.isLocked() : !party.isLocked()) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Party." + (lock ? "IsLocked" : "IsntLocked")));
            return;
        }

        party.setLocked(lock);
        sender.sendMessage(pluginRef.getLocaleManager().getString("Party." + (lock ? "Locked" : "Unlocked")));
    }
}
