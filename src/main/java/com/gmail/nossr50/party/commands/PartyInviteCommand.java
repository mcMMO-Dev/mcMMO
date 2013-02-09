package com.gmail.nossr50.party.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class PartyInviteCommand implements CommandExecutor {
    private McMMOPlayer mcMMOTarget;
    private Player target;

    private McMMOPlayer mcMMOPlayer;
    private Player player;
    private Party playerParty;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.hasPermission(sender, "mcmmo.commands.party.invite")) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        switch (args.length) {
        case 2:
            if (!mcMMO.p.getServer().getOfflinePlayer(args[1]).isOnline()) {
                sender.sendMessage(LocaleLoader.getString("Party.NotOnline", args[1]));
                return false;
            }

            mcMMOTarget = Users.getPlayer(args[1]);

            if (mcMMOTarget == null) {
                sender.sendMessage(LocaleLoader.getString("Party.Player.Invalid"));
                return false;
            }

            target = mcMMOTarget.getPlayer();
            mcMMOPlayer = Users.getPlayer((Player) sender);
            player = mcMMOPlayer.getPlayer();

            if (PartyManager.inSameParty(player, target)) {
                sender.sendMessage(LocaleLoader.getString("Party.Player.InSameParty", target.getName()));
                return true;
            }

            playerParty = mcMMOPlayer.getParty();

            if (!PartyManager.canInvite(player, playerParty)) {
                player.sendMessage(LocaleLoader.getString("Party.Locked"));
                return true;
            }

            mcMMOTarget.setPartyInvite(playerParty);

            sender.sendMessage(LocaleLoader.getString("Commands.Invite.Success"));
            target.sendMessage(LocaleLoader.getString("Commands.Party.Invite.0", playerParty.getName(), player.getName()));
            target.sendMessage(LocaleLoader.getString("Commands.Party.Invite.1"));
            return true;

        default:
            sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "invite", "<" + LocaleLoader.getString("Commands.Usage.Player") + ">"));
            return true;
        }
    }
}
