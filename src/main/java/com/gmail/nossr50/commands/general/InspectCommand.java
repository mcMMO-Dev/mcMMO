package com.gmail.nossr50.commands.general;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class InspectCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String usage = "Proper usage is /inspect <player>"; //TODO: Needs more locale.

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.inspect")) {
            return true;
        }

        switch (args.length) {
        case 1:
            McMMOPlayer mcmmoPlayer = Users.getPlayer(args[0]);

            if (mcmmoPlayer != null) {
                Player target = mcmmoPlayer.getPlayer();

                if (sender instanceof Player && !sender.isOp() && !Misc.isNear(((Player) sender).getLocation(), target.getLocation(), 5.0) && !Permissions.getInstance().inspectDistanceBypass((Player) sender)) {
                    sender.sendMessage(LocaleLoader.getString("Inspect.TooFar"));
                    return true;
                }

                sender.sendMessage(LocaleLoader.getString("Inspect.Stats", new Object[] { target.getName() }));
                CommandHelper.printGatheringSkills(target, sender);
                CommandHelper.printCombatSkills(target, sender);
                CommandHelper.printMiscSkills(target, sender);
                sender.sendMessage(LocaleLoader.getString("Commands.PowerLevel", new Object[] { mcmmoPlayer.getPowerLevel() }));

                return true;
            }
            else {
                if (sender instanceof Player && !sender.isOp() && !Permissions.getInstance().inspectOfflineBypass((Player) sender)) {
                    sender.sendMessage(LocaleLoader.getString("Inspect.Offline"));
                    return true;
                }

                PlayerProfile profile = new PlayerProfile(args[0], false); //Temporary Profile

                if (!profile.isLoaded()) {
                    sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                    return true;
                }

                sender.sendMessage(LocaleLoader.getString("Inspect.OfflineStats", new Object[] { args[0] }));

                sender.sendMessage(LocaleLoader.getString("Stats.Header.Gathering"));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Excavation.Listener"), profile.getSkillLevel(SkillType.EXCAVATION), profile.getSkillXpLevel(SkillType.EXCAVATION), profile.getXpToLevel(SkillType.EXCAVATION) }));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Fishing.Listener"), profile.getSkillLevel(SkillType.FISHING), profile.getSkillXpLevel(SkillType.FISHING), profile.getXpToLevel(SkillType.FISHING) }));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Herbalism.Listener"), profile.getSkillLevel(SkillType.HERBALISM), profile.getSkillXpLevel(SkillType.HERBALISM), profile.getXpToLevel(SkillType.HERBALISM) }));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Mining.Listener"), profile.getSkillLevel(SkillType.MINING), profile.getSkillXpLevel(SkillType.MINING), profile.getXpToLevel(SkillType.MINING) }));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Woodcutting.Listener"), profile.getSkillLevel(SkillType.WOODCUTTING), profile.getSkillXpLevel(SkillType.WOODCUTTING), profile.getXpToLevel(SkillType.WOODCUTTING) }));

                sender.sendMessage(LocaleLoader.getString("Stats.Header.Combat"));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Axes.Listener"), profile.getSkillLevel(SkillType.AXES), profile.getSkillXpLevel(SkillType.AXES), profile.getXpToLevel(SkillType.AXES) }));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Archery.Listener"), profile.getSkillLevel(SkillType.ARCHERY), profile.getSkillXpLevel(SkillType.ARCHERY), profile.getXpToLevel(SkillType.ARCHERY) }));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Swords.Listener"), profile.getSkillLevel(SkillType.SWORDS), profile.getSkillXpLevel(SkillType.SWORDS), profile.getXpToLevel(SkillType.SWORDS) }));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Taming.Listener"), profile.getSkillLevel(SkillType.TAMING), profile.getSkillXpLevel(SkillType.TAMING), profile.getXpToLevel(SkillType.TAMING) }));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Unarmed.Listener"), profile.getSkillLevel(SkillType.UNARMED), profile.getSkillXpLevel(SkillType.UNARMED), profile.getXpToLevel(SkillType.UNARMED) }));

                sender.sendMessage(LocaleLoader.getString("Stats.Header.Misc"));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Acrobatics.Listener"), profile.getSkillLevel(SkillType.ACROBATICS), profile.getSkillXpLevel(SkillType.ACROBATICS), profile.getXpToLevel(SkillType.ACROBATICS) }));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Repair.Listener"), profile.getSkillLevel(SkillType.REPAIR), profile.getSkillXpLevel(SkillType.REPAIR), profile.getXpToLevel(SkillType.REPAIR) }));

                return true;
            }

        default:
            sender.sendMessage(usage);
            return true;
        }
    }
}
