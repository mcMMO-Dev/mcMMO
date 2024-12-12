package com.gmail.nossr50.commands.player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.commands.McTopCommandAsyncTask;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.text.StringUtils;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class McTopCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
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

                // Check if the command is for Maces but the MC version is not correct
                if (skill == PrimarySkillType.MACES
                        && !mcMMO.getCompatibilityManager().getMinecraftGameVersion().isAtLeast(1, 21, 0)) {
                    return true;
                }

                display(1, skill, sender, command);
                return true;

            case 2:
                if (CommandUtils.isInvalidInteger(sender, args[1])) {
                    return true;
                }

                skill = extractSkill(sender, args[0]);

                if (skill == null) {
                    return true;
                }

                // Check if the command is for Maces but the MC version is not correct
                if (skill == PrimarySkillType.MACES
                        && !mcMMO.getCompatibilityManager().getMinecraftGameVersion().isAtLeast(1, 21, 0)) {
                    return true;
                }

                display(Math.abs(Integer.parseInt(args[1])), skill, sender, command);
                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], mcMMO.p.getSkillTools().LOCALIZED_SKILL_NAMES, new ArrayList<>(mcMMO.p.getSkillTools().LOCALIZED_SKILL_NAMES.size()));
        }
        return ImmutableList.of();
    }

    private void display(int page, PrimarySkillType skill, CommandSender sender, Command command) {
        if (skill != null && !Permissions.mctop(sender, skill)) {
            sender.sendMessage(command.getPermissionMessage());
            return;
        }

        if (sender instanceof Player) {
            if (!CommandUtils.hasPlayerDataKey(sender)) {
                return;
            }

            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(sender.getName());
            long cooldownMillis = Math.max(mcMMO.p.getGeneralConfig().getDatabasePlayerCooldown(), 1750);

            if (mcMMOPlayer.getDatabaseATS() + cooldownMillis > System.currentTimeMillis()) {
                double seconds = ((mcMMOPlayer.getDatabaseATS() + cooldownMillis) - System.currentTimeMillis()) / 1000.0D;
                if (seconds < 1) {
                    seconds = 1;
                }

                sender.sendMessage(LocaleLoader.formatString(LocaleLoader.getString("Commands.Database.Cooldown"), seconds));
                return;
            }

            if (((Player) sender).hasMetadata(MetadataConstants.METADATA_KEY_DATABASE_COMMAND)) {
                sender.sendMessage(LocaleLoader.getString("Commands.Database.Processing"));
                return;
            } else {
                ((Player) sender).setMetadata(MetadataConstants.METADATA_KEY_DATABASE_COMMAND, new FixedMetadataValue(mcMMO.p, null));
            }

            mcMMOPlayer.actualizeDatabaseATS();
        }

        display(page, skill, sender);
    }

    private void display(int page, PrimarySkillType skill, CommandSender sender) {
        boolean useBoard = (sender instanceof Player) && (mcMMO.p.getGeneralConfig().getTopUseBoard());
        boolean useChat = !useBoard || mcMMO.p.getGeneralConfig().getTopUseChat();

        mcMMO.p.getFoliaLib().getImpl().runAsync(new McTopCommandAsyncTask(page, skill, sender, useBoard, useChat));
    }

    private PrimarySkillType extractSkill(CommandSender sender, String skillName) {
        if (CommandUtils.isInvalidSkill(sender, skillName)) {
            return null;
        }

        PrimarySkillType skill = mcMMO.p.getSkillTools().matchSkill(skillName);

        if (CommandUtils.isChildSkill(sender, skill)) {
            return null;
        }

        return skill;
    }
}
