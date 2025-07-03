package com.gmail.nossr50.commands;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.notifications.SensitiveCommandType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.text.StringUtils;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

public class XprateCommand implements TabExecutor {
    private final double ORIGINAL_XP_RATE = ExperienceConfig.getInstance()
            .getExperienceGainsGlobalMultiplier();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, String[] args) {
        switch (args.length) {
            case 1:
                if (!args[0].equalsIgnoreCase("reset") && !args[0].equalsIgnoreCase("clear")) {
                    return false;
                }

                if (!Permissions.xprateReset(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (mcMMO.p.isXPEventEnabled()) {

                    if (mcMMO.p.getAdvancedConfig().useTitlesForXPEvent()) {
                        NotificationManager.broadcastTitle(mcMMO.p.getServer(),
                                LocaleLoader.getString("Commands.Event.Stop"),
                                LocaleLoader.getString("Commands.Event.Stop.Subtitle"),
                                10, 10 * 20, 20);
                    }

                    if (mcMMO.p.getGeneralConfig().broadcastEventMessages()) {
                        mcMMO.p.getServer()
                                .broadcastMessage(LocaleLoader.getString("Commands.Event.Stop"));
                        mcMMO.p.getServer().broadcastMessage(
                                LocaleLoader.getString("Commands.Event.Stop.Subtitle"));
                    }

                    //Admin notification
                    NotificationManager.processSensitiveCommandNotification(sender,
                            SensitiveCommandType.XPRATE_END);

                    mcMMO.p.toggleXpEventEnabled();
                }

                ExperienceConfig.getInstance().setExperienceGainsGlobalMultiplier(ORIGINAL_XP_RATE);
                return true;

            case 2:
                if (CommandUtils.isInvalidInteger(sender, args[0])) {
                    return true;
                }

                if (!Permissions.xprateSet(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (CommandUtils.shouldDisableToggle(args[1])) {
                    mcMMO.p.setXPEventEnabled(false);
                } else if (CommandUtils.shouldEnableToggle(args[1])) {
                    mcMMO.p.setXPEventEnabled(true);
                } else {
                    return false;
                }

                int newXpRate = Integer.parseInt(args[0]);

                if (newXpRate < 0) {
                    sender.sendMessage(
                            ChatColor.RED + LocaleLoader.getString("Commands.NegativeNumberWarn"));
                    return true;
                }

                ExperienceConfig.getInstance().setExperienceGainsGlobalMultiplier(newXpRate);

                if (mcMMO.p.getAdvancedConfig().useTitlesForXPEvent()) {
                    NotificationManager.broadcastTitle(mcMMO.p.getServer(),
                            LocaleLoader.getString("Commands.Event.Start"),
                            LocaleLoader.getString("Commands.Event.XP", newXpRate),
                            10, 10 * 20, 20);
                }

                if (mcMMO.p.getGeneralConfig().broadcastEventMessages()) {
                    mcMMO.p.getServer()
                            .broadcastMessage(LocaleLoader.getString("Commands.Event.Start"));
                    mcMMO.p.getServer().broadcastMessage(
                            LocaleLoader.getString("Commands.Event.XP", newXpRate));
                }

                //Admin notification
                NotificationManager.processSensitiveCommandNotification(sender,
                        SensitiveCommandType.XPRATE_MODIFY, String.valueOf(newXpRate));

                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String alias, String[] args) {
        switch (args.length) {
            case 1:
                if (StringUtils.isInt(args[0])) {
                    return ImmutableList.of();
                }

                return StringUtil.copyPartialMatches(args[0], CommandUtils.RESET_OPTIONS,
                        new ArrayList<>(CommandUtils.RESET_OPTIONS.size()));
            case 2:
                return StringUtil.copyPartialMatches(args[1], CommandUtils.TRUE_FALSE_OPTIONS,
                        new ArrayList<>(CommandUtils.TRUE_FALSE_OPTIONS.size()));
            default:
                return ImmutableList.of();
        }
    }
}
