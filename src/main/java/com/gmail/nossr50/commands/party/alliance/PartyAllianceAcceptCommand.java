package com.gmail.nossr50.commands.party.alliance;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyAllianceAcceptCommand implements CommandExecutor {

    private mcMMO pluginRef;

    public PartyAllianceAcceptCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2:
                if (pluginRef.getUserManager().getPlayer((Player) sender) == null) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
                    return true;
                }
                Player player = (Player) sender;
                McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);

                if (!mcMMOPlayer.hasPartyAllianceInvite()) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("mcMMO.NoInvites"));
                    return true;
                }

                if (mcMMOPlayer.getParty().getAlly() != null) {
                    player.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.Alliance.AlreadyAllies"));
                    return true;
                }

                pluginRef.getPartyManager().acceptAllianceInvite(mcMMOPlayer);
                return true;

            default:
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Usage.2", "party", "alliance", "accept"));
                return true;
        }
    }
}
