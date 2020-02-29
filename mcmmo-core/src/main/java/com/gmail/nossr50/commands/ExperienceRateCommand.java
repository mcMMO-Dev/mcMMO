package com.gmail.nossr50.commands;

import com.gmail.nossr50.datatypes.notifications.SensitiveCommandType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.StringUtils;
import com.google.common.collect.ImmutableList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ExperienceRateCommand implements TabExecutor {

    private final mcMMO pluginRef;

    public ExperienceRateCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                if (!args[0].equalsIgnoreCase("reset") && !args[0].equalsIgnoreCase("clear")) {
                    return false;
                }

                if (!pluginRef.getPermissionTools().xprateReset(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (pluginRef.isXPEventEnabled()) {

                    if (pluginRef.getConfigManager().getConfigEvent().isSendTitleMessages()) {
                        pluginRef.getNotificationManager().broadcastTitle(Bukkit.getServer(),
                                pluginRef.getLocaleManager().getString("Commands.Event.Stop"),
                                pluginRef.getLocaleManager().getString("Commands.Event.Stop.Subtitle"),
                                10, 10 * 20, 20);
                    }

                    if (pluginRef.getConfigManager().getConfigEvent().isBroadcastXPRateEventMessages()) {
                        Bukkit.getServer().broadcastMessage(pluginRef.getLocaleManager().getString("Commands.Event.Stop"));
                        Bukkit.getServer().broadcastMessage(pluginRef.getLocaleManager().getString("Commands.Event.Stop.Subtitle"));
                    }

                    //Admin notification
                    pluginRef.getNotificationManager().processSensitiveCommandNotification(sender, SensitiveCommandType.XPRATE_END);

                    pluginRef.toggleXpEventEnabled();
                }

                pluginRef.getDynamicSettingsManager().getExperienceManager().resetGlobalXpMult();
                return true;

            case 2:
                if (pluginRef.getCommandTools().isInvalidInteger(sender, args[0])) {
                    return true;
                }

                if (!pluginRef.getPermissionTools().xprateSet(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (pluginRef.getCommandTools().shouldDisableToggle(args[1])) {
                    pluginRef.setXPEventEnabled(false);
                } else if (pluginRef.getCommandTools().shouldEnableToggle(args[1])) {
                    pluginRef.setXPEventEnabled(true);
                } else {
                    return false;
                }

                int newXpRate = Integer.parseInt(args[0]);

                if (newXpRate < 0) {
                    sender.sendMessage(ChatColor.RED + pluginRef.getLocaleManager().getString("Commands.NegativeNumberWarn"));
                    return true;
                }

                pluginRef.getDynamicSettingsManager().getExperienceManager().setGlobalXpMult(newXpRate);

                if (pluginRef.getConfigManager().getConfigEvent().isSendTitleMessages()) {
                    pluginRef.getNotificationManager().broadcastTitle(Bukkit.getServer(),
                            pluginRef.getLocaleManager().getString("Commands.Event.Start"),
                            pluginRef.getLocaleManager().getString("Commands.Event.XP", newXpRate),
                            10, 10 * 20, 20);
                }

                if (pluginRef.getConfigManager().getConfigEvent().isBroadcastXPRateEventMessages()) {
                    Bukkit.getServer().broadcastMessage(pluginRef.getLocaleManager().getString("Commands.Event.Start"));
                    Bukkit.getServer().broadcastMessage(pluginRef.getLocaleManager().getString("Commands.Event.XP", newXpRate));
                }

                //Admin notification
                pluginRef.getNotificationManager().processSensitiveCommandNotification(sender, SensitiveCommandType.XPRATE_MODIFY, String.valueOf(newXpRate));

                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                if (StringUtils.isInt(args[0])) {
                    return ImmutableList.of();
                }

                return StringUtil.copyPartialMatches(args[0], CommandConstants.RESET_OPTIONS, new ArrayList<>(CommandConstants.RESET_OPTIONS.size()));
            case 2:
                return StringUtil.copyPartialMatches(args[1], CommandConstants.TRUE_FALSE_OPTIONS, new ArrayList<>(CommandConstants.TRUE_FALSE_OPTIONS.size()));
            default:
                return ImmutableList.of();
        }
    }
}
