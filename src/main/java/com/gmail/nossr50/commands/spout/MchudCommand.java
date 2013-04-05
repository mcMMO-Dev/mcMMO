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
        try {
            playerProfile.setHudType(HudType.valueOf(args[0].toUpperCase().trim()));
            spoutHud.initializeXpBar();
            spoutHud.updateXpBar();
            return true;
        }
        catch (IllegalArgumentException ex) {
            sender.sendMessage(LocaleLoader.getString("Commands.mchud.Invalid"));
            return true;
        }
    }
}
