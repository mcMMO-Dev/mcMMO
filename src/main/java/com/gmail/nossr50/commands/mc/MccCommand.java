package com.gmail.nossr50.commands.mc;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;

//TODO: Rework this whole thing. It's ugly. Also is missing all the admin & spout commands.
public class MccCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        Player player = (Player) sender;

        player.sendMessage(ChatColor.RED + "---[]" + ChatColor.YELLOW + "mcMMO Commands" + ChatColor.RED + "[]---"); //TODO: Needs more locale.

        if (Permissions.getInstance().party(player)) {
            player.sendMessage(LocaleLoader.getString("Commands.Party.Commands"));
            player.sendMessage("/party " + LocaleLoader.getString("Commands.Party"));
            player.sendMessage("/party q " + LocaleLoader.getString("Commands.Party.Quit"));

            if (Permissions.getInstance().partyChat(player)) {
                player.sendMessage("/p " + LocaleLoader.getString("Commands.Party.Toggle"));
            }

            player.sendMessage("/invite " + LocaleLoader.getString("Commands.Party.Invite"));
            player.sendMessage("/accept " + LocaleLoader.getString("Commands.Party.Accept"));

            if (Permissions.getInstance().partyTeleport(player)) {
                player.sendMessage("/ptp " + LocaleLoader.getString("Commands.Party.Teleport"));
            }
        }

        player.sendMessage(LocaleLoader.getString("Commands.Other"));
        player.sendMessage("/mcstats " + LocaleLoader.getString("Commands.Stats"));
        player.sendMessage("/mctop " + LocaleLoader.getString("Commands.Leaderboards"));

        if (Permissions.getInstance().mcAbility(player)) {
            player.sendMessage("/mcability " + LocaleLoader.getString("Commands.ToggleAbility"));
        }

        if (Permissions.getInstance().adminChat(player)) {
            player.sendMessage("/a " + LocaleLoader.getString("Commands.AdminToggle"));
        }

        if (Permissions.getInstance().inspect(player)) {
            player.sendMessage("/inspect " + LocaleLoader.getString("Commands.Inspect"));
        }

        if (Permissions.getInstance().mmoedit(player)) {
            player.sendMessage("/mmoedit " + LocaleLoader.getString("Commands.mmoedit"));
        }

        if (Permissions.getInstance().mcgod(player)) {
            player.sendMessage("/mcgod " + LocaleLoader.getString("Commands.mcgod"));
        }

        player.sendMessage(LocaleLoader.getString("Commands.SkillInfo"));
        player.sendMessage("/mcmmo " + LocaleLoader.getString("Commands.ModDescription"));

        return true;
    }
}
