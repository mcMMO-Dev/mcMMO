package com.gmail.nossr50.commands.player;

import com.gmail.nossr50.mcMMO;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class SkillStatsCommand implements TabExecutor {

    private final mcMMO pluginRef;

    public SkillStatsCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (pluginRef.getCommandTools().noConsoleUsage(sender)) {
            return true;
        }

        if (!pluginRef.getCommandTools().hasPlayerDataKey(sender)) {
            return true;
        }

        switch (args.length) {
            case 0:
                if (pluginRef.getUserManager().getPlayer((Player) sender) == null) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
                    return true;
                }

                Player player = (Player) sender;

                if (pluginRef.getScoreboardSettings().isScoreboardEnabled(pluginRef.getScoreboardManager().SidebarType.STATS_BOARD) && pluginRef.getScoreboardSettings().getScoreboardsEnabled()) {
                    pluginRef.getScoreboardManager().enablePlayerStatsScoreboard(player);

                    if (!pluginRef.getScoreboardSettings().isScoreboardPrinting(pluginRef.getScoreboardManager().SidebarType.STATS_BOARD)) {
                        return true;
                    }
                }

                player.sendMessage(pluginRef.getLocaleManager().getString("Stats.Own.Stats"));
                player.sendMessage(pluginRef.getLocaleManager().getString("mcMMO.NoSkillNote"));

                pluginRef.getCommandTools().printGatheringSkills(player);
                pluginRef.getCommandTools().printCombatSkills(player);
                pluginRef.getCommandTools().printMiscSkills(player);

                int powerLevelCap = pluginRef.getPlayerLevelingSettings().getConfigSectionLevelCaps().getPowerLevelSettings().getLevelCap();

                if (pluginRef.getPlayerLevelingSettings().getConfigSectionLevelCaps().getPowerLevelSettings().isLevelCapEnabled()) {
                    player.sendMessage(pluginRef.getLocaleManager().getString("Commands.PowerLevel.Capped", pluginRef.getUserManager().getPlayer(player).getPowerLevel(), powerLevelCap));
                } else {
                    player.sendMessage(pluginRef.getLocaleManager().getString("Commands.PowerLevel", pluginRef.getUserManager().getPlayer(player).getPowerLevel()));
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
