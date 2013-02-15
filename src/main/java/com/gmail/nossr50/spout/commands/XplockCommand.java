package com.gmail.nossr50.spout.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;

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
        if (args[0].equalsIgnoreCase("on")) {
            lockXpBar(sender, spoutHud.getLastGained());
            return true;
        }

        if (args[0].equalsIgnoreCase("off")) {
            unlockXpBar(sender);
            return true;
        }

        if (!SkillTools.isSkill(args[0])) {
            sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
            return true;
        }

        SkillType skill = SkillType.getSkill(args[0]);

        if (!sender.hasPermission("mcmmo.commands.xplock." + skill.toString().toLowerCase())) {
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
            sender.sendMessage(LocaleLoader.getString("Commands.xplock.locked", SkillTools.getSkillName(skill)));
        }
    }

    private void unlockXpBar(CommandSender sender) {
        spoutHud.setXpBarLocked(false);
        sender.sendMessage(LocaleLoader.getString("Commands.xplock.unlocked"));
    }
}
