package com.gmail.nossr50.commands.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;

import com.google.common.collect.ImmutableList;

public class InspectCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                if (sender instanceof Player && Config.getInstance().getInspectScoreboardEnabled()) {
                    ScoreboardManager.setupPlayerScoreboard(sender.getName());
                }

                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(args[0]);

                // If the mcMMOPlayer doesn't exist, create a temporary profile and check if it's present in the database. If it's not, abort the process.
                if (mcMMOPlayer == null) {
                    PlayerProfile profile = new PlayerProfile(args[0], false); // Temporary Profile

                    if (CommandUtils.inspectOffline(sender, profile, Permissions.inspectOffline(sender))) {
                        return true;
                    }

                    if (sender instanceof Player && Config.getInstance().getInspectScoreboardEnabled()) {
                        ScoreboardManager.enablePlayerInspectScoreboardOffline((Player) sender, profile);
                        return true;
                    }

                    sender.sendMessage(LocaleLoader.getString("Inspect.OfflineStats", args[0]));

                    sender.sendMessage(LocaleLoader.getString("Stats.Header.Gathering"));
                    CommandUtils.displaySkill(sender, profile, SkillType.EXCAVATION);
                    CommandUtils.displaySkill(sender, profile, SkillType.FISHING);
                    CommandUtils.displaySkill(sender, profile, SkillType.HERBALISM);
                    CommandUtils.displaySkill(sender, profile, SkillType.MINING);
                    CommandUtils.displaySkill(sender, profile, SkillType.WOODCUTTING);

                    sender.sendMessage(LocaleLoader.getString("Stats.Header.Combat"));
                    CommandUtils.displaySkill(sender, profile, SkillType.AXES);
                    CommandUtils.displaySkill(sender, profile, SkillType.ARCHERY);
                    CommandUtils.displaySkill(sender, profile, SkillType.SWORDS);
                    CommandUtils.displaySkill(sender, profile, SkillType.TAMING);
                    CommandUtils.displaySkill(sender, profile, SkillType.UNARMED);

                    sender.sendMessage(LocaleLoader.getString("Stats.Header.Misc"));
                    CommandUtils.displaySkill(sender, profile, SkillType.ACROBATICS);
                    CommandUtils.displaySkill(sender, profile, SkillType.REPAIR);
                }
                else {
                    Player target = mcMMOPlayer.getPlayer();

                    if (CommandUtils.tooFar(sender, target, Permissions.inspectFar(sender))) {
                        return true;
                    }

                    if (sender instanceof Player && Config.getInstance().getInspectScoreboardEnabled()) {
                        ScoreboardManager.enablePlayerInspectScoreboardOnline((Player) sender, mcMMOPlayer);
                        return true;
                    }

                    sender.sendMessage(LocaleLoader.getString("Inspect.Stats", target.getName()));
                    CommandUtils.printGatheringSkills(target, sender);
                    CommandUtils.printCombatSkills(target, sender);
                    CommandUtils.printMiscSkills(target, sender);
                    sender.sendMessage(LocaleLoader.getString("Commands.PowerLevel", mcMMOPlayer.getPowerLevel()));
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
                Set<String> playerNames = UserManager.getPlayers().keySet();
                return StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<String>(playerNames.size()));
            default:
                return ImmutableList.of();
        }
    }
}
