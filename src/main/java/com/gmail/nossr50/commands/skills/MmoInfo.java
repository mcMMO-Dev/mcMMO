package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkill;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.listeners.InteractionManager;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.TextComponentFactory;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the command that retrieves data about skills from in-game sources
 */
public class MmoInfo implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player)
        {
            Player player = (Player) commandSender;
            if(Permissions.mmoinfo(player))
            {
                if(args == null || args[0] == null)
                    return false;

                //Real skill
                if(InteractionManager.getAbstractByName(args[0]) != null || PrimarySkill.SUBSKILL_NAMES.contains(args[0]))
                {
                    displayInfo(player, args[0]);
                    return true;
                }

                //Not a real skill
                player.sendMessage(LocaleLoader.getString("Commands.MmoInfo.NoMatch"));
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return StringUtil.copyPartialMatches(args[0], PrimarySkill.SUBSKILL_NAMES, new ArrayList<String>(PrimarySkill.SUBSKILL_NAMES.size()));
            default:
                return ImmutableList.of();
        }
    }

    private void displayInfo(Player player, String subSkillName)
    {
        System.out.println("[mcMMO] Debug: Grabbing info for skill "+subSkillName);

        //Check to see if the skill exists in the new system
        AbstractSubSkill abstractSubSkill = InteractionManager.getAbstractByName(subSkillName);
        if(abstractSubSkill != null)
        {
            /* New System Skills are programmable */
            abstractSubSkill.printInfo(player);
            //TextComponentFactory.sendPlayerUrlHeader(player);
        } else {
            /*
             * Skill is only in the old system
             */
            player.sendMessage(LocaleLoader.getString("Commands.MmoInfo.Header"));
            player.sendMessage(LocaleLoader.getString("Commands.MmoInfo.SubSkillHeader", subSkillName));
            player.sendMessage(LocaleLoader.getString("Commands.MmoInfo.DetailsHeader"));
            player.sendMessage(LocaleLoader.getString("Commands.MmoInfo.OldSkill"));
            //TextComponentFactory.sendPlayerUrlHeader(player);
        }
    }
}
