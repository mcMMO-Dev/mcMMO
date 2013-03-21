package com.gmail.nossr50.commands.spout;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class XplockCommand extends SpoutCommand {
    @Override
    protected boolean noArguments(Command command, CommandSender sender, String[] args) {
        if (spoutHud.getXpBarLocked()) {
            unlockXpBar(sender);
            return true;
        }

        lockXpBar(sender, spoutHud.getLastGained());
        return true;
    }

    @Override
    protected boolean oneArgument(Command command, CommandSender sender, String[] args) {
        if (CommandUtils.shouldEnableToggle(args[0])) {
            lockXpBar(sender, spoutHud.getLastGained());
            return true;
        }

        if (CommandUtils.shouldDisableToggle(args[0])) {
            unlockXpBar(sender);
            return true;
        }

        if (CommandUtils.isInvalidSkill(sender, args[0])) {
            return true;
        }

        SkillType skill = SkillType.getSkill(args[0]);

        if (CommandUtils.isChildSkill(sender, skill)) {
            return true;
        }

        if (!Permissions.xplock(sender, skill)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        lockXpBar(sender, skill);
        return true;
    }

    private void lockXpBar(CommandSender sender, SkillType skill) {
        if (skill != null) {
            spoutHud.setXpBarLocked(true);
            spoutHud.setSkillLock(skill);
            spoutHud.updateXpBar();
            sender.sendMessage(LocaleLoader.getString("Commands.xplock.locked", SkillUtils.getSkillName(skill)));
        }
    }

    private void unlockXpBar(CommandSender sender) {
        spoutHud.setXpBarLocked(false);
        sender.sendMessage(LocaleLoader.getString("Commands.xplock.unlocked"));
    }
}
