package com.gmail.nossr50.commands.party;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.player.UserManager;

public class PartyCreateCommand implements CommandExecutor {
    private McMMOPlayer mcMMOPlayer;
    private Player player;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2:
                // Fallthrough
            case 3:
                Party newParty = PartyManager.getParty(args[1]);

                // Check to see if the party exists, and if it does cancel creating a new party
                if (newParty != null) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Party.AlreadyExists", args[1]));
                    return true;
                }

                player = (Player) sender;
                mcMMOPlayer = UserManager.getPlayer(player);

                // Changing parties
                if (!PartyManager.changeOrJoinParty(mcMMOPlayer, player, mcMMOPlayer.getParty(), args[1])) {
                    return true;
                }

                PartyManager.createParty(player, mcMMOPlayer, args[1], getPassword(args));
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
