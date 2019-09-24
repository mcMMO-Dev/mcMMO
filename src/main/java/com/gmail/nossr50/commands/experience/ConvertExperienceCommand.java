package com.gmail.nossr50.commands.experience;

import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.database.FormulaConversionTask;
import com.gmail.nossr50.runnables.player.PlayerProfileLoadingTask;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConvertExperienceCommand implements CommandExecutor {

    private final mcMMO pluginRef;

    public ConvertExperienceCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2:

                for(FormulaType formulaType : FormulaType.values()) {
                    if(formulaType.toString().equalsIgnoreCase(args[1])) {
                        FormulaType previousType = formulaType;

                        sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.mcconvert.Experience.Start", previousType.toString(), pluginRef.getConfigManager().getConfigLeveling().getFormulaType().toString()));

                        pluginRef.getUserManager().saveAll();
                        pluginRef.getUserManager().clearAll();

                        new FormulaConversionTask(pluginRef, sender, previousType).runTaskLater(pluginRef, 1);

                        for (Player player : pluginRef.getServer().getOnlinePlayers()) {
                            new PlayerProfileLoadingTask(pluginRef, player).runTaskLaterAsynchronously(pluginRef, 1); // 1 Tick delay to ensure the player is marked as online before we begin loading
                        }

                        return true;
                    }
                }

                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.mcconvert.Experience.Invalid"));
                return true;

            default:
                return false;
        }
    }
}
