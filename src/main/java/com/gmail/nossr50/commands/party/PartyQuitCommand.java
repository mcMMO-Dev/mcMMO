package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.mcMMO;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyQuitCommand implements CommandExecutor {

    private mcMMO pluginRef;

    public PartyQuitCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                Player player = (Player) sender;

                if (pluginRef.getUserManager().getPlayer((Player) sender) == null) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
                    return true;
                }

                McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);
                Party playerParty = mcMMOPlayer.getParty();

                if (!pluginRef.getPartyManager().handlePartyChangeEvent(player, playerParty.getName(), null, EventReason.LEFT_PARTY)) {
                    return true;
                }

                pluginRef.getPartyManager().removeFromParty(mcMMOPlayer);
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.Leave"));
                return true;

            default:
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Usage.1", "party", "quit"));
                return true;
        }
    }
}
