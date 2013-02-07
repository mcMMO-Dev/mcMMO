package com.gmail.nossr50.spout.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.spout.SpoutConfig;
import com.gmail.nossr50.spout.huds.SpoutHud;
import com.gmail.nossr50.util.Users;

public abstract class SpoutCommand implements CommandExecutor {
    protected PlayerProfile playerProfile;
    protected SpoutHud spoutHud;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (!mcMMO.spoutEnabled || !SpoutConfig.getInstance().getXPBarEnabled()) {
            sender.sendMessage(LocaleLoader.getString("Commands.Disabled"));
            return true;
        }

        playerProfile = Users.getPlayer((Player) sender).getProfile();
        spoutHud = playerProfile.getSpoutHud();

        if (spoutHud == null) {
            sender.sendMessage(LocaleLoader.getString("Commands.Disabled"));
            return true;
        }

        switch (args.length) {
        case 0:
            return noArguments(command, sender, args);

        case 1:
            return oneArgument(command, sender, args);

        default:
            return false;
        }
    }

    protected abstract boolean noArguments(Command command, CommandSender sender, String[] args);
    protected abstract boolean oneArgument(Command command, CommandSender sender, String[] args);
}
