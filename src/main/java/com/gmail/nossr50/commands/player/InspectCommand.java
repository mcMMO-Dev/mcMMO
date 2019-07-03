package com.gmail.nossr50.commands.player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class InspectCommand implements TabExecutor {

    private mcMMO pluginRef;

    public InspectCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                String playerName = pluginRef.getCommandTools().getMatchedPlayerName(args[0]);
                McMMOPlayer mcMMOPlayer = UserManager.getOfflinePlayer(playerName);

                // If the mcMMOPlayer doesn't exist, create a temporary profile and check if it's present in the database. If it's not, abort the process.
                if (mcMMOPlayer == null) {
                    PlayerProfile profile = pluginRef.getDatabaseManager().loadPlayerProfile(playerName, false); // Temporary Profile

                    if (!pluginRef.getCommandTools().isLoaded(sender, profile)) {
                        return true;
                    }


                    if (pluginRef.getScoreboardSettings().getScoreboardsEnabled() && sender instanceof Player
                            && pluginRef.getScoreboardSettings().getConfigSectionScoreboardTypes().getConfigSectionInspectBoard().isUseThisBoard()) {
                        ScoreboardManager.enablePlayerInspectScoreboard((Player) sender, profile);

                        if (!pluginRef.getScoreboardSettings().getConfigSectionScoreboardTypes().getConfigSectionInspectBoard().isPrintToChat()) {
                            return true;
                        }
                    }

                    sender.sendMessage(pluginRef.getLocaleManager().getString("Inspect.OfflineStats", playerName));

                    sender.sendMessage(pluginRef.getLocaleManager().getString("Stats.Header.Gathering"));
                    for (PrimarySkillType skill : PrimarySkillType.GATHERING_SKILLS) {
                        sender.sendMessage(pluginRef.getCommandTools().displaySkill(profile, skill));
                    }

                    sender.sendMessage(pluginRef.getLocaleManager().getString("Stats.Header.Combat"));
                    for (PrimarySkillType skill : PrimarySkillType.COMBAT_SKILLS) {
                        sender.sendMessage(pluginRef.getCommandTools().displaySkill(profile, skill));
                    }

                    sender.sendMessage(pluginRef.getLocaleManager().getString("Stats.Header.Misc"));
                    for (PrimarySkillType skill : PrimarySkillType.MISC_SKILLS) {
                        sender.sendMessage(pluginRef.getCommandTools().displaySkill(profile, skill));
                    }

                } else {
                    Player target = mcMMOPlayer.getPlayer();

                    if (pluginRef.getCommandTools().hidden(sender, target, Permissions.inspectHidden(sender))) {
                        sender.sendMessage(pluginRef.getLocaleManager().getString("Inspect.Offline"));
                        return true;
                    }
                    else if (pluginRef.getCommandTools().tooFar(sender, target, Permissions.inspectFar(sender))) {
                        return true;
                    }

                    if (pluginRef.getScoreboardSettings().getScoreboardsEnabled() && sender instanceof Player && pluginRef.getScoreboardSettings().getConfigSectionScoreboardTypes().getConfigSectionInspectBoard().isUseThisBoard()) {
                        ScoreboardManager.enablePlayerInspectScoreboard((Player) sender, mcMMOPlayer.getProfile());

                        if (!pluginRef.getScoreboardSettings().getConfigSectionScoreboardTypes().getConfigSectionInspectBoard().isPrintToChat()) {
                            return true;
                        }
                    }

                    sender.sendMessage(pluginRef.getLocaleManager().getString("Inspect.Stats", target.getName()));
                    pluginRef.getCommandTools().printGatheringSkills(target, sender);
                    pluginRef.getCommandTools().printCombatSkills(target, sender);
                    pluginRef.getCommandTools().printMiscSkills(target, sender);
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.PowerLevel", mcMMOPlayer.getPowerLevel()));
                }

                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                List<String> playerNames = pluginRef.getCommandTools().getOnlinePlayerNames(sender);
                return StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<>(playerNames.size()));
            default:
                return ImmutableList.of();
        }
    }
}
