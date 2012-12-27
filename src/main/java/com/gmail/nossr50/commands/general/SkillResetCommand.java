package com.gmail.nossr50.commands.general;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Users;

public class SkillResetCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        //ensure they have the skillreset perm
        if (CommandHelper.noCommandPermissions(sender, "mcmmo.skillreset")) {
            return true;
        }

        SkillType skillType = null; //simple initialization

        //make sure there's only one argument.  output at least some kind of error if not
        if (args.length == 0 || (args.length != 1 && args[0] != null)) {
            sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
            return true;
        }


        //parse the skilltype that they sent
        try
        {
            skillType = SkillType.valueOf(args[0].toUpperCase().trim());  //ucase needed to match enum since it's case sensitive. trim to be nice
        }catch(IllegalArgumentException ex)
        {
            sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
            return true;
        }

        //reset the values in the hash table and persist them
        PlayerProfile profile = Users.getProfile((Player)sender);

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
    }
}
