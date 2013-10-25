package com.gmail.nossr50.commands.player;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.google.common.collect.ImmutableList;

public class MccooldownCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        switch (args.length) {
            case 0:
                Player player = (Player) sender;

                if (Config.getInstance().getCooldownUseBoard()) {
                    ScoreboardManager.enablePlayerCooldownScoreboard(player);
                    if (!Config.getInstance().getCooldownUseChat()) return true;
                }

                PlayerProfile profile = UserManager.getPlayer(player).getProfile();

                player.sendMessage(LocaleLoader.getString("Commands.Cooldowns.Header"));
                player.sendMessage(LocaleLoader.getString("mcMMO.NoSkillNote"));

                for (AbilityType ability : AbilityType.NORMAL_ABILITIES) {
                    if (!hasPermission(player, ability)) {
                        continue;
                    }

                    int seconds = SkillUtils.calculateTimeLeft(ability, profile, player);

                    if (seconds <= 0) {
                        player.sendMessage(LocaleLoader.getString("Commands.Cooldowns.Row.Y", ability.getAbilityName()));
                    }
                    else {
                        player.sendMessage(LocaleLoader.getString("Commands.Cooldowns.Row.N", ability.getAbilityName(), Integer.toString(seconds)));
                    }
                }

                return true;

            default:
                return false;
        }
    }

    private boolean hasPermission(Permissible permissible, AbilityType ability) {
        switch (ability) {
        case BERSERK:
            return Permissions.berserk(permissible);
        case BLAST_MINING:
            return Permissions.remoteDetonation(permissible);
        case BLOCK_CRACKER:
            return Permissions.blockCracker(permissible);
        case GIGA_DRILL_BREAKER:
            return Permissions.gigaDrillBreaker(permissible);
        case GREEN_TERRA:
            return Permissions.greenTerra(permissible);
        case LEAF_BLOWER:
            return Permissions.leafBlower(permissible);
        case SERRATED_STRIKES:
            return Permissions.serratedStrikes(permissible);
        case SKULL_SPLITTER:
            return Permissions.skullSplitter(permissible);
        case SUPER_BREAKER:
            return Permissions.superBreaker(permissible);
        case TREE_FELLER:
            return Permissions.treeFeller(permissible);
        default:
            mcMMO.p.getLogger().warning("MccooldownCommand - couldn't check permission for AbilityType." + ability.name());
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return ImmutableList.of();
    }
}
