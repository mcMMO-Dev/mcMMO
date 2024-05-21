package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PartyLockCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
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

                if (CommandUtils.shouldEnableToggle(args[1])) {
                    togglePartyLock(sender, true);
                } else if (CommandUtils.shouldDisableToggle(args[1])) {
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
        sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "lock", "[on|off]"));
        sender.sendMessage(LocaleLoader.getString("Commands.Usage.1", "party", "unlock"));
    }

    private void togglePartyLock(CommandSender sender, boolean lock) {
        if (UserManager.getPlayer((Player) sender) == null) {
            sender.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));
            return;
        }

        Party party = UserManager.getPlayer((Player) sender).getParty();

        if (!Permissions.partySubcommand(sender, lock ? PartySubcommandType.LOCK : PartySubcommandType.UNLOCK)) {
            sender.sendMessage(LocaleLoader.getString("mcMMO.NoPermission"));
            return;
        }

        if (lock == party.isLocked()) {
            sender.sendMessage(LocaleLoader.getString("Party." + (lock ? "IsLocked" : "IsntLocked")));
            return;
        }

        party.setLocked(lock);
        sender.sendMessage(LocaleLoader.getString("Party." + (lock ? "Locked" : "Unlocked")));
    }
}
