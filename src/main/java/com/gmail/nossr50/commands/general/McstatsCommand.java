package com.gmail.nossr50.commands.general;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Users;

public class McstatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        Player player = (Player) sender;

        player.sendMessage(LocaleLoader.getString("Stats.Own.Stats"));
        player.sendMessage(LocaleLoader.getString("mcMMO.NoSkillNote"));

        CommandHelper.printGatheringSkills(player);
        CommandHelper.printCombatSkills(player);
        CommandHelper.printMiscSkills(player);

        int powerLevelCap = Config.getInstance().getPowerLevelCap();

        if (powerLevelCap > 0)
            player.sendMessage(LocaleLoader.getString("Commands.PowerLevel.Capped", new Object[] { String.valueOf(Users.getPlayer(player).getPowerLevel()), String.valueOf(powerLevelCap) }));
        else
            player.sendMessage(LocaleLoader.getString("Commands.PowerLevel", new Object[] { String.valueOf(Users.getPlayer(player).getPowerLevel()) }));

        return true;
    }
}
