package com.gmail.nossr50.commands.hardcore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.gmail.nossr50.datatypes.skills.PrimarySkill;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.commands.CommandUtils;

import com.google.common.collect.ImmutableList;

public abstract class HardcoreModeCommand implements TabExecutor {
    protected final DecimalFormat percent = new DecimalFormat("##0.00%");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
                if (!checkTogglePermissions(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (checkEnabled(null)) {
                    disable(null);
                }
                else {
                    enable(null);
                }

                return true;

            case 1:
                if (CommandUtils.shouldEnableToggle(args[0])) {
                    if (!Permissions.hardcoreToggle(sender)) {
                        sender.sendMessage(command.getPermissionMessage());
                        return true;
                    }

                    enable(null);
                    return true;
                }

                if (CommandUtils.shouldDisableToggle(args[0])) {
                    if (!Permissions.hardcoreToggle(sender)) {
                        sender.sendMessage(command.getPermissionMessage());
                        return true;
                    }

                    disable(null);
                    return true;
                }

                if (CommandUtils.isInvalidDouble(sender, args[0])) {
                    return true;
                }

                if (!Permissions.hardcoreModify(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                modify(sender, Double.parseDouble(args[0]));
                return true;


            case 2:
                if (CommandUtils.isInvalidSkill(sender, args[0])) {
                    return true;
                }

                PrimarySkill skill = PrimarySkill.getSkill(args[0]);

                if (!CommandUtils.isChildSkill(sender, skill)) {
                    return true;
                }

                if (CommandUtils.shouldEnableToggle(args[1])) {
                    if (!Permissions.hardcoreToggle(sender)) {
                        sender.sendMessage(command.getPermissionMessage());
                        return true;
                    }

                    enable(skill);
                    return true;
                }

                if (CommandUtils.shouldDisableToggle(args[1])) {
                    if (!Permissions.hardcoreToggle(sender)) {
                        sender.sendMessage(command.getPermissionMessage());
                        return true;
                    }

                    enable(skill);
                    return true;
                }

                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                if (StringUtils.isDouble(args[0])) {
                    return ImmutableList.of();
                }

                return StringUtil.copyPartialMatches(args[0], CommandUtils.TRUE_FALSE_OPTIONS, new ArrayList<String>(CommandUtils.TRUE_FALSE_OPTIONS.size()));
            default:
                return ImmutableList.of();
        }
    }

    protected abstract boolean checkTogglePermissions(CommandSender sender);
    protected abstract boolean checkModifyPermissions(CommandSender sender);
    protected abstract boolean checkEnabled(PrimarySkill skill);
    protected abstract void enable(PrimarySkill skill);
    protected abstract void disable(PrimarySkill skill);
    protected abstract void modify(CommandSender sender, double newPercentage);
}
