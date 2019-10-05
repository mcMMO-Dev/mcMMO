package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.mcMMO;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyCreateCommand implements CommandExecutor {

    private final mcMMO pluginRef;

    public PartyCreateCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2:
            case 3:
                Player player = (Player) sender;
                BukkitMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);

                if (pluginRef.getUserManager().getPlayer(player) == null) {
                    player.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
                    return true;
                }

                // Check to see if the party exists, and if it does cancel creating a new party
                if (pluginRef.getPartyManager().checkPartyExistence(player, args[1])) {
                    return true;
                }

                // Changing parties
                if (!pluginRef.getPartyManager().changeOrJoinParty(mcMMOPlayer, args[1])) {
                    return true;
                }

                pluginRef.getPartyManager().createParty(mcMMOPlayer, args[1], getPassword(args));
                return true;

            default:
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Usage.3", "party", "create", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.PartyName") + ">", "[" + pluginRef.getLocaleManager().getString("Commands.Usage.Password") + "]"));
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
