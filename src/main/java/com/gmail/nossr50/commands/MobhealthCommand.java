package com.gmail.nossr50.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;

import com.google.common.collect.ImmutableList;

public class MobhealthCommand implements TabExecutor {
    private static final List<String> MOB_HEALTHBAR_TYPES;

    static {
        ArrayList<String> types = new ArrayList<String>();

        for (MobHealthbarType type : MobHealthbarType.values()) {
            types.add(type.toString());
        }

        Collections.sort(types);
        MOB_HEALTHBAR_TYPES = ImmutableList.copyOf(types);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        switch (args.length) {
            case 1:
                PlayerProfile playerProfile = UserManager.getPlayer(sender.getName()).getProfile();

                try {
                    MobHealthbarType type = MobHealthbarType.valueOf(args[0].toUpperCase().trim());
                    playerProfile.setMobHealthbarType(type);
                    sender.sendMessage("Display type changed to: " + type); //TODO: Localize
                    return true;
                }
                catch (IllegalArgumentException ex) {
                    sender.sendMessage("Invalid type!"); //TODO: Localize
                    return true;
                }

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return StringUtil.copyPartialMatches(args[0], MOB_HEALTHBAR_TYPES, new ArrayList<String>(MOB_HEALTHBAR_TYPES.size()));
            default:
                return ImmutableList.of();
        }
    }
}
