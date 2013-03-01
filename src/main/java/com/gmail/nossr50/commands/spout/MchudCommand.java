package com.gmail.nossr50.commands.spout;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.datatypes.spout.huds.HudType;
import com.gmail.nossr50.locale.LocaleLoader;

public class MchudCommand extends SpoutCommand {
    @Override
    protected boolean noArguments(Command command, CommandSender sender, String[] args) {
        return false;
    }

    @Override
    protected boolean oneArgument(Command command, CommandSender sender, String[] args) {
        for (HudType hudType : HudType.values()) {
            if (hudType.toString().equalsIgnoreCase(args[0])) {
                playerProfile.setHudType(hudType);
                spoutHud.initializeXpBar();
                spoutHud.updateXpBar();
                return true;
            }
        }

        sender.sendMessage(LocaleLoader.getString("Commands.mchud.Invalid"));
        return true;
    }
}
