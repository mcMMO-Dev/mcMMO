package com.gmail.nossr50.commands.player;

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
        PlayerProfile profile;
        String usage = LocaleLoader.getString("Commands.Usage.1", "mcability", "<" + LocaleLoader.getString("Commands.Usage.Player") + ">");

        switch (args.length) {
        case 0:
            if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.mcability")) {
                return true;
            }

            profile = Users.getPlayer((Player) sender).getProfile();

            if (profile.getAbilityUse()) {
                sender.sendMessage(LocaleLoader.getString("Commands.Ability.Off"));
            }
            else {
                sender.sendMessage(LocaleLoader.getString("Commands.Ability.On"));
            }

            profile.toggleAbilityUse();
            return true;

        case 1:
            if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.mcability.others")) {
                return true;
            }

            OfflinePlayer modifiedPlayer = mcMMO.p.getServer().getOfflinePlayer(args[0]);
            profile = Users.getPlayer(args[0]).getProfile();

            // TODO:Not sure if we actually need a null check here
            if (profile == null || !profile.isLoaded()) {
                sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                return true;
            }

            if (profile.getAbilityUse()) {
                ((Player) modifiedPlayer).sendMessage(LocaleLoader.getString("Commands.Ability.Off"));
            }
            else {
                ((Player) modifiedPlayer).sendMessage(LocaleLoader.getString("Commands.Ability.On"));
            }

            profile.toggleAbilityUse();
            return true;

        default:
            sender.sendMessage(usage);
            return true;
        }
    }
}
