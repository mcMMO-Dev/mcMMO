package com.gmail.nossr50.commands.player;

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

        player.sendMessage(LocaleLoader.getString("Commands.mcc.Header"));

        if (Permissions.party(player)) {
            player.sendMessage(LocaleLoader.getString("Commands.Party.Commands"));
            player.sendMessage("/party create <" + LocaleLoader.getString("Commands.Usage.PartyName") + "> " + LocaleLoader.getString("Commands.Party1"));
            player.sendMessage("/party join <" + LocaleLoader.getString("Commands.Usage.Player") + "> " + LocaleLoader.getString("Commands.Party2"));
            player.sendMessage("/party quit " + LocaleLoader.getString("Commands.Party.Quit"));

            if (Permissions.partyChat(player)) {
                player.sendMessage("/p " + LocaleLoader.getString("Commands.Party.Toggle"));
            }

            player.sendMessage("/party invite <" + LocaleLoader.getString("Commands.Usage.Player") + "> " + LocaleLoader.getString("Commands.Party.Invite"));
            player.sendMessage("/party accept " + LocaleLoader.getString("Commands.Party.Accept"));

            if (Permissions.partyTeleport(player)) {
                player.sendMessage("/ptp " + LocaleLoader.getString("Commands.Party.Teleport"));
            }
        }

        player.sendMessage(LocaleLoader.getString("Commands.Other"));
        player.sendMessage("/mcstats " + LocaleLoader.getString("Commands.Stats"));
        player.sendMessage("/mctop " + LocaleLoader.getString("Commands.Leaderboards"));

        if (Permissions.skillReset(player)) {
            player.sendMessage("/skillreset <skill|all> " + LocaleLoader.getString("Commands.Reset"));
        }

        if (Permissions.mcAbility(player)) {
            player.sendMessage("/mcability " + LocaleLoader.getString("Commands.ToggleAbility"));
        }

        if (Permissions.adminChat(player)) {
            player.sendMessage("/a " + LocaleLoader.getString("Commands.AdminToggle"));
        }

        if (Permissions.inspect(player)) {
            player.sendMessage("/inspect " + LocaleLoader.getString("Commands.Inspect"));
        }

        if (Permissions.mmoedit(player)) {
            player.sendMessage("/mmoedit " + LocaleLoader.getString("Commands.mmoedit"));
        }

        if (Permissions.mcgod(player)) {
            player.sendMessage("/mcgod " + LocaleLoader.getString("Commands.mcgod"));
        }

        player.sendMessage(LocaleLoader.getString("Commands.SkillInfo"));
        player.sendMessage("/mcmmo " + LocaleLoader.getString("Commands.ModDescription"));

        return true;
    }
}
