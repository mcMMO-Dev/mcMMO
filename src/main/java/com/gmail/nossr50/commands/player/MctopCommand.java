package com.gmail.nossr50.commands.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.runnables.commands.MctopCommandAsyncTask;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;

import com.google.common.collect.ImmutableList;

public class MctopCommand implements TabExecutor {
    private SkillType skill;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
                display(1, "ALL", sender, command);
                return true;

            case 1:
                if (StringUtils.isInt(args[0])) {
                    display(Math.abs(Integer.parseInt(args[0])), "ALL", sender, command);
                    return true;
                }

                if (!extractSkill(sender, args[0])) {
                    return true;
                }

                display(1, skill.toString(), sender, command);
                return true;

            case 2:
                if (CommandUtils.isInvalidInteger(sender, args[1])) {
                    return true;
                }

                if (!extractSkill(sender, args[0])) {
                    return true;
                }

                display(Math.abs(Integer.parseInt(args[1])), skill.toString(), sender, command);
                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return StringUtil.copyPartialMatches(args[0], SkillType.SKILL_NAMES, new ArrayList<String>(SkillType.SKILL_NAMES.size()));
            default:
                return ImmutableList.of();
        }
    }

    private void display(int page, String skill, CommandSender sender, Command command) {
        if (!skill.equalsIgnoreCase("all") && !Permissions.mctop(sender, this.skill)) {
            sender.sendMessage(command.getPermissionMessage());
            return;
        }

        if (sender instanceof Player && Config.getInstance().getTopUseBoard()) {
            ScoreboardManager.enableGlobalStatsScoreboard((Player) sender, skill, page);
            if (!Config.getInstance().getTopUseChat()) return;
        }
        display(page, skill, sender);
    }

    private void display(int page, String query, CommandSender sender) {
        new MctopCommandAsyncTask(page, query, sender).runTaskAsynchronously(mcMMO.p);
    }

    private boolean extractSkill(CommandSender sender, String skillName) {
        if (CommandUtils.isInvalidSkill(sender, skillName)) {
            return false;
        }

        skill = SkillType.getSkill(skillName);

        if (CommandUtils.isChildSkill(sender, skill)) {
            return false;
        }

        return true;
    }
}
