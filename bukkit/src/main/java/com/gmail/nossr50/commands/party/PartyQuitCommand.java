package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.core.data.UserManager;
import com.gmail.nossr50.core.datatypes.party.Party;
import com.gmail.nossr50.core.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.core.locale.LocaleLoader;
import com.gmail.nossr50.core.party.PartyManager;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyQuitCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                Player player = (Player) sender;
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
                Party playerParty = mcMMOPlayer.getParty();

                if (!PartyManager.handlePartyChangeEvent(player, playerParty.getName(), null, EventReason.LEFT_PARTY)) {
                    return true;
                }

                PartyManager.removeFromParty(mcMMOPlayer);
                sender.sendMessage(LocaleLoader.getString("Commands.Party.Leave"));
                return true;

            default:
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.1", "party", "quit"));
                return true;
        }
    }
}
