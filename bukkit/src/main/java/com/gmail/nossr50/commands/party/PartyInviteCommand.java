package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.core.config.Config;
import com.gmail.nossr50.core.data.UserManager;
import com.gmail.nossr50.core.datatypes.party.Party;
import com.gmail.nossr50.core.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.core.locale.LocaleLoader;
import com.gmail.nossr50.core.party.PartyManager;
import com.gmail.nossr50.core.util.commands.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyInviteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2:
                String targetName = CommandUtils.getMatchedPlayerName(args[1]);
                McMMOPlayer mcMMOTarget = UserManager.getOfflinePlayer(targetName);

                if (!CommandUtils.checkPlayerExistence(sender, targetName, mcMMOTarget)) {
                    return false;
                }

                Player target = mcMMOTarget.getPlayer();
                Player player = (Player) sender;
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
                String playerName = player.getName();

                if (player.equals(target)) {
                    sender.sendMessage(LocaleLoader.getString("Party.Invite.Self"));
                    return true;
                }

                if (PartyManager.inSameParty(player, target)) {
                    sender.sendMessage(LocaleLoader.getString("Party.Player.InSameParty", targetName));
                    return true;
                }

                if (!PartyManager.canInvite(mcMMOPlayer)) {
                    player.sendMessage(LocaleLoader.getString("Party.Locked"));
                    return true;
                }

                Party playerParty = mcMMOPlayer.getParty();

                if (PartyManager.isPartyFull(target, playerParty)) {
                    player.sendMessage(LocaleLoader.getString("Commands.Party.PartyFull.Invite", target.getName(), playerParty.toString(), Config.getInstance().getPartyMaxSize()));
                    return true;
                }

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
