package com.gmail.nossr50.commands.mc;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.locale.mcLocale;

//TODO: Rework this whole thing. It's ugly. Also is missing all the admin & spout commands.
public class MccCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        Player player = (Player) sender;

        player.sendMessage(ChatColor.RED + "---[]" + ChatColor.YELLOW + "mcMMO Commands" + ChatColor.RED + "[]---"); //TODO: Needs more locale.

        if (mcPermissions.getInstance().party(player)) {
            player.sendMessage(mcLocale.getString("Commands.Party.Commands"));
            player.sendMessage("/party " + mcLocale.getString("Commands.Party"));
            player.sendMessage("/party q " + mcLocale.getString("Commands.Party.Quit"));

            if (mcPermissions.getInstance().partyChat(player)) {
                player.sendMessage("/p " + mcLocale.getString("Commands.Party.Toggle"));
            }

            player.sendMessage("/invite " + mcLocale.getString("Commands.Party.Invite"));
            player.sendMessage("/accept " + mcLocale.getString("Commands.Party.Accept"));

            if (mcPermissions.getInstance().partyTeleport(player)) {
                player.sendMessage("/ptp " + mcLocale.getString("Commands.Party.Teleport"));
            }
        }

        player.sendMessage(mcLocale.getString("Commands.Other"));
        player.sendMessage("/mcstats " + mcLocale.getString("Commands.Stats"));
        player.sendMessage("/mctop " + mcLocale.getString("m.mccLeaderboards"));

        if (mcPermissions.getInstance().mcAbility(player)) {
            player.sendMessage("/mcability " + mcLocale.getString("Commands.ToggleAbility"));
        }

        if (mcPermissions.getInstance().adminChat(player)) {
            player.sendMessage("/a " + mcLocale.getString("Commands.AdminToggle"));
        }

        if (mcPermissions.getInstance().inspect(player)) {
            player.sendMessage("/inspect " + mcLocale.getString("Commands.Inspect"));
        }

        if (mcPermissions.getInstance().mmoedit(player)) {
            player.sendMessage("/mmoedit " + mcLocale.getString("Commands.mmoedit"));
        }

        if (mcPermissions.getInstance().mcgod(player)) {
            player.sendMessage("/mcgod " + mcLocale.getString("Commands.mcgod"));
        }

        player.sendMessage(mcLocale.getString("Commands.SkillInfo"));
        player.sendMessage("/mcmmo " + mcLocale.getString("Commands.ModDescription"));

        return true;
    }
}
