package com.gmail.nossr50.commands.player;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class McstatsCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        if (!CommandUtils.hasPlayerDataKey(sender)) {
            return true;
        }

        switch (args.length) {
            case 0:
                Player player = (Player) sender;

                if (mcMMO.getScoreboardSettings().isScoreboardEnabled(ScoreboardManager.SidebarType.STATS_BOARD) && mcMMO.getScoreboardSettings().getScoreboardsEnabled()) {
                    ScoreboardManager.enablePlayerStatsScoreboard(player);

                    if (!mcMMO.getScoreboardSettings().isScoreboardPrinting(ScoreboardManager.SidebarType.STATS_BOARD)) {
                        return true;
                    }
                }

                player.sendMessage(LocaleLoader.getString("Stats.Own.Stats"));
                player.sendMessage(LocaleLoader.getString("mcMMO.NoSkillNote"));

                CommandUtils.printGatheringSkills(player);
                CommandUtils.printCombatSkills(player);
                CommandUtils.printMiscSkills(player);

                int powerLevelCap = mcMMO.getPlayerLevelingSettings().getConfigSectionLevelCaps().getPowerLevel().getLevelCap();

                if (mcMMO.getPlayerLevelingSettings().getConfigSectionLevelCaps().getPowerLevel().isLevelCapEnabled()) {
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
