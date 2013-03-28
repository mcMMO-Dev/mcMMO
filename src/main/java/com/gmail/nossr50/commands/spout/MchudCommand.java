package com.gmail.nossr50.commands.spout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import com.gmail.nossr50.datatypes.spout.huds.HudType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.google.common.collect.ImmutableList;

public class MchudCommand extends SpoutCommand {
    private static final List<String> HUD_TYPES;

    static {
        ArrayList<String> types = new ArrayList<String>();

        for (HudType type : HudType.values()) {
            types.add(type.toString());
        }

        Collections.sort(types);
        HUD_TYPES = ImmutableList.copyOf(types);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return StringUtil.copyPartialMatches(args[0], HUD_TYPES, new ArrayList<String>(HUD_TYPES.size()));
            default:
                return ImmutableList.of();
        }
    }

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
