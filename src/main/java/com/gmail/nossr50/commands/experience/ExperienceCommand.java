package com.gmail.nossr50.commands.experience;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillTools;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

public abstract class ExperienceCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, String[] args) {
        PrimarySkillType skill;

        if (args.length < 2) {
            return false;
        } else {
            if (args.length == 2 && !isSilent(args) || args.length == 3 && isSilent(args)) {
                if (CommandUtils.noConsoleUsage(sender)) {
                    return true;
                }

                if (!permissionsCheckSelf(sender)) {
                    if (command.getPermissionMessage() != null) {
                        sender.sendMessage(command.getPermissionMessage());
                    }
                    sender.sendMessage("(mcMMO) No permission!");
                    return true;
                }

                if (!validateArguments(sender, args[0], args[1])) {
                    return true;
                }

                skill = mcMMO.p.getSkillTools().matchSkill(args[0]);

                if (args[1].equalsIgnoreCase("all")) {
                    skill = null;
                }

                if (skill != null && SkillTools.isChildSkill(skill)) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Skill.ChildSkill"));
                    return true;
                }

                //Profile not loaded
                if (UserManager.getPlayer(sender.getName()) == null) {
                    sender.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));
                    return true;
                }

                editValues((Player) sender, UserManager.getPlayer(sender.getName()).getProfile(),
                        skill, Integer.parseInt(args[1]), isSilent(args));
                return true;
            } else if ((args.length == 3 && !isSilent(args))
                    || (args.length == 4 && isSilent(args))) {
                if (!permissionsCheckOthers(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (!validateArguments(sender, args[1], args[2])) {
                    return true;
                }

                skill = mcMMO.p.getSkillTools().matchSkill(args[1]);

                if (args[1].equalsIgnoreCase("all")) {
                    skill = null;
                }

                if (skill != null && SkillTools.isChildSkill(skill)) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Skill.ChildSkill"));
                    return true;
                }

                int value = Integer.parseInt(args[2]);

                String playerName = CommandUtils.getMatchedPlayerName(args[0]);
                final McMMOPlayer mmoPlayer = UserManager.getOfflinePlayer(playerName);

                // If the mmoPlayer doesn't exist, create a temporary profile and check if it's present in the database. If it's not, abort the process.
                if (mmoPlayer == null) {
                    PlayerProfile profile;

                    profile = mcMMO.getDatabaseManager().loadPlayerProfile(playerName);

                    //Check loading by UUID
                    if (CommandUtils.unloadedProfile(sender, profile)) {
                        //Check loading by name
                        profile = mcMMO.getDatabaseManager().loadPlayerProfile(playerName);

                        if (CommandUtils.unloadedProfile(sender, profile)) {
                            return true;
                        }
                    }

                    editValues(null, profile, skill, value, isSilent(args));
                } else {
                    editValues(mmoPlayer.getPlayer(), mmoPlayer.getProfile(), skill, value,
                            isSilent(args));
                }

                handleSenderMessage(sender, playerName, skill);
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean isSilent(String[] args) {
        int length = args.length;

        if (length == 0) {
            return false;
        }

        return args[length - 1].equalsIgnoreCase("-s");
    }


    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String alias, String[] args) {
        switch (args.length) {
            case 1:
                List<String> playerNames = CommandUtils.getOnlinePlayerNames(sender);
                return StringUtil.copyPartialMatches(args[0], playerNames,
                        new ArrayList<>(playerNames.size()));
            case 2:
                return StringUtil.copyPartialMatches(args[1],
                        mcMMO.p.getSkillTools().LOCALIZED_SKILL_NAMES,
                        new ArrayList<>(mcMMO.p.getSkillTools().LOCALIZED_SKILL_NAMES.size()));
            default:
                return ImmutableList.of();
        }
    }

    protected abstract boolean permissionsCheckSelf(CommandSender sender);

    protected abstract boolean permissionsCheckOthers(CommandSender sender);

    protected abstract void handleCommand(Player player, PlayerProfile profile,
            PrimarySkillType skill, int value);

    protected abstract void handlePlayerMessageAll(Player player, int value, boolean isSilent);

    protected abstract void handlePlayerMessageSkill(Player player, int value,
            PrimarySkillType skill, boolean isSilent);

    private boolean validateArguments(CommandSender sender, String skillName, String value) {
        return !(CommandUtils.isInvalidInteger(sender, value) || (!skillName.equalsIgnoreCase("all")
                && CommandUtils.isInvalidSkill(sender, skillName)));
    }

    protected static void handleSenderMessage(CommandSender sender, String playerName,
            PrimarySkillType skill) {
        if (skill == null) {
            sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.2", playerName));
        } else {
            sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardSkill.2",
                    mcMMO.p.getSkillTools().getLocalizedSkillName(skill), playerName));
        }
    }

    protected void editValues(Player player, PlayerProfile profile, PrimarySkillType skill,
            int value, boolean isSilent) {
        if (skill == null) {
            for (PrimarySkillType primarySkillType : SkillTools.NON_CHILD_SKILLS) {
                handleCommand(player, profile, primarySkillType, value);
            }

            if (player != null) {
                handlePlayerMessageAll(player, value, isSilent);
            }
        } else {
            handleCommand(player, profile, skill, value);

            if (player != null) {
                handlePlayerMessageSkill(player, value, skill, isSilent);
            }
        }
    }
}
