package com.gmail.nossr50.commands.player;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;

import com.google.common.collect.ImmutableList;

public class McstatsCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        switch (args.length) {
            case 0:
                Player player = (Player) sender;

                if (Config.getInstance().getStatsUseBoard()) {
                    ScoreboardManager.enablePlayerStatsScoreboard(player);
                    if (!Config.getInstance().getStatsUseChat()) return true;
                }
                player.sendMessage(LocaleLoader.getString("Stats.Own.Stats"));
                player.sendMessage(LocaleLoader.getString("mcMMO.NoSkillNote"));

                CommandUtils.printGatheringSkills(player);
                CommandUtils.printCombatSkills(player);
                CommandUtils.printMiscSkills(player);

                int powerLevelCap = Config.getInstance().getPowerLevelCap();

                if (powerLevelCap != Integer.MAX_VALUE) {
                    player.sendMessage(LocaleLoader.getString("Commands.PowerLevel.Capped", UserManager.getPlayer(player).getPowerLevel(), powerLevelCap));
                }
                else {
                    player.sendMessage(LocaleLoader.getString("Commands.PowerLevel", UserManager.getPlayer(player).getPowerLevel()));
                }

                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return ImmutableList.of();
    }
}
