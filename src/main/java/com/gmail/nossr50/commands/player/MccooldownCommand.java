package com.gmail.nossr50.commands.player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
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

    private mcMMO pluginRef;

    public MccooldownCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

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

                if (pluginRef.getScoreboardSettings().getScoreboardsEnabled() && pluginRef.getScoreboardSettings().isScoreboardEnabled(ScoreboardManager.SidebarType.COOLDOWNS_BOARD)) {
                    ScoreboardManager.enablePlayerCooldownScoreboard(player);

                    if (!pluginRef.getScoreboardSettings().isScoreboardPrinting(ScoreboardManager.SidebarType.COOLDOWNS_BOARD)) {
                        return true;
                    }
                }

                if (UserManager.getPlayer(player) == null) {
                    player.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
                    return true;
                }

                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

                player.sendMessage(pluginRef.getLocaleManager().getString("Commands.Cooldowns.Header"));
                player.sendMessage(pluginRef.getLocaleManager().getString("mcMMO.NoSkillNote"));

                for (SuperAbilityType ability : SuperAbilityType.values()) {
                    if (!ability.getPermissions(player)) {
                        continue;
                    }

                    int seconds = mcMMOPlayer.calculateTimeRemaining(ability);

                    if (seconds <= 0) {
                        player.sendMessage(pluginRef.getLocaleManager().getString("Commands.Cooldowns.Row.Y", ability.getName()));
                    } else {
                        player.sendMessage(pluginRef.getLocaleManager().getString("Commands.Cooldowns.Row.N", ability.getName(), seconds));
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
