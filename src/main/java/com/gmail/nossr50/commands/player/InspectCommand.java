package com.gmail.nossr50.commands.player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.google.common.collect.ImmutableList;
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
            McMMOPlayer mmoPlayer = mcMMO.getUserManager().getOfflinePlayer(playerName);

            // If the mmoPlayer doesn't exist, create a temporary profile and check if it's present in the database. If it's not, abort the process.
            if (mmoPlayer == null) {
                PlayerProfile profile = mcMMO.getDatabaseManager().queryPlayerDataByUUID(playerName, false); // Temporary Profile

                if (!CommandUtils.isLoaded(sender, profile)) {
                    return true;
                }

                if (Config.getInstance().getScoreboardsEnabled() && sender instanceof Player && Config.getInstance().getInspectUseBoard()) {
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
                Player target = mmoPlayer.getPlayer();

                if (CommandUtils.hidden(sender, target, Permissions.inspectHidden(sender))) {
                    sender.sendMessage(LocaleLoader.getString("Inspect.Offline"));
                    return true;
                } else if (CommandUtils.tooFar(sender, target, Permissions.inspectFar(sender))) {
                    return true;
                }

                if (Config.getInstance().getScoreboardsEnabled() && sender instanceof Player && Config.getInstance().getInspectUseBoard()) {
                    ScoreboardManager.enablePlayerInspectScoreboard((Player) sender, mmoPlayer);

                    if (!Config.getInstance().getInspectUseChat()) {
                        return true;
                    }
                }

                sender.sendMessage(LocaleLoader.getString("Inspect.Stats", target.getName()));
                CommandUtils.printGatheringSkills(target, sender);
                CommandUtils.printCombatSkills(target, sender);
                CommandUtils.printMiscSkills(target, sender);
                sender.sendMessage(LocaleLoader.getString("Commands.PowerLevel", mmoPlayer.getPowerLevel()));
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
