package com.gmail.nossr50.commands.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Users;

public class McstatsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        switch (args.length) {
        case 0:
            Player player = (Player) sender;
            McMMOPlayer mcMMOPlayer = Users.getPlayer(player);
            PlayerProfile profile = mcMMOPlayer.getProfile();

            player.sendMessage(LocaleLoader.getString("Stats.Own.Stats"));
            player.sendMessage(LocaleLoader.getString("mcMMO.NoSkillNote"));

            CommandHelper.printGatheringSkills(player, profile);
            CommandHelper.printCombatSkills(player, profile);
            CommandHelper.printMiscSkills(player, profile);

            int powerLevelCap = Config.getInstance().getPowerLevelCap();

            if (powerLevelCap != Integer.MAX_VALUE) {
                player.sendMessage(LocaleLoader.getString("Commands.PowerLevel.Capped", String.valueOf(mcMMOPlayer.getPowerLevel()), String.valueOf(powerLevelCap)));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Commands.PowerLevel", String.valueOf(mcMMOPlayer.getPowerLevel())));
            }

            return true;

        default:
            return false;
        }
    }
}
