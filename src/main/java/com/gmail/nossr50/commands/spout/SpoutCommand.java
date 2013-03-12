package com.gmail.nossr50.commands.spout;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.spout.SpoutConfig;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.spout.huds.McMMOHud;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;

public abstract class SpoutCommand implements CommandExecutor {
    protected PlayerProfile playerProfile;
    protected McMMOHud spoutHud;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        if (!mcMMO.spoutEnabled || !SpoutConfig.getInstance().getXPBarEnabled()) {
            sender.sendMessage(LocaleLoader.getString("Commands.Disabled"));
            return true;
        }

        playerProfile = UserManager.getPlayer(sender.getName()).getProfile();
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
