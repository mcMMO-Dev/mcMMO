package com.gmail.nossr50.commands.party;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;

public class PartyLockCommand implements CommandExecutor {
    private Party playerParty;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        playerParty = UserManager.getPlayer(sender.getName()).getParty();

        switch (args.length) {
            case 1:
                if (args[0].equalsIgnoreCase("lock")) {
                    lockParty(sender, command.getPermissionMessage());
                }
                else if (args[0].equalsIgnoreCase("unlock")) {
                    unlockParty(sender, command.getPermissionMessage());
                }

                return true;

            case 2:
                if (!args[0].equalsIgnoreCase("lock")) {
                    sendUsageStrings(sender);
                    return true;
                }

                if (CommandUtils.shouldEnableToggle(args[1])) {
                    lockParty(sender, command.getPermissionMessage());
                }
                else if (CommandUtils.shouldDisableToggle(args[1])) {
                    unlockParty(sender, command.getPermissionMessage());
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
    private void lockParty(CommandSender sender, String permissionMessage) {
        if (!Permissions.partySubcommand(sender, PartySubcommandType.LOCK)) {
            sender.sendMessage(permissionMessage);
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
    private void unlockParty(CommandSender sender, String permissionMessage) {
        if (!Permissions.partySubcommand(sender, PartySubcommandType.UNLOCK)) {
            sender.sendMessage(permissionMessage);
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
