package com.gmail.nossr50.commands.player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.CoreSkills;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.commands.MctopCommandAsyncTask;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.text.StringUtils;
import com.google.common.collect.ImmutableList;
import com.neetgames.mcmmo.skill.RootSkill;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MctopCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        RootSkill rootSkill = null;

        switch (args.length) {
            case 0:
                display(1, rootSkill, sender, command);
                return true;

            case 1:
                if (StringUtils.isInt(args[0])) {
                    display(Math.abs(Integer.parseInt(args[0])), rootSkill, sender, command);
                    return true;
                }

                rootSkill = extractSkill(sender, args[0]);

                if (rootSkill == null) {
                    return true;
                }

                display(1, rootSkill, sender, command);
                return true;

            case 2:
                if (CommandUtils.isInvalidInteger(sender, args[1])) {
                    return true;
                }

                rootSkill = extractSkill(sender, args[0]);

                if (rootSkill == null) {
                    return true;
                }

                display(Math.abs(Integer.parseInt(args[1])), rootSkill, sender, command);
                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], PrimarySkillType.SKILL_NAMES, new ArrayList<>(PrimarySkillType.SKILL_NAMES.size()));
        }
        return ImmutableList.of();
    }

    private void display(int page, @NotNull RootSkill rootSkill, @NotNull CommandSender sender, @NotNull Command command) {
        if (rootSkill != null && !Permissions.mctop(sender, CoreSkills.getSkill(rootSkill))) {
            sender.sendMessage(command.getPermissionMessage());
            return;
        }

        if (sender instanceof Player) {
            if (!CommandUtils.hasPlayerDataKey(sender)) {
                return;
            }

            Player player = (Player) sender;

            McMMOPlayer mmoPlayer = (McMMOPlayer) mcMMO.getUserManager().queryPlayer(player);
            long cooldownMillis = Math.max(Config.getInstance().getDatabasePlayerCooldown(), 1750);

            if (mmoPlayer.getDatabaseCommandATS() + cooldownMillis > System.currentTimeMillis()) {
                double seconds = ((mmoPlayer.getDatabaseCommandATS() + cooldownMillis) - System.currentTimeMillis()) / 1000.0D;
                if (seconds < 1) {
                    seconds = 1;
                }

                sender.sendMessage(LocaleLoader.formatString(LocaleLoader.getString("Commands.Database.Cooldown"), seconds));
                return;
            }

            if (((Player) sender).hasMetadata(mcMMO.databaseCommandKey)) {
                sender.sendMessage(LocaleLoader.getString("Commands.Database.Processing"));
                return;
            } else {
                ((Player) sender).setMetadata(mcMMO.databaseCommandKey, new FixedMetadataValue(mcMMO.p, null));
            }

            mmoPlayer.actualizeDatabaseCommandATS();
        }

        display(page, rootSkill, sender);
    }

    private void display(int page, @NotNull RootSkill rootSkill, @NotNull CommandSender sender) {
        boolean useBoard = (sender instanceof Player) && (Config.getInstance().getTopUseBoard());
        boolean useChat = !useBoard || Config.getInstance().getTopUseChat();

        new MctopCommandAsyncTask(page, rootSkill, sender, useBoard, useChat).runTaskAsynchronously(mcMMO.p);
    }

    private PrimarySkillType extractSkill(CommandSender sender, String skillName) {
        if (CommandUtils.isInvalidSkill(sender, skillName)) {
            return null;
        }

        RootSkill rootSkill = mcMMO.p.getSkillRegister().getSkill(rootSkillName);

        if (CommandUtils.isChildSkill(sender, skill)) {
            return null;
        }

        return skill;
    }
}
