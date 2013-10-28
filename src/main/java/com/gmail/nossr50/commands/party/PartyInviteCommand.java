package com.gmail.nossr50.commands.party;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;

public class PartyInviteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return false;
        }
        switch (args.length) {
            case 2:
                Player player = (Player) sender;
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
                String playerName = player.getName();

                if (playerName.equalsIgnoreCase(args[1])) {
                    player.sendMessage(LocaleLoader.getString("Party.Invite.Self"));
                    return true;
                }

                if (!PartyManager.canInvite(mcMMOPlayer)) {
                    player.sendMessage(LocaleLoader.getString("Party.Locked"));
                    return true;
                }

                McMMOPlayer mcMMOTarget = UserManager.getPlayer(args[1], true);

                if (mcMMOTarget == null) {
                    player.sendMessage(LocaleLoader.getString("Commands.Offline"));
                    return true;
                }

                Player target = mcMMOTarget.getPlayer();

                if (PartyManager.inSameParty(player, target)) {
                    sender.sendMessage(LocaleLoader.getString("Party.Player.InSameParty", target.getName()));
                    return true;
                }

                Party playerParty = mcMMOPlayer.getParty();
                mcMMOTarget.setPartyInvite(playerParty);

                sender.sendMessage(LocaleLoader.getString("Commands.Invite.Success"));
                target.sendMessage(LocaleLoader.getString("Commands.Party.Invite.0", playerParty.getName(), playerName));
                target.sendMessage(LocaleLoader.getString("Commands.Party.Invite.1"));
                return true;

            default:
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "invite", "<" + LocaleLoader.getString("Commands.Usage.Player") + ">"));
                return true;
        }
    }
}
