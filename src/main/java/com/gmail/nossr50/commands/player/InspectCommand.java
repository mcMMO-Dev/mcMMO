package com.gmail.nossr50.commands.player;

import com.gmail.nossr50.config.Config;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InspectCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            String playerName = CommandUtils.getMatchedPlayerName(args[0]);

            PlayerProfile playerProfile = mcMMO.getUserManager().queryPlayer(playerName);
            Player targetPlayer = Bukkit.getPlayer(playerName);

            if(playerProfile == null) {
                //TODO: Localize
                sender.sendMessage("Data was not found in the database for the given player name!");
                return true;
            }


            if(targetPlayer == null) {
                //Target is offline

                if (Config.getInstance().getScoreboardsEnabled()
                        && sender instanceof Player
                        && Config.getInstance().getInspectUseBoard()) {
                    ScoreboardManager.enablePlayerInspectScoreboard((Player) sender, profile);

                    if (!Config.getInstance().getInspectUseChat()) {
                        return true;
                    }
                }

                sender.sendMessage(LocaleLoader.getString("Inspect.OfflineStats", playerName));

                sender.sendMessage(LocaleLoader.getString("Stats.Header.Gathering"));
                for (PrimarySkillType skill : PrimarySkillType.GATHERING_SKILLS) {
                    sender.sendMessage(CommandUtils.displaySkill(profile, skill));
                }

                sender.sendMessage(LocaleLoader.getString("Stats.Header.Combat"));
                for (PrimarySkillType skill : PrimarySkillType.COMBAT_SKILLS) {
                    sender.sendMessage(CommandUtils.displaySkill(profile, skill));
                }

                sender.sendMessage(LocaleLoader.getString("Stats.Header.Misc"));
                for (PrimarySkillType skill : PrimarySkillType.MISC_SKILLS) {
                    sender.sendMessage(CommandUtils.displaySkill(profile, skill));
                }
            } else {

                if (CommandUtils.hidden(sender, targetPlayer, Permissions.inspectHidden(sender))) {
                    sender.sendMessage(LocaleLoader.getString("Inspect.Offline"));
                    return true;
                } else if (CommandUtils.tooFar(sender, targetPlayer, Permissions.inspectFar(sender))) {
                    return true;
                }

                if (Config.getInstance().getScoreboardsEnabled()
                        && sender instanceof Player && Config.getInstance().getInspectUseBoard()) {
                    ScoreboardManager.enablePlayerInspectScoreboard((Player) sender, playerProfile);

                    if (!Config.getInstance().getInspectUseChat()) {
                        return true;
                    }
                }

                sender.sendMessage(LocaleLoader.getString("Inspect.Stats", targetPlayer.getName()));
                CommandUtils.printGatheringSkills(targetPlayer, sender);
                CommandUtils.printCombatSkills(targetPlayer, sender);
                CommandUtils.printMiscSkills(targetPlayer, sender);
                sender.sendMessage(LocaleLoader.getString("Commands.PowerLevel", playerProfile.getExperienceHandler().getPowerLevel()));
            }

            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            List<String> playerNames = CommandUtils.getOnlinePlayerNames(sender);
            return StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<>(playerNames.size()));
        }
        return ImmutableList.of();
    }
}
