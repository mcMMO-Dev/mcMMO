package com.gmail.nossr50.commands.party.alliance;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyAllianceInviteCommand implements CommandExecutor {

    private mcMMO pluginRef;

    public PartyAllianceInviteCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 3:
                String targetName = pluginRef.getCommandTools().getMatchedPlayerName(args[2]);
                McMMOPlayer mcMMOTarget = UserManager.getOfflinePlayer(targetName);

                if (!pluginRef.getCommandTools().checkPlayerExistence(sender, targetName, mcMMOTarget)) {
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

                if (!mcMMOTarget.inParty()) {
                    player.sendMessage(pluginRef.getLocaleManager().getString("Party.PlayerNotInParty", targetName));
                    return true;
                }

                if (pluginRef.getPartyManager().inSameParty(player, target)) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Party.Player.InSameParty", targetName));
                    return true;
                }

                if (!mcMMOTarget.getParty().getLeader().getUniqueId().equals(target.getUniqueId())) {
                    player.sendMessage(pluginRef.getLocaleManager().getString("Party.Target.NotOwner", targetName));
                    return true;
                }

                Party playerParty = mcMMOPlayer.getParty();

                if (playerParty.getAlly() != null) {
                    player.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.Alliance.AlreadyAllies"));
                    return true;
                }

                mcMMOTarget.setPartyAllianceInvite(playerParty);

                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Invite.Success"));
                target.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.Alliance.Invite.0", playerParty.getName(), playerName));
                target.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.Alliance.Invite.1"));
                return true;

            default:
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Usage.3", "party", "alliance", "invite", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + ">"));
                return true;
        }
    }
}
