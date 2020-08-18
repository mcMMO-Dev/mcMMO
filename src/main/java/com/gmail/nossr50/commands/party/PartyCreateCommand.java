package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PartyCreateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        switch (args.length) {
            case 2:
            case 3:
                Player player = (Player) sender;
                McMMOPlayer mmoPlayer = mcMMO.getUserManager().getPlayer(player);

                if(mcMMO.getUserManager().getPlayer(player) == null)
                {
                    player.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));
                    return true;
                }

                // Check to see if the party exists, and if it does cancel creating a new party
                if (mcMMO.getPartyManager().checkPartyExistence(player, args[1])) {
                    return true;
                }

                // Changing parties
                if (!mcMMO.getPartyManager().changeOrJoinParty(mmoPlayer, args[1])) {
                    return true;
                }

                mcMMO.getPartyManager().createParty(mmoPlayer, args[1], getPassword(args));
                return true;

            default:
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.3", "party", "create", "<" + LocaleLoader.getString("Commands.Usage.PartyName") + ">", "[" + LocaleLoader.getString("Commands.Usage.Password") + "]"));
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
