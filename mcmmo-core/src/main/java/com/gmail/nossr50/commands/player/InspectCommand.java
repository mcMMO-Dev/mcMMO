package com.gmail.nossr50.commands.player;

import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class InspectCommand implements TabExecutor {

    private final mcMMO pluginRef;

    public InspectCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                String playerName = pluginRef.getCommandTools().getMatchedPlayerName(args[0]);
                BukkitMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getOfflinePlayer(playerName);

                // If the mcMMOPlayer doesn't exist, create a temporary profile and check if it's present in the database. If it's not, abort the process.
                if (mcMMOPlayer == null) {
                    PlayerProfile profile = pluginRef.getDatabaseManager().loadPlayerProfile(playerName, false); // Temporary Profile

                    if (!pluginRef.getCommandTools().isLoaded(sender, profile)) {
                        return true;
                    }


                    if (pluginRef.getScoreboardSettings().getScoreboardsEnabled() && sender instanceof Player
                            && pluginRef.getScoreboardSettings().getConfigSectionScoreboardTypes().getConfigSectionInspectBoard().isUseThisBoard()) {
                        pluginRef.getScoreboardManager().enablePlayerInspectScoreboard((Player) sender, profile);

                        if (!pluginRef.getScoreboardSettings().getConfigSectionScoreboardTypes().getConfigSectionInspectBoard().isPrintToChat()) {
                            return true;
                        }
                    }

                    sender.sendMessage(pluginRef.getLocaleManager().getString("Inspect.OfflineStats", playerName));

                    sender.sendMessage(pluginRef.getLocaleManager().getString("Stats.Header.Gathering"));
                    for (PrimarySkillType skill : pluginRef.getSkillTools().GATHERING_SKILLS) {
                        sender.sendMessage(pluginRef.getCommandTools().displaySkill(profile, skill));
                    }

                    sender.sendMessage(pluginRef.getLocaleManager().getString("Stats.Header.Combat"));
                    for (PrimarySkillType skill : pluginRef.getSkillTools().COMBAT_SKILLS) {
                        sender.sendMessage(pluginRef.getCommandTools().displaySkill(profile, skill));
                    }

                    sender.sendMessage(pluginRef.getLocaleManager().getString("Stats.Header.Misc"));
                    for (PrimarySkillType skill : pluginRef.getSkillTools().MISC_SKILLS) {
                        sender.sendMessage(pluginRef.getCommandTools().displaySkill(profile, skill));
                    }

                } else {
                    Player target = mcMMOPlayer.getNative();

                    if (pluginRef.getCommandTools().hidden(sender, target, pluginRef.getPermissionTools().inspectHidden(sender))) {
                        sender.sendMessage(pluginRef.getLocaleManager().getString("Inspect.Offline"));
                        return true;
                    }
                    else if (pluginRef.getCommandTools().tooFar(sender, target, pluginRef.getPermissionTools().inspectFar(sender))) {
                        return true;
                    }

                    if (pluginRef.getScoreboardSettings().getScoreboardsEnabled() && sender instanceof Player && pluginRef.getScoreboardSettings().getConfigSectionScoreboardTypes().getConfigSectionInspectBoard().isUseThisBoard()) {
                        pluginRef.getScoreboardManager().enablePlayerInspectScoreboard((Player) sender, mcMMOPlayer.getProfile());

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
