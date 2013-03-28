package com.gmail.nossr50.commands.hardcore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.commands.CommandUtils;

import com.google.common.collect.ImmutableList;

public abstract class HardcoreModeCommand implements TabExecutor {
    protected CommandSender sender;
    protected double newPercent;
    protected DecimalFormat percent;

    public HardcoreModeCommand() {
        percent = new DecimalFormat("##0.00%");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
                this.sender = sender;

                if (!checkTogglePermissions()) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (checkEnabled()) {
                    disable();
                }
                else {
                    enable();
                }

                return true;

            case 1:
                if (CommandUtils.shouldEnableToggle(args[0])) {
                    if (!Permissions.hardcoreToggle(sender)) {
                        sender.sendMessage(command.getPermissionMessage());
                        return true;
                    }

                    enable();
                    return true;
                }

                if (CommandUtils.shouldDisableToggle(args[0])) {
                    if (!Permissions.hardcoreToggle(sender)) {
                        sender.sendMessage(command.getPermissionMessage());
                        return true;
                    }

                    disable();
                    return true;
                }

                if (isInvalidPercentage(sender, args[0])) {
                    return true;
                }

                if (!Permissions.hardcoreModify(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                modify();
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

    protected abstract boolean checkTogglePermissions();
    protected abstract boolean checkModifyPermissions();
    protected abstract boolean checkEnabled();
    protected abstract void enable();
    protected abstract void disable();
    protected abstract void modify();

    private boolean isInvalidPercentage(CommandSender sender, String value) {
        if (CommandUtils.isInvalidDouble(sender, value)) {
            return true;
        }

        newPercent = Double.parseDouble(value);
        return false;
    }
}
