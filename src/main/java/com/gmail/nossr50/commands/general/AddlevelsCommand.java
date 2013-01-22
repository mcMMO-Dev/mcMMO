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
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Users;

public class AddlevelsCommand implements CommandExecutor{
    private final mcMMO plugin;

    public AddlevelsCommand (mcMMO plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        OfflinePlayer modifiedPlayer;
        PlayerProfile profile;
        int levels;
        SkillType skill;
        String usage = LocaleLoader.getString("Commands.Usage.3", new Object[] {"addlevels", "[" + LocaleLoader.getString("Commands.Usage.Player") + "]", "<" + LocaleLoader.getString("Commands.Usage.Skill") + ">", "<" + LocaleLoader.getString("Commands.Usage.Level") + ">" });

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.tools.mmoedit")) {
            return true;
        }

        switch (args.length) {
        case 2:
            if (sender instanceof Player) {
                if (!Skills.isSkill(args[0])) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                    return true;
                }

                if (Misc.isInt(args[1])) {
                    modifiedPlayer = (Player) sender;
                    levels = Integer.valueOf(args[1]);
                    skill = Skills.getSkillType(args[0]);
                    profile = Users.getProfile(modifiedPlayer);

                    if (skill.equals(SkillType.ALL)) {
                        sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.1", new Object[] {levels}));
                    }
                    else {
                        sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardSkill.1", new Object[] {levels, Misc.getCapitalized(skill.toString())}));
                    }

                    profile.addLevels(skill, levels);
                }
            }
            else {
                sender.sendMessage(usage);
            }

            return true;

        case 3:
            modifiedPlayer = plugin.getServer().getOfflinePlayer(args[0]);
            String playerName = modifiedPlayer.getName();
            profile = Users.getProfile(modifiedPlayer);

            if (profile == null) {
                sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                return true;
            }

            if (!profile.isLoaded()) {
                sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                return true;
            }

            if (!Skills.isSkill(args[1])) {
                sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                return true;
            }

            if (Misc.isInt(args[2])) {
                levels = Integer.valueOf(args[2]);
                skill = Skills.getSkillType(args[1]);

                Users.getProfile(modifiedPlayer).addLevels(skill, levels);

                if (skill.equals(SkillType.ALL)) {
                    sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.2", new Object[] {playerName}));
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardSkill.2", new Object[] {Misc.getCapitalized(skill.toString()), playerName}));
                }

                if (modifiedPlayer.isOnline()) {
                    if (skill.equals(SkillType.ALL)) {
                        ((Player) modifiedPlayer).sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.1", new Object[] {levels}));
                    }
                    else {
                        ((Player) modifiedPlayer).sendMessage(LocaleLoader.getString("Commands.addlevels.AwardSkill.1", new Object[] {levels, Misc.getCapitalized(skill.toString())}));
                    }
                }
            }
            else {
                sender.sendMessage(usage);
            }

            return true;

        default:
            sender.sendMessage(usage);
            return true;
        }
    }
}
