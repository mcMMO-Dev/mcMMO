package com.gmail.nossr50.commands.general;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.skills.Skills;

public class InspectCommand implements CommandExecutor {
    private final mcMMO plugin;

    public InspectCommand(mcMMO instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (sender instanceof Player  && !mcPermissions.getInstance().inspect(player)) {
            sender.sendMessage(ChatColor.YELLOW + "[mcMMO] " + ChatColor.DARK_RED + mcLocale.getString("mcPlayerListener.NoPermission"));
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Proper usage is /inspect <playername>"); //TODO: Needs more locale.
            return true;
        }
        
        // if split[1] is an online player
        if (plugin.getServer().getPlayer(args[0]) != null) 
        {
            Player target = plugin.getServer().getPlayer(args[0]);
            PlayerProfile PPt = Users.getProfile(target);
            
            //If they are not an Op they have to be close
            if(sender instanceof Player && !player.isOp() && !m.isNear(player.getLocation(), target.getLocation(), 5.0))
            {
                sender.sendMessage("You are too far away to inspect that player!"); //TODO: Needs more locale.
            }
            
            sender.sendMessage(ChatColor.GREEN + "mcMMO Stats for " + ChatColor.YELLOW + target.getName()); //TODO: Needs more locale.

            sender.sendMessage(ChatColor.GOLD + "-=GATHERING SKILLS=-"); //TODO: Needs more locale.
            if (mcPermissions.getInstance().excavation(target))
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.ExcavationSkill"), PPt.getSkillLevel(SkillType.EXCAVATION), PPt.getSkillXpLevel(SkillType.EXCAVATION), PPt.getXpToLevel(SkillType.EXCAVATION)));
            if (mcPermissions.getInstance().fishing(target))
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.FishingSkill"), PPt.getSkillLevel(SkillType.FISHING), PPt.getSkillXpLevel(SkillType.FISHING), PPt.getXpToLevel(SkillType.FISHING)));
            if (mcPermissions.getInstance().herbalism(target))
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.HerbalismSkill"), PPt.getSkillLevel(SkillType.HERBALISM), PPt.getSkillXpLevel(SkillType.HERBALISM), PPt.getXpToLevel(SkillType.HERBALISM)));
            if (mcPermissions.getInstance().mining(target))
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.MiningSkill"), PPt.getSkillLevel(SkillType.MINING), PPt.getSkillXpLevel(SkillType.MINING), PPt.getXpToLevel(SkillType.MINING)));
            if (mcPermissions.getInstance().woodcutting(target))
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.WoodcuttingSkill"), PPt.getSkillLevel(SkillType.WOODCUTTING), PPt.getSkillXpLevel(SkillType.WOODCUTTING), PPt.getXpToLevel(SkillType.WOODCUTTING)));

            sender.sendMessage(ChatColor.GOLD + "-=COMBAT SKILLS=-"); //TODO: Needs more locale.
            if (mcPermissions.getInstance().axes(target))
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.AxesSkill"), PPt.getSkillLevel(SkillType.AXES), PPt.getSkillXpLevel(SkillType.AXES), PPt.getXpToLevel(SkillType.AXES)));
            if (mcPermissions.getInstance().archery(target))
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.ArcherySkill"), PPt.getSkillLevel(SkillType.ARCHERY), PPt.getSkillXpLevel(SkillType.ARCHERY), PPt.getXpToLevel(SkillType.ARCHERY)));
            if (mcPermissions.getInstance().swords(target))
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.SwordsSkill"), PPt.getSkillLevel(SkillType.SWORDS), PPt.getSkillXpLevel(SkillType.SWORDS), PPt.getXpToLevel(SkillType.SWORDS)));
            if (mcPermissions.getInstance().taming(target))
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.TamingSkill"), PPt.getSkillLevel(SkillType.TAMING), PPt.getSkillXpLevel(SkillType.TAMING), PPt.getXpToLevel(SkillType.TAMING)));
            if (mcPermissions.getInstance().unarmed(target))
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.UnarmedSkill"), PPt.getSkillLevel(SkillType.UNARMED), PPt.getSkillXpLevel(SkillType.UNARMED), PPt.getXpToLevel(SkillType.UNARMED)));

            sender.sendMessage(ChatColor.GOLD + "-=MISC SKILLS=-"); //TODO: Needs more locale.
            if (mcPermissions.getInstance().acrobatics(target))
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.AcrobaticsSkill"), PPt.getSkillLevel(SkillType.ACROBATICS), PPt.getSkillXpLevel(SkillType.ACROBATICS), PPt.getXpToLevel(SkillType.ACROBATICS)));
            if (mcPermissions.getInstance().repair(target))
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.RepairSkill"), PPt.getSkillLevel(SkillType.REPAIR), PPt.getSkillXpLevel(SkillType.REPAIR), PPt.getXpToLevel(SkillType.REPAIR)));

            sender.sendMessage(mcLocale.getString("mcPlayerListener.PowerLevel") + ChatColor.GREEN + (PPt.getPowerLevel()));
        } else {
            if(sender instanceof Player && !player.isOp())
            {
                sender.sendMessage("That player is offline, inspecting offline players is limited to Ops!"); //TODO: Needs more locale.
                return true;
            }
            
            PlayerProfile PPt = Users.getOfflineProfile(args[0]);
            
            if(!PPt.isLoaded())
            {
                sender.sendMessage("Player does not exist in the database!"); //TODO: Needs more locale.
                return true;
            }
            
            sender.sendMessage(ChatColor.GREEN + "mcMMO Stats for Offline Player " + ChatColor.YELLOW + args[0]); //TODO: Needs more locale.

            sender.sendMessage(ChatColor.GOLD + "-=GATHERING SKILLS=-"); //TODO: Needs more locale.
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.ExcavationSkill"), PPt.getSkillLevel(SkillType.EXCAVATION), PPt.getSkillXpLevel(SkillType.EXCAVATION), PPt.getXpToLevel(SkillType.EXCAVATION)));
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.FishingSkill"), PPt.getSkillLevel(SkillType.FISHING), PPt.getSkillXpLevel(SkillType.FISHING), PPt.getXpToLevel(SkillType.FISHING)));
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.HerbalismSkill"), PPt.getSkillLevel(SkillType.HERBALISM), PPt.getSkillXpLevel(SkillType.HERBALISM), PPt.getXpToLevel(SkillType.HERBALISM)));
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.MiningSkill"), PPt.getSkillLevel(SkillType.MINING), PPt.getSkillXpLevel(SkillType.MINING), PPt.getXpToLevel(SkillType.MINING)));
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.WoodcuttingSkill"), PPt.getSkillLevel(SkillType.WOODCUTTING), PPt.getSkillXpLevel(SkillType.WOODCUTTING), PPt.getXpToLevel(SkillType.WOODCUTTING)));

            sender.sendMessage(ChatColor.GOLD + "-=COMBAT SKILLS=-"); //TODO: Needs more locale.
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.AxesSkill"), PPt.getSkillLevel(SkillType.AXES), PPt.getSkillXpLevel(SkillType.AXES), PPt.getXpToLevel(SkillType.AXES)));
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.ArcherySkill"), PPt.getSkillLevel(SkillType.ARCHERY), PPt.getSkillXpLevel(SkillType.ARCHERY), PPt.getXpToLevel(SkillType.ARCHERY)));
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.SwordsSkill"), PPt.getSkillLevel(SkillType.SWORDS), PPt.getSkillXpLevel(SkillType.SWORDS), PPt.getXpToLevel(SkillType.SWORDS)));
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.TamingSkill"), PPt.getSkillLevel(SkillType.TAMING), PPt.getSkillXpLevel(SkillType.TAMING), PPt.getXpToLevel(SkillType.TAMING)));
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.UnarmedSkill"), PPt.getSkillLevel(SkillType.UNARMED), PPt.getSkillXpLevel(SkillType.UNARMED), PPt.getXpToLevel(SkillType.UNARMED)));

            sender.sendMessage(ChatColor.GOLD + "-=MISC SKILLS=-"); //TODO: Needs more locale.
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.AcrobaticsSkill"), PPt.getSkillLevel(SkillType.ACROBATICS), PPt.getSkillXpLevel(SkillType.ACROBATICS), PPt.getXpToLevel(SkillType.ACROBATICS)));
                sender.sendMessage(Skills.getSkillStats(mcLocale.getString("mcPlayerListener.RepairSkill"), PPt.getSkillLevel(SkillType.REPAIR), PPt.getSkillXpLevel(SkillType.REPAIR), PPt.getXpToLevel(SkillType.REPAIR)));
        }

        return true;
    }
}
