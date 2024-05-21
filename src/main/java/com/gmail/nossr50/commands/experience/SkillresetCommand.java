package com.gmail.nossr50.commands.experience;

import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillTools;
import com.google.common.collect.ImmutableList;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This class mirrors the structure of ExperienceCommand, except the
 * value/quantity argument is removed.
 */
public class SkillresetCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        PrimarySkillType skill;
        switch (args.length) {
            case 1:
                if (CommandUtils.noConsoleUsage(sender)) {
                    return true;
                }

                if (!permissionsCheckSelf(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (!validateArguments(sender, args[0])) {
                    return true;
                }

                if (args[0].equalsIgnoreCase("all")) {
                    skill = null;
                } else {
                    skill = mcMMO.p.getSkillTools().matchSkill(args[0]);
                }

                editValues((Player) sender, UserManager.getPlayer(sender.getName()).getProfile(), skill);
                return true;

            case 2:
                if (!permissionsCheckOthers(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (!validateArguments(sender, args[1])) {
                    return true;
                }

                if (args[1].equalsIgnoreCase("all")) {
                    skill = null;
                } else {
                    skill = mcMMO.p.getSkillTools().matchSkill(args[1]);
                }

                String playerName = CommandUtils.getMatchedPlayerName(args[0]);
                McMMOPlayer mcMMOPlayer = UserManager.getOfflinePlayer(playerName);

                // If the mcMMOPlayer doesn't exist, create a temporary profile and check if it's present in the database. If it's not, abort the process.
                if (mcMMOPlayer == null) {
                    OfflinePlayer offlinePlayer = mcMMO.p.getServer().getOfflinePlayer(playerName);
                    PlayerProfile profile = mcMMO.getDatabaseManager().loadPlayerProfile(offlinePlayer);

                    //Check loading by UUID
                    if (CommandUtils.unloadedProfile(sender, profile)) {
                        //Didn't find it by UUID so try to find it by name
                        profile = mcMMO.getDatabaseManager().loadPlayerProfile(playerName);

                        //Check if it was present in DB
                        if (CommandUtils.unloadedProfile(sender, profile)) {
                            return true;
                        }
                    }

                    editValues(null, profile, skill);
                } else {
                    editValues(mcMMOPlayer.getPlayer(), mcMMOPlayer.getProfile(), skill);
                }

                handleSenderMessage(sender, playerName, skill);
                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        switch (args.length) {
            case 1:
                List<String> playerNames = CommandUtils.getOnlinePlayerNames(sender);
                return StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<>(playerNames.size()));
            case 2:
                return StringUtil.copyPartialMatches(args[1], mcMMO.p.getSkillTools().LOCALIZED_SKILL_NAMES, new ArrayList<>(mcMMO.p.getSkillTools().LOCALIZED_SKILL_NAMES.size()));
            default:
                return ImmutableList.of();
        }
    }

    protected void handleCommand(Player player, PlayerProfile profile, PrimarySkillType skill) {
        int levelsRemoved = profile.getSkillLevel(skill);
        float xpRemoved = profile.getSkillXpLevelRaw(skill);

        profile.modifySkill(skill, 0);

        if (player == null) {
            profile.scheduleAsyncSave();
            return;
        }

        EventUtils.tryLevelChangeEvent(player, skill, levelsRemoved, xpRemoved, false, XPGainReason.COMMAND);
    }

    protected boolean permissionsCheckSelf(CommandSender sender) {
        return Permissions.skillreset(sender);
    }

    protected boolean permissionsCheckOthers(CommandSender sender) {
        return Permissions.skillresetOthers(sender);
    }

    protected void handlePlayerMessageAll(Player player) {
        player.sendMessage(LocaleLoader.getString("Commands.Reset.All"));
    }

    protected void handlePlayerMessageSkill(Player player, PrimarySkillType skill) {
        player.sendMessage(LocaleLoader.getString("Commands.Reset.Single", mcMMO.p.getSkillTools().getLocalizedSkillName(skill)));
    }

    private boolean validateArguments(CommandSender sender, String skillName) {
        return skillName.equalsIgnoreCase("all") || !CommandUtils.isInvalidSkill(sender, skillName);
    }

    protected static void handleSenderMessage(CommandSender sender, String playerName, PrimarySkillType skill) {
        if (skill == null) {
            sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.2", playerName));
        } else {
            sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardSkill.2", mcMMO.p.getSkillTools().getLocalizedSkillName(skill), playerName));
        }
    }

    protected void editValues(Player player, PlayerProfile profile, PrimarySkillType skill) {
        if (skill == null) {
            for (PrimarySkillType primarySkillType : SkillTools.NON_CHILD_SKILLS) {
                handleCommand(player, profile, primarySkillType);
            }

            if (player != null) {
                handlePlayerMessageAll(player);
            }
        } else {
            handleCommand(player, profile, skill);

            if (player != null) {
                handlePlayerMessageSkill(player, skill);
            }
        }
    }
}
