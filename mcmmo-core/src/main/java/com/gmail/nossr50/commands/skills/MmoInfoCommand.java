package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.listeners.InteractionManager;
import com.gmail.nossr50.mcMMO;
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
public class MmoInfoCommand implements TabExecutor {

    private final mcMMO pluginRef;

    public MmoInfoCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        /*
         * Only allow players to use this command
         */
        if (commandSender instanceof Player) {
            if (args.length < 1)
                return false;

            Player player = (Player) commandSender;
            if (pluginRef.getPermissionTools().mmoinfo(player)) {
                if (args == null || args[0] == null)
                    return false;

                if (args[0].equalsIgnoreCase("???")) {
                    player.sendMessage(pluginRef.getLocaleManager().getString("Commands.MmoInfo.Header"));
                    player.sendMessage(pluginRef.getLocaleManager().getString("Commands.MmoInfo.SubSkillHeader", "???"));
                    player.sendMessage(pluginRef.getLocaleManager().getString("Commands.MmoInfo.DetailsHeader"));
                    player.sendMessage(pluginRef.getLocaleManager().getString("Commands.MmoInfo.Mystery"));
                    return true;
                } else if (InteractionManager.getAbstractByName(args[0]) != null || pluginRef.getSkillTools().isSubSkillNameExact(args[0])) {
                    displayInfo(player, args[0]);
                    return true;
                }

                //Not a real skill
                player.sendMessage(pluginRef.getLocaleManager().getString("Commands.MmoInfo.NoMatch"));
                return true;
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return StringUtil.copyPartialMatches(args[0], pluginRef.getSkillTools().EXACT_SUBSKILL_NAMES, new ArrayList<>(pluginRef.getSkillTools().EXACT_SUBSKILL_NAMES.size()));
            default:
                return ImmutableList.of();
        }
    }

    private void displayInfo(Player player, String subSkillName) {
        //Check to see if the skill exists in the new system
        AbstractSubSkill abstractSubSkill = InteractionManager.getAbstractByName(subSkillName);
        if (abstractSubSkill != null) {
            /* New System Skills are programmable */
            abstractSubSkill.printInfo(player);
            //pluginRef.getTextComponentFactory().sendPlayerUrlHeader(player);
        } else {
            /*
             * Skill is only in the old system
             */
            player.sendMessage(pluginRef.getLocaleManager().getString("Commands.MmoInfo.Header"));
            player.sendMessage(pluginRef.getLocaleManager().getString("Commands.MmoInfo.SubSkillHeader", subSkillName));
            player.sendMessage(pluginRef.getLocaleManager().getString("Commands.MmoInfo.DetailsHeader"));
            player.sendMessage(pluginRef.getLocaleManager().getString("Commands.MmoInfo.OldSkill"));
        }

        for (SubSkillType subSkillType : SubSkillType.values()) {
            if (subSkillType.getNiceNameNoSpaces(subSkillType).equalsIgnoreCase(subSkillName))
                subSkillName = subSkillType.getWikiName(subSkillType.toString());
        }

        //Send Player Wiki Link
        pluginRef.getTextComponentFactory().sendPlayerSubSkillWikiLink(player, subSkillName);
    }
}
