package com.gmail.nossr50.commands.player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class MccooldownCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        if (!CommandUtils.hasPlayerDataKey(sender)) {
            return true;
        }

        switch (args.length) {
            case 0:
                Player player = (Player) sender;

                if (Config.getInstance().getScoreboardsEnabled() && Config.getInstance().getCooldownUseBoard()) {
                    ScoreboardManager.enablePlayerCooldownScoreboard(player);

                    if (!Config.getInstance().getCooldownUseChat()) {
                        return true;
                    }
                }

                if(UserManager.getPlayer(player) == null)
                {
                    player.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));
                    return true;
                }

                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

                player.sendMessage(LocaleLoader.getString("Commands.Cooldowns.Header"));
                player.sendMessage(LocaleLoader.getString("mcMMO.NoSkillNote"));

                for (SuperAbilityType ability : SuperAbilityType.values()) {
                    if (!ability.getPermissions(player)) {
                        continue;
                    }

                    int seconds = mcMMOPlayer.calculateTimeRemaining(ability);

                    if (seconds <= 0) {
                        player.sendMessage(LocaleLoader.getString("Commands.Cooldowns.Row.Y", ability.getName()));
                    }
                    else {
                        player.sendMessage(LocaleLoader.getString("Commands.Cooldowns.Row.N", ability.getName(), seconds));
                    }
                }

                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return ImmutableList.of();
    }
}
