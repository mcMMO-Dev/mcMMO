package com.gmail.nossr50.commands.experience;

import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.database.FormulaConversionTask;
import com.gmail.nossr50.runnables.player.PlayerProfileLoadingTask;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class ConvertExperienceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 2) {
            FormulaType previousType = mcMMO.getFormulaManager().getPreviousFormulaType();
            FormulaType newType = FormulaType.getFormulaType(args[1].toUpperCase(Locale.ENGLISH));

            if (newType == FormulaType.UNKNOWN) {
                sender.sendMessage(LocaleLoader.getString("Commands.mcconvert.Experience.Invalid"));
                return true;
            }

            if (previousType == newType) {
                sender.sendMessage(LocaleLoader.getString("Commands.mcconvert.Experience.Same", newType.toString()));
                return true;
            }

            sender.sendMessage(LocaleLoader.getString("Commands.mcconvert.Experience.Start", previousType.toString(), newType.toString()));

            UserManager.saveAll();
            UserManager.clearAll();

            new FormulaConversionTask(sender, newType).runTaskLater(mcMMO.p, 1);

            for (Player player : mcMMO.p.getServer().getOnlinePlayers()) {
                new PlayerProfileLoadingTask(player).runTaskLaterAsynchronously(mcMMO.p, 1); // 1 Tick delay to ensure the player is marked as online before we begin loading
            }

            return true;
        }
        return false;
    }
}
