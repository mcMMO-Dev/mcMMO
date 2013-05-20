package com.gmail.nossr50.commands.database;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.database.FormulaConversionTask;
import com.gmail.nossr50.util.player.UserManager;
import com.google.common.collect.ImmutableList;

public class McconvertCommand implements TabExecutor {

    /*
    * Do this later; Use mcconvert instead of mmoupdate:
    * OLD :
    * /mmoupdate flatfile / mysql
    *
    * NEW :
    * /mcconvert <database> <flatfile / sql>
    * /mcconvert <experience> <linear / exponential>
    * */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
                sender.sendMessage("Usage is /mcconvert <linear | exponential>");
                return true;

            case 1:
                FormulaType previousType = mcMMO.getFormulaManager().getPreviousFormulaType();
                FormulaType newType = FormulaType.getFormulaType(args[0].toUpperCase());

                if (newType == FormulaType.UNKNOWN) {
                    sender.sendMessage("Unknown formula type! Valid types are: LINEAR and EXPONENTIAL");
                    return true;
                }

                if (previousType == newType) {
                    sender.sendMessage(LocaleLoader.getString("Commands.mcconvert.Same", newType));
                    return true;
                }

                sender.sendMessage(LocaleLoader.getString("Commands.mcconvert.Start", previousType.toString(), newType.toString()));
                UserManager.saveAll();
                UserManager.clearAll();
                new FormulaConversionTask(sender, newType).runTaskLater(mcMMO.p, 1);

                for (Player player : mcMMO.p.getServer().getOnlinePlayers()) {
                    UserManager.addUser(player);
                }
                return true;

            default:
                break;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return ImmutableList.of();
    }
}
