package com.gmail.nossr50.commands.party;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;

public class PartyLockCommand implements CommandExecutor {
    private Party playerParty;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        playerParty = UserManager.getPlayer((Player) sender).getParty();

        switch (args.length) {
            case 1:
                if (args[0].equalsIgnoreCase("lock")) {
                    lockParty(sender, command);
                }
                else if (args[0].equalsIgnoreCase("unlock")) {
                    unlockParty(sender, command);
                }

                return true;

            case 2:
                if (!args[0].equalsIgnoreCase("lock")) {
                    sendUsageStrings(sender);
                    return true;
                }

                if (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("true")) {
                    lockParty(sender, command);
                }
                else if (args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("false")) {
                    unlockParty(sender, command);
                }
                else {
                    sendUsageStrings(sender);
                }

                return true;

            default:
                sendUsageStrings(sender);
                return true;
        }
    }

    /**
     * Handle locking a party.
     */
    private void lockParty(CommandSender sender, Command command) {
        if (!Permissions.partySubcommand(sender, PartySubcommandType.LOCK)) {
            sender.sendMessage(command.getPermissionMessage());
            return;
        }

        if (playerParty.isLocked()) {
            sender.sendMessage(LocaleLoader.getString("Party.IsLocked"));
            return;
        }

        playerParty.setLocked(true);
        sender.sendMessage(LocaleLoader.getString("Party.Locked"));
    }

    /**
     * Handle unlocking a party.
     *
     * @return true if party is successfully unlocked, false otherwise.
     */
    private void unlockParty(CommandSender sender, Command command) {
        if (!Permissions.partySubcommand(sender, PartySubcommandType.UNLOCK)) {
            sender.sendMessage(command.getPermissionMessage());
            return;
        }

        if (!playerParty.isLocked()) {
            sender.sendMessage(LocaleLoader.getString("Party.IsntLocked"));
            return;
        }

        playerParty.setLocked(false);
        sender.sendMessage(LocaleLoader.getString("Party.Unlocked"));
    }

    private void sendUsageStrings(CommandSender sender) {
        sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "lock", "[on|off]"));
        sender.sendMessage(LocaleLoader.getString("Commands.Usage.1", "party", "unlock"));
    }
}
