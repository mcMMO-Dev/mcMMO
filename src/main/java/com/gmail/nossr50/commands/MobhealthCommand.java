package com.gmail.nossr50.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;

public class MobhealthCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        switch (args.length) {
            case 1:
                PlayerProfile playerProfile = UserManager.getPlayer(sender.getName()).getProfile();

                try {
                    MobHealthbarType type = MobHealthbarType.valueOf(args[0].toUpperCase().trim());
                    playerProfile.setMobHealthbarType(type);
                    sender.sendMessage("Display type changed to: " + type); //TODO: Localize
                    return true;
                }
                catch (IllegalArgumentException ex) {
                    sender.sendMessage("Invalid type!"); //TODO: Localize
                    return true;
                }

            default:
                return false;
        }
    }
}
