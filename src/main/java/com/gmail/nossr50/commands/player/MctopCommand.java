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
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.runnables.commands.MctopCommandAsyncTask;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.google.common.collect.ImmutableList;

public class MctopCommand implements TabExecutor {
    private SkillType skill;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
                display(1, sender, command);
                return true;

            case 1:
                if (StringUtils.isInt(args[0])) {
                    display(Math.abs(Integer.parseInt(args[0])), sender, command);
                    return true;
                }

                if (!extractSkill(sender, args[0])) {
                    return true;
                }

                display(1, sender, command);
                return true;

            case 2:
                if (CommandUtils.isInvalidInteger(sender, args[1])) {
                    return true;
                }

                if (!extractSkill(sender, args[0])) {
                    return true;
                }

                display(Math.abs(Integer.parseInt(args[1])), sender, command);
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

    private void display(int page, CommandSender sender, Command command) {
        if (skill != null && !Permissions.mctop(sender, skill)) {
            sender.sendMessage(command.getPermissionMessage());
            return;
        }

        if (sender instanceof Player) {
            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(sender.getName());

            if (mcMMOPlayer.getDatabaseATS() + Misc.PLAYER_DATABASE_COOLDOWN_MILLIS > System.currentTimeMillis()) {
                sender.sendMessage(LocaleLoader.getString("Commands.Database.Cooldown"));
                return;
            }

            mcMMOPlayer.actualizeDatabaseATS();
        }

        display(page, skill, sender);
    }

    private void display(int page, SkillType skill, CommandSender sender) {
        boolean useBoard = (sender instanceof Player) && (Config.getInstance().getTopUseBoard());
        boolean useChat = useBoard ? Config.getInstance().getTopUseChat() : true;

        new MctopCommandAsyncTask(page, skill, sender, useBoard, useChat).runTaskAsynchronously(mcMMO.p);
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
