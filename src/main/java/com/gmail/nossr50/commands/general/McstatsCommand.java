package com.gmail.nossr50.commands.general;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.mcLocale;

public class McstatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        Player player = (Player) sender;
        PlayerProfile PP = Users.getProfile(player);

        player.sendMessage(mcLocale.getString("Stats.Own.Stats"));
        player.sendMessage(mcLocale.getString("mcMMO.NoSkillNote"));

        CommandHelper.printGatheringSkills(player);
        CommandHelper.printCombatSkills(player);
        CommandHelper.printMiscSkills(player);

        player.sendMessage(mcLocale.getString("Commands.PowerLevel", new Object[] { String.valueOf(PP.getPowerLevel()) }));

        return true;
    }
}
