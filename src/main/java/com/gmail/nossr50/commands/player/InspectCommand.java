package com.gmail.nossr50.commands.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;

public class InspectCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlayerProfile profile;

        switch (args.length) {
            case 1:
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(args[0]);

                // If the mcMMOPlayer doesn't exist, create a temporary profile and check if it's present in the database. If it's not, abort the process.
                if (mcMMOPlayer == null) {
                    profile = new PlayerProfile(args[0], false); // Temporary Profile

                    if (!profile.isLoaded()) {
                        sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                        return true;
                    }

                    // TODO: Why do we care if this is a player?
                    if (sender instanceof Player && !Permissions.inspectOffline(sender)) {
                        sender.sendMessage(LocaleLoader.getString("Inspect.Offline"));
                        return true;
                    }

                    sender.sendMessage(LocaleLoader.getString("Inspect.OfflineStats", args[0]));

                    sender.sendMessage(LocaleLoader.getString("Stats.Header.Gathering"));
                    sender.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Excavation.Listener"), profile.getSkillLevel(SkillType.EXCAVATION), profile.getSkillXpLevel(SkillType.EXCAVATION), profile.getXpToLevel(SkillType.EXCAVATION)));
                    sender.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Fishing.Listener"), profile.getSkillLevel(SkillType.FISHING), profile.getSkillXpLevel(SkillType.FISHING), profile.getXpToLevel(SkillType.FISHING)));
                    sender.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Herbalism.Listener"), profile.getSkillLevel(SkillType.HERBALISM), profile.getSkillXpLevel(SkillType.HERBALISM), profile.getXpToLevel(SkillType.HERBALISM)));
                    sender.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Mining.Listener"), profile.getSkillLevel(SkillType.MINING), profile.getSkillXpLevel(SkillType.MINING), profile.getXpToLevel(SkillType.MINING)));
                    sender.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Woodcutting.Listener"), profile.getSkillLevel(SkillType.WOODCUTTING), profile.getSkillXpLevel(SkillType.WOODCUTTING), profile.getXpToLevel(SkillType.WOODCUTTING)));

                    sender.sendMessage(LocaleLoader.getString("Stats.Header.Combat"));
                    sender.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Axes.Listener"), profile.getSkillLevel(SkillType.AXES), profile.getSkillXpLevel(SkillType.AXES), profile.getXpToLevel(SkillType.AXES)));
                    sender.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Archery.Listener"), profile.getSkillLevel(SkillType.ARCHERY), profile.getSkillXpLevel(SkillType.ARCHERY), profile.getXpToLevel(SkillType.ARCHERY)));
                    sender.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Swords.Listener"), profile.getSkillLevel(SkillType.SWORDS), profile.getSkillXpLevel(SkillType.SWORDS), profile.getXpToLevel(SkillType.SWORDS)));
                    sender.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Taming.Listener"), profile.getSkillLevel(SkillType.TAMING), profile.getSkillXpLevel(SkillType.TAMING), profile.getXpToLevel(SkillType.TAMING)));
                    sender.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Unarmed.Listener"), profile.getSkillLevel(SkillType.UNARMED), profile.getSkillXpLevel(SkillType.UNARMED), profile.getXpToLevel(SkillType.UNARMED)));

                    sender.sendMessage(LocaleLoader.getString("Stats.Header.Misc"));
                    sender.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Acrobatics.Listener"), profile.getSkillLevel(SkillType.ACROBATICS), profile.getSkillXpLevel(SkillType.ACROBATICS), profile.getXpToLevel(SkillType.ACROBATICS)));
                    sender.sendMessage(LocaleLoader.getString("Skills.Stats", LocaleLoader.getString("Repair.Listener"), profile.getSkillLevel(SkillType.REPAIR), profile.getSkillXpLevel(SkillType.REPAIR), profile.getXpToLevel(SkillType.REPAIR)));
                }
                else {
                    Player target = mcMMOPlayer.getPlayer();

                    if (sender instanceof Player) {
                        Player inspector = (Player) sender;

                        if (!Misc.isNear(inspector.getLocation(), target.getLocation(), 5.0) && !Permissions.inspectFar(inspector)) {
                            sender.sendMessage(LocaleLoader.getString("Inspect.TooFar"));
                            return true;
                        }
                    }
                    profile = mcMMOPlayer.getProfile();

                    sender.sendMessage(LocaleLoader.getString("Inspect.Stats", target.getName()));
                    CommandUtils.printGatheringSkills(target, profile, sender);
                    CommandUtils.printCombatSkills(target, profile, sender);
                    CommandUtils.printMiscSkills(target, profile, sender);
                    sender.sendMessage(LocaleLoader.getString("Commands.PowerLevel", mcMMOPlayer.getPowerLevel()));
                }

                return true;

            default:
                return false;
        }
    }
}
