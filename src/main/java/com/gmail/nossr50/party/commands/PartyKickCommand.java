package com.gmail.nossr50.party.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class PartyKickCommand implements CommandExecutor {
    private Player player;
    private Party playerParty;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.hasPermission(sender, "mcmmo.commands.party.kick")) {
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

            McMMOPlayer mcMMOTarget = Users.getPlayer(args[1]);

            // Would be nice to find a way to check if a player is valid here - this won't work directly because it'll also throw null for an offline player
//            if (mcMMOTarget == null) {
//                sender.sendMessage(LocaleLoader.getString("Party.Player.Invalid"));
//                return false;
//            }

            Player target = mcMMOTarget.getPlayer();

            if (!PartyManager.inSameParty(player, target)) {
                sender.sendMessage(LocaleLoader.getString("Party.NotInYourParty", args[1]));
                return true;
            }

            if (mcMMO.p.getServer().getOfflinePlayer(args[1]).isOnline()) {
                String partyName = playerParty.getName();

                McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(target, partyName, null, EventReason.KICKED_FROM_PARTY);
                mcMMO.p.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return true;
                }

                target.sendMessage(LocaleLoader.getString("Commands.Party.Kick", partyName));
            }

            PartyManager.removeFromParty(target, playerParty);
            return true;

        default:
            sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "kick", "<" + LocaleLoader.getString("Commands.Usage.Player") + ">"));
            return true;
        }

        // TODO Auto-generated method stub
        return false;
    }

}
