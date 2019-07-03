package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyJoinCommand implements CommandExecutor {

    private final mcMMO pluginRef;

    public PartyJoinCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2:
            case 3:
                String targetName = pluginRef.getCommandTools().getMatchedPlayerName(args[1]);
                McMMOPlayer mcMMOTarget = pluginRef.getUserManager().getPlayer(targetName);

                if (!pluginRef.getCommandTools().checkPlayerExistence(sender, targetName, mcMMOTarget)) {
                    return true;
                }

                Player target = mcMMOTarget.getPlayer();

                if (!mcMMOTarget.inParty()) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Party.PlayerNotInParty", targetName));
                    return true;
                }

                Player player = (Player) sender;

                if (pluginRef.getUserManager().getPlayer((Player) sender) == null) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
                    return true;
                }

                McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);
                Party targetParty = mcMMOTarget.getParty();

                if (player.equals(target) || (mcMMOPlayer.inParty() && mcMMOPlayer.getParty().equals(targetParty))) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Party.Join.Self"));
                    return true;
                }

                String password = getPassword(args);

                // Make sure party passwords match
                if (!pluginRef.getPartyManager().checkPartyPassword(player, targetParty, password)) {
                    return true;
                }

                String partyName = targetParty.getName();

                // Changing parties
                if (!pluginRef.getPartyManager().changeOrJoinParty(mcMMOPlayer, partyName)) {
                    return true;
                }

                if (pluginRef.getConfigManager().getConfigParty().getPartyGeneral().isPartySizeCapped())
                    if (pluginRef.getPartyManager().isPartyFull(player, targetParty)) {
                        player.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.PartyFull", targetParty.toString()));
                        return true;
                    }

                player.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.Join", partyName));
                pluginRef.getPartyManager().addToParty(mcMMOPlayer, targetParty);
                return true;

            default:
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Usage.3", "party", "join", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + ">", "[" + pluginRef.getLocaleManager().getString("Commands.Usage.Password") + "]"));
                return true;
        }
    }

    private String getPassword(String[] args) {
        if (args.length == 3) {
            return args[2];
        }

        return null;
    }
}
