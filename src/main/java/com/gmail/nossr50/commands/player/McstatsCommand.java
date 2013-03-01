package com.gmail.nossr50.commands.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;

public class McstatsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        switch (args.length) {
            case 0:
                Player player = (Player) sender;
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
                PlayerProfile profile = mcMMOPlayer.getProfile();

                player.sendMessage(LocaleLoader.getString("Stats.Own.Stats"));
                player.sendMessage(LocaleLoader.getString("mcMMO.NoSkillNote"));

                CommandUtils.printGatheringSkills(player, profile);
                CommandUtils.printCombatSkills(player, profile);
                CommandUtils.printMiscSkills(player, profile);

                int powerLevelCap = Config.getInstance().getPowerLevelCap();

                if (powerLevelCap != Integer.MAX_VALUE) {
                    player.sendMessage(LocaleLoader.getString("Commands.PowerLevel.Capped", mcMMOPlayer.getPowerLevel(), powerLevelCap));
                }
                else {
                    player.sendMessage(LocaleLoader.getString("Commands.PowerLevel", mcMMOPlayer.getPowerLevel()));
                }

                return true;

            default:
                return false;
        }
    }
}
