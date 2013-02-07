package com.gmail.nossr50.spout.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.spout.SpoutConfig;
import com.gmail.nossr50.spout.huds.SpoutHud;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class XplockCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (!mcMMO.spoutEnabled || !SpoutConfig.getInstance().getXPBarEnabled()) {
            sender.sendMessage(LocaleLoader.getString("Commands.Disabled"));
            return true;
        }

        PlayerProfile playerProfile = Users.getPlayer((Player) sender).getProfile();
        SpoutHud spoutHud = playerProfile.getSpoutHud();
        SkillType lastGained;

        if (spoutHud == null) {
            sender.sendMessage(LocaleLoader.getString("Commands.Disabled"));
            return true;
        }

        switch (args.length) {
        case 0:
            if (spoutHud.getXpBarLocked()) {
                spoutHud.toggleXpBarLocked();
                sender.sendMessage(LocaleLoader.getString("Commands.xplock.unlocked"));
                return true;
            }

            lastGained = spoutHud.getLastGained();

            if (lastGained == null) {
                return false;
            }

            spoutHud.toggleXpBarLocked();
            spoutHud.setSkillLock(lastGained);
            spoutHud.updateXpBar();

            sender.sendMessage(LocaleLoader.getString("Commands.xplock.locked", Misc.getCapitalized(lastGained.toString())));
            return true;

        case 1:
            if (args[0].equalsIgnoreCase("on")) {
                lastGained = spoutHud.getLastGained();

                spoutHud.setXpBarLocked(true);
                spoutHud.setSkillLock(lastGained);
                spoutHud.updateXpBar();

                sender.sendMessage(LocaleLoader.getString("Commands.xplock.locked", Misc.getCapitalized(lastGained.toString())));
                return true;
            }

            if (args[0].equalsIgnoreCase("off")) {
                spoutHud.setXpBarLocked(false);
                sender.sendMessage(LocaleLoader.getString("Commands.xplock.unlocked"));
                return true;
            }

            if (!SkillTools.isSkill(args[0])) {
                sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                return true;
            }

            if (!Permissions.hasPermission(sender, "mcmmo.commands.xplock." + args[0].toLowerCase())) {
                sender.sendMessage(command.getPermissionMessage());
            }

            spoutHud.setXpBarLocked(true);
            spoutHud.setSkillLock(SkillTools.getSkillType(args[0]));
            spoutHud.updateXpBar();

            sender.sendMessage(LocaleLoader.getString("Commands.xplock.locked", Misc.getCapitalized(args[0])));
            return true;

        default:
            return false;
        }
    }
}
