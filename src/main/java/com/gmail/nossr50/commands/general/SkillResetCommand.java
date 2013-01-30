package com.gmail.nossr50.commands.general;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Users;

public class SkillResetCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        // DEPRECATED PERMISSION
        boolean oldPermission = !CommandHelper.noCommandPermissions(sender, "mcmmo.skillreset");
        String usage = LocaleLoader.getString("Commands.Usage.3", new Object[] {"skillreset", "[" + LocaleLoader.getString("Commands.Usage.Player") + "]", "<" + LocaleLoader.getString("Commands.Usage.Skill") + ">"});

        String perm = "mcmmo.commands.skillreset";
        if (!oldPermission && CommandHelper.noCommandPermissions(sender, perm)) {
            return true;
        }

        SkillType skillType = null; //simple initialization
	PlayerProfile profile = null;

        switch (args.length) {
        case 1:
            //make sure there's only one argument.  output at least some kind of error if not
            if (args.length == 0 || (args.length != 1 && args[0] != null)) {
                
            }

            skillType = getSkillType(sender, args[0], perm, oldPermission);

            if (skillType == null) {
                return true;
            }

            //reset the values in the hash table and persist them
            profile = Users.getProfile((Player)sender);

            if (profile == null) {
                sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                return true;
            }

            profile.resetSkill(skillType);
            profile.save();

            //display a success message to the user
            if (skillType == SkillType.ALL)
                sender.sendMessage(LocaleLoader.getString("Commands.Reset.All"));
            else
                sender.sendMessage(LocaleLoader.getString("Commands.Reset.Single", new Object[] { args[0] }));

            return true;
        case 2:
            perm += ".others";
            if (!oldPermission && CommandHelper.noCommandPermissions(sender, perm)) {
                return true;
            }

            OfflinePlayer modifiedPlayer = mcMMO.p.getServer().getOfflinePlayer(args[0]);
            profile = Users.getProfile(modifiedPlayer);

            if (profile == null) {
                sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                return true;
            }

            if (!profile.isLoaded()) {
                sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                return true;
            }

            skillType = getSkillType(sender, args[1], perm, oldPermission);

            if (skillType == null) {
                return true;
            }

            //reset the values in the hash table and persist them
            profile.resetSkill(skillType);
            profile.save();

            //display a success message to the user
            if (skillType == SkillType.ALL)
                sender.sendMessage(LocaleLoader.getString("Commands.Reset.All"));
            else
                sender.sendMessage(LocaleLoader.getString("Commands.Reset.Single", new Object[] { args[1] }));

            return true;
	default:
            sender.sendMessage(usage);
            return true;
	}
    }

    private SkillType getSkillType(CommandSender sender, String name, String perm, boolean oldPermission) {
        //parse the skilltype that they sent
        try {
            //ucase needed to match enum since it's case sensitive. trim to be nice
            SkillType type = SkillType.valueOf(name.toUpperCase().trim());
            String lowerName = type.name().toLowerCase();

            if (type == SkillType.ALL && !checkAll(sender, perm, oldPermission))
                return null;
            else if (!oldPermission && CommandHelper.noCommandPermissions(sender, perm + "." + name))
                return null;

            return type;
        } catch(IllegalArgumentException ex) {
            sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
            return null;
        }
    }

    private boolean checkAll(CommandSender sender, String perm, boolean oldPermission) {
        for (SkillType type : SkillType.values()) {
            if (type.name().equalsIgnoreCase("all"))
                continue;

            String name = type.name().toLowerCase();

            if (!oldPermission && CommandHelper.noCommandPermissions(sender, perm + "." + name))
                return false;
        }

        return true;
    }
}
