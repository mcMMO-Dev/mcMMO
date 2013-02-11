package com.gmail.nossr50.party.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.party.ShareHandler;
import com.gmail.nossr50.party.ShareHandler.ShareMode;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class PartyExpShareCommand implements CommandExecutor {
    private Player player;
    private Party playerParty;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.hasPermission(sender, "mcmmo.commands.party.expshare")) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        switch (args.length) {
        case 2:
            player = (Player) sender;
            playerParty = Users.getPlayer(player).getParty();

            if (!playerParty.getLeader().equals(player.getName())) {
                sender.sendMessage(LocaleLoader.getString("Party.NotOwner"));
                return true;
            }

            if (args[1].equalsIgnoreCase("none") || args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("false")) {
                handleChangingShareMode(ShareMode.NONE);
            }
            else if (args[1].equalsIgnoreCase("equal") || args[1].equalsIgnoreCase("even") || args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("true")) {
                handleChangingShareMode(ShareMode.EQUAL);
            }
            else {
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "expshare", "[NONE | EQUAL]"));
            }

            return true;

        default:
            sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "expshare", "<NONE | EQUAL>"));
            return true;
        }
    }

    private void handleChangingShareMode(ShareHandler.ShareMode mode) {
        playerParty.setXpShareMode(mode);

        for (Player member : playerParty.getOnlineMembers()) {
            member.sendMessage(LocaleLoader.getString("Commands.Party.SetSharing", LocaleLoader.getString("Party.ShareType.Exp"), LocaleLoader.getString("Party.ShareMode." + Misc.getCapitalized(mode.toString()))));
        }
    }
}
