package com.gmail.nossr50.commands.spout;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.SpoutConfig;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.datatypes.SpoutHud;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Skills;
import com.gmail.nossr50.util.Users;

public class XplockCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String usage = ChatColor.RED + "Proper usage is /xplock [skill]";

        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (!mcMMO.spoutEnabled || !SpoutConfig.getInstance().getXPBarEnabled() || !Config.getInstance().getCommandXPLockEnabled()) {
            sender.sendMessage(LocaleLoader.getString("Commands.Disabled"));
            return true;
        }

        Player player = (Player) sender;
        PlayerProfile playerProfile = Users.getProfile(player);
        SpoutHud spoutHud = playerProfile.getSpoutHud();

        if (spoutHud == null) {
            sender.sendMessage(LocaleLoader.getString("Commands.Disabled"));
            return true;
        }

        switch (args.length) {
        case 0:
            if (spoutHud.getXpBarLocked()) {
                spoutHud.toggleXpBarLocked();
                player.sendMessage(LocaleLoader.getString("Commands.xplock.unlocked"));
                return true;
            }

            SkillType lastGained = spoutHud.getLastGained();

            if (lastGained != null) {
                spoutHud.toggleXpBarLocked();
                spoutHud.setSkillLock(lastGained);
                player.sendMessage(LocaleLoader.getString("Commands.xplock.locked", new Object[] { Misc.getCapitalized(lastGained.toString()) }));
            }
            else {
                player.sendMessage(usage);
            }

            return true;

        case 1:
            if (Skills.isSkill(args[0])) {
                if (Permissions.getInstance().permission(player, "mcmmo.skills." + args[0].toLowerCase())) {
                    spoutHud.setXpBarLocked(true);
                    spoutHud.setSkillLock(Skills.getSkillType(args[0]));
                    spoutHud.updateXpBar();

                    player.sendMessage(LocaleLoader.getString("Commands.xplock.locked", new Object[] { Misc.getCapitalized(args[0]) }));
                }
                else {
                    player.sendMessage(LocaleLoader.getString("mcMMO.NoPermission"));
                }
            }
            else {
                player.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
            }

            return true;

        default:
            player.sendMessage(usage);
            return true;
        }
    }
}
