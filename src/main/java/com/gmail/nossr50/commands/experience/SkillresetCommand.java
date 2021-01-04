package com.gmail.nossr50.commands.experience;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.google.common.collect.ImmutableList;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.neetgames.mcmmo.skill.RootSkill;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class mirrors the structure of ExperienceCommand, except the
 * value/quantity argument is removed.
 */
public class SkillresetCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        RootSkill rootSkill;
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
                    rootSkill = null;
                }
                else {
                    rootSkill = mcMMO.p.getSkillRegister().getSkill(args[0]);
                }

                editValues((Player) sender, mcMMO.getUserManager().queryPlayer(player)
, skill);
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
                    rootSkill = null;
                }
                else {
                    rootSkill = mcMMO.p.getSkillRegister().getSkill(args[1]);
                }

                String playerName = CommandUtils.getMatchedPlayerName(args[0]);
                OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(playerName);

                // If the mmoPlayer doesn't exist, create a temporary profile and check if it's present in the database. If it's not, abort the process.
                if (mmoPlayer == null) {
                    UUID uuid = null;
                    OfflinePlayer player = mcMMO.p.getServer().getOfflinePlayer(playerName);
                    if (player != null) {
                        uuid = player.getUniqueId();
                    }
                    PlayerProfile profile = mcMMO.getDatabaseManager().queryPlayerDataByUUID(playerName, uuid, false);

                    if (CommandUtils.hasNoProfile(sender, profile)) {
                        return true;
                    }

                    editValues(null, profile, skill);
                }
                else {
                    editValues(Misc.adaptPlayer(mmoPlayer), mmoPlayer, skill);
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
                return StringUtil.copyPartialMatches(args[1], PrimarySkillType.SKILL_NAMES, new ArrayList<>(PrimarySkillType.SKILL_NAMES.size()));
            default:
                return ImmutableList.of();
        }
    }

    protected void handleCommand(Player player, PlayerProfile profile, RootSkill rootSkill) {
        int levelsRemoved = profile.getSkillLevel(rootSkill);
        float xpRemoved = profile.getSkillXpLevelRaw(rootSkill);

        profile.modifySkill(rootSkill, 0);

        if (player == null) {
            profile.scheduleAsyncSave();
            return;
        }

        EventUtils.tryLevelChangeEvent(player, rootSkill, levelsRemoved, xpRemoved, false, XPGainReason.COMMAND);
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

    protected void handlePlayerMessageSkill(Player player, RootSkill rootSkill) {
        player.sendMessage(LocaleLoader.getString("Commands.Reset.Single", rootSkill.getLocalizedName()));
    }

    private boolean validateArguments(CommandSender sender, String skillName) {
        return skillName.equalsIgnoreCase("all") || !CommandUtils.isInvalidSkill(sender, skillName);
    }

    protected static void handleSenderMessage(CommandSender sender, String playerName, RootSkill rootSkill) {
        if (rootSkill == null) {
            sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.2", playerName));
        }
        else {
            sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardSkill.2", rootSkill.getLocalizedName(), playerName));
        }
    }

    protected void editValues(Player player, PlayerProfile profile, RootSkill rootSkill) {
        if (rootSkill == null) {
            for (RootSkill rootSkill : PrimarySkillType.NON_CHILD_SKILLS) {
                handleCommand(player, profile, primarySkillType);
            }

            if (player != null) {
                handlePlayerMessageAll(player);
            }
        }
        else {
            handleCommand(player, profile, skill);

            if (player != null) {
                handlePlayerMessageSkill(player, skill);
            }
        }
    }
}
