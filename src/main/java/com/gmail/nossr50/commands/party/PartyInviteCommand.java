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
        switch (args.length) {
            case 2:
                McMMOPlayer mcMMOTarget = UserManager.getPlayer(args[1]);

                if (CommandUtils.checkPlayerExistence(sender, args[1], mcMMOTarget)) {
                    return true;
                }

                Player target = mcMMOTarget.getPlayer();
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(sender.getName());
                Player player = mcMMOPlayer.getPlayer();

                if (player.equals(target)) {
                    sender.sendMessage(LocaleLoader.getString("Party.Invite.Self"));
                    return true;
                }

                if (PartyManager.inSameParty(player, target)) {
                    sender.sendMessage(LocaleLoader.getString("Party.Player.InSameParty", target.getName()));
                    return true;
                }

                Party playerParty = mcMMOPlayer.getParty();

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
