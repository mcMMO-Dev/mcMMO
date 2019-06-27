package com.gmail.nossr50.commands.player;

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

    private mcMMO pluginRef;

    public McstatsCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

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
                if (UserManager.getPlayer((Player) sender) == null) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
                    return true;
                }

                Player player = (Player) sender;

                if (pluginRef.getScoreboardSettings().isScoreboardEnabled(ScoreboardManager.SidebarType.STATS_BOARD) && pluginRef.getScoreboardSettings().getScoreboardsEnabled()) {
                    ScoreboardManager.enablePlayerStatsScoreboard(player);

                    if (!pluginRef.getScoreboardSettings().isScoreboardPrinting(ScoreboardManager.SidebarType.STATS_BOARD)) {
                        return true;
                    }
                }

                player.sendMessage(pluginRef.getLocaleManager().getString("Stats.Own.Stats"));
                player.sendMessage(pluginRef.getLocaleManager().getString("mcMMO.NoSkillNote"));

                CommandUtils.printGatheringSkills(player);
                CommandUtils.printCombatSkills(player);
                CommandUtils.printMiscSkills(player);

                int powerLevelCap = pluginRef.getPlayerLevelingSettings().getConfigSectionLevelCaps().getPowerLevelSettings().getLevelCap();

                if (pluginRef.getPlayerLevelingSettings().getConfigSectionLevelCaps().getPowerLevelSettings().isLevelCapEnabled()) {
                    player.sendMessage(pluginRef.getLocaleManager().getString("Commands.PowerLevel.Capped", UserManager.getPlayer(player).getPowerLevel(), powerLevelCap));
                } else {
                    player.sendMessage(pluginRef.getLocaleManager().getString("Commands.PowerLevel", UserManager.getPlayer(player).getPowerLevel()));
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
