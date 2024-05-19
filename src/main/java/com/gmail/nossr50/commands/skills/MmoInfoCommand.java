package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.text.TextComponentFactory;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the command that retrieves data about skills from in-game sources
 */
public class MmoInfoCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        /*
         * Only allow players to use this command
         */
        if (commandSender instanceof Player player) {
            if (args == null || args.length < 1 || args[0] == null || args[0].isEmpty())
                return false;

            if (Permissions.mmoinfo(player)) {
                if (args[0].equalsIgnoreCase( "???")) {
                    player.sendMessage(LocaleLoader.getString("Commands.MmoInfo.Header"));
                    player.sendMessage(LocaleLoader.getString("Commands.MmoInfo.SubSkillHeader", "???"));
                    player.sendMessage(LocaleLoader.getString("Commands.MmoInfo.DetailsHeader"));
                    player.sendMessage(LocaleLoader.getString("Commands.MmoInfo.Mystery"));
                    return true;
                }

                final SubSkillType subSkillType = matchSubSkill(args[0]);
                if  (subSkillType != null) {
                    displayInfo(player, subSkillType);
                } else {
                    //Not a real skill
                    player.sendMessage(LocaleLoader.getString("Commands.MmoInfo.NoMatch"));
                }
                return true;
            }
        }

        return false;
    }

    public SubSkillType matchSubSkill(String name) {
        for(SubSkillType subSkillType : SubSkillType.values()) {
            if (subSkillType.getNiceNameNoSpaces(subSkillType).equalsIgnoreCase(name)
                    || subSkillType.name().equalsIgnoreCase(name))
                return subSkillType;
        }
        return null;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], mcMMO.p.getSkillTools().EXACT_SUBSKILL_NAMES, new ArrayList<>(mcMMO.p.getSkillTools().EXACT_SUBSKILL_NAMES.size()));
        }
        return ImmutableList.of();
    }

    private void displayInfo(Player player, SubSkillType subSkillType) {
        player.sendMessage(LocaleLoader.getString("Commands.MmoInfo.Header"));
        player.sendMessage(LocaleLoader.getString("Commands.MmoInfo.SubSkillHeader", subSkillType.getLocaleName()));
        player.sendMessage(LocaleLoader.getString("Commands.MmoInfo.DetailsHeader"));

        //Send Player Wiki Link
        TextComponentFactory.sendPlayerSubSkillWikiLink(player, subSkillType.getLocaleName(), subSkillType);
    }
}
