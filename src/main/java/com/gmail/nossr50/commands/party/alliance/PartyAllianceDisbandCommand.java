package com.gmail.nossr50.commands.party.alliance;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyAllianceDisbandCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2:
                if(UserManager.getPlayer((Player) sender) == null)
                {
                    sender.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));
                    return true;
                }
                Player player = (Player) sender;
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
                Party party = mcMMOPlayer.getParty();

                if (party.getAlly() == null) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Party.Alliance.None"));
                    return true;
                }

                PartyManager.disbandAlliance(player, party, party.getAlly());
                return true;

            default:
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "alliance", "disband"));
                return true;
        }
    }
}
