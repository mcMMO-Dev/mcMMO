package com.gmail.nossr50.commands.mc;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Users;

public class McabilityCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        // DEPRECATED PERMISSION
        boolean oldPermission = !CommandHelper.noCommandPermissions(sender, "mcmmo.commands.ability");

        if (!oldPermission && CommandHelper.noCommandPermissions(sender, "mcmmo.commands.mcability")) {
            return true;
        }

        PlayerProfile profile = null;

        if(args.length > 0 && args[0] != null) {
            if (!oldPermission && CommandHelper.noCommandPermissions(sender, "mcmmo.commands.mcability.others")) {
                return true;
            }

            OfflinePlayer modifiedPlayer = mcMMO.p.getServer().getOfflinePlayer(args[0]);
            profile = Users.getProfile(modifiedPlayer);
        }
	else
            profile = Users.getProfile((Player) sender);

        if (profile == null) {
            sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
            return true;
        }

        if (profile.getAbilityUse()) {
            sender.sendMessage(LocaleLoader.getString("Commands.Ability.Off"));
        }
        else {
            sender.sendMessage(LocaleLoader.getString("Commands.Ability.On"));
        }

        profile.toggleAbilityUse();

        return true;
    }
}
