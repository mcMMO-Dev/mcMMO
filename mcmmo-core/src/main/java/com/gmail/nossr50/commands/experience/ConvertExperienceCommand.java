package com.gmail.nossr50.commands.experience;

import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.database.FormulaConversionTask;
import com.gmail.nossr50.runnables.player.PlayerProfileLoadingTask;

import org.bukkit.Bukkit;
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

                        pluginRef.getPlatformProvider().getScheduler().getTaskBuilder()
                                .setDelay(1L)
                                .setTask(new FormulaConversionTask(pluginRef, sender, previousType))
                                .schedule();

                        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                            pluginRef.getPlatformProvider().getScheduler().getTaskBuilder()
                                    .setAsync(true)
                                    .setDelay(1L) // 1 Tick delay to ensure the player is marked as online before we begin loading
                                    .setTask(new PlayerProfileLoadingTask(pluginRef, player))
                                    .schedule();
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
