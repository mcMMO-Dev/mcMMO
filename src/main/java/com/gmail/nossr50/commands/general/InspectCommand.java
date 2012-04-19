package com.gmail.nossr50.commands.general;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;

public class InspectCommand implements CommandExecutor {
    private final mcMMO plugin;

    public InspectCommand(mcMMO instance) {
        this.plugin = instance;
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

                if (sender instanceof Player && !sender.isOp() && !m.isNear(((Player) sender).getLocation(), player.getLocation(), 5.0)) {
                    sender.sendMessage(mcLocale.getString("Inspect.TooFar"));
                    return true;
                }

                sender.sendMessage(mcLocale.getString("Inspect.Stats", new Object[] { target.getName() }));
                CommandHelper.printGatheringSkills(player, sender);
                CommandHelper.printCombatSkills(player, sender);
                CommandHelper.printMiscSkills(player, sender);
                sender.sendMessage(mcLocale.getString("mcPlayerListener.PowerLevel", new Object[] { String.valueOf(PP.getPowerLevel()) }));

                return true;
            }
            else {
                if (sender instanceof Player && !sender.isOp()) {
                    sender.sendMessage(mcLocale.getString("Inspect.Offline"));
                    return true;
                }

                if (!PP.isLoaded()) {
                    sender.sendMessage(mcLocale.getString("Commands.DoesNotExist"));
                    return true;
                }

                sender.sendMessage(mcLocale.getString("Inspect.OfflineStats", new Object[] { args[0] }));

                sender.sendMessage(mcLocale.getString("Stats.GatheringHeader"));
                sender.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.ExcavationSkill"), PP.getSkillLevel(SkillType.EXCAVATION), PP.getSkillXpLevel(SkillType.EXCAVATION), PP.getXpToLevel(SkillType.EXCAVATION) }));
                sender.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.FishingSkill"), PP.getSkillLevel(SkillType.FISHING), PP.getSkillXpLevel(SkillType.FISHING), PP.getXpToLevel(SkillType.FISHING) }));
                sender.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.HerbalismSkill"), PP.getSkillLevel(SkillType.HERBALISM), PP.getSkillXpLevel(SkillType.HERBALISM), PP.getXpToLevel(SkillType.HERBALISM) }));
                sender.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.MiningSkill"), PP.getSkillLevel(SkillType.MINING), PP.getSkillXpLevel(SkillType.MINING), PP.getXpToLevel(SkillType.MINING) }));
                sender.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.WoodcuttingSkill"), PP.getSkillLevel(SkillType.WOODCUTTING), PP.getSkillXpLevel(SkillType.WOODCUTTING), PP.getXpToLevel(SkillType.WOODCUTTING) }));

                sender.sendMessage(mcLocale.getString("Stats.CombatHeader"));
                sender.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.AxesSkill"), PP.getSkillLevel(SkillType.AXES), PP.getSkillXpLevel(SkillType.AXES), PP.getXpToLevel(SkillType.AXES) }));
                sender.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.ArcherySkill"), PP.getSkillLevel(SkillType.ARCHERY), PP.getSkillXpLevel(SkillType.ARCHERY), PP.getXpToLevel(SkillType.ARCHERY) }));
                sender.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.SwordsSkill"), PP.getSkillLevel(SkillType.SWORDS), PP.getSkillXpLevel(SkillType.SWORDS), PP.getXpToLevel(SkillType.SWORDS) }));
                sender.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.TamingSkill"), PP.getSkillLevel(SkillType.TAMING), PP.getSkillXpLevel(SkillType.TAMING), PP.getXpToLevel(SkillType.TAMING) }));
                sender.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.UnarmedSkill"), PP.getSkillLevel(SkillType.UNARMED), PP.getSkillXpLevel(SkillType.UNARMED), PP.getXpToLevel(SkillType.UNARMED) }));

                sender.sendMessage(mcLocale.getString("Stats.MiscHeader"));
                sender.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.AcrobaticsSkill"), PP.getSkillLevel(SkillType.ACROBATICS), PP.getSkillXpLevel(SkillType.ACROBATICS), PP.getXpToLevel(SkillType.ACROBATICS) }));
                sender.sendMessage(mcLocale.getString("m.SkillStats", new Object[] { mcLocale.getString("mcPlayerListener.RepairSkill"), PP.getSkillLevel(SkillType.REPAIR), PP.getSkillXpLevel(SkillType.REPAIR), PP.getXpToLevel(SkillType.REPAIR) }));

                return true;
            }

        default:
            sender.sendMessage(usage);
            return true;
        }
    }
}
