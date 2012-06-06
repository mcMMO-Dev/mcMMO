package com.gmail.nossr50.commands.general;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.McMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class InspectCommand implements CommandExecutor {
    private final McMMO plugin;

    public InspectCommand (McMMO plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        OfflinePlayer target;
        PlayerProfile PP;
        String usage = "Proper usage is /inspect <player>"; //TODO: Needs more locale.

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.inspect")) {
            return true;
        }

        switch (args.length) {
        case 1:
            target = plugin.getServer().getOfflinePlayer(args[0]);
            PP = Users.getProfile(target);

            if (target.isOnline()) {
                Player player = (Player) target;

                if (sender instanceof Player && !sender.isOp() && !Misc.isNear(((Player) sender).getLocation(), player.getLocation(), 5.0) && !Permissions.getInstance().inspectDistanceBypass((Player) sender)) {
                    sender.sendMessage(LocaleLoader.getString("Inspect.TooFar"));
                    return true;
                }

                sender.sendMessage(LocaleLoader.getString("Inspect.Stats", new Object[] { target.getName() }));
                CommandHelper.printGatheringSkills(player, sender);
                CommandHelper.printCombatSkills(player, sender);
                CommandHelper.printMiscSkills(player, sender);
                sender.sendMessage(LocaleLoader.getString("Commands.PowerLevel", new Object[] { PP.getPowerLevel() }));

                return true;
            }
            else {
                if (sender instanceof Player && !sender.isOp() && !Permissions.getInstance().inspectOfflineBypass((Player) sender)) {
                    sender.sendMessage(LocaleLoader.getString("Inspect.Offline"));
                    return true;
                }

                if (!PP.isLoaded()) {
                    sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                    return true;
                }

                sender.sendMessage(LocaleLoader.getString("Inspect.OfflineStats", new Object[] { args[0] }));

                sender.sendMessage(LocaleLoader.getString("Stats.Header.Gathering"));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Excavation.Listener"), PP.getSkillLevel(SkillType.EXCAVATION), PP.getSkillXpLevel(SkillType.EXCAVATION), PP.getXpToLevel(SkillType.EXCAVATION) }));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Fishing.Listener"), PP.getSkillLevel(SkillType.FISHING), PP.getSkillXpLevel(SkillType.FISHING), PP.getXpToLevel(SkillType.FISHING) }));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Herbalism.Listener"), PP.getSkillLevel(SkillType.HERBALISM), PP.getSkillXpLevel(SkillType.HERBALISM), PP.getXpToLevel(SkillType.HERBALISM) }));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Mining.Listener"), PP.getSkillLevel(SkillType.MINING), PP.getSkillXpLevel(SkillType.MINING), PP.getXpToLevel(SkillType.MINING) }));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Woodcutting.Listener"), PP.getSkillLevel(SkillType.WOODCUTTING), PP.getSkillXpLevel(SkillType.WOODCUTTING), PP.getXpToLevel(SkillType.WOODCUTTING) }));

                sender.sendMessage(LocaleLoader.getString("Stats.Header.Combat"));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Axes.Listener"), PP.getSkillLevel(SkillType.AXES), PP.getSkillXpLevel(SkillType.AXES), PP.getXpToLevel(SkillType.AXES) }));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Archery.Listener"), PP.getSkillLevel(SkillType.ARCHERY), PP.getSkillXpLevel(SkillType.ARCHERY), PP.getXpToLevel(SkillType.ARCHERY) }));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Swords.Listener"), PP.getSkillLevel(SkillType.SWORDS), PP.getSkillXpLevel(SkillType.SWORDS), PP.getXpToLevel(SkillType.SWORDS) }));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Taming.Listener"), PP.getSkillLevel(SkillType.TAMING), PP.getSkillXpLevel(SkillType.TAMING), PP.getXpToLevel(SkillType.TAMING) }));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Unarmed.Listener"), PP.getSkillLevel(SkillType.UNARMED), PP.getSkillXpLevel(SkillType.UNARMED), PP.getXpToLevel(SkillType.UNARMED) }));

                sender.sendMessage(LocaleLoader.getString("Stats.Header.Misc"));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Acrobatics.Listener"), PP.getSkillLevel(SkillType.ACROBATICS), PP.getSkillXpLevel(SkillType.ACROBATICS), PP.getXpToLevel(SkillType.ACROBATICS) }));
                sender.sendMessage(LocaleLoader.getString("Skills.Stats", new Object[] { LocaleLoader.getString("Repair.Listener"), PP.getSkillLevel(SkillType.REPAIR), PP.getSkillXpLevel(SkillType.REPAIR), PP.getXpToLevel(SkillType.REPAIR) }));

                return true;
            }

        default:
            sender.sendMessage(usage);
            return true;
        }
    }
}
