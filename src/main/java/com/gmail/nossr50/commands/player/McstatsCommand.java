package com.gmail.nossr50.commands.player;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class McstatsCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        if (!CommandUtils.hasPlayerDataKey(sender)) {
            return true;
        }

        if (args.length == 0) {
            if (UserManager.getPlayer((Player) sender) == null) {
                sender.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));
                return true;
            }

            final Player player = (Player) sender;

            if (mcMMO.p.getGeneralConfig().getStatsUseBoard() && mcMMO.p.getGeneralConfig()
                    .getScoreboardsEnabled()) {
                ScoreboardManager.enablePlayerStatsScoreboard(player);

                if (!mcMMO.p.getGeneralConfig().getStatsUseChat()) {
                    return true;
                }
            }

            player.sendMessage(LocaleLoader.getString("Stats.Own.Stats"));
            player.sendMessage(LocaleLoader.getString("mcMMO.NoSkillNote"));

            CommandUtils.printGatheringSkills(player);
            CommandUtils.printCombatSkills(player);
            CommandUtils.printMiscSkills(player);

            int powerLevelCap = mcMMO.p.getGeneralConfig().getPowerLevelCap();

            if (powerLevelCap != Integer.MAX_VALUE) {
                player.sendMessage(LocaleLoader.getString("Commands.PowerLevel.Capped",
                        UserManager.getPlayer(player).getPowerLevel(), powerLevelCap));
            } else {
                player.sendMessage(LocaleLoader.getString("Commands.PowerLevel",
                        UserManager.getPlayer(player).getPowerLevel()));
            }

            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String alias, String[] args) {
        return ImmutableList.of();
    }
}
