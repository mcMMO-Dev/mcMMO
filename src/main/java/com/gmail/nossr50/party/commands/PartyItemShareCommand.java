package com.gmail.nossr50.party.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.party.ShareHandler;
import com.gmail.nossr50.party.ShareHandler.ShareMode;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Users;

public class PartyItemShareCommand implements CommandExecutor {
    private Party playerParty;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Config.getInstance().getItemShareEnabled()) {
            sender.sendMessage(LocaleLoader.getString("Party.ItemShare.Disabled"));
            return true;
        }

        if (!sender.hasPermission("mcmmo.commands.party.itemshare")) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        switch (args.length) {
        case 2:
            playerParty = Users.getPlayer((Player) sender).getParty();

            if (args[1].equalsIgnoreCase("none") || args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("false")) {
                handleChangingShareMode(ShareMode.NONE);
            }
//            else if (args[1].equalsIgnoreCase("equal") || args[1].equalsIgnoreCase("even")) {
//                handleChangingShareMode(ShareMode.EQUAL);
//            }
            else if (args[1].equalsIgnoreCase("random")) {
                handleChangingShareMode(ShareMode.RANDOM);
            }
            else {
//                sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "itemshare", "<NONE | EQUAL | RANDOM>"));
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "itemshare", "<NONE | RANDOM>"));
            }

            return true;

        default:
//          sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "itemshare", "<NONE | EQUAL | RANDOM>"));
          sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "itemshare", "<NONE | RANDOM>"));
            return true;
        }
    }

    private void handleChangingShareMode(ShareHandler.ShareMode mode) {
        playerParty.setItemShareMode(mode);

        for (Player member : playerParty.getOnlineMembers()) {
            member.sendMessage(LocaleLoader.getString("Commands.Party.SetSharing", LocaleLoader.getString("Party.ShareType.Item"), LocaleLoader.getString("Party.ShareMode." + Misc.getCapitalized(mode.toString()))));
        }
    }
}