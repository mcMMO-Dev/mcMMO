//package com.gmail.nossr50.commands.hardcore;
//
//import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
//import com.gmail.nossr50.util.Permissions;
//import com.gmail.nossr50.util.commands.CommandUtils;
//import com.gmail.nossr50.util.text.StringUtils;
//import com.google.common.collect.ImmutableList;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandSender;
//import org.bukkit.command.TabExecutor;
//import org.bukkit.util.StringUtil;
//import org.jetbrains.annotations.NotNull;
//
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.List;
//
//public abstract class HardcoreModeCommand implements TabExecutor {
//    protected final DecimalFormat percent = new DecimalFormat("##0.00%");
//
//    @Override
//    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
//        switch (args.length) {
//            case 0:
//                if (!checkTogglePermissions(sender)) {
//                    sender.sendMessage(command.getPermissionMessage());
//                    return true;
//                }
//
//                if (checkEnabled(null)) {
//                    disable(null);
//                }
//                else {
//                    enable(null);
//                }
//
//                return true;
//
//            case 1:
//                if (CommandUtils.shouldEnableToggle(args[0])) {
//                    if (!Permissions.hardcoreToggle(sender)) {
//                        sender.sendMessage(command.getPermissionMessage());
//                        return true;
//                    }
//
//                    enable(null);
//                    return true;
//                }
//
//                if (CommandUtils.shouldDisableToggle(args[0])) {
//                    if (!Permissions.hardcoreToggle(sender)) {
//                        sender.sendMessage(command.getPermissionMessage());
//                        return true;
//                    }
//
//                    disable(null);
//                    return true;
//                }
//
//                if (CommandUtils.isInvalidDouble(sender, args[0])) {
//                    return true;
//                }
//
//                if (!Permissions.hardcoreModify(sender)) {
//                    sender.sendMessage(command.getPermissionMessage());
//                    return true;
//                }
//
//                modify(sender, Double.parseDouble(args[0]));
//                return true;
//
//
//            case 2:
//                if (CommandUtils.isInvalidSkill(sender, args[0])) {
//                    return true;
//                }
//
//                PrimarySkillType skill = PrimarySkillType.getSkill(args[0]);
//
//                if (!CommandUtils.isChildSkill(sender, skill)) {
//                    return true;
//                }
//
//                if (CommandUtils.shouldEnableToggle(args[1])) {
//                    if (!Permissions.hardcoreToggle(sender)) {
//                        sender.sendMessage(command.getPermissionMessage());
//                        return true;
//                    }
//
//                    enable(skill);
//                    return true;
//                }
//
//                if (CommandUtils.shouldDisableToggle(args[1])) {
//                    if (!Permissions.hardcoreToggle(sender)) {
//                        sender.sendMessage(command.getPermissionMessage());
//                        return true;
//                    }
//
//                    enable(skill);
//                    return true;
//                }
//
//                return true;
//
//            default:
//                return false;
//        }
//    }
//
//    @Override
//    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
//        if (args.length == 1) {
//            if (StringUtils.isDouble(args[0])) {
//                return ImmutableList.of();
//            }
//
//            return StringUtil.copyPartialMatches(args[0], CommandUtils.TRUE_FALSE_OPTIONS, new ArrayList<>(CommandUtils.TRUE_FALSE_OPTIONS.size()));
//        }
//        return ImmutableList.of();
//    }
//
//    protected abstract boolean checkTogglePermissions(CommandSender sender);
//    protected abstract boolean checkModifyPermissions(CommandSender sender);
//    protected abstract boolean checkEnabled(PrimarySkillType skill);
//    protected abstract void enable(PrimarySkillType skill);
//    protected abstract void disable(PrimarySkillType skill);
//    protected abstract void modify(CommandSender sender, double newPercentage);
//}
