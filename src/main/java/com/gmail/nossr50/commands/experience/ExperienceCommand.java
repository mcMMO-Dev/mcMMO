package com.gmail.nossr50.commands.experience;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;


public abstract class ExperienceCommand implements CommandExecutor {
    protected McMMOPlayer mcMMOPlayer;
    protected Player player;
    protected PlayerProfile profile;

    protected boolean allSkills;
    protected SkillType skill;
    protected int value;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2:
                if (CommandUtils.noConsoleUsage(sender)) {
                    return true;
                }

                if (!permissionsCheckSelf(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (!validateArguments(sender, args[0], args[1])) {
                    return true;
                }

                mcMMOPlayer = UserManager.getPlayer(sender.getName());
                player = mcMMOPlayer.getPlayer();
                profile = mcMMOPlayer.getProfile();

                editValues();
                return true;

            case 3:
                if (!permissionsCheckOthers(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (!validateArguments(sender, args[1], args[2])) {
                    return true;
                }

                mcMMOPlayer = UserManager.getPlayer(args[0]);

                // If the mcMMOPlayer doesn't exist, create a temporary profile and check if it's present in the database. If it's not, abort the process.
                if (mcMMOPlayer == null) {
                    profile = new PlayerProfile(args[0], false);

                    if (CommandUtils.unloadedProfile(sender, profile)) {
                        return true;
                    }

                    editValues();
                    profile.save(); // Since this is a temporary profile, we save it here.
                }
                else {
                    profile = mcMMOPlayer.getProfile();
                    editValues();
                }

                handleSenderMessage(sender, args[0]);
                return true;

            default:
                return false;
        }
    }

    protected abstract boolean permissionsCheckSelf(CommandSender sender);
    protected abstract boolean permissionsCheckOthers(CommandSender sender);
    protected abstract void handleCommand(SkillType skill);
    protected abstract void handlePlayerMessageAll();
    protected abstract void handlePlayerMessageSkill();

    private boolean validateArguments(CommandSender sender, String skillName, String value) {
        if (isInvalidInteger(sender, value) || isInvalidSkill(sender, skillName)) {
            return false;
        }

        return true;
    }

    private boolean isInvalidInteger(CommandSender sender, String value) {
        if (CommandUtils.isInvalidInteger(sender, value)) {
            return true;
        }

        this.value = Integer.parseInt(value);
        return false;
    }

    protected boolean isInvalidSkill(CommandSender sender, String skillName) {
        if (skillName.equalsIgnoreCase("all")) {
            allSkills = true;
            return false;
        }
        else if (CommandUtils.isInvalidSkill(sender, skillName)) {
            return true;
        }

        skill = SkillType.getSkill(skillName);
        return false;
    }

    protected void handleSenderMessage(CommandSender sender, String playerName) {
        if (allSkills) {
            sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.2", playerName));
        }
        else {
            sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardSkill.2", SkillUtils.getSkillName(skill), playerName));
        }
    }

    protected void editValues() {
        if (allSkills) {
            for (SkillType skillType : SkillType.values()) {
                handleCommand(skillType);
            }

            if (player != null) {
                handlePlayerMessageAll();
            }
        }
        else {
            handleCommand(skill);

            if (player != null) {
                handlePlayerMessageSkill();
            }
        }
    }
}
