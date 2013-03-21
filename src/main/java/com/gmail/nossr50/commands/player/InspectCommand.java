package com.gmail.nossr50.commands.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;

public class InspectCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(args[0]);

                // If the mcMMOPlayer doesn't exist, create a temporary profile and check if it's present in the database. If it's not, abort the process.
                if (mcMMOPlayer == null) {
                    PlayerProfile profile = new PlayerProfile(args[0], false); // Temporary Profile

                    if (CommandUtils.inspectOffline(sender, profile, Permissions.inspectOffline(sender))) {
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
}
