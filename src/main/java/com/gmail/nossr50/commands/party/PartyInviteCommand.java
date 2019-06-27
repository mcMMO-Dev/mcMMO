package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
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

                if (UserManager.getPlayer((Player) sender) == null) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
                    return true;
                }

                Player player = (Player) sender;
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
                String playerName = player.getName();

                if (player.equals(target)) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Party.Invite.Self"));
                    return true;
                }

                if (pluginRef.getPartyManager().inSameParty(player, target)) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Party.Player.InSameParty", targetName));
                    return true;
                }

                if (!pluginRef.getPartyManager().canInvite(mcMMOPlayer)) {
                    player.sendMessage(pluginRef.getLocaleManager().getString("Party.Locked"));
                    return true;
                }

                Party playerParty = mcMMOPlayer.getParty();

                if (pluginRef.getConfigManager().getConfigParty().getPartyGeneral().isPartySizeCapped())
                    if (pluginRef.getPartyManager().isPartyFull(target, playerParty)) {
                        player.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.PartyFull.Invite",
                                target.getName(), playerParty.toString(),
                                pluginRef.getConfigManager().getConfigParty().getPartySizeLimit()));
                        return true;
                    }

                mcMMOTarget.setPartyInvite(playerParty);

                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Invite.Success"));
                target.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.Invite.0", playerParty.getName(), playerName));
                target.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.Invite.1"));
                return true;

            default:
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Usage.2", "party", "invite", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + ">"));
                return true;
        }
    }
}
