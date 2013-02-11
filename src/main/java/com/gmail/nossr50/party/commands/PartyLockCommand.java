package com.gmail.nossr50.party.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class PartyLockCommand implements CommandExecutor {
    private Player player;
    private Party playerParty;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        player = (Player) sender;
        playerParty = Users.getPlayer(player).getParty();

        if (!playerParty.getLeader().equals(player.getName())) {
            sender.sendMessage(LocaleLoader.getString("Party.NotOwner"));
            return true;
        }

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
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "lock", "[on|off]"));
                return true;
            }

            if (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("true")) {
                lockParty(sender, command);
            }
            else if (args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("false")) {
                unlockParty(sender, command);
            }
            else {
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "lock", "[on|off]"));
            }

            return true;

        default:
            sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "lock", "[on|off]"));
            return true;
        }
    }

    /**
     * Handle locking a party.
     */
    private void lockParty(CommandSender sender, Command command) {
        if (!Permissions.hasPermission(sender, "mcmmo.commands.party.lock")) {
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
        if (!Permissions.hasPermission(sender, "mcmmo.commands.party.unlock")) {
            sender.sendMessage(command.getPermissionMessage());
            return;
        }

        if (!playerParty.isLocked()) {
            player.sendMessage(LocaleLoader.getString("Party.IsntLocked"));
            return;
        }

        playerParty.setLocked(false);
        player.sendMessage(LocaleLoader.getString("Party.Unlocked"));
    }
}
