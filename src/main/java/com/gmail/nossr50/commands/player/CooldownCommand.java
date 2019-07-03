package com.gmail.nossr50.commands.player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class CooldownCommand implements TabExecutor {

    private mcMMO pluginRef;

    public CooldownCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (pluginRef.getCommandTools().noConsoleUsage(sender)) {
            return true;
        }

        if (!pluginRef.getCommandTools().hasPlayerDataKey(sender)) {
            return true;
        }

        switch (args.length) {
            case 0:
                Player player = (Player) sender;

                if (pluginRef.getScoreboardSettings().getScoreboardsEnabled() && pluginRef.getScoreboardSettings().isScoreboardEnabled(pluginRef.getScoreboardManager().SidebarType.COOLDOWNS_BOARD)) {
                    pluginRef.getScoreboardManager().enablePlayerCooldownScoreboard(player);

                    if (!pluginRef.getScoreboardSettings().isScoreboardPrinting(pluginRef.getScoreboardManager().SidebarType.COOLDOWNS_BOARD)) {
                        return true;
                    }
                }

                if (pluginRef.getUserManager().getPlayer(player) == null) {
                    player.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
                    return true;
                }

                McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);

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
