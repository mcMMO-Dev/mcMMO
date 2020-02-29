package com.gmail.nossr50.commands.player;

import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.commands.LeaderboardsCommandAsyncTask;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.scoreboards.SidebarType;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardCommand implements TabExecutor {

    private final mcMMO pluginRef;

    public LeaderboardCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PrimarySkillType skill = null;

        switch (args.length) {
            case 0:
                display(1, skill, sender, command);
                return true;

            case 1:
                if (StringUtils.isInt(args[0])) {
                    display(Math.abs(Integer.parseInt(args[0])), skill, sender, command);
                    return true;
                }

                skill = extractSkill(sender, args[0]);

                if (skill == null) {
                    return true;
                }

                display(1, skill, sender, command);
                return true;

            case 2:
                if (pluginRef.getCommandTools().isInvalidInteger(sender, args[1])) {
                    return true;
                }

                skill = extractSkill(sender, args[0]);

                if (skill == null) {
                    return true;
                }

                display(Math.abs(Integer.parseInt(args[1])), skill, sender, command);
                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return StringUtil.copyPartialMatches(args[0], pluginRef.getSkillTools().LOCALIZED_SKILL_NAMES, new ArrayList<>(pluginRef.getSkillTools().LOCALIZED_SKILL_NAMES.size()));
            default:
                return ImmutableList.of();
        }
    }

    private void display(int page, PrimarySkillType skill, CommandSender sender, Command command) {
        if (skill != null && !pluginRef.getPermissionTools().mctop(sender, skill)) {
            sender.sendMessage(command.getPermissionMessage());
            return;
        }

        if (sender instanceof Player) {
            if (!pluginRef.getCommandTools().hasPlayerDataKey(sender)) {
                return;
            }

            BukkitMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(sender.getName());
            long cooldownMillis = 5000;

            if (mcMMOPlayer.getDatabaseATS() + cooldownMillis > System.currentTimeMillis()) {
                double seconds = ((mcMMOPlayer.getDatabaseATS() + cooldownMillis) - System.currentTimeMillis()) / 1000;
                if (seconds < 1) {
                    seconds = 1;
                }

                sender.sendMessage(pluginRef.getLocaleManager().formatString(pluginRef.getLocaleManager().getString("Commands.Database.Cooldown"), seconds));
                return;
            }

            if (((Player) sender).hasMetadata(MetadataConstants.DATABASE_PROCESSING_COMMAND_METAKEY.getKey())) {
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Database.Processing"));
                return;
            } else {
                ((Player) sender).setMetadata(MetadataConstants.DATABASE_PROCESSING_COMMAND_METAKEY.getKey(), new FixedMetadataValue((Plugin) pluginRef.getPlatformProvider(), null));
            }

            mcMMOPlayer.actualizeDatabaseATS();
        }

        display(page, skill, sender);
    }

    private void display(int page, PrimarySkillType skill, CommandSender sender) {
        boolean useBoard = (sender instanceof Player) && (pluginRef.getScoreboardSettings().isScoreboardEnabled(SidebarType.TOP_BOARD));
        boolean useChat = !useBoard || pluginRef.getScoreboardSettings().isScoreboardPrinting(SidebarType.TOP_BOARD);

        pluginRef.getPlatformProvider().getScheduler().getTaskBuilder()
                .setAsync(true)
                .setTask(new LeaderboardsCommandAsyncTask(pluginRef, page, skill, sender, useBoard, useChat))
                .schedule();
    }

    private PrimarySkillType extractSkill(CommandSender sender, String skillName) {
        if (pluginRef.getCommandTools().isInvalidSkill(sender, skillName)) {
            return null;
        }

        PrimarySkillType skill = pluginRef.getSkillTools().matchSkill(skillName);

        if (pluginRef.getCommandTools().isChildSkill(sender, skill)) {
            return null;
        }

        return skill;
    }
}
